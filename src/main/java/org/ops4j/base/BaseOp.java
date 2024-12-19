package org.ops4j.base;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.Lifecycle;
import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.exception.ConfigurationException;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Fallback;
import org.ops4j.inf.Op;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.log.OpLogging;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.typesafe.config.Config;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;

@Command(name = "base-op", mixinStandardHelpOptions = false)
public class BaseOp<T extends BaseOp<T>> implements Op<T>, Fallback, OpLogging
{
  @JsonIgnore
  private @Getter @Setter Lifecycle  lifecycle   = new Lifecycle();

  @JsonIgnore
  protected @Getter @Setter OpLogger logger;

  @Option(names = { "-N", "--name" },
      description = "The name of this operation.")
  private @Getter @Setter String     name        = null;

  @Option(names = { "-L", "--log" },
      description = "The log level of this operation.")
  private @Setter @Getter LogLevel   logLevel    = LogLevel.INFO;

  @Option(names = { "-C", "--config" },
      description = "The configuration view for this operation.")
  private @Getter @Setter String     view        = null;

  private @Getter @Setter String     defaultView = null;

  private @Getter @Setter Config     config      = null;

  public BaseOp()
  {
    setName(this.getClass().getName());
    logger = OpLoggerFactory.getLogger(getName());
    logger.setLogLevel(getLogLevel());
  }

  public BaseOp(String name)
  {
    setName(name);
    logger = OpLoggerFactory.getLogger(getName());
    logger.setLogLevel(getLogLevel());
  }

  @SuppressWarnings("unchecked")
  public T create()
  {
    try
    {
      return (T) this.getClass().newInstance();
    }
    catch(InstantiationException | IllegalAccessException ex)
    {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    return null;
  }

  @SuppressWarnings("unchecked") @Override
  public T initialize() throws OpsException
  {
    return (T) this;
  }

  @SuppressWarnings("unchecked") @Override
  public T open() throws OpsException
  {
    return (T) this;
  }

  @Override
  public List<OpData> execute(OpData input) throws OpsException
  {
    if (input == null)
    {
      new ArrayList<OpData>();
    }
    return input.asList();
  }

  @Override
  public List<OpData> close() throws OpsException
  {
    return new ArrayList<OpData>();
  }

  @SuppressWarnings("unchecked") @Override
  public T cleanup() throws OpsException
  {
    return (T) this;
  }

  public void configure(String config) throws OpsException
  {
    // Do nothing for now.
    CommandSpec spec = CommandSpec.create();
    new CommandLine(this).parseArgs(config);
    debug("Op Configuration ", getName(), ": ",
        JacksonUtil.toString(this, "N/A"));
  }

  public void configure(String args[]) throws OpsException
  {
    // Do nothing for now.
    CommandSpec spec = CommandSpec.create();
    new CommandLine(this).parseArgs(args);
    debug("Op Configuration ", getName(), ": ",
        JacksonUtil.toString(this, "N/A"));
  }

  public void configure(List<String> args) throws OpsException
  {
    // Do nothing for now.
    CommandSpec spec = CommandSpec.create();
    new CommandLine(this).setCaseInsensitiveEnumValuesAllowed(true)
        .parseArgs(args.toArray(new String[0]));
    debug("Op Configuration ", getName(), ": ",
        JacksonUtil.toString(this, "N/A"));
  }

  @Override
  public void setLogLevel(LogLevel logLevel)
  {
    this.logLevel = logLevel;
    logger.setLogLevel(logLevel);
  }

  public Config config() throws OpsException
  {
    if (config != null)
    {
      return config;
    }
    if (getView() != null)
    {
      TRACE("Using view: ", getView(), " in config ", Ops4J.config());
      config = Ops4J.config().getConfig(getView());
      TRACE("VIEW=", config);
    }
    else if (getDefaultView() != null)
    {
      TRACE("BASE-CONFIG= ", Ops4J.config());
      DEBUG("VIEW: ", getDefaultView(), "=",
          Ops4J.config().getString(getDefaultView()));
      config = Ops4J.config()
          .getConfig(Ops4J.config().getString(getDefaultView()));
    }
    if (config == null)
    {
      throw new ConfigurationException(
          "No configuration defined for view '" + view + "'");
    }
    DEBUG("CONFIG: '", config, "'");
    return config;
  }

  @SuppressWarnings("unchecked")
  public T name(String name)
  {
    setName(name);
    return (T) this;
  }

  public T copy() throws OpsException
  {
    String json = JacksonUtil.toString(this);
    try
    {
      return (T) JacksonUtil.mapper().readValue(json, this.getClass());
    }
    catch(JsonProcessingException ex)
    {
      throw new OpsException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  public T defaultView(String defaultView)
  {
    setDefaultView(defaultView);
    return (T) this;
  }
}
