package org.ops4j;

import java.util.List;

import org.ops4j.OpLogger.LogLevel;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;

@Command(name = "base-node-op", mixinStandardHelpOptions = false)
public class BaseNodeOp<T extends BaseNodeOp<T>> implements NodeOp<T>
{
  private @Getter @Setter String    name      = "unamed";

  @Option(names = { "-L",
      "--log-level" }, description = "The log level of this operation.")
  private @Getter LogLevel          logLevel  = LogLevel.INFO;

  private @Getter @Setter OpLogger opLogger = new OpLogger("jpex.nodeop");

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
    return t.startsWith(getName() + "(") && t.endsWith(")");
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

  public void configure(String args[]) throws OpsException
  {
    //OpsLogger.syserr("CONFIG: ", config.length);s
    if (args != null && args.length > 0)
    {
      CommandSpec spec = CommandSpec.create();
      new CommandLine(this).parseArgs(args);
      //OpsLogger.syserr("NodeOp Configuration ", getName(), ": ",
      //    JacksonUtil.toString(this, "N/A"));
    }
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
    opLogger.setLogLevel(logLevel);
  }
}
