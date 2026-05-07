package org.ops4j.base;

import org.ops4j.inf.OpModule;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.log.OpLogging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "base-module", mixinStandardHelpOptions = false)
public abstract class BaseModule<T extends BaseModule<T>>
    implements OpModule<T>, OpLogging
{
  @JsonIgnore
  protected @Getter @Setter OpLogger logger = null;

  @Option(names = { "-N", "--name" }, description = "The name of this module.")
  private @Getter @Setter String     name      = null;

  @Option(names = { "-NS", "--namespace" },
      description = "The namespace of this module.")
  private @Getter @Setter String     namespace = null;

  @JsonIgnore
  Config                             config    = null;

  public BaseModule(String namespace, String name)
  {
    setNamespace(namespace);
    setName(name);
    logger = OpLoggerFactory.getLogger("ops.module." + getName());
  }

  @Override
  public Config config()
  {
    if (config == null)
    {
      config = ConfigFactory.load(getName() + ".conf");
      logger.trace("CONFIG: " + config.root().render());
    }
    return config;
  }
}
