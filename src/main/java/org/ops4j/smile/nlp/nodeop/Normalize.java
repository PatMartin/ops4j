package org.ops4j.smile.nlp.nodeop;

import java.util.function.Predicate;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonTransform;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;
import smile.nlp.normalizer.SimpleNormalizer;

@AutoService(NodeOp.class) @Command(name = "normalize",
    mixinStandardHelpOptions = false, description = "Normalize text.")
public class Normalize extends BaseNodeOp<Normalize>
{
  SimpleNormalizer normalizer = SimpleNormalizer.getInstance();

  public Normalize()
  {
    super("normalize");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    if (input == null)
    {
      return NullNode.getInstance();
    }
    JsonNode target = getTarget(input);

    if (target == null)
    {
      return NullNode.getInstance();
    }

    Predicate<JsonNode> stringsOnly = (
        json) -> json.getNodeType() == JsonNodeType.STRING;
    JsonTransform transform = (json) -> new TextNode(
        normalizer.normalize(json.asText()));
    JsonNode result = JacksonUtil.transform(target, stringsOnly, transform);

    return result;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Normalize(), args);
  }
}
