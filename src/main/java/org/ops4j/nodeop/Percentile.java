package org.ops4j.nodeop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "pct", mixinStandardHelpOptions = false,
    description = "Average a running series of numbers.")
public class Percentile extends BaseNodeOp<Percentile>
{
  private List<Double>           data    = new ArrayList<>();

  @Option(names = { "-w", "-window" }, required = false, defaultValue = "100",
      description = "The window.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter int    window  = 100;

  @Option(names = { "-p", "-percent" }, required = false, defaultValue = "95",
      description = "The window.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter double percent = 95.0;

  public Percentile() throws OpsException
  {
    super("pct");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode target = getTarget(input);
    DEBUG("TARGET='", target, "'");

    if (target != null)
    {
      data.add(target.asDouble());
    }
    else
    {
      if (data.size() == 0)
      {
        return NullNode.getInstance();
      }
    }
    if (data.size() > getWindow())
    {
      data.remove(0);
    }
    int index = (int) Math.floor(data.size() * getPercent() / 100.0);
    List<Double> ranked = new ArrayList<Double>();
    for (double d : data)
    {
      ranked.add(d);
    }
    Collections.sort(ranked);

    return new DoubleNode(ranked.get(index));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Percentile(), args);
  }
}
