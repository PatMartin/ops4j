package org.ops4j.cmd;

import java.util.concurrent.Callable;

import org.ops4j.Ops4J;
import org.ops4j.inf.OpRepo;
import org.ops4j.log.OpLogger;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "remove", mixinStandardHelpOptions = true, aliases = { "rm" },
    description = "Remove an operation from the designated repository.")
public class RemoveCmd extends SubCmd implements Callable<Integer>
{
  @Option(names = { "-r", "--repo" }, required = false,
      description = "The repository to save the operation to.")
  private @Getter @Setter String repoName = null;

  @Parameters(index = "0", arity = "0..1",
      description = "The name of the operation to save.")
  private @Getter @Setter String opName   = null;

  public RemoveCmd()
  {
    super("rm");
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
      return 0;
    }

    OpLogger.syserr("REMOVING: " + getOpName());
    OpRepo repo = Ops4J.repo();
    OpLogger.syserr("REPO: name=", repo.name(), ", type=", repo.type());
    repo.remove(opName);
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new RemoveCmd());
    cli.execute(args);
  }
}
