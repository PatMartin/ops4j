package org.ops4j.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "get", mixinStandardHelpOptions = true,
    description = "Get one or more configuration options.")
public class GetCmd extends SubCmd implements Callable<Integer>
{
  @Parameters(index = "0", arity = "0..+", paramLabel = "<name> [<name>...]",
      description = "One or more configuration options to get.%n%n"
          + "Examples:%n%n# get option a.%nops get a%n%n"
          + "# get options a, b and c%n" + "ops get a b")
  private @Getter @Setter List<String> settings = new ArrayList<String>();

  public GetCmd()
  {
  }

  @Parameters(index = "0", arity = "1..+", description = "Get detailed help.")
  private @Getter @Setter List<String> ops;

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
    CommandLine cli = new CommandLine(new GetCmd());
    cli.execute(args);
  }
}
