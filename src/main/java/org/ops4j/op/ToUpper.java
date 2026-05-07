package org.ops4j.op;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class)
@Command(name = "to-upper",
    description = "Convert all string values in the JSON document to uppercase.")
public class ToUpper extends BaseOp<ToUpper>
{
  public ToUpper()
  {
    super("to-upper");
  }

  @Override
  public List<OpData> execute(OpData input) throws OpsException
  {
    ObjectNode result = toUpper(input.getJson().deepCopy());
    return new OpData(result).asList();
  }

  private ObjectNode toUpper(ObjectNode node)
  {
    Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
    while (fields.hasNext())
    {
      Map.Entry<String, JsonNode> entry = fields.next();
      node.set(entry.getKey(), upperNode(entry.getValue()));
    }
    return node;
  }

  private JsonNode upperNode(JsonNode node)
  {
    if (node.isTextual())
    {
      return new TextNode(node.asText().toUpperCase());
    }
    else if (node.isObject())
    {
      return toUpper((ObjectNode) node);
    }
    else if (node.isArray())
    {
      ArrayNode array = (ArrayNode) node;
      for (int i = 0; i < array.size(); i++)
      {
        array.set(i, upperNode(array.get(i)));
      }
      return array;
    }
    return node;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new ToUpper(), args);
  }
}
