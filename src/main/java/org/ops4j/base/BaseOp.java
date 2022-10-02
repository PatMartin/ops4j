package org.ops4j.base;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.Lifecycle;
import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.exception.ConfigurationException;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Configuration;
import org.ops4j.inf.Op;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;

@Command(name = "base-op", mixinStandardHelpOptions = false)
public class BaseOp<T extends BaseOp<T>> implements Op<T>
{
  @JsonIgnore
  private @Getter @Setter Lifecycle lifecycle   = new Lifecycle();

  private OpLogger                  logger;

  @Option(names = { "-N", "--name" },
      description = "The name of this operation.")
  private @Getter @Setter String    name        = null;

  @Option(names = { "-L", "--log-level" },
      description = "The log level of this operation.")
  private @Getter LogLevel          logLevel    = LogLevel.INFO;

  @Option(names = { "-C", "--config" },
      description = "The configuration view for this operation.")
  private @Getter @Setter String    view        = null;

  private @Getter @Setter String    defaultView = null;

  public BaseOp()
  {
    setName(this.getClass().getName());
    logger = new OpLogger(getName());
    logger.setLogLevel(getLogLevel());
  }

  public BaseOp(String name)
  {
    setName(name);
    logger = new OpLogger(getName());
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
  public List<OpData> flush() throws OpsException
  {
    return new ArrayList<OpData>();
  }

  @SuppressWarnings("unchecked") @Override
  public T close() throws OpsException
  {
    return (T) this;
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
    new CommandLine(this).parseArgs(args.toArray(new String[0]));
    debug("Op Configuration ", getName(), ": ",
        JacksonUtil.toString(this, "N/A"));
  }

  public void setLogLevel(LogLevel logLevel)
  {
    this.logLevel = logLevel;
    logger.setLogLevel(logLevel);
  }

  @Override
  public OpLogger getOpLogger()
  {
    return this.logger;
  }

  @Override
  public void setOpLogger(OpLogger logger)
  {
    this.logger = logger;
  }

  public Configuration<?> config() throws ConfigurationException
  {
    debug("VIEW: ", getView());
    if (getView() != null)
    {
      return Ops4J.config().view(view);
    }
    else if (getDefaultView() != null)
    {
      debug("DEFAULT-VIEW: ", getDefaultView());
      return Ops4J.config().view(Ops4J.config().getString(getDefaultView()));
    }
    throw new ConfigurationException(
        "No configuration defined for view '" + view + "'");
  }
}