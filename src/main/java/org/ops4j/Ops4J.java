package org.ops4j;

import java.util.concurrent.Callable;

import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;

@Command(name = "ops", mixinStandardHelpOptions = false,
    description = "This is " + "the main CLI for Ops4J.")
public class Ops4J implements Callable<Integer>
{
  private static @Getter @Setter Locator locator;
  private static OpLogger                logger = new OpLogger("ops");

  static
  {
    try
    {
      locator = new Locator();
    }
    catch(OpsException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public Ops4J()
  {
  }

  public Integer call()
  {
    return 0;
  }

  public static OpLogger logger()
  {
    return logger;
  }

  public static Locator locator()
  {
    return locator;
  }
}
