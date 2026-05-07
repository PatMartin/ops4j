package org.ops4j.visual.op;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Locator;
import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class) @Command(name = "viz-flow",
    description = "Render a transition tree of the data.")
public class VisualFlow extends BaseOp<VisualFlow>
{
  @Option(names = { "-o", "--output" },
      description = "The output location." + "  Default = flow.svg")
  private @Getter @Setter String             outputLocation = "file(flow.svg)";

  @Option(names = { "-s", "--src" },
      description = "The source path." + "  Default = /src")
  private @Getter @Setter String             source         = "/src";

  @Option(names = { "-d", "--dest" },
      description = "The destination path." + "  Default = /dest")
  private @Getter @Setter String             destination    = "/dst";

  @Option(names = { "-e", "--entry" }, description = "The entry point.")
  private @Getter @Setter String             entry          = null;

  @Option(names = { "-g", "--guard" },
      description = "Guard against circular references by terminating on "
          + "nodes which have already been encountered.")
  private @Getter @Setter boolean            guard          = false;

  private StringWriter                       sw             = new StringWriter();
  private OutputStream                       os;

  private Map<String, Map<String, JsonNode>> mapping        = new HashMap<String, Map<String, JsonNode>>();

  public VisualFlow()
  {
    super("viz-flow");
  }

  public VisualFlow open() throws OpsException
  {
    Locator locator = new Locator();
    os = locator.resolveDestination(getOutputLocation()).stream();

    sw.write("@startmindmap\n");
    return this;
  }

  private void traverse(String name, JsonNode node, int depth)
  {
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    JsonNode srcNode = JacksonUtil.at(input.toJson(), getSource());
    JsonNode dstNode = JacksonUtil.at(input.toJson(), getDestination());

    if (srcNode != null && dstNode != null && srcNode.isTextual()
        && dstNode.isTextual())
    {
      Map<String, JsonNode> paths;
      if (!mapping.containsKey(srcNode.asText()))
      {
        paths = new HashMap<String, JsonNode>();
        mapping.put(srcNode.asText(), paths);
      }
      else
      {
        paths = mapping.get(srcNode.asText());
      }

      paths.put(dstNode.asText(), input.toJson());
    }
    return input.asList();
  }

  private void traverse(String name, Map<String, Boolean> traversed, int depth)
  {
    if (isGuard())
    {
      if (traversed.containsKey(name))
      {
        sw.write(StringUtils.repeat('*', depth) + "[#wheat] " + name + "\n");
        return;
      }
    }
    traversed.put(name, true);

    sw.write(StringUtils.repeat('*', depth) + " " + name + "\n");
    if (mapping.containsKey(name))
    {
      Map<String, JsonNode> paths = mapping.get(name);
      for (String path : paths.keySet())
      {
        traverse(path, traversed, depth + 1);
      }
    }
  }

  public List<OpData> close() throws OpsException
  {
    Map<String, Boolean> traversed = new HashMap<String, Boolean>();
    if (getEntry() == null)
    {
      // sw.write("* root\n");
      for (String entry : mapping.keySet())
      {
        if (!traversed.containsKey(entry))
          traverse(entry, traversed, 1);
      }
    }
    else
    {
      traverse(getEntry(), traversed, 1);
    }
    sw.write("@endmindmap\n");
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
    OpCLI.cli(new VisualFlow(), args);
  }
}
