package org.ops4j.nodeop;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;

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
    name("to:upper");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(input.asText().toUpperCase());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToUpper(), args);
  }
}
