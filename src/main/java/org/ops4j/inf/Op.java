package org.ops4j.inf;

import java.util.List;

import org.ops4j.Lifecycle;
import org.ops4j.OpData;
import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger.LogLevel;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
    property = "@class")
public interface Op<T extends Op<T>>
{
  public enum PhaseType {
    INITIALIZE, OPEN, EXECUTE, FLUSH, CLEANUP, CLOSE
  }

  public Lifecycle getLifecycle();

  public default Lifecycle lifecycle()
  {
    return getLifecycle();
  }

  public default boolean provides(PhaseType phase)
  {
    return getLifecycle().provides(phase);
  }

  public T initialize() throws OpsException;

  public T open() throws OpsException;

  public List<OpData> execute(OpData input) throws OpsException;

  public List<OpData> flush() throws OpsException;

  public T close() throws OpsException;

  public T cleanup() throws OpsException;

  public T copy() throws OpsException;

  public String getName();

  public void setName(String name);

  public void configure(String args) throws OpsException;

  public void configure(String args[]) throws OpsException;

  public void configure(List<String> args) throws OpsException;

  public void setLogLevel(LogLevel logLevel);

  public LogLevel getLogLevel();

  public Op<T> create();
}
