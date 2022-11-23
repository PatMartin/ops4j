package org.ops4j;

import java.util.Map;
import java.util.concurrent.Callable;

import org.ops4j.exception.OpsException;
import org.ops4j.inf.OpModule;
import org.ops4j.inf.OpRepo;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import picocli.CommandLine.Command;

@Command(name = "ops", mixinStandardHelpOptions = false,
    description = "This is " + "the main CLI for Ops4J.")
public class Ops4J implements Callable<Integer>
{
  private static Locator  locator = null;
  private static OpLogger logger  = null;
  private static Config   config  = null;
  private static OpRepo   repo    = null;

  public Ops4J()
  {
  }

  public Integer call()
  {
    return 0;
  }

  public static OpLogger logger()
  {
    if (logger == null)
    {
      logger = OpLoggerFactory.getLogger("ops");
    }
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

  public static OpRepo repo() throws OpsException
  {
    if (repo == null)
    {
      String defaultRepo = config().getString("DEFAULT.REPO");
      logger().DEBUG("DEFAULT-REPO: ", defaultRepo);
      Config repoConfig = config().getConfig(defaultRepo);

      logger().DEBUG("REPO-CONFIG: ", repoConfig);
      String classType = repoConfig.getString("type");
      try
      {
        logger().DEBUG("CLASS-TYPE: ", classType);
        repo = (OpRepo) Class.forName(classType).newInstance();
        repo.configure(repoConfig.getString("args").split(" "));
      }
      catch(InstantiationException | IllegalAccessException
          | ClassNotFoundException ex)
      {
        throw new OpsException(ex);
      }
    }
    return repo;
  }

  // TODO: Find modules and load associated configuration.
  public static Config config() throws OpsException
  {
    if (config == null)
    {
      config = ConfigFactory.load("ops4j.conf");
      logger().trace("CONFIG: " + config.root().render());
      Map<String, OpModule<?>> modules = locator().getModules();
      for (String name : modules.keySet())
      {
        logger().INFO("LOADING MODULE: " + name + " with config: "
            + modules.get(name).config());
        config = config.withFallback(modules.get(name).config());
      }
    }
    return config;
  }
}
