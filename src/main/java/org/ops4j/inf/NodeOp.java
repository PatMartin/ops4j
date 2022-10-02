package org.ops4j.inf;

import org.ops4j.exception.OpsException;
import org.ops4j.log.LocalOpLogger;
import org.ops4j.log.OpLogger.LogLevel;

import com.fasterxml.jackson.databind.JsonNode;

public interface NodeOp<T extends NodeOp<T>> extends LocalOpLogger
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

  public LogLevel getLogLevel();

  public NodeOp<T> create();

  public void configure(String args[]) throws OpsException;

  public void setLogLevel(LogLevel logLevel);

}