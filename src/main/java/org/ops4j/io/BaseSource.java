package org.ops4j.io;

import java.io.File;
import java.io.InputStream;

import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.log.OpLogging;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;

@Command(name = "input-source", mixinStandardHelpOptions = false)
public abstract class BaseSource<T extends BaseSource<T>>
    implements InputSource<T>, OpLogging
{
  private @Getter @Setter OpLogger opLogger = new OpLogger("ops4j.in");

  private @Getter @Setter LogLevel logLevel = LogLevel.WARN;

  private @Getter @Setter String   name     = null;

  @Override
  public abstract InputStream stream() throws OpsException;

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
}
