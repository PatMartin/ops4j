package org.ops4j.cli;

import java.util.concurrent.Callable;

import org.ops4j.Ops4J;
import org.ops4j.cmd.EnvCmd;
import org.ops4j.cmd.GetCmd;
import org.ops4j.cmd.SetCmd;
import org.ops4j.cmd.TocCmd;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ops4j", description = "This is the main CLI for ops4j.")
public class CLI implements Callable<Integer>
{
  public CLI()
  {
  }

  @Option(names = { "-h", "--help" }, description = "Get detailed help.")
  private @Getter @Setter boolean help = false;

  @Override
  public Integer call() throws Exception
  {
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new Ops4J())
        .addSubcommand("toc", new TocCmd()).addSubcommand("get", new GetCmd())
        .addSubcommand("set", new SetCmd()).addSubcommand("env", new EnvCmd());
    cli.execute(args);
  }
}
