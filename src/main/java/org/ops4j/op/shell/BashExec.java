package org.ops4j.op.shell;

import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class) @Command(name = "bash:exec",
    description = "Execute a bash command leaving the stream unchanged.")
public class BashExec extends ShellOp<BashExec>
{
  public BashExec()
  {
    super("bash:exec");
  }

  public BashExec open() throws OpsException
  {
    getCommands().add(0, "-c");
    getCommands().add(0, "bash");
    super.open();
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    System.out.println("HI");
    OpCLI.cli(new BashExec(), args);
  }
}
