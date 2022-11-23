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
@Command(name = "to:upper", mixinStandardHelpOptions = false,
    description = "Converts a text node to upper case.")
public class ToUpper extends BaseNodeOp<ToUpper>
{
  public ToUpper()
  {
    super("to:upper");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(getTarget(input).asText().toUpperCase());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToUpper(), args);
  }
}
