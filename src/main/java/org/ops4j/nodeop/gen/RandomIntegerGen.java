package org.ops4j.nodeop.gen;

import org.apache.commons.lang3.RandomUtils;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "random:int", mixinStandardHelpOptions = false,
    description = "Generate a first name." + "%n%nExample: random:text")
public class RandomIntegerGen extends BaseNodeOp<RandomIntegerGen>
{
  @Option(names = { "-min" }, required = false,
      description = "The minimum int to be generated.")
  private @Getter @Setter int min = 0;

  @Option(names = { "-max" }, required = false,
      description = "The maximum int to be generated.")
  private @Getter @Setter int max = 100;

  public RandomIntegerGen()
  {
    name("random:int");
  }

  public RandomIntegerGen create()
  {
    return new RandomIntegerGen();
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new IntNode(RandomUtils.nextInt(getMin(), getMax() + 1));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new RandomIntegerGen(), args);
  }
}
