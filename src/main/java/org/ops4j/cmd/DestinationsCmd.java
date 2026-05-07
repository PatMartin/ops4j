package org.ops4j.cmd;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.ops4j.Ops4J;
import org.ops4j.io.OutputDestination;
import org.ops4j.util.HelpUtil;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "destinations", mixinStandardHelpOptions = true,
    aliases = { "dest", "out", "output" },
    description = "Get information on output destinations.")
public class DestinationsCmd extends SubCmd implements Callable<Integer>
{
  @Option(names = { "-l", "--long" },
      description = "When set, produce long listings.")
  private boolean longListing = false;

  @Parameters(index = "0", arity = "0..1", paramLabel = "<pattern>",
      description = "An optional search pattern.")
  private String  pattern;

  public DestinationsCmd()
  {
    super("destinations");
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
    }

    if (pattern == null)
    {
      pattern = ".*";
    }

    Pattern p = Pattern.compile(pattern);
    Map<String, OutputDestination<?>> destinations = Ops4J.locator().getDestinations();
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
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new DestinationsCmd());
    cli.execute(args);
  }
}
