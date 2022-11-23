package org.ops4j.cmd;

import java.util.concurrent.Callable;

import org.ops4j.Ops4J;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpRepo;
import org.ops4j.log.OpLogger;
import org.ops4j.util.JacksonUtil;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "run", mixinStandardHelpOptions = true,
    description = "Run the designated operation from the "
        + "optionally designated op repository.")
public class RunCmd extends SubCmd implements Callable<Integer>
{
  @Option(names = { "-r", "--repo" }, required = false,
      description = "The repository to run the operation from.")
  private @Getter @Setter String repoName = null;

  @Parameters(index = "0", arity = "1",
      description = "The name of the operation to run.")
  private @Getter @Setter String opName   = null;

  public RunCmd()
  {
    super("run");
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
    Op<?> op = repo.load(getOpName());
    OpCLI.cli(op);
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new RunCmd());
    cli.execute(args);
  }
}
