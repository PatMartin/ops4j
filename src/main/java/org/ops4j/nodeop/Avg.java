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
@Command(name = "avg", mixinStandardHelpOptions = false,
    description = "Average a running series of numbers.")
public class Avg extends BaseNodeOp<Avg>
{
  private List<Double>        data   = new ArrayList<>();

  @Option(names = { "-w", "-window" }, required = false, defaultValue = "10",
      description = "The window.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter int window = 10;

  public Avg() throws OpsException
  {
    super("avg");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode target = getTarget(input);
    DEBUG("TARGET='", target, "'");
    if (target == null)
    {
      if (data.size() == 0)
      {
        return new DoubleNode(0.0);
      }
      else
      {
        return new DoubleNode(average(data));
      }
    }
    data.add(target.asDouble());
    if (data.size() > getWindow())
    {
      data.remove(0);
    }
    return new DoubleNode(average(data));
  }

  private double average(List<Double> list)
  {
    double total = 0;

    for (Double d : list)
    {
      total += d;
    }

    return total / list.size();
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Avg(), args);
  }
}
