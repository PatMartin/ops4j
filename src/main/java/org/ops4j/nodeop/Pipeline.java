package org.ops4j.nodeop;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.NodeOps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "run", mixinStandardHelpOptions = false,
    description = "Returns current time as milliseconds " + "since 1/1/1970")
public class Pipeline extends BaseNodeOp<Pipeline>
{
  @Parameters(index = "0", arity = "1..*",
      description = "Run a series of node operations in a pipeline.")
  private @Getter @Setter List<String> commands;

  private List<NodeOp<?>>              nodeOps = null;

  public Pipeline()
  {
    name("pipeline");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    String cmd = StringUtils.join(getCommands(), "");
    // syserr("input=", input, " - cmd='", cmd, "'");
    String cmds[] = StringUtils.split(cmd, "=>");
    JsonNode srcNode;
    if (cmds != null && cmds.length > 0)
    {
      if (cmds[0].startsWith("/"))
      {
        srcNode = input.at(cmds[0]);
      }
      else
      {
        srcNode = input;
      }
    }
    else
    {
      return new TextNode(cmd);
    }

    if (cmds.length == 1 && cmds[0].startsWith("/"))
    {
      return srcNode.deepCopy();
    }

    // If we get here, we should have at least one => separator
    if (nodeOps == null)
    {
      if (cmds[0].startsWith("/"))
      {
        // syserr("substring cmd: ", cmd.substring(cmd.indexOf("=>") + 2));
        nodeOps = NodeOps.create(cmd.substring(cmd.indexOf("=>") + 2));
      }
      else
      {
        nodeOps = NodeOps.create(cmd);
      }
    }

    // syserr("nodeops: ", nodeOps.size());
    JsonNode output = srcNode.deepCopy();
    for (NodeOp<?> op : nodeOps)
    {
      // syserr("Executing: ", op.getName() + " on - ", output);
      output = op.execute(output);
    }
    return output;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Pipeline(), args);
  }
}
