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
    description = "Return the minimum of a running series of numbers.")
public class Min extends BaseNodeOp<Min>
{
  private List<Double>        data   = new ArrayList<>();

  @Option(names = { "-w", "-window" }, required = false, defaultValue = "10",
      description = "The window.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter int window = 10;

  public Min() throws OpsException
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
        return new DoubleNode(min(data));
      }
    }
    data.add(target.asDouble());
    if (data.size() > getWindow())
    {
      data.remove(0);
    }
    return new DoubleNode(min(data));
  }

  private double min(List<Double> list)
  {
    double min = Double.MAX_VALUE;

    for (Double d : list)
    {
      if (d < min)
      {
        min = d;
      }
    }

    return min;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Min(), args);
  }
}
