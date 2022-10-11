package org.ops4j.log;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.log.OpLogger.LogLevel;
import org.slf4j.Marker;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface OpLogging
{
  public OpLogger getOpLogger();

  public default OpLogger opLogger()
  {
    return getOpLogger();
  }

  @JsonIgnore
  public default boolean isTraceEnabled()
  {
    return opLogger().isTraceEnabled();
  }

  @JsonIgnore
  public default boolean isDebugEnabled()
  {
    return opLogger().isDebugEnabled();
  }

  @JsonIgnore
  public default boolean isInfoEnabled()
  {
    return opLogger().isInfoEnabled();
  }

  @JsonIgnore
  public default boolean isWarnEnabled()
  {
    return opLogger().isWarnEnabled();
  }

  @JsonIgnore
  public default boolean isErrorEnabled()
  {
    return opLogger().isErrorEnabled();
  }

  public default void trace(String msg)
  {
    opLogger().trace(msg);
  }

  public default void trace(String format, Object arg)
  {
    opLogger().trace(format, arg);
  }

  public default void trace(String format, Object arg1, Object arg2)
  {
    opLogger().trace(format, arg1, arg2);
  }

  public default void trace(String format, Object... arguments)
  {
    opLogger().trace(format, arguments);
  }

  public default void trace(String msg, Throwable t)
  {
    opLogger().trace(msg, t);
  }

  public default boolean isTraceEnabled(Marker marker)
  {
    return opLogger().isTraceEnabled(marker);
  }

  public default void trace(Marker marker, String msg)
  {
    opLogger().trace(marker, msg);
  }

  public default void trace(Marker marker, String format, Object arg)
  {
    if (isTraceEnabled())
    {
      opLogger().trace(marker, format, arg);
    }
  }

  public default void trace(Marker marker, String format, Object arg1,
      Object arg2)
  {
    opLogger().trace(marker, format, arg1, arg2);
  }

  public default void trace(Marker marker, String format, Object... argArray)
  {
    opLogger().trace(marker, format, argArray);
  }

  public default void trace(Marker marker, String msg, Throwable t)
  {
    opLogger().trace(marker, msg, t);
  }

  public default void debug(String msg)
  {
    opLogger().debug(msg);
  }

  public default void debug(String format, Object arg)
  {
    opLogger().debug(format, arg);
  }

  public default void debug(String format, Object arg1, Object arg2)
  {
    opLogger().debug(format, arg1, arg2);
  }

  public default void debug(String format, Object... arguments)
  {
    opLogger().debug(format, arguments);
  }

  public default void debug(String msg, Throwable t)
  {
    opLogger().debug(msg, t);
  }

  public default boolean isDebugEnabled(Marker marker)
  {
    return opLogger().isDebugEnabled(marker);
  }

  public default void debug(Marker marker, String msg)
  {
    opLogger().debug(marker, msg);
  }

  public default void debug(Marker marker, String format, Object arg)
  {
    opLogger().debug(marker, format, arg);
  }

  public default void debug(Marker marker, String format, Object arg1,
      Object arg2)
  {
    opLogger().debug(marker, format, arg1, arg2);
  }

  public default void debug(Marker marker, String format, Object... arguments)
  {
    opLogger().debug(marker, format, arguments);
  }

  public default void debug(Marker marker, String msg, Throwable t)
  {
    opLogger().debug(marker, msg, t);
  }

  public default void info(String msg)
  {
    opLogger().info(msg);
  }

  public default void info(String format, Object arg)
  {
    opLogger().info(format, arg);
  }

  public default void info(String format, Object arg1, Object arg2)
  {
    opLogger().info(format, arg1, arg2);
  }

  public default void info(String format, Object... arguments)
  {
    opLogger().info(format, arguments);
  }

  public default void info(String msg, Throwable t)
  {
    opLogger().info(msg, t);
  }

  public default boolean isInfoEnabled(Marker marker)
  {
    return opLogger().isInfoEnabled(marker);
  }

  public default void info(Marker marker, String msg)
  {
    opLogger().info(marker, msg);
  }

  public default void info(Marker marker, String format, Object arg)
  {
    opLogger().info(marker, format, arg);
  }

  public default void info(Marker marker, String format, Object arg1,
      Object arg2)
  {
    opLogger().info(marker, format, arg1, arg2);
  }

  public default void info(Marker marker, String format, Object... arguments)
  {
    opLogger().info(marker, format, arguments);
  }

  public default void info(Marker marker, String msg, Throwable t)
  {
    opLogger().info(marker, msg, t);
  }

  public default void warn(String msg)
  {
    opLogger().warn(msg);
  }

  public default void warn(String format, Object arg)
  {
    opLogger().warn(format, arg);
  }

  public default void warn(String format, Object... arguments)
  {
    opLogger().warn(format, arguments);
  }

  public default void warn(String format, Object arg1, Object arg2)
  {
    opLogger().warn(format, arg1, arg2);
  }

  public default void warn(String msg, Throwable t)
  {
    opLogger().warn(msg, t);
  }

  public default boolean isWarnEnabled(Marker marker)
  {
    return opLogger().isWarnEnabled(marker);
  }

  public default void warn(Marker marker, String msg)
  {
    opLogger().warn(marker, msg);
  }

  public default void warn(Marker marker, String format, Object arg)
  {
    opLogger().warn(marker, format, arg);
  }

  public default void warn(Marker marker, String format, Object arg1,
      Object arg2)
  {
    opLogger().warn(marker, format, arg1, arg2);
  }

  public default void warn(Marker marker, String format, Object... arguments)
  {
    opLogger().warn(marker, format, arguments);
  }

  public default void warn(Marker marker, String msg, Throwable t)
  {
    opLogger().warn(marker, msg, t);
  }

  public default void error(String msg)
  {
    opLogger().error(msg);
  }

  public default void error(String format, Object arg)
  {
    opLogger().error(format, arg);
  }

  public default void error(String format, Object arg1, Object arg2)
  {
    opLogger().error(format, arg1, arg2);
  }

  public default void error(String format, Object... arguments)
  {
    opLogger().error(format, arguments);
  }

  public default void error(String msg, Throwable t)
  {
    opLogger().error(msg, t);
  }

  public default boolean isErrorEnabled(Marker marker)
  {
    return opLogger().isErrorEnabled(marker);
  }

  public default void error(Marker marker, String msg)
  {
    opLogger().error(marker, msg);
  }

  public default void error(Marker marker, String format, Object arg)
  {
    opLogger().error(marker, format, arg);
  }

  public default void error(Marker marker, String format, Object arg1,
      Object arg2)
  {
    opLogger().error(marker, format, arg1, arg2);
  }

  public default void error(Marker marker, String format, Object... arguments)
  {
    opLogger().error(marker, format, arguments);
  }

  public default void error(Marker marker, String msg, Throwable t)
  {
    opLogger().error(marker, msg, t);
  }

  /**
   * ADDITIONAL OpLogger capabilities begin here:
   */
  public default void TRACE(Object... msg)
  {
    opLogger().TRACE(msg);
  }

  public default void DEBUG(Object... msg)
  {
    opLogger().DEBUG(msg);
  }

  public default void INFO(Object... msg)
  {
    opLogger().INFO(msg);
  }

  public default void WARN(Object... msg)
  {
    opLogger().WARN(msg);
  }

  public default void ERROR(Object... msg)
  {
    opLogger().ERROR(msg);
  }

  public static void syserr(Object... msg)
  {
    System.err.println(StringUtils.join(msg));
  }

  public static void sysout(Object... msg)
  {
    System.out.println(StringUtils.join(msg));
  }

  public default LogLevel getLogLevel()
  {
    return opLogger().getLogLevel();
  }

  public default void setLogLevel(LogLevel logLevel)
  {
    opLogger().setLogLevel(logLevel);
  }
}
