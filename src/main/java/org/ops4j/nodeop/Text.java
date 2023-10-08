package org.ops4j.nodeop;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "text", mixinStandardHelpOptions = false,
    description = "Create a text node with optional interpolation.")
public class Text extends BaseNodeOp<Text>
{
  @Parameters(index = "0", arity = "1..*", description = "The text.")
  private @Getter @Setter List<String> text;

  public Text()
  {
    super("text");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    System.out.println("EXECUTING text(text='" + getText() + "'");
    return new TextNode(
        JacksonUtil.interpolate(StringUtils.join(text, " "), input));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Text(), args);
  }
}
