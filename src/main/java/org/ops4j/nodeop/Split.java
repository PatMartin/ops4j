package org.ops4j.nodeop;

import org.apache.commons.lang3.StringUtils;
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
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "split", mixinStandardHelpOptions = false,
    description = "Splits the given node into parts.")
public class Split extends BaseNodeOp<Split>
{
  @Option(names = { "-s", "--separator" }, description = "")
  private @Getter @Setter String separator = ",";

  @Parameters(index = "0", arity = "1", description = "")
  private @Getter @Setter String path      = "/";

  public Split()
  {
    name("split");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode srcNode = input.at(getPath());
    syserr("INPUT: '", srcNode.asText(), "'");
    if (srcNode != null && srcNode.isTextual())
    {
      String parts[] = StringUtils.split(srcNode.asText(), getSeparator());
      ArrayNode array = JacksonUtil.createArrayNode();
      for (String part : parts)
      {
        array.add(part);
      }
      return array;
    }
    return input;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Split(), args);
  }
}
