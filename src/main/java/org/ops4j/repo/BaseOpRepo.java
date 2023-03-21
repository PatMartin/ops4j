package org.ops4j.repo;

import org.ops4j.inf.OpRepo;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.log.OpLogging;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "base-repo", mixinStandardHelpOptions = false)
public abstract class BaseOpRepo<T extends BaseOpRepo<T>>
    implements OpRepo, OpLogging
{
  @Option(names = { "-n", "--name" },
      description = "The name of the op-repository.")
  private @Getter @Setter String     name;

  @Option(names = { "-L", "--log" },
      description = "The log level of this operation.")
  private @Getter LogLevel           logLevel = LogLevel.INFO;

  protected @Getter @Setter OpLogger logger   = null;

  public BaseOpRepo()
  {
    logger = OpLoggerFactory.getLogger("ops.repo");
  }
  
  public BaseOpRepo(String name)
  {
    this();
    setName(name);
  }
  
  public String getType()
  {
    return this.getClass().getCanonicalName();
  }
}
