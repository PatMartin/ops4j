package org.ops4j.log;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.log.OpLogger.LogLevel;
import org.slf4j.Marker;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface OpLogging
{
  public OpLogger getLogger();

  public default OpLogger logger()
  {
    return getLogger();
  }

  @JsonIgnore
  public default boolean isTraceEnabled()
  {
    return logger().isTraceEnabled();
  }

  @JsonIgnore
  public default boolean isDebugEnabled()
  {
    return logger().isDebugEnabled();
  }

  @JsonIgnore
  public default boolean isInfoEnabled()
  {
    return logger().isInfoEnabled();
  }

  @JsonIgnore
  public default boolean isWarnEnabled()
  {
    return logger().isWarnEnabled();
  }

  @JsonIgnore
  public default boolean isErrorEnabled()
  {
    return logger().isErrorEnabled();
  }

  public default void trace(String msg)
  {
    logger().trace(msg);
  }

  public default void trace(String format, Object arg)
  {
    logger().trace(format, arg);
  }

  public default void trace(String format, Object arg1, Object arg2)
  {
    logger().trace(format, arg1, arg2);
  }

  public default void trace(String format, Object... arguments)
  {
    logger().trace(format, arguments);
  }

  public default void trace(String msg, Throwable t)
  {
    logger().trace(msg, t);
  }

  public default boolean isTraceEnabled(Marker marker)
  {
    return logger().isTraceEnabled(marker);
  }

  public default void trace(Marker marker, String msg)
  {
    logger().trace(marker, msg);
  }

  public default void trace(Marker marker, String format, Object arg)
  {
    if (isTraceEnabled())
    {
      logger().trace(marker, format, arg);
    }
  }

  public default void trace(Marker marker, String format, Object arg1,
      Object arg2)
  {
    logger().trace(marker, format, arg1, arg2);
  }

  public default void trace(Marker marker, String format, Object... argArray)
  {
    logger().trace(marker, format, argArray);
  }

  public default void trace(Marker marker, String msg, Throwable t)
  {
    logger().trace(marker, msg, t);
  }

  public default void debug(String msg)
  {
    logger().debug(msg);
  }

  public default void debug(String format, Object arg)
  {
    logger().debug(format, arg);
  }

  public default void debug(String format, Object arg1, Object arg2)
  {
    logger().debug(format, arg1, arg2);
  }

  public default void debug(String format, Object... arguments)
  {
    logger().debug(format, arguments);
  }

  public default void debug(String msg, Throwable t)
  {
    logger().debug(msg, t);
  }

  public default boolean isDebugEnabled(Marker marker)
  {
    return logger().isDebugEnabled(marker);
  }

  public default void debug(Marker marker, String msg)
  {
    logger().debug(marker, msg);
  }

  public default void debug(Marker marker, String format, Object arg)
  {
    logger().debug(marker, format, arg);
  }

  public default void debug(Marker marker, String format, Object arg1,
      Object arg2)
  {
    logger().debug(marker, format, arg1, arg2);
  }

  public default void debug(Marker marker, String format, Object... arguments)
  {
    logger().debug(marker, format, arguments);
  }

  public default void debug(Marker marker, String msg, Throwable t)
  {
    logger().debug(marker, msg, t);
  }

  public default void info(String msg)
  {
    logger().info(msg);
  }

  public default void info(String format, Object arg)
  {
    logger().info(format, arg);
  }

  public default void info(String format, Object arg1, Object arg2)
  {
    logger().info(format, arg1, arg2);
  }

  public default void info(String format, Object... arguments)
  {
    logger().info(format, arguments);
  }

  public default void info(String msg, Throwable t)
  {
    logger().info(msg, t);
  }

  public default boolean isInfoEnabled(Marker marker)
  {
    return logger().isInfoEnabled(marker);
  }

  public default void info(Marker marker, String msg)
  {
    logger().info(marker, msg);
  }

  public default void info(Marker marker, String format, Object arg)
  {
    logger().info(marker, format, arg);
  }

  public default void info(Marker marker, String format, Object arg1,
      Object arg2)
  {
    logger().info(marker, format, arg1, arg2);
  }

  public default void info(Marker marker, String format, Object... arguments)
  {
    logger().info(marker, format, arguments);
  }

  public default void info(Marker marker, String msg, Throwable t)
  {
    logger().info(marker, msg, t);
  }

  public default void warn(String msg)
  {
    logger().warn(msg);
  }

  public default void warn(String format, Object arg)
  {
    logger().warn(format, arg);
  }

  public default void warn(String format, Object... arguments)
  {
    logger().warn(format, arguments);
  }

  public default void warn(String format, Object arg1, Object arg2)
  {
    logger().warn(format, arg1, arg2);
  }

  public default void warn(String msg, Throwable t)
  {
    logger().warn(msg, t);
  }

  public default boolean isWarnEnabled(Marker marker)
  {
    return logger().isWarnEnabled(marker);
  }

  public default void warn(Marker marker, String msg)
  {
    logger().warn(marker, msg);
  }

  public default void warn(Marker marker, String format, Object arg)
  {
    logger().warn(marker, format, arg);
  }

  public default void warn(Marker marker, String format, Object arg1,
      Object arg2)
  {
    logger().warn(marker, format, arg1, arg2);
  }

  public default void warn(Marker marker, String format, Object... arguments)
  {
    logger().warn(marker, format, arguments);
  }

  public default void warn(Marker marker, String msg, Throwable t)
  {
    logger().warn(marker, msg, t);
  }

  public default void error(String msg)
  {
    logger().error(msg);
  }

  public default void error(String format, Object arg)
  {
    logger().error(format, arg);
  }

  public default void error(String format, Object arg1, Object arg2)
  {
    logger().error(format, arg1, arg2);
  }

  public default void error(String format, Object... arguments)
  {
    logger().error(format, arguments);
  }

  public default void error(String msg, Throwable t)
  {
    logger().error(msg, t);
  }

  public default boolean isErrorEnabled(Marker marker)
  {
    return logger().isErrorEnabled(marker);
  }

  public default void error(Marker marker, String msg)
  {
    logger().error(marker, msg);
  }

  public default void error(Marker marker, String format, Object arg)
  {
    logger().error(marker, format, arg);
  }

  public default void error(Marker marker, String format, Object arg1,
      Object arg2)
  {
    logger().error(marker, format, arg1, arg2);
  }

  public default void error(Marker marker, String format, Object... arguments)
  {
    logger().error(marker, format, arguments);
  }

  public default void error(Marker marker, String msg, Throwable t)
  {
    logger().error(marker, msg, t);
  }

  /**
   * ADDITIONAL OpLogger capabilities begin here:
   */
  public default void TRACE(Object... msg)
  {
    logger().TRACE(msg);
  }

  public default void DEBUG(Object... msg)
  {
    logger().DEBUG(msg);
  }

  public default void INFO(Object... msg)
  {
    logger().INFO(msg);
  }

  public default void WARN(Object... msg)
  {
    logger().WARN(msg);
  }

  public default void ERROR(Object... msg)
  {
    logger().ERROR(msg);
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
    return logger().getLogLevel();
  }

  public default void setLogLevel(LogLevel logLevel)
  {
    logger().setLogLevel(logLevel);
  }
}
