package org.ops4j.nodeop;

import java.util.concurrent.atomic.AtomicLong;

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

  private AtomicLong           value     = null;

  public Sequence()
  {
    super("seq");
    logger.DEBUG("Creating sequence: '", getName(), "' - start=", getStart(),
        ", inc=", getIncrement());
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new LongNode(getValue().getAndAdd(getIncrement()));
  }

  public AtomicLong getValue()
  {
    if (value == null)
    {
      value = new AtomicLong(getStart());
    }
    return value;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Sequence(), args);
  }
}
