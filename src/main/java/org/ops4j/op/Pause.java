package org.ops4j.op;

import java.util.List;

import org.ops4j.BaseOp;
import org.ops4j.Op;
import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.ThreadUtil;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "pause", description = "Pause execution for the specified number of milliseconds.")
public class Pause extends BaseOp<Pause>
{
  @Parameters(index = "0", arity = "1", description = "The number of milliseconds to pause.")
  private @Getter @Setter Long pauseMs = 1000L;

  public Pause()
  {
    super("pause");
  }

  public List<OpData> execute(OpData input)
  {
    debug("pause ", getPauseMs(), " ms.");

    ThreadUtil.sleep(getPauseMs());
    return input.asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Pause(), args);
  }
}
