package org.ops4j.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class) @Command(name = "simulate",
    description = "Pause execution for the specified number of milliseconds.")
public class Simulate extends BaseOp<Simulate>
{
  @Option(names = { "-i", "--iterate" },
      description = "The number of times to iterate")
  private @Getter @Setter long iterations = 0L;

  public Simulate()
  {
    super("simulate");
  }

  public List<OpData> execute(OpData input)
  {
    for (long l = 0L; l < getIterations(); l++)
    {
    }
    return input.asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Simulate(), args);
  }
}
