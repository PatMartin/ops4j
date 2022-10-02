package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "to:float", mixinStandardHelpOptions = false,
    description = "Converts a text node to an double.")
public class ToFloat extends BaseNodeOp<ToFloat>
{
  public ToFloat()
  {
    name("to:float");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new FloatNode(Float.parseFloat(input.asText()));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToFloat(), args);
  }
}
