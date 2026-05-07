package org.ops4j.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.ThreadUtil;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "pause",
    description = "Pause execution for the specified number of milliseconds.")
public class Pause extends BaseOp<Pause>
{
  @Parameters(index = "0", arity = "1",
      description = "The number of milliseconds to pause.%n"
          + "DEFAULT='${DEFAULT-VALUE}'")
  private @Getter @Setter Long pause = 1000L;

  public Pause()
  {
    super("pause");
  }

  public List<OpData> execute(OpData input)
  {
    debug("pause ", pause(), " ms.");

    ThreadUtil.sleep(pause());
    return input.asList();
  }

  public Pause pause(long pause)
  {
    setPause(pause);
    return this;
  }

  public long pause()
  {
    return getPause();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Pause(), args);
  }
}
