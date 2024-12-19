package org.ops4j.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.ops4j.exception.OpsException;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(InputSource.class) @Command(name = "text",
    mixinStandardHelpOptions = false, description = "Streams literal text.")
public class TextSource extends BaseSource<TextSource>
{
  @Parameters(index = "0", arity = "1", paramLabel = "text",
      description = "Some text.")
  private @Getter @Setter String text;

  public TextSource()
  {
    super("text");
  }

  public TextSource create()
  {
    return new TextSource();
  }

  @Override
  public InputStream stream() throws OpsException
  {
    debug("TEXT: '", getText(), "'");
    return new ByteArrayInputStream(getText().getBytes());
  }

  public static void main(String args[]) throws OpsException
  {
    InputSourceCLI.cli(new TextSource(), args);
  }
}
