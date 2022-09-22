package org.ops4j.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "set", mixinStandardHelpOptions = true,
    description = "Set zero or more configuration options.")
public class SetCmd extends SubCmd implements Callable<Integer>
{

  public SetCmd()
  {
  }

  @Parameters(index = "0", arity = "0..*", paramLabel = "<name>=<value>",
      description = "Zero or more name value pairs indicating the "
          + "configuration options that we wish to set.  These configuration "
          + "options are set within the user's home directory in a file named "
          + "ops4j.yaml.%n%nExamples:%n%n# set option a to value b.%n"
          + "ops set a=b%n# set option a=b and option c=d%nops set a=b c=d")
  private @Getter
  @Setter Map<String, String> config = new HashMap<String, String>();

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
    CommandLine cli = new CommandLine(new SetCmd());
    cli.execute(args);
  }
}
