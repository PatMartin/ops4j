package org.ops4j.nodeop;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "min", mixinStandardHelpOptions = false,
    description = "Return the maximum of a running series of numbers.")
public class Max extends BaseNodeOp<Max>
{
  private List<Double>        data   = new ArrayList<>();

  @Option(names = { "-w", "-window" }, required = false, defaultValue = "10",
      description = "The window.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter int window = 10;

  public Max() throws OpsException
  {
    super("min");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode target = getTarget(input);
    DEBUG("TARGET='", target, "'");
    if (target == null)
    {
      if (data.size() == 0)
      {
        return new DoubleNode(Double.MAX_VALUE);
      }
      else
      {
        return new DoubleNode(max(data));
      }
    }
    data.add(target.asDouble());
    if (data.size() > getWindow())
    {
      data.remove(0);
    }
    return new DoubleNode(max(data));
  }

  private double max(List<Double> list)
  {
    double max = Double.MIN_VALUE;

    for (Double d : list)
    {
      if (d > max)
      {
        max = d;
      }
    }

    return max;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Max(), args);
  }
}
