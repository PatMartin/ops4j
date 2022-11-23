package org.ops4j.cmd;

import java.util.concurrent.Callable;

import org.ops4j.Ops4J;
import org.ops4j.base.BaseOp;
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

@Command(name = "save", mixinStandardHelpOptions = true,
    description = "Save an operation to the designated repository.")
public class SaveCmd extends SubCmd implements Callable<Integer>
{
  @Option(names = { "-r", "--repo" }, required = false,
      description = "The repository to save the operation to.")
  private @Getter @Setter String repoName = null;

  @Parameters(index = "0", arity = "0..1",
      description = "The name of the operation to save.")
  private @Getter @Setter String opName   = null;

  public SaveCmd()
  {
    super("save");
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
      return 0;
    }

    Op<?> op = JacksonUtil.mapper().readValue(System.in, BaseOp.class);
    if (op != null)
    {
      if (getOpName() == null)
      {
        setOpName(op.getName());
      }
      OpLogger.syserr("SAVING: " + getOpName());
      OpRepo repo = Ops4J.repo();
      OpLogger.syserr("REPO: name=", repo.name(), ", type=", repo.type());
      repo.store(opName, op);
    }
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new SaveCmd());
    cli.execute(args);
  }
}
