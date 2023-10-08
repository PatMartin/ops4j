package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "cos", mixinStandardHelpOptions = false,
    description = "Generate a cosine.")
public class Cosine extends BaseNodeOp<Cosine>
{
  public Cosine()
  {
    super("cos");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode json = getTarget(input);
    if (json == null)
    {
      return NullNode.getInstance();
    }
    switch (json.getNodeType())
    {
      case NUMBER:
      {
        return new DoubleNode(Math.cos(json.asDouble() * Math.PI / 180));
      }
      default:
      {
        return NullNode.getInstance();
      }
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Cosine(), args);
  }
}
