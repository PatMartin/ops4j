package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class) @Command(name = "missing",
    mixinStandardHelpOptions = false, description = "Set a node to missing.")
public class GenMissing extends BaseNodeOp<GenMissing>
{

  public GenMissing()
  {
    super("missing");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return MissingNode.getInstance();
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new GenMissing(), args);
  }
}
