package org.ops4j.op;

import java.util.List;

import org.ops4j.BaseOp;
import org.ops4j.OpData;
import org.ops4j.OpCLI;
import org.ops4j.exception.OpsException;

import picocli.CommandLine.Command;

@Command(name = "noop", description = "This operation does nothing.")
public class NoOp extends BaseOp<NoOp>
{
  public NoOp()
  {
    super("noop");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    debug("noop: ", input.toString());
    return input.asList();
  }
  
  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new NoOp(), args);
  }
}
