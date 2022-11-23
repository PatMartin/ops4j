package org.ops4j.inf;

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

  public String getPath();

  public void setPath(String path);

  default NodeOp<T> path(String path)
  {
    setPath(path);
    return this;
  }
  
  public LogLevel getLogLevel();

  public NodeOp<T> create();

  public void configure(String args[]) throws OpsException;

  public void setLogLevel(LogLevel logLevel);
  
  public OpLogger logger();

  public JsonNode getTarget(JsonNode doc);
}
