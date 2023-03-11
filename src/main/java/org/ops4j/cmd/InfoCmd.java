package org.ops4j.cmd;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.ops4j.Ops4J;
import org.ops4j.buddies.StringBuddy;
import org.ops4j.inf.NodeOp;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpModule;
import org.ops4j.inf.OpRepo;
import org.ops4j.io.InputSource;
import org.ops4j.io.OutputDestination;
import org.ops4j.util.HelpUtil;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "info", mixinStandardHelpOptions = true, aliases = { "help" },
    description = "Get information on operations, node-operations, "
        + "repositories, input sources, output destinations or modules.")
public class InfoCmd extends SubCmd implements Callable<Integer>
{
  public enum InfoType {
    OP, NODEOP, REPO, SOURCE, DESTINATION, MODULE, ALL
  }

  @Option(names = { "-l", "--long" },
      description = "When set, produce long " + "listings for operations.")
  private boolean  longListing = false;

  @Option(names = { "-t", "--type" },
      description = "Filter information to the specified type. "
          + "Valid Values: (${COMPLETION-CANDIDATES})")
  private InfoType infoType    = null;

  @Parameters(index = "0", arity = "0..1", paramLabel = "<pattern>",
      description = "An optional search pattern.")
  private String   pattern;

  public InfoCmd()
  {
    super("info");
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
      return 0;
    }

    if (pattern == null)
    {
      pattern = ".*";
    }

    Pattern p = Pattern.compile(pattern);
    if (infoType == null || infoType == InfoType.ALL || infoType == InfoType.OP)
    {
      System.out.println(StringBuddy.from("== OPERATIONS ==").banner());
      Map<String, Op<?>> ops = Ops4J.locator().getOps();
      for (String name : ops.keySet())
      {
        if (p.matcher(name).matches())
        {
          if (longListing)
          {
            System.out.println(HelpUtil.getHelp(ops.get(name)));
          }
          else
          {
            System.out.println(HelpUtil.getUsage(ops.get(name)));
          }
        }
      }
    }

    if (infoType == null || infoType == InfoType.ALL
        || infoType == InfoType.NODEOP)
    {
      System.out.println(StringBuddy.from("== NODE-OPERATIONS ==").banner());
      Map<String, NodeOp<?>> nodeops = Ops4J.locator().getNodeOps();
      for (String name : nodeops.keySet())
      {
        if (p.matcher(name).matches())
        {
          if (longListing)
          {
            System.out.println(HelpUtil.getHelp(nodeops.get(name)));
          }
          else
          {
            System.out.println(HelpUtil.getUsage(nodeops.get(name)));
          }
        }
      }
    }

    if (infoType == null || infoType == InfoType.ALL
        || infoType == InfoType.SOURCE)
    {
      System.out.println(StringBuddy.from("== INPUT-SOURCES ==").banner());
      Map<String, InputSource<?>> sources = Ops4J.locator().getSources();
      for (String name : sources.keySet())
      {
        if (p.matcher(name).matches())
        {
          if (longListing)
          {
            System.out.println(HelpUtil.getHelp(sources.get(name)));
          }
          else
          {
            System.out.println(HelpUtil.getUsage(sources.get(name)));
          }
        }
      }
    }

    if (infoType == null || infoType == InfoType.ALL
        || infoType == InfoType.DESTINATION)
    {
      System.out.println(StringBuddy.from("== DESTINATIONS ==").banner());
      Map<String, OutputDestination<?>> destinations = Ops4J.locator()
          .getDestinations();
      for (String name : destinations.keySet())
      {
        if (p.matcher(name).matches())
        {
          if (longListing)
          {
            System.out.println(HelpUtil.getHelp(destinations.get(name)));
          }
          else
          {
            System.out.println(HelpUtil.getUsage(destinations.get(name)));
          }
        }
      }
    }

    if (infoType == null || infoType == InfoType.ALL
        || infoType == InfoType.MODULE)
    {
      System.out.println(StringBuddy.from("== MODULES ==").banner());
      Map<String, OpModule<?>> modules = Ops4J.locator().getModules();
      for (String name : modules.keySet())
      {
        if (p.matcher(name).matches())
        {
          if (longListing)
          {
            System.out.println(HelpUtil.getHelp(modules.get(name)));
          }
          else
          {
            System.out.println(HelpUtil.getUsage(modules.get(name)));
          }
        }
      }
    }

    if (infoType == null || infoType == InfoType.ALL
        || infoType == InfoType.REPO)
    {
      System.out.println(StringBuddy.from("== OP-REPOSITORIES ==").banner());
      Map<String, OpRepo> modules = Ops4J.locator().getRepos();
      for (String name : modules.keySet())
      {
        if (p.matcher(name).matches())
        {
          if (longListing)
          {
            System.out.println(HelpUtil.getHelp(modules.get(name)));
          }
          else
          {
            System.out.println(HelpUtil.getUsage(modules.get(name)));
          }
        }
      }
    }
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new InfoCmd());
    cli.execute(args);
  }
}
