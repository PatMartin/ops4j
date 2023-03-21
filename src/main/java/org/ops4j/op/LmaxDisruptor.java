package org.ops4j.op;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.QueuesOf;
import org.ops4j.util.Ops;
import org.ops4j.util.ThreadUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.auto.service.AutoService;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.LiteTimeoutBlockingWaitStrategy;
import com.lmax.disruptor.PhasedBackoffWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "disruptor",
    description = "Run a set of operations in an LMAX disruptor.")
public class LmaxDisruptor extends BaseOp<LmaxDisruptor>
    implements QueuesOf<List<OpData>>
{
  enum WaitType {
    BLOCKING, BUSY_SPIN, LITE_BLOCKING, LITE_TIMEOUT, PHASED_BACKOFF, SLEEPING,
    TIMEOUT, YIELDING
  }

  @Option(names = { "-s", "--buffer-size" },
      description = "The number of slots in the ringbuffer.")
  private @Getter @Setter int                 bufferSize      = 1024;

  @Option(names = { "-w", "--wait" }, description = "The wait strategy.")
  private @Getter
  @Setter WaitType                            waitType        = WaitType.BLOCKING;

  @Option(names = { "-p", "--producer-type" },
      description = "The type of producer, MULTI or SINGLE.")
  private @Getter
  @Setter ProducerType                        producerType    = ProducerType.MULTI;

  @Parameters(index = "0", arity = "1..*",
      description = "One or more operations to be executed")
  private @Getter
  @Setter List<String>                        cmds            = new LinkedList<>();

  @Option(names = { "--dsl" },
      description = "A DSL which controls the execution of the "
          + "operations within the disruptor.  When this argument is set, the operations must "
          + "be named; ie: set as <name>=<value> and the names should coorespond to the names "
          + "within the DSL.%n%nEx: --dsl \"op1 -> op2\" op1=flatten op2=benchmark")
  private @Getter @Setter String              dsl             = null;
  private Map<String, Op<?>>                  opmap;
  private List<Op<?>>                         ops;

  @Option(names = { "--output-queue" },
      description = "The output queue type.  ")
  private @Getter
  @Setter QueuesOf.QueueType                  outputQueueType = QueuesOf.QueueType.BLOCKING_ARRAY;

  @JsonIgnore
  private @Getter @Setter Queue<List<OpData>> outputQueue;

  @JsonIgnore
  private Disruptor<OpDataEvent>              disruptor;

  @JsonIgnore
  private RingBuffer<OpDataEvent>             ringBuffer;

  public LmaxDisruptor()
  {
    super("disruptor");
  }

  public class OpDataEvent
  {
    List<OpData> data;
  }

  public class OpDataEventFactory implements EventFactory<OpDataEvent>
  {
    @Override
    public OpDataEvent newInstance()
    {
      return new OpDataEvent();
    }
  }

  public OpDataEventHandler[] createHandlers(Op<?> op, int totalWorkers)
      throws OpsException
  {
    OpDataEventHandler[] handlers = new OpDataEventHandler[totalWorkers];
    for (int i = 0; i < totalWorkers; i++)
    {
      Op<?> concurrentOp = op.copy();
      concurrentOp.setName(concurrentOp.getName() + "-" + i);
      concurrentOp.initialize().open();
      handlers[i] = new OpDataEventHandler(concurrentOp, i, totalWorkers);
    }
    return handlers;
  }

  public class OpDataEventHandler implements EventHandler<OpDataEvent>
  {
    private final Op<?> op;
    private boolean     last;
    private int         totalWorkers = 1;
    private int         workerIndex  = 0;

    public OpDataEventHandler(Op<?> op)
    {
      this.op = op;
      this.last = false;
    }

    public OpDataEventHandler(Op<?> op, int workerIndex, int totalWorkers)
    {
      this(op);
      this.workerIndex = workerIndex;
      this.totalWorkers = totalWorkers;
    }

    @Override
    public void onEvent(final OpDataEvent event, final long sequence,
        final boolean endOfBatch) throws OpsException
    {
      if ((sequence % totalWorkers) != workerIndex)
      {
        return;
      }
      // OpLogger.syserr("Handler: ", op.getName(), " handling sequence ",
      // sequence);
      // System.out.println("on-event: json=" + event.data.toString());
      List<OpData> output = new ArrayList<>();

      if (last)
      {
        for (OpData data : event.data)
        {
          output.addAll(op.execute(data));
        }
        // event.data = null;
        while (outputQueue.offer(output) == false)
        {
          ThreadUtil.sleep(1L);
        }
      }
      else
      {
        for (OpData data : event.data)
        {
          output.addAll(op.execute(data));
        }
        event.data = output;
      }
    }

    public void setLast(boolean last)
    {
      this.last = last;
    }
  }

  public LmaxDisruptor initialize() throws OpsException
  {
    opmap = new HashMap<String, Op<?>>();
    outputQueue = queueOf(getOutputQueueType());

    if (getDsl() != null)
    {
      for (String cmd : getCmds())
      {
        int splitIndex = cmd.indexOf('=');

        if (splitIndex == -1)
        {
          throw new OpsException(
              "Command: 'cmd' must be defined in the form of <name>=" + cmd
                  + " the dsl option is set.");
        }

        String opName = cmd.substring(0, splitIndex).trim();

        ops = Ops.parseCommands(cmd.substring(splitIndex + 1));
        if (ops.size() == 1)
        {
          opmap.put(opName, ops.get(0));
        }
        else if (ops.size() > 1)
        {
          opmap.put(opName, Pipeline.of(ops).name(opName));
        }
        else
        {
          throw new OpsException(
              "No operations supplied in argument: '" + cmd + "'");
        }
      }
      ops = new ArrayList<Op<?>>(opmap.values());
    }
    else
    {
      ops = Ops.parseCommands(StringUtils.join(getCmds(), " "));
    }

    if (ops != null && ops.size() > 0)
    {
      for (Op<?> op : ops)
      {
        op.initialize();
      }
    }
    else
    {
      throw new OpsException("No operations defined for disruptor.");
    }

    return this;
  }

  enum GroupType {
    SEQUENTIAL, CONCURRENT
  }

  public LmaxDisruptor open() throws OpsException
  {
    for (Op<?> op : ops)
    {
      op.open();
    }
    // Construct the Disruptor
    // disruptor = new Disruptor<>(OpDataEvent::new, bufferSize,
    // DaemonThreadFactory.INSTANCE);
    disruptor = new Disruptor<>(OpDataEvent::new, bufferSize,
        DaemonThreadFactory.INSTANCE, ProducerType.MULTI, getWaitStrategy());
    if (getDsl() != null)
    {
      OpDataEventHandler previous[] = null;
      Pattern MULTI_WORKER = Pattern
          .compile("^\\s*(\\d+)\\s*\\(\\s*(.*)\\s*\\)");

      // parse dsl
      String groups[] = StringUtils.split(getDsl(), '|');
      for (int g = 0; g < groups.length; g++)
      {
        String ops[] = StringUtils.split(groups[g], '&');
        List<OpDataEventHandler> handlers = new ArrayList<>();
        for (int i = 0; i < ops.length; i++)
        {
          Matcher m = MULTI_WORKER.matcher(ops[i].trim());
          if (m.matches())
          {
            DEBUG("Creating ", m.group(1), " instances of '", m.group(2), "'");
            OpDataEventHandler h[] = createHandlers(opmap.get(m.group(2)),
                Integer.parseInt(m.group(1)));
            for (OpDataEventHandler hnd : h)
            {
              DEBUG("Handler: ", hnd.op.getName());
              handlers.add(hnd);
            }
          }
          else
          {
            DEBUG("Creating 1 instance of '", ops[i].trim(), "'");
            handlers.add(new OpDataEventHandler(opmap.get(ops[i].trim())));
          }
        }
        // This group is last and its last handler should write to the output
        // queue.
        if (g == (groups.length - 1))
        {
          // OpLogger.syserr("Setting ",
          // handlers.get(handlers.size() - 1).op.getName(), " to last.");
          handlers.get(handlers.size() - 1).setLast(true);
        }
        if (previous != null)
        {
          disruptor.after(previous)
              .then(handlers.toArray(new OpDataEventHandler[0]));
        }
        else
        {
          disruptor
              .handleEventsWith(handlers.toArray(new OpDataEventHandler[0]));
        }
        previous = handlers.toArray(new OpDataEventHandler[0]);
      }
    }
    else
    {
      OpDataEventHandler handlers[] = new OpDataEventHandler[ops.size()];
      for (int i = 0; i < handlers.length; i++)
      {
        handlers[i] = new OpDataEventHandler(ops.get(i));
      }
      handlers[handlers.length - 1].setLast(true);
      disruptor.handleEventsWith(handlers);
    }

    // Start the Disruptor, starts all threads running
    disruptor.start();

    // Get the ring buffer from the Disruptor to be used for publishing.
    ringBuffer = disruptor.getRingBuffer();

    return this;
  }

  public WaitStrategy getWaitStrategy()
  {
    switch (getWaitType())
    {
      case BUSY_SPIN:
      {
        return new BusySpinWaitStrategy();
      }
      case BLOCKING:
      {
        return new BlockingWaitStrategy();
      }
      case LITE_BLOCKING:
      {
        return new LiteBlockingWaitStrategy();
      }
      case LITE_TIMEOUT:
      {
        return new LiteTimeoutBlockingWaitStrategy(300L, TimeUnit.SECONDS);
      }
      case PHASED_BACKOFF:
      {
        return new PhasedBackoffWaitStrategy(300L, 300L, TimeUnit.SECONDS,
            new BlockingWaitStrategy());
      }
      case SLEEPING:
      {
        return new SleepingWaitStrategy();
      }
      case TIMEOUT:
      {
        return new TimeoutBlockingWaitStrategy(300L, TimeUnit.SECONDS);
      }
      case YIELDING:
      {
        return new YieldingWaitStrategy();
      }
      default:
      {
        return new BlockingWaitStrategy();
      }
    }
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    ringBuffer
        .publishEvent((event, sequence, buffer) -> event.data = input.asList());

    List<OpData> outputs = new ArrayList<>();
    List<OpData> output;

    while ((output = outputQueue.poll()) != null)
    {
      outputs.addAll(output);
    }

    // OpLogger.syserr("OUTPUTS: ", outputs.size());
    return outputs;
    // return OpData.emptyList();
  }

  public List<OpData> close() throws OpsException
  {
    // TODO: How to handle this?
    for (Op<?> op : ops)
    {
      op.close();
    }

    try
    {
      disruptor.shutdown(-1L, TimeUnit.MILLISECONDS);
    }
    catch(TimeoutException ex)
    {
      throw new OpsException(ex);
    }
    return OpData.emptyList();
  }

  public LmaxDisruptor cleanup() throws OpsException
  {
    for (Op<?> op : ops)
    {
      op.cleanup();
    }
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new LmaxDisruptor(), args);
  }
}
