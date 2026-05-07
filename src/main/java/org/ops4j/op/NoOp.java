package org.ops4j.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class)
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
