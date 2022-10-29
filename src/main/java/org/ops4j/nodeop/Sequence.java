package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class) @Command(name = "seq",
    mixinStandardHelpOptions = false, description = "Generate a sequence.")
public class Sequence extends BaseNodeOp<Sequence>
{
  @Option(names = { "-s", "-start" }, required = false,
      description = "The starting value.  Default = 1")
  private @Getter @Setter Long start     = 1L;

  @Option(names = { "-i", "-inc" }, required = false,
      description = "The increment.  Default = 1")
  private @Getter @Setter Long increment = 1L;

  public Sequence()
  {
    super("seq");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode node = new LongNode(increment++);
    return node;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Sequence(), args);
  }
}
