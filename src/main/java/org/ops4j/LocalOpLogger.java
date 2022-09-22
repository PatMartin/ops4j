package org.ops4j;

import org.apache.commons.lang3.StringUtils;

public interface LocalOpLogger
{
  public OpLogger getOpLogger();
  public void setOpLogger(OpLogger logger);

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

  public static void syserr(Object... msg)
  {
    OpLogger.syserr(msg);
  }

  public static void sysout(Object... msg)
  {
    OpLogger.sysout(msg);
  }
}
