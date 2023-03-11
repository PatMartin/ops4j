package org.ops4j.util;

import java.util.List;

import org.ops4j.exception.OpsException;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

public class ConfigUtil
{
  public static void configure(Object cmd, String config) throws OpsException
  {
    // Do nothing for now.
    CommandSpec spec = CommandSpec.create();
    new CommandLine(cmd).parseArgs(config);
  }

  public static void configure(Object cmd, String args[]) throws OpsException
  {
    // Do nothing for now.
    CommandSpec spec = CommandSpec.create();
    new CommandLine(cmd).parseArgs(args);
  }

  public void configure(Object cmd, List<String> args) throws OpsException
  {
    // Do nothing for now.
    CommandSpec spec = CommandSpec.create();
    new CommandLine(cmd).setCaseInsensitiveEnumValuesAllowed(true)
        .parseArgs(args.toArray(new String[0]));
  }
}
