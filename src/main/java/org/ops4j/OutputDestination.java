package org.ops4j;

import java.io.OutputStream;

import org.ops4j.OpLogger.LogLevel;
import org.ops4j.exception.OpsException;

public interface OutputDestination<T extends OutputDestination<T>> extends LocalOpLogger
{
  public OutputStream stream() throws OpsException;

  public boolean canResolve(String location);

  public String getName();

  public void setName(String name);

  default OutputDestination<T> name(String name)
  {
    setName(name);
    return this;
  }

  public LogLevel getLogLevel();

  //public InputLocation<T> create();

  public void configure(String... config) throws OpsException;

  public void setLogLevel(LogLevel logLevel);

  public OutputDestination<T> create();
}
