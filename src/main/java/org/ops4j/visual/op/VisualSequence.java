package org.ops4j.visual.op;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

import org.ops4j.Locator;
import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class)
@Command(name = "viz-sequence", description = "Render a sequence diagram.")
public class VisualSequence extends BaseOp<VisualSequence>
{
  @Option(names = { "-o", "--output" },
      description = "The output location." + "  Default = sequence.svg")
  private @Getter @Setter String outputLocation  = "file(sequence.svg)";

  @Option(names = { "-s", "--src" },
      description = "The JSON pointer path to the source of the sequence "
          + "diagram production: <src> -> <dest> : <comment>%n"
          + "Default = source")
  private @Getter @Setter String sourcePath      = "/source";

  @Option(names = { "-d", "--dst" },
      description = "The JSON pointer path to the destination of the sequence "
          + "diagram production: <src> -> <dest> : <comment>%n"
          + "Default = source")
  private @Getter @Setter String destinationPath = "/dest";

  @Option(names = { "-c", "--comment" },
      description = "The JSON pointer path to the comment portion "
          + "of the sequence diagram production: "
          + "<src> -> <dest> : <comment>%n" + "Default = source")
  private @Getter @Setter String commentPath     = null;

  private StringWriter           sw              = new StringWriter();
  private OutputStream           os;

  public VisualSequence()
  {
    super("viz-sequence");
  }

  public VisualSequence open() throws OpsException
  {
    Locator locator = new Locator();
    os = locator.resolveDestination(getOutputLocation()).stream();

    sw.write("@startuml\n");
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    sw.write(JacksonUtil.at(input.getJson(), getSourcePath()).asText() + " -> "
        + JacksonUtil.at(input.getJson(), getDestinationPath()).asText());

    if (getCommentPath() != null)
    {
      sw.write(
          " : " + JacksonUtil.at(input.getJson(), getCommentPath()).asText());
    }
    sw.write("\n");
    return input.asList();
  }

  public List<OpData> close() throws OpsException
  {
    sw.write("@enduml\n");
    // System.out.println(sw.toString());
    SourceStringReader ssr = new SourceStringReader(sw.toString());
    try
    {
      ssr.outputImage(os, new FileFormatOption(FileFormat.SVG));
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new VisualSequence(), args);
  }
}
