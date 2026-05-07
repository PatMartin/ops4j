package org.ops4j.visual.op;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Locator;
import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class)
@Command(name = "viz-tree", description = "Render a visual tree of the data.")
public class VisualTree extends BaseOp<VisualTree>
{
  @Option(names = { "-o", "--output" },
      description = "The output location." + "  Default = tree.svg")
  private @Getter @Setter String outputLocation = "file(tree.svg)";

  @Option(names = { "-t", "--type" },
      description = "The output type.  One of (mindmap | wbs )"
          + "  Default = mindmap")
  private @Getter @Setter String diagramType = "mindmap";

  private StringWriter           sw             = new StringWriter();
  private OutputStream           os;

  public VisualTree()
  {
    super("viz-tree");
  }

  public VisualTree open() throws OpsException
  {
    Locator locator = new Locator();
    os = locator.resolveDestination(getOutputLocation()).stream();

    sw.write("@start" + getDiagramType() + "\n");
    return this;
  }

  private void traverse(String name, JsonNode node, int depth)
  {
    switch (node.getNodeType())
    {
      case OBJECT:
      {
        sw.write(
            StringUtils.repeat("*", depth) + "[#lightblue] " + name + "\n");
        ObjectNode onode = (ObjectNode) node;
        Iterator<String> nameIt = onode.fieldNames();
        while (nameIt.hasNext())
        {
          String childName = nameIt.next();
          traverse(childName, onode.get(childName), depth + 1);
        }
        break;
      }
      case STRING:
      {
        sw.write(
            StringUtils.repeat("*", depth) + "[#lightgreen] " + name + "\n");
        sw.write(StringUtils.repeat("*", depth + 1) + "[#lightgreen] "
            + node.asText() + "\n");
        break;
      }
      case NUMBER:
      {
        sw.write(StringUtils.repeat("*", depth) + "[#orange] " + name + "\n");
        sw.write(StringUtils.repeat("*", depth + 1) + "[#orange] "
            + node.asText() + "\n");
        break;
      }
      case ARRAY:
      {
        sw.write(StringUtils.repeat("*", depth) + "[#yellow] " + name + "\n");
        ArrayNode anode = (ArrayNode) node;
        for (int i = 0; i < anode.size(); i++)
        {
          traverse("" + i, anode.get(i), depth + 1);
        }
        break;
      }
      case MISSING:
      {
        sw.write(StringUtils.repeat("*", depth) + "[#red] " + name + "\n");
        sw.write(StringUtils.repeat("*", depth + 1) + "[#red] MISSING\n");
        break;
      }
      case NULL:
      {
        sw.write(StringUtils.repeat("*", depth) + "[#red] " + name + "\n");
        sw.write(StringUtils.repeat("*", depth + 1) + "[#red] NULL\n");
        break;
      }
      case BOOLEAN:
      {
        sw.write(StringUtils.repeat("*", depth) + "[#salmon] " + name + "\n");
        sw.write(StringUtils.repeat("*", depth + 1) + "[#salmon] "
            + node.asText() + "\n");

        break;
      }
      case BINARY:
      {
        sw.write(StringUtils.repeat("*", depth) + "[#silver] " + name + "\n");
        sw.write(StringUtils.repeat("*", depth + 1) + "[#silver] BINARY\n");

        break;
      }
      case POJO:
      {
        sw.write(StringUtils.repeat("*", depth) + "[#pink] " + name + "\n");
        sw.write(StringUtils.repeat("*", depth + 1) + "[#pink] POJO\n");

        break;
      }
    }
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    traverse("input", input.getJson(), 1);
    return input.asList();
  }

  public List<OpData> close() throws OpsException
  {
    sw.write("@end" + getDiagramType() + "\n");
    // System.out.println(sw.toString());
    SourceStringReader ssr = new SourceStringReader(sw.toString());
    try
    {
      ssr.generateImage(os, new FileFormatOption(FileFormat.SVG));
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new VisualTree(), args);
  }
}
