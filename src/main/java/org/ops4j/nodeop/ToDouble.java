package org.ops4j.nodeop;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "to:double", mixinStandardHelpOptions = false,
    description = "Converts a text node to an double.")
public class ToDouble extends BaseNodeOp<ToDouble>
{
  public ToDouble()
  {
    name("to:double");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new DoubleNode(Double.parseDouble(input.asText()));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToDouble(), args);
  }
}
