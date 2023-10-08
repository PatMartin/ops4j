package org.ops4j.nodeop;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
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
@Command(name = "slope", mixinStandardHelpOptions = false,
    description = "Return the slope of a running series of numbers.")
public class Slope extends BaseNodeOp<Slope>
{
  private List<Double>        data   = new ArrayList<>();

  @Option(names = { "-w", "-window" }, required = false, defaultValue = "10",
      description = "The window.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter int window = 10;

  public Slope() throws OpsException
  {
    super("slope");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode target = getTarget(input);
    DEBUG("TARGET='", target, "'");
    if (target == null)
    {
      if (data.size() == 0)
      {
        return new DoubleNode(0);
      }
      else
      {
        return new DoubleNode(slope(data));
      }
    }
    data.add(target.asDouble());
    if (data.size() > getWindow())
    {
      data.remove(0);
    }
    return new DoubleNode(slope(data));
  }

  private double slope(List<Double> list)
  {
    if (list == null || list.size() <= 0)
    {
      return 0.0;
    }
    double xy[][] = new double[list.size()][];
    for (int i = 0; i < xy.length; i++)
    {
      xy[i] = new double[2];
      xy[i][0] = i;
      xy[i][1] = list.get(i);
    }

    SimpleRegression regression = new SimpleRegression();
    regression.addData(xy);
    return regression.getSlope();
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Slope(), args);
  }
}
