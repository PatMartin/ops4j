package org.ops4j.nodeop;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
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

  private SimpleDateFormat     fmt    = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  public Now()
  {
    super("now");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(
        fmt.format(new Date(System.currentTimeMillis() + offset)));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Now(), args);
  }
}
