package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "to:double", mixinStandardHelpOptions = false,
    description = "Converts a node to an double.")
public class ToDouble extends BaseNodeOp<ToDouble>
{
  public ToDouble()
  {
    super("to:double");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    if (getArgs() == null || getArgs().size() == 0 || getArgs().get(0) == null)
    {
      return new DoubleNode(Double.parseDouble(input.asText()));
    }
    return new DoubleNode(
        Double.parseDouble(input.at(getArgs().get(0)).asText()));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToDouble(), args);
  }
}
