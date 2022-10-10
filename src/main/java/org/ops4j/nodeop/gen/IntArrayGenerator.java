package org.ops4j.nodeop.gen;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "gen:array:int", mixinStandardHelpOptions = false,
    description = "Generate a date.%n" + "%nExample: gen:date")
public class IntArrayGenerator extends BaseNodeOp<IntArrayGenerator>
{
  @Option(names = { "-s", "-start" }, description = "The starting value.")
  private @Getter @Setter Integer start = 1;

  @Option(names = { "-e", "-end" }, description = "The ending value.")
  private @Getter @Setter Integer end   = 10;

  @Option(names = { "-i", "-inc" }, description = "The increment.")
  private @Getter @Setter Integer inc   = 1;

  public IntArrayGenerator()
  {
    name("gen:array:int");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    ArrayNode array = JacksonUtil.createArrayNode();
    for (int i = getStart(); i <= getEnd(); i += inc)
    {
      array.add(i);
    }
    return array;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new NameGenerator(), args);
  }
}
