package org.ops4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import org.ops4j.config.JsonConfiguration;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Configuration;
import org.ops4j.log.OpLogger;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;

@Command(name = "ops", mixinStandardHelpOptions = false,
    description = "This is " + "the main CLI for Ops4J.")
public class Ops4J implements Callable<Integer>
{
  private static @Getter @Setter Locator       locator;
  private static OpLogger                      logger = new OpLogger("ops");
  private static @Getter @Setter Configuration config = null;

  static
  {
    try
    {
      locator = new Locator();
      InputStream configIs = locator.getClass()
          .getResourceAsStream("/ops4j.yaml");
      JsonNode configNode = JacksonUtil.yamlMapper().readTree(configIs);
      config = new JsonConfiguration(configNode);
    }
    catch(OpsException | IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public Configuration loadConfig()
  {
    return null;
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

  public static Configuration config()
  {
    return config;
  }
}
