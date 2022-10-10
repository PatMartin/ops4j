package org.ops4j.log;

import java.util.HashMap;
import java.util.Map;

import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.util.MapUtil;

import lombok.NonNull;

public class OpLoggerFactory
{
  public final static Map<String, LogLevel> logLevels         = new HashMap<>();
  public final LogLevel                     DEFAULT_LOG_LEVEL = LogLevel.WARN;

  public static OpLogger getLogger(@NonNull String name)
  {
    OpLogger logger = new OpLogger(name);
    if (logLevels.containsKey(name))
    {
      logger.setLogLevel(logLevels.get(name));
    }
    else
    {
      String key = MapUtil.findKey(name, logLevels, ".");
      if (key != null)
      {
        logger.setLogLevel(logLevels.get(key));
      }
    }
    return logger;
  }

  public static void setLogLevels(Map<String, LogLevel> levels)
  {
    logLevels.putAll(levels);
  }
}
