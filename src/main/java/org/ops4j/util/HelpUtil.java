package org.ops4j.util;

import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Ansi.Style;
import picocli.CommandLine.Help.ColorScheme;

public class HelpUtil
{
  public static ColorScheme scheme = new ColorScheme.Builder()
      .commands(Style.bold, Style.fg_yellow).options(Style.fg_cyan)
      .parameters(Style.bold, Style.fg_white).optionParams(Style.fg_cyan)
      .errors(Style.fg_red, Style.bold).stackTraces(Style.italic)
      .applySystemProperties().build();

  public static String getUsage(Object obj)
  {
    StringBuffer sb = new StringBuffer();

    CommandLine cmd = new CommandLine(obj);
    cmd.setColorScheme(scheme);
    Help opHelp = new Help(cmd.getCommandSpec(), scheme);

    sb.append(opHelp.fullSynopsis());
    sb.append("\n");
    sb.append(opHelp.description());
    sb.append("\n");
    return sb.toString();
  }

  public static String getHelp(Object obj)
  {
    StringBuffer sb = new StringBuffer();
    CommandLine cmd = new CommandLine(obj);
    cmd.setColorScheme(scheme);
    Help opHelp = new Help(cmd.getCommandSpec(), scheme);

    sb.append(opHelp.fullSynopsis());
    sb.append("\n");
    sb.append(opHelp.description());
    sb.append("\n");

    if (opHelp.parameterList().trim().length() > 0)
    {
      sb.append(opHelp.parameterList());
      sb.append("\n");
    }
    if (opHelp.optionList().trim().length() > 0)
    {
      sb.append(opHelp.optionList());
      sb.append("\n");
    }
    System.out.println("Class: " + obj.getClass().getName());
    return sb.toString();
  }
}
