package org.ops4j.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class OpLogger implements Logger
{
  public final static Map<String, LogLevel> logLevels         = new HashMap<>();
  public final LogLevel                     DEFAULT_LOG_LEVEL = LogLevel.WARN;
  private @Getter @Setter String            name;
  private Logger                            logger            = null;
  private LogLevel                          logLevel          = LogLevel.WARN;

  public enum LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR, NONE
  }

  public OpLogger(String name)
  {
    setName(name);
    logger = LoggerFactory.getLogger(name);
  }

  @Override
  public boolean isTraceEnabled()
  {
    return logLevel == LogLevel.TRACE;
  }

  @Override
  public boolean isDebugEnabled()
  {
    return logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG;
  }

  @Override
  public boolean isInfoEnabled()
  {
    return logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG
        || logLevel == LogLevel.INFO;
  }

  @Override
  public boolean isWarnEnabled()
  {
    return logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG
        || logLevel == LogLevel.INFO || logLevel == LogLevel.WARN;
  }

  @Override
  public boolean isErrorEnabled()
  {
    return !(logLevel == LogLevel.NONE);
  }

  @Override
  public void trace(String msg)
  {
    if (isTraceEnabled())
    {
      logger.trace(msg);
    }
  }

  @Override
  public void trace(String format, Object arg)
  {
    if (isTraceEnabled())
    {
      logger.trace(format, arg);
    }
  }

  @Override
  public void trace(String format, Object arg1, Object arg2)
  {
    if (isTraceEnabled())
    {
      logger.trace(format, arg1, arg2);
    }
  }

  @Override
  public void trace(String format, Object... arguments)
  {
    if (isTraceEnabled())
    {
      logger.trace(format, arguments);
    }
  }

  @Override
  public void trace(String msg, Throwable t)
  {
    if (isTraceEnabled())
    {
      logger.trace(msg, t);
    }
  }

  @Override
  public boolean isTraceEnabled(Marker marker)
  {
    return isTraceEnabled() && isTraceEnabled(marker);
  }

  @Override
  public void trace(Marker marker, String msg)
  {
    if (isTraceEnabled())
    {
      logger.trace(marker, msg);
    }
  }

  @Override
  public void trace(Marker marker, String format, Object arg)
  {
    if (isTraceEnabled())
    {
      logger.trace(marker, format, arg);
    }
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2)
  {
    if (isTraceEnabled())
    {
      logger.trace(marker, format, arg1, arg2);
    }
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray)
  {
    if (isTraceEnabled())
    {
      logger.trace(marker, format, argArray);
    }
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t)
  {
    if (isTraceEnabled())
    {
      logger.trace(marker, msg, t);
    }
  }

  @Override
  public void debug(String msg)
  {
    if (isDebugEnabled())
    {
      logger.debug(msg);
    }
  }

  @Override
  public void debug(String format, Object arg)
  {
    if (isDebugEnabled())
    {
      logger.debug(format, arg);
    }
  }

  @Override
  public void debug(String format, Object arg1, Object arg2)
  {
    if (isDebugEnabled())
    {
      logger.debug(format, arg1, arg2);
    }
  }

  @Override
  public void debug(String format, Object... arguments)
  {
    if (isDebugEnabled())
    {
      logger.debug(format, arguments);
    }
  }

  @Override
  public void debug(String msg, Throwable t)
  {
    if (isDebugEnabled())
    {
      logger.debug(msg, t);
    }
  }

  @Override
  public boolean isDebugEnabled(Marker marker)
  {
    return isDebugEnabled() && logger.isDebugEnabled(marker);
  }

  @Override
  public void debug(Marker marker, String msg)
  {
    if (isDebugEnabled())
    {
      logger.debug(marker, msg);
    }
  }

  @Override
  public void debug(Marker marker, String format, Object arg)
  {
    if (isDebugEnabled())
    {
      logger.debug(marker, format, arg);
    }
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2)
  {
    if (isDebugEnabled())
    {
      logger.debug(marker, format, arg1, arg2);
    }
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments)
  {
    if (isDebugEnabled())
    {
      logger.debug(marker, format, arguments);
    }
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t)
  {
    if (isDebugEnabled())
    {
      logger.debug(marker, msg, t);
    }
  }

  @Override
  public void info(String msg)
  {
    if (isInfoEnabled())
    {
      logger.info(msg);
    }
  }

  @Override
  public void info(String format, Object arg)
  {
    if (isInfoEnabled())
    {
      logger.info(format, arg);
    }
  }

  @Override
  public void info(String format, Object arg1, Object arg2)
  {
    if (isInfoEnabled())
    {
      logger.info(format, arg1, arg2);
    }
  }

  @Override
  public void info(String format, Object... arguments)
  {
    if (isInfoEnabled())
    {
      logger.info(format, arguments);
    }
  }

  @Override
  public void info(String msg, Throwable t)
  {
    if (isInfoEnabled())
    {
      logger.info(msg, t);
    }
  }

  @Override
  public boolean isInfoEnabled(Marker marker)
  {
    return isInfoEnabled() && logger.isInfoEnabled(marker);
  }

  @Override
  public void info(Marker marker, String msg)
  {
    if (isInfoEnabled())
    {
      logger.info(marker, msg);
    }
  }

  @Override
  public void info(Marker marker, String format, Object arg)
  {
    if (isInfoEnabled())
    {
      logger.info(marker, format, arg);
    }
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2)
  {
    if (isInfoEnabled())
    {
      logger.info(marker, format, arg1, arg2);
    }
  }

  @Override
  public void info(Marker marker, String format, Object... arguments)
  {
    if (isInfoEnabled())
    {
      logger.info(marker, format, arguments);
    }
  }

  @Override
  public void info(Marker marker, String msg, Throwable t)
  {
    if (isInfoEnabled())
    {
      logger.info(marker, msg, t);
    }
  }

  @Override
  public void warn(String msg)
  {
    if (isWarnEnabled())
    {
      logger.warn(msg);
    }
  }

  @Override
  public void warn(String format, Object arg)
  {
    if (isWarnEnabled())
    {
      logger.warn(format, arg);
    }
  }

  @Override
  public void warn(String format, Object... arguments)
  {
    if (isWarnEnabled())
    {
      logger.warn(format, arguments);
    }
  }

  @Override
  public void warn(String format, Object arg1, Object arg2)
  {
    if (isWarnEnabled())
    {
      logger.warn(format, arg1, arg2);
    }
  }

  @Override
  public void warn(String msg, Throwable t)
  {
    if (isWarnEnabled())
    {
      logger.warn(msg, t);
    }
  }

  @Override
  public boolean isWarnEnabled(Marker marker)
  {
    return isWarnEnabled() && logger.isWarnEnabled(marker);
  }

  @Override
  public void warn(Marker marker, String msg)
  {
    if (isWarnEnabled())
    {
      logger.warn(marker, msg);
    }
  }

  @Override
  public void warn(Marker marker, String format, Object arg)
  {
    if (isWarnEnabled())
    {
      logger.warn(marker, format, arg);
    }
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2)
  {
    if (isWarnEnabled())
    {
      logger.warn(marker, format, arg1, arg2);
    }
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments)
  {
    if (isWarnEnabled())
    {
      logger.warn(marker, format, arguments);
    }
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t)
  {
    if (isWarnEnabled())
    {
      logger.warn(marker, msg, t);
    }
  }

  @Override
  public void error(String msg)
  {
    if (isErrorEnabled())
    {
      logger.error(msg);
    }
  }

  @Override
  public void error(String format, Object arg)
  {
    if (isErrorEnabled())
    {
      logger.error(format, arg);
    }
  }

  @Override
  public void error(String format, Object arg1, Object arg2)
  {
    if (isErrorEnabled())
    {
      logger.error(format, arg1, arg2);
    }
  }

  @Override
  public void error(String format, Object... arguments)
  {
    if (isErrorEnabled())
    {
      logger.error(format, arguments);
    }
  }

  @Override
  public void error(String msg, Throwable t)
  {
    if (isErrorEnabled())
    {
      logger.error(msg, t);
    }
  }

  @Override
  public boolean isErrorEnabled(Marker marker)
  {
    return isErrorEnabled() && logger.isErrorEnabled(marker);
  }

  @Override
  public void error(Marker marker, String msg)
  {
    if (isErrorEnabled())
    {
      logger.error(marker, msg);
    }
  }

  @Override
  public void error(Marker marker, String format, Object arg)
  {
    if (isErrorEnabled())
    {
      logger.error(marker, format, arg);
    }
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2)
  {
    if (isErrorEnabled())
    {
      logger.error(marker, format, arg1, arg2);
    }
  }

  @Override
  public void error(Marker marker, String format, Object... arguments)
  {
    if (isErrorEnabled())
    {
      logger.error(marker, format, arguments);
    }
  }

  @Override
  public void error(Marker marker, String msg, Throwable t)
  {
    if (isErrorEnabled())
    {
      logger.error(marker, msg, t);
    }
  }

  /**
   * ADDITIONAL OpLogger capabilities begin here:
   */
  public void TRACE(Object... msg)
  {
    if (isTraceEnabled())
    {
      logger.trace(StringUtils.join(msg));
    }
  }

  public void DEBUG(Object... msg)
  {
    if (isDebugEnabled())
    {
      logger.debug(StringUtils.join(msg));
    }
  }

  public void INFO(Object... msg)
  {
    if (isInfoEnabled())
    {
      logger.info(StringUtils.join(msg));
    }
  }

  public void WARN(Object... msg)
  {
    if (isWarnEnabled())
    {
      logger.warn(StringUtils.join(msg));
    }
  }

  public void ERROR(Object... msg)
  {
    if (isErrorEnabled())
    {
      logger.error(StringUtils.join(msg));
    }
  }

  public static void syserr(Object... msg)
  {
    System.err.println(StringUtils.join(msg));
  }

  public static void sysout(Object... msg)
  {
    System.out.println(StringUtils.join(msg));
  }

  public LogLevel getLogLevel()
  {
    return logLevel;
  }

  public void setLogLevel(LogLevel logLevel)
  {
    logLevels.put(getName(), logLevel);
    this.logLevel = logLevel;
  }

  public static void setLogLevels(@NonNull Map<String, LogLevel> levels)
  {
    for (Entry<String, LogLevel> entry : levels.entrySet())
    {
      logLevels.put(entry.getKey(), entry.getValue());
    }
  }

  public static void main(String args[])
  {
    OpLogger logger = OpLoggerFactory.getLogger("myname");
    logger.setLogLevel(LogLevel.WARN);
    OpLogger.sysout("LogLevel=", logger.getLogLevel());
    logger.trace("TRACE");
    logger.debug("DEBUG");
    logger.info("INFO");
    logger.warn("WARN");
    logger.error("ERROR");
  }
}
