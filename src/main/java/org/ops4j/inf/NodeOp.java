package org.ops4j.inf;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;

import com.fasterxml.jackson.databind.JsonNode;

public interface NodeOp<T extends NodeOp<T>>
{ 
  public JsonNode execute(JsonNode node) throws OpsException;

  public boolean canResolve(String expression);

  public String getName();

  public void setName(String name);

  default NodeOp<T> name(String name)
  {
    setName(name);
    return this;
  }

  public List<String> getArgs();

  public void setArgs(List<String> args);

  default NodeOp<T> args(List<String> args)
  {
    setArgs(args);
    return this;
  }

  default void insertArg(String arg)
  {
    if (getArgs() == null)
    {
      setArgs(new ArrayList<String>());
    }
    getArgs().add(0, arg);
  }

  default void appendArg(String arg)
  {
    if (getArgs() == null)
    {
      setArgs(new ArrayList<String>());
    }
    getArgs().add(arg);
  }
  
  public LogLevel getLogLevel();

  public NodeOp<T> create();

  public void configure(String args[]) throws OpsException;

  public void configure(String args) throws OpsException;
  
  public void setLogLevel(LogLevel logLevel);

  public OpLogger logger();
}
