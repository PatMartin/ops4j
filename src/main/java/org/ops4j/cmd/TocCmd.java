package org.ops4j.cmd;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "toc", description = "Table of contents.")
public class TocCmd extends SubCmd implements Callable<Integer>
{
  public TocCmd()
  {
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
    }
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new TocCmd());
    cli.execute(args);
  }
}
