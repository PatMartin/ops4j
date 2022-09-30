package org.ops4j.nodeop;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "to:int", mixinStandardHelpOptions = false,
    description = "Converts a text node to an integer.")
public class ToInteger extends BaseNodeOp<ToInteger>
{
  public ToInteger()
  {
    name("to:int");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new IntNode(Integer.parseInt(input.asText()));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToInteger(), args);
  }
}
