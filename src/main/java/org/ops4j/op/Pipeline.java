package org.ops4j.op;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.Ops;

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
  private @Getter @Setter String  commands;
  @Option(names = { "-i", "--immutable" },
      description = "Runs the pipeline as an immutable pipeline.")
  private @Getter @Setter Boolean immutable = false;
  private List<Op<?>>             ops;

  public Pipeline()
  {
    super("pipeline");
  }

  public Pipeline initialize() throws OpsException
  {
    ops = Ops.create(getCommands());
    // debug("OPS: ", ops.size());
    for (Op<?> op : ops)
    {
      if (op.provides(PhaseType.INITIALIZE))
      {
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
        op.open();
      }
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    List<OpData> curInput = input.copy().asList();
    List<OpData> curOutput = input.asList();
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
        op.cleanup();
      }
    }
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Pipeline(), args);
  }
}
