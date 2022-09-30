package org.ops4j.log;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.log.OpLogger.LogLevel;

public interface LocalOpLogger
{
  public OpLogger getOpLogger();

  public void setOpLogger(OpLogger logger);

  default public boolean traceEnabled()
  {
    return getOpLogger().traceEnabled();
  }

  default public boolean debugEnabled()
  {
    return getOpLogger().debugEnabled();
  }

  default public boolean infoEnabled()
  {
    return getOpLogger().infoEnabled();
  }

  default public boolean warnEnabled()
  {
    return getOpLogger().warnEnabled();
  }

  default public boolean errorEnabled()
  {
    return getOpLogger().errorEnabled();
  }

  default public void trace(Object... msg)
  {
    getOpLogger().trace(msg);
  }

  default public void debug(Object... msg)
  {
    getOpLogger().debug(msg);
  }

  default public void info(Object... msg)
  {
    getOpLogger().info(StringUtils.join(msg));
  }

  default public void warn(Object... msg)
  {
    getOpLogger().warn(StringUtils.join(msg));
  }

  default public void error(Object... msg)
  {
    getOpLogger().error(StringUtils.join(msg));
  }

  default public void syserr(Object... msg)
  {
    OpLogger.syserr(msg);
  }

  default public void sysout(Object... msg)
  {
    OpLogger.sysout(msg);
  }

  default void setLogLevel(LogLevel level)
  {
    getOpLogger().setLogLevel(level);
  }
  
  default LogLevel getLogLevel()
  {
    return getOpLogger().getLogLevel();
  }
}
