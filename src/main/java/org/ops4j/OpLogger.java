package org.ops4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

public class OpLogger
{
  public enum LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR, NONE
  }

  private @Getter @Setter LogLevel logLevel = LogLevel.WARN;

  private Logger                   logger;

  public OpLogger(String name)
  {
    logger = LoggerFactory.getLogger(name);
  }

  public OpLogger(Object obj)
  {
    this(obj.getClass().getName());
  }

  public void trace(Object... msg)
  {
    if (logLevel == LogLevel.TRACE)
    {
      logger.trace(StringUtils.join(msg));
    }
  }

  public void debug(Object... msg)
  {
    if (logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG)
    {
      logger.debug(StringUtils.join(msg));
    }
  }

  public void info(Object... msg)
  {
    if (logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG
        || logLevel == LogLevel.INFO)
    {
      logger.info(StringUtils.join(msg));
    }
  }

  public void warn(Object... msg)
  {
    if (logLevel == LogLevel.TRACE || logLevel == LogLevel.DEBUG
        || logLevel == LogLevel.INFO || logLevel == LogLevel.WARN)
    {
      logger.warn(StringUtils.join(msg));
    }
  }

  public void error(Object... msg)
  {
    if (logLevel != LogLevel.NONE)
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
}
