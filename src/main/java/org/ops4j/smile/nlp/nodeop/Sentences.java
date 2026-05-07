package org.ops4j.smile.nlp.nodeop;

import java.util.function.Predicate;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonTransform;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;
import smile.nlp.tokenizer.SimpleSentenceSplitter;

@AutoService(NodeOp.class)
@Command(name = "sentences", mixinStandardHelpOptions = false,
    description = "Transform text to sentences.")
public class Sentences extends BaseNodeOp<Sentences>
{
  public Sentences()
  {
    super("sentences");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    ArrayNode array = JacksonUtil.createArrayNode();

    if (input == null)
    {
      return array;
    }
    JsonNode target = getTarget(input);
    if (target == null)
    {
      return array;
    }

    Predicate<JsonNode> stringsOnly = (
        json) -> json.getNodeType() == JsonNodeType.STRING;
    JsonTransform transform = (json) -> {
      ArrayNode arr = JacksonUtil.createArrayNode();

      String sentences[] = SimpleSentenceSplitter.getInstance()
          .split(json.asText());

      for (int i = 0; i < sentences.length; i++)
      {
        arr.add(new TextNode(sentences[i]));
      }

      return arr;
    };

    return JacksonUtil.transform(target, stringsOnly, transform);
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Sentences(), args);
  }
}
