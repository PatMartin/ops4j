package org.ops4j.base;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Ops4J;
import org.ops4j.exception.ConfigurationException;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Fallback;
import org.ops4j.inf.NodeOp;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.log.OpLogging;
import org.ops4j.util.ArgumentTokenizer;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "base-node-op", mixinStandardHelpOptions = false)
public class BaseNodeOp<T extends BaseNodeOp<T>>
    implements NodeOp<T>, Fallback, OpLogging
{
  @Option(names = { "-n", "--name" }, required = false,
      description = "The name of the node operation.")
  private @Getter @Setter String       name        = "unamed";

  @Parameters(index = "0", arity = "0..*",
      description = "The arguments to the node operation.")
  private @Getter @Setter List<String> args        = null;

  @Option(names = { "-L", "--log" },
      description = "The log level of this operation.")
  private @Getter LogLevel             logLevel    = LogLevel.INFO;

  @Option(names = { "-C", "--config" },
      description = "The configuration view for this operation.")
  private @Getter @Setter String       view        = null;

  private @Getter @Setter String       defaultView = null;

  private @Getter @Setter Config       config      = null;

  protected @Getter @Setter OpLogger   logger      = null;

  public BaseNodeOp(String name)
  {
    setName(name);
    logger = OpLoggerFactory.getLogger("ops.nodeop." + name);
    // logger.setLogLevel(getLogLevel());
    // OpLogger.syserr("SETTING LOG LEVEL OF: '", name, "' to '", getLogLevel(),
    // "'");
    // logger.setLogLevel(getLogLevel());
  }

  @Override
  public JsonNode execute(JsonNode node) throws OpsException
  {
    return node;
  }

  @Override
  public boolean canResolve(String url)
  {
    if (url == null)
    {
      return false;
    }
    String t = url.trim();
    if (t.equalsIgnoreCase(getName()))
    {
      return true;
    }
    if (t.startsWith(getName()))
    {
      String remaining = t.substring(getName().length());
      if (remaining.startsWith("(") && remaining.endsWith(")"))
      {
        return true;
      }
      // TODO: Make this smarter
      else if (remaining.startsWith(":/"))
      {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public T name(String name)
  {
    setName(name);
    return (T) this;
  }

  @SuppressWarnings("unchecked") @Override
  public NodeOp<T> create()
  {
    try
    {
      return ((T) this).getClass().newInstance();
    }
    catch(InstantiationException | IllegalAccessException ex)
    {
      // TODO Auto-generated catch block
      error(ex.getMessage());
    }
    return null;
  }

  public void configure(String args) throws OpsException
  {
    logger.DEBUG(getName(), ".configure(", args, ")");

    if (args != null && args.length() > 0)
    {
      configure(ArgumentTokenizer.tokenize(args));
    }
  }

  public void configure(String args[]) throws OpsException
  {
    DEBUG(getName(), ".configure([", StringUtils.join(args, ","), " ])");
    if (args != null && args.length > 0)
    {
      CommandSpec spec = CommandSpec.create();
      new CommandLine(this).parseArgs(args);

      // Refresh
      setLogLevel(getLogLevel());
      DEBUG("NodeOp config: ", getName(), ": ",
          JacksonUtil.toString(this, "N/A"));
    }
  }

  public void configure(List<String> args) throws OpsException
  {
    // Do nothing for now.
    CommandSpec spec = CommandSpec.create();
    new CommandLine(this).parseArgs(args.toArray(new String[0]));

    // Refresh
    setLogLevel(getLogLevel());
    DEBUG("NodeOp config: ", getName(), ": ",
        JacksonUtil.toString(this, "N/A"));
  }

  public void setLogLevel(LogLevel logLevel)
  {
    this.logLevel = logLevel;
    logger().setLogLevel(logLevel);
  }

  @Override
  public OpLogger logger()
  {
    if (logger == null)
    {
      logger = OpLoggerFactory.getLogger("ops.nodeop." + getName());
      logger.setLogLevel(getLogLevel());
    }
    return logger;
  }

  public JsonNode getTarget(JsonNode doc)
  {
    DEBUG("getArgs()=", getArgs());

    if (getArgs() == null || getArgs().size() == 0 || getArgs().get(0) == null)
    {
      DEBUG("getTarget(no-args)=", doc);
      return doc;
    }
    else if (getArgs().get(0).equals("/"))
    {
      DEBUG("getTarget(/)=", doc);
      return doc;
    }
    else if (getArgs().get(0).charAt(0) == '/')
    {
      DEBUG("getTarget(doc.at(", getArgs().get(0), ")=",
          doc.at(getArgs().get(0)));
      return doc.at(getArgs().get(0));
    }
    return doc;
  }

  public Config config() throws OpsException
  {
    if (config != null)
    {
      return config;
    }
    if (getView() != null)
    {
      config = Ops4J.config().getConfig(getView());
    }
    else if (getDefaultView() != null)
    {
      TRACE("Using default view: ", getDefaultView(), " in config ",
          Ops4J.config().toString());
      DEBUG("Using default view: ", getDefaultView(), "=",
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
}
