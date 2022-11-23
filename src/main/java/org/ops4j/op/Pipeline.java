package org.ops4j.op;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.Ops;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "pipeline", description = "Benchmark something.")
public class Pipeline extends BaseOp<Pipeline>
{
  @Parameters(index = "0", arity = "0..*",
      description = "Run a pipeline of operations.")
  private @Getter @Setter List<String> commands;

  @Option(names = { "-i", "--immutable" },
      description = "Runs the pipeline as an immutable pipeline.")
  private @Getter @Setter Boolean      immutable = false;
  
  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
      property = "@class")
  private @Getter @Setter List<Op<?>>  ops       = new ArrayList<>();

  public Pipeline()
  {
    super("pipeline");
  }

  public Pipeline initialize() throws OpsException
  {
    if (ops == null)
    {
      ops = Ops.parseCommands(StringUtils.join(getCommands(), ' '));
    }
    // OpLogger.syserr("OPS: '",
    // ops.stream().map(op -> op.getName()).collect(Collectors.toList()), ",");
    // OpLogger.syserr("COMMANDS: '", StringUtils.join(getCommands()), ",");
    // debug("OPS: ", ops.size());
    for (Op<?> op : ops)
    {
      if (op.provides(PhaseType.INITIALIZE))
      {
        DEBUG("initializing ", op.getName());
        op.initialize();
      }
    }
    return this;
  }

  public Pipeline open() throws OpsException
  {
    for (Op<?> op : ops)
    {
      if (op.provides(PhaseType.OPEN))
      {
        DEBUG("opening ", op.getName());
        op.open();
      }
    }
    return this;
  }

  public List<OpData> execute(final OpData input) throws OpsException
  {
    List<OpData> curInput = input.asList();
    List<OpData> curOutput = null;
    for (Op<?> op : ops)
    {
      curOutput = new ArrayList<OpData>();
      for (OpData in : curInput)
      {
        curOutput.addAll(op.execute(in));
      }
      curInput = curOutput;
    }
    return curOutput;
  }

  public Pipeline close() throws OpsException
  {
    for (Op<?> op : ops)
    {
      if (op.provides(PhaseType.CLOSE))
      {
        DEBUG("close ", op.getName());
        op.close();
      }
    }
    return this;
  }

  public Pipeline cleanup() throws OpsException
  {
    for (Op<?> op : ops)
    {
      if (op.provides(PhaseType.CLEANUP))
      {
        DEBUG("cleanup ", op.getName());
        op.cleanup();
      }
    }
    return this;
  }

  public Pipeline ops(List<Op<?>> ops)
  {
    setOps(ops);
    return this;
  }

  public Pipeline add(Op<?> op)
  {
    getOps().add(op);
    return this;
  }

  public static Pipeline of(List<Op<?>> ops)
  {
    return new Pipeline().ops(ops);
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Pipeline(), args);
  }
}
