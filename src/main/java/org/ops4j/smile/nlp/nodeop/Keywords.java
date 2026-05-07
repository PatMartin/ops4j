package org.ops4j.smile.nlp.nodeop;

import java.util.function.Predicate;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonTransform;
import org.ops4j.inf.NodeOp;
import org.ops4j.log.OpLogger;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;
import smile.nlp.collocation.NGram;
import smile.nlp.keyword.CooccurrenceKeywords;

@AutoService(NodeOp.class)
@Command(name = "keywords", mixinStandardHelpOptions = false,
    description = "Return the top K keywords.")
public class Keywords extends BaseNodeOp<Keywords>
{
  public Keywords()
  {
    super("keywords");
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

    DEBUG("TARGET: ", target);
    Predicate<JsonNode> stringsOnly = (
        json) -> json.getNodeType() == JsonNodeType.STRING;
    JsonTransform transform = (json) -> {
      ArrayNode arr = JacksonUtil.createArrayNode();
      DEBUG("JSON: ", json);
      NGram[] keywords = CooccurrenceKeywords.of(json.asText());
      DEBUG("JSON: ", json, " KEYWORDS.length=", keywords.length);
      for (int i = 0; i < keywords.length; i++)
      {
        ObjectNode ngram = JacksonUtil.createObjectNode();
        ngram.put("name", keywords[i].toString());
        ngram.put("count", keywords[i].count);
        arr.add(ngram);
      }

      return arr;
    };

    return JacksonUtil.transform(target, stringsOnly, transform);
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Keywords(), args);
  }
}
