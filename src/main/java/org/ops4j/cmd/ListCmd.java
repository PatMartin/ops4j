package org.ops4j.cmd;

import java.util.concurrent.Callable;

import org.ops4j.Ops4J;
import org.ops4j.inf.OpRepo;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "list", mixinStandardHelpOptions = true, aliases = { "ls" },
    description = "Save an operation to the designated repository.")
public class ListCmd extends SubCmd implements Callable<Integer>
{
  @Option(names = { "-r", "--repo" }, required = false,
      description = "The repository to list operations from.")
  private @Getter @Setter String repoName = null;

  public ListCmd()
  {
    super("list");
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
      return 0;
    }

    OpRepo repo = Ops4J.repo();
    for (String name : repo.names())
    {
      System.out.println(name);
    }

    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new ListCmd());
    cli.execute(args);
  }
}
