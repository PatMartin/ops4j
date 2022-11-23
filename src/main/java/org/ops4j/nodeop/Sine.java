package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.auto.service.AutoService;
import com.google.common.util.concurrent.AtomicDouble;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class) @Command(name = "sin",
    mixinStandardHelpOptions = false, description = "Generate sine sequence.")
public class Sine extends BaseNodeOp<Sine>
{
  @Option(names = { "-s", "-start" }, required = false,
      description = "The starting value.  Default = 1")
  private @Getter @Setter Long start     = 0L;

  @Option(names = { "-i", "-inc" }, required = false,
      description = "The increment.  Default = 1")
  private @Getter @Setter Long increment = 1L;

  private AtomicDouble         value     = null;

  public Sine()
  {
    super("sin");
    logger.DEBUG("Creating sequence: '", getName(), "' - start=", getStart(),
        ", inc=", getIncrement());
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new DoubleNode(
        Math.sin(getValue().getAndAdd(getIncrement()) * Math.PI / 180.0));
  }

  public AtomicDouble getValue()
  {
    if (value == null)
    {
      value = new AtomicDouble(getStart());
    }
    return value;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Sine(), args);
  }
}
