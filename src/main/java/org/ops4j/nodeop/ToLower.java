package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "to-lower", mixinStandardHelpOptions = false,
    description = "Converts a text node to lower case.")
public class ToLower extends BaseNodeOp<ToLower>
{
  public ToLower()
  {
    super("to-lower");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(getTarget(input).asText().toLowerCase());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToLower(), args);
  }
}
