package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "noop", mixinStandardHelpOptions = false,
    description = "This node-op does nothing.")
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
