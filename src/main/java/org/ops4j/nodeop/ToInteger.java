package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "to:int", mixinStandardHelpOptions = false,
    description = "Converts a text node to an integer.")
public class ToInteger extends BaseNodeOp<ToInteger>
{
  public ToInteger()
  {
    super("to:int");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new IntNode(Integer.parseInt(getTarget(input).asText()));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToInteger(), args);
  }
}
