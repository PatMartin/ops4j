package org.ops4j.nodeop;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "text", mixinStandardHelpOptions = false,
    description = "Returns current time as milliseconds " + "since 1/1/1970")
public class Text extends BaseNodeOp<Text>
{
  @Parameters(index = "0", arity = "1..*",
      description = "An optional offset to be "
          + "applied to the value returned by the now node operation.")
  private @Getter @Setter List<String> text;

  public Text()
  {
    super("text");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(StringUtils.join(getText(), " "));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Text(), args);
  }
}
