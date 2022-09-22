package org.ops4j.nodeop;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOpCLI;
import org.ops4j.exception.OpsException;

import com.fasterxml.jackson.databind.JsonNode;

import picocli.CommandLine.Command;

@Command(name = "noop", mixinStandardHelpOptions = false, description = "This node-op does nothing.")
public class NoOp extends BaseNodeOp<NoOp>
{
  public NoOp()
  {
    name("noop");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return input;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new NoOp(), args);
  }
}
