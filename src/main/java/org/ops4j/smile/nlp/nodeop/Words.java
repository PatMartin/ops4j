package org.ops4j.smile.nlp.nodeop;

import java.util.Arrays;
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
import smile.nlp.dictionary.EnglishPunctuations;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.normalizer.SimpleNormalizer;
import smile.nlp.tokenizer.SimpleTokenizer;

@AutoService(NodeOp.class) @Command(name = "words",
    mixinStandardHelpOptions = false, description = "Transform text to words.")
public class Words extends BaseNodeOp<Words>
{
  SimpleNormalizer normalizer = SimpleNormalizer.getInstance();

  public Words()
  {
    super("words");
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

    SimpleTokenizer tokenizer = new SimpleTokenizer(true);

    Predicate<JsonNode> stringsOnly = (
        json) -> json.getNodeType() == JsonNodeType.STRING;
    JsonTransform transform = (json) -> {
      ArrayNode arr = JacksonUtil.createArrayNode();

      String words[] = Arrays.stream(tokenizer.split(json.asText()))
          .filter(w -> !(EnglishStopWords.DEFAULT.contains(w.toLowerCase())
              || EnglishPunctuations.getInstance().contains(w)))
          .distinct().toArray(String[]::new);
      for (int i = 0; i < words.length; i++)
      {
        arr.add(new TextNode(words[i]));
      }

      return arr;
    };

    JsonNode result = JacksonUtil.transform(target, stringsOnly, transform);
    return result;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Words(), args);
  }
}
