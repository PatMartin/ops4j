package org.ops4j.util;

import picocli.CommandLine;
import picocli.CommandLine.Help;

public class HelpUtil
{
  public void help(Object obj) throws Exception
  {
    CommandLine cmdLine = new CommandLine(obj);
    Help help = new Help(cmdLine.getCommandSpec());
    System.out.println(help.synopsis(1));
    System.out.println(help.fullSynopsis());
    System.out.println(help.description());
    if (help.parameterList().trim().length() > 0)
    {
      System.out.println(help.parameterList());
    }
    System.out.println(help.optionList());
  }
}
