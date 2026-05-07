package org.ops4j.nodeop;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.log.OpLogger;
import org.ops4j.util.WeightedCollection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "choose", mixinStandardHelpOptions = false,
    description = "Randomly choose one of the given options.")
public class RandomChoice extends BaseNodeOp<RandomChoice>
{
  @Parameters(arity = "1..*", description = "At least one choice")
  private List<String>               choices = new ArrayList<>();

  private WeightedCollection<String> options = null;

  public RandomChoice()
  {
    super("choose");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    if (options == null)
    {
      options = new WeightedCollection<>();
      for (String choice : choices)
      {
        String parts[] = StringUtils.split(choice, "=");
        if (parts.length == 2)
        {
          double weight = Double.parseDouble(parts[1]);
          options.add(weight, parts[0]);
        }
        else
        {
          options.add(1.0, choice);
        }
      }
    }
    return new TextNode(options.next());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new RandomChoice(), args);
  }
}
