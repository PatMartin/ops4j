package org.ops4j.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.ops4j.exception.OpsException;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(OutputDestination.class)
@Command(name = "file", mixinStandardHelpOptions = false,
    description = "Streams a file into an output destination.")
public class FileDestination extends BaseDestination<FileDestination>
{
  @Parameters(index = "0", arity = "1", paramLabel = "<destination>",
      description = "The location of the output destination.")
  private @Getter @Setter String location = null;

  private @Setter OpLogger       logger;

  public FileDestination()
  {
    super("file");
  }

  public FileDestination create()
  {
    return new FileDestination();
  }

  @Override
  public OutputStream stream() throws OpsException
  {
    try
    {
      return new FileOutputStream(new File(getLocation()));
    }
    catch(FileNotFoundException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static void main(String args[]) throws OpsException
  {
    OutputDestinationCLI.cli(new FileDestination(), args);
  }

  @Override
  public OpLogger getLogger()
  {
    if (logger == null)
    {
      logger = OpLoggerFactory.getLogger("ops.out.file");
    }
    return logger;
  }
}
