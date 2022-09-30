package org.ops4j.nodeop;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.NodeOps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "pipeline", mixinStandardHelpOptions = false,
    description = "Returns current time as milliseconds " + "since 1/1/1970")
public class Pipeline extends BaseNodeOp<Pipeline>
{
  @Parameters(index = "0", arity = "1",
      description = "The path to the input node.")
  private @Getter @Setter String path;

  @Parameters(index = "1", arity = "0..*",
      description = "Run a series of node operations in a pipeline.")
  private @Getter @Setter List<String> commands;

  private List<NodeOp<?>>              nodeOps = null;

  public Pipeline()
  {
    name("pipeline");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    syserr("PATH : ", getPath());
    syserr("INPUT: ", input);
    
    JsonNode srcNode = input.at(getPath());
    
    if (nodeOps == null)
    {
      nodeOps = NodeOps.create(StringUtils.join(getCommands(), ""));
    }
    
    JsonNode output = srcNode.deepCopy();
    for (NodeOp<?> op : nodeOps)
    {
      syserr("OUTPUT: ", output);
      output = op.execute(output);
    }
    return output;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Pipeline(), args);
  }
}
