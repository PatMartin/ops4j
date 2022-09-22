package org.ops4j.io;

import java.io.File;
import java.io.OutputStream;

import org.ops4j.OpLogger;
import org.ops4j.OpLogger.LogLevel;
import org.ops4j.OutputDestination;
import org.ops4j.exception.OpsException;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "base-input-source", mixinStandardHelpOptions = false)
public abstract class BaseDestination<T extends BaseDestination<T>>
    implements OutputDestination<T>
{
  private OpLogger                 opLogger = new OpLogger("ops4j.io.dst");

  private @Getter @Setter LogLevel logLevel = LogLevel.WARN;

  private @Getter @Setter String   name     = null;

  @Override
  public OpLogger getOpLogger()
  {
    return opLogger;
  }

  @Override
  public void setOpLogger(OpLogger logger)
  {
    this.opLogger = logger;
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
  //OpsLogger.syserr("CONFIG: ", config.length);
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
