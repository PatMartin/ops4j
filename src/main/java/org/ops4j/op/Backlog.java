package org.ops4j.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.concurrent.OpDataCallable;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.QueuesOf;
import org.ops4j.util.Ops;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "backlog",
    description = "Run operations using a backlog feeding concurrent workers.")
public class Backlog extends BaseOp<Backlog>
    implements QueuesOf<Future<List<OpData>>>
{
  @Parameters(index = "0", arity = "1",
      description = "The commands to be executed.")
  private @Getter @Setter String      commands;

  @Option(names = { "-iqt", "--input-queue-type" },
      description = "The input queue type.  DEFAULT='${DEFAULT-VALUE}'"
          + "%nVALID VALUES: ${COMPLETION-CANDIDATES}")
  @Getter @Setter
  QueuesOf.QueueType                  inputQueueType  = QueuesOf.QueueType.BLOCKING_ARRAY;

  @Option(names = { "-oqt", "--output-queue-type" },
      description = "The output queue type.  DEFAULT='${DEFAULT-VALUE}'"
          + "%nVALID VALUES: ${COMPLETION-CANDIDATES})")
  @Getter @Setter
  QueuesOf.QueueType                  outputQueueType = QueuesOf.QueueType.CONCURRENT_LINKED;

  Queue<OpData>                       inputQueue      = OpData
      .createQueue(inputQueueType);

  Queue<Future<List<OpData>>>         outputQueue     = queueOf(
      outputQueueType);
  Pipeline                            pipeline;

  @Option(names = { "-min", "--min-threads" },
      description = "The minimum number of threads."
          + "  DEFAULT='${DEFAULT-VALUE}'")
  @Getter @Setter
  int                                 minThreads      = 1;

  @Option(names = { "-max", "--max-threads" },
      description = "The maximum number of threads."
          + "  DEFAULT='${DEFAULT-VALUE}'")
  @Getter @Setter
  int                                 maxThreads      = 1;

  ExecutorService                     executor;

  private @Getter @Setter List<Op<?>> ops             = null;

  public Backlog()
  {
    super("backlog");
  }

  public Backlog initialize() throws OpsException
  {
    if (ops == null)
    {
      ops = Ops.parseCommands(getCommands());
    }
    pipeline = new Pipeline().ops(ops).initialize();
    executor = Executors.newFixedThreadPool(minThreads());
    DEBUG("CREATED EXECUTOR: ", executor);
    return this;
  }

  public Backlog open() throws OpsException
  {
    DEBUG("OPEN ", pipeline);
    pipeline.open();
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    List<OpData> output = new ArrayList<>();
    List<Future<List<OpData>>> completed = new ArrayList<>();

    outputQueue.add(executor.submit(new OpDataCallable(pipeline, input)));
    outputQueue.stream().forEach(f -> {
      if (f.isDone())
      {
        try
        {
          completed.add(f);
          output.addAll(f.get());
        }
        catch(InterruptedException | ExecutionException ex)
        {
          ex.printStackTrace();
        }
      }
    });
    outputQueue.removeAll(completed);
    completed.clear();
    DEBUG("EXECUTING EXECUTOR=", executor);
    return output;
  }

  @Override
  public List<OpData> close() throws OpsException
  {
    List<OpData> output = pipeline.close();
    executor.shutdown();
    try
    {
      executor.awaitTermination(60L, TimeUnit.SECONDS);
    }
    catch(InterruptedException ex)
    {
      WARN("Interrupted Exception: ", ex.getMessage());
    }
    return output;
  }

  public Backlog cleanup() throws OpsException
  {
    pipeline.cleanup();
    return this;
  }

  // Quality of life
  public Backlog minThreads(int minThreads)
  {
    setMinThreads(minThreads);
    return this;
  }

  public int minThreads()
  {
    return getMinThreads();
  }

  public Backlog maxThreads(int maxThreads)
  {
    setMaxThreads(maxThreads);
    return this;
  }

  public int maxThreads()
  {
    return getMaxThreads();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Backlog(), args);
  }
}
