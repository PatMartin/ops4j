package org.ops4j.nodeop;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOpCLI;
import org.ops4j.exception.OpsException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "now", mixinStandardHelpOptions = false, description = "Returns current time as milliseconds "
    + "since 1/1/1970")
public class Now extends BaseNodeOp<Now>
{
  @Option(names = { "-o",
      "--offset" }, required = false, description = "An optional offset to be "
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
