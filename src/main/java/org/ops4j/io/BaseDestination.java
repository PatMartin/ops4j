package org.ops4j.io;

import java.io.File;
import java.io.OutputStream;

import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.log.OpLogging;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "base-input-source", mixinStandardHelpOptions = false)
public abstract class BaseDestination<T extends BaseDestination<T>>
    implements OutputDestination<T>, OpLogging
{
  private @Setter OpLogger         logger;

  private @Getter @Setter LogLevel logLevel = LogLevel.WARN;

  private @Getter @Setter String   name     = null;

  public BaseDestination(String name)
  {
    setName(name);
  }

  @Override
  public OpLogger getLogger()
  {
    if (logger == null)
    {
      logger = OpLoggerFactory.getLogger("ops.out." + getName());
    }
    return logger;
  }

  @Override
  public abstract OutputStream stream() throws OpsException;

  @Override
  public boolean canResolve(String location)
  {
    if (location.startsWith(getName()))
    {
      return true;
    }
    return new File(location).exists();
  }

  @Override
  public void configure(String... config) throws OpsException
  {
    // OpsLogger.syserr("CONFIG: ", config.length);
    if (config.length > 0 && config[0].trim().length() > 0)
    {
      CommandSpec spec = CommandSpec.create();
      new CommandLine(this).parseArgs(config);
    }
  }

  @Override
  public void setLogLevel(LogLevel logLevel)
  {
    // TODO Auto-generated method stub
  }
}
