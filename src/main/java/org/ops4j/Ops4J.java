package org.ops4j;

import java.util.concurrent.Callable;

import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;

@Command(name = "ops", mixinStandardHelpOptions = false,
    description = "This is " + "the main CLI for Ops4J.")
public class Ops4J implements Callable<Integer>
{
  private static Locator locator = null;
  private static OpLogger                logger = new OpLogger("ops");
  private static @Getter @Setter Config  config = null;

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

  public static Locator locator() throws OpsException
  {
    if (locator == null)
    {
      locator = new Locator();
    }
    return locator;
  }

  public static Config config()
  {
    if (config == null)
    {
      config = ConfigFactory.load("ops4j.conf");
      logger.trace("CONFIG: " + config.root().render());
    }
    return config;
  }
}
