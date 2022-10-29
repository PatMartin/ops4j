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
@Command(name = "to:month", mixinStandardHelpOptions = false,
    description = "Converts a numeric node into a month.")
public class ToMonth extends BaseNodeOp<ToMonth>
{
  private String MONTH[] = new String[] { "JAN", "FEB", "MAR", "APR", "MAY",
      "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

  public ToMonth()
  {
    super("to:month");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    if (input.isNumber())
    {
      return new TextNode(MONTH[input.asInt() % 12]);
    }
    throw new OpsException("Could not convert non-numeric to month.");
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToLower(), args);
  }
}
