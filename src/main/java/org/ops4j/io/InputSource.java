package org.ops4j.io;

import java.io.InputStream;

import org.ops4j.exception.OpsException;
import org.ops4j.log.LocalOpLogger;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;

public interface InputSource<T extends InputSource<T>> extends LocalOpLogger
{
  public InputStream stream() throws OpsException;

  public boolean canResolve(String location);

  public String getName();

  public void setName(String name);

  default InputSource<T> name(String name)
  {
    setName(name);
    return this;
  }

  public LogLevel getLogLevel();

  //public InputLocation<T> create();

  public void configure(String... config) throws OpsException;

  public void setLogLevel(LogLevel logLevel);

  public InputSource<T> create();
}
