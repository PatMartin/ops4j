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

@AutoService(NodeOp.class)
@Command(name = "now", mixinStandardHelpOptions = false,
    description = "Returns current time as milliseconds " + "since 1/1/1970")
public class Now extends BaseNodeOp<Now>
{
  @Option(names = { "-o", "--offset" }, required = false,
      description = "An optional offset to be "
          + "applied to the value returned by the now node operation.")
  private @Getter @Setter Long offset = 0L;

  public Now()
  {
    name("gen:now");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode node = new LongNode(System.currentTimeMillis() + offset);
    debug("NODE: ", node);
    return node;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Now(), args);
  }
}
