package org.ops4j.nodeop;

import java.util.List;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.auto.service.AutoService;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "json:path", mixinStandardHelpOptions = false,
    description = "Returns the result of the specified jsonpath "
        + "expression run upon he input node.")
public class JsonPath extends BaseNodeOp<JsonPath>
{
  @Parameters(index = "0", arity = "1", description = "The operand.")
  private @Getter @Setter String expression;

  public JsonPath()
  {
    super("json:path");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    // syserr("plus(", getOperand(), ") on ", input);
    if (input == null)
    {
      return input;
    }
    String json = input.toString();
    com.jayway.jsonpath.Configuration conf = com.jayway.jsonpath.Configuration
        .defaultConfiguration().jsonProvider(new JacksonJsonProvider())
        .addOptions(Option.ALWAYS_RETURN_LIST);
    
    List<Object> results = com.jayway.jsonpath.JsonPath.using(conf)
        .parse(input.toString()).read(getExpression());
    ArrayNode array = JacksonUtil.createArrayNode();
    for (Object result : results)
    {
      array.add(JacksonUtil.mapper().valueToTree(result));
    }
    return array;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new JsonPath(), args);
  }
}
