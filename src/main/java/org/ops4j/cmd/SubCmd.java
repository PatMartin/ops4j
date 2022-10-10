package org.ops4j.cmd;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.Option;

public class SubCmd
{
  @Option(names = { "-h", "--help" }, description = "Get help.")
  private @Getter @Setter boolean help = false;

  public SubCmd()
  {
  }

  public void help(Object obj)
  {
    CommandLine cmdLine = new CommandLine(obj);
    Help help = new Help(cmdLine.getCommandSpec());

    //System.out.println(help.synopsis(1));
    System.out.println(help.fullSynopsis());
    System.out.println(help.description());
    if (help.parameterList().trim().length() > 0)
    {
      System.out.println("Parameters:\n");
      System.out.println(help.parameterList());
    }
    if (help.optionList().trim().length() > 0)
    {
      System.out.println("Options:\n");
      System.out.println(help.optionList());
    }
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new SubCmd());
    cli.execute(args);
  }
}
