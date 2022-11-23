package org.ops4j.nodeop;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.Locator;
import org.ops4j.Ops4J;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "array", mixinStandardHelpOptions = false,
    description = "Create an array with the specified contents.")
public class CreateArray extends BaseNodeOp<CreateArray>
{
  @Parameters(index = "0", arity = "0..*",
      description = "Zero or more elements to add.")
  private @Getter @Setter List<String> paths = new ArrayList<>();
  
  public CreateArray()
  {
    super("array");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    ArrayNode array = JacksonUtil.createArrayNode();

    for (String path : paths)
    {
      if (Ops4J.locator().isNodeOp(path))
      {
        logger.trace("NodeOp(srcPath='", path, "', src='", input.toString(),
            "')");
        array.add(Ops4J.locator().evaluate(path, input));
      }
      else if (path.startsWith("/"))
      {
        array.add(path.equals("/") ? input : input.at(path));
      }
      else
      {
        array.add(new TextNode(path));
      }
    }

    return array;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new CreateArray(), args);
  }
}
