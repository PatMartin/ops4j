package org.ops4j.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ops4j.exception.OpsException;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "file", mixinStandardHelpOptions = false,
    description = "Streams a file.")
public class FileSource extends BaseSource<FileSource>
{
  @Parameters(index = "0", arity = "1", paramLabel = "<input-location>",
      description = "The location of the input.")
  private @Getter @Setter String location = null;

  public FileSource()
  {
    super();
    setName("file");
  }

  public FileSource create()
  {
    return new FileSource();
  }

  @Override
  public InputStream stream() throws OpsException
  {
    try
    {
      return new FileInputStream(new File(getLocation()));
    }
    catch(FileNotFoundException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static void main(String args[]) throws OpsException
  {
    InputSourceCLI.cli(new FileSource(), args);
  }
}
