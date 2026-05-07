package org.ops4j.cli;

import java.util.concurrent.Callable;

import org.ops4j.cmd.EnvCmd;
import org.ops4j.cmd.GetCmd;
import org.ops4j.cmd.InfoCmd;
import org.ops4j.cmd.ListCmd;
import org.ops4j.cmd.RemoveCmd;
import org.ops4j.cmd.RunCmd;
import org.ops4j.cmd.SaveCmd;
import org.ops4j.cmd.SetCmd;
import org.ops4j.cmd.TocCmd;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ops", mixinStandardHelpOptions = true,
    subcommands = { EnvCmd.class, GetCmd.class, InfoCmd.class, ListCmd.class,
        RemoveCmd.class, RunCmd.class, SaveCmd.class, SetCmd.class,
        TocCmd.class },
    description = "This is the top level CLI for Ops4J.  This CLI "
        + "provides tooling for managing operations.")
public class Ops4JCLI implements Callable<Integer>
{
  @Option(names = { "-h", "--help" }, usageHelp = true,
      description = "Display help.")
  private boolean usageHelpRequested;

  public Ops4JCLI()
  {
  }

  @Override
  public Integer call() throws Exception
  {
    // Default mode is a grid node?
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new Ops4JCLI());
    cli.setCaseInsensitiveEnumValuesAllowed(true);
    cli.execute(args);
  }
}
