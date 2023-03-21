package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.log.OpLogger;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "text", mixinStandardHelpOptions = false,
    description = "Create a text node with optional interpolation.")
public class Text extends BaseNodeOp<Text>
{
  @Parameters(index = "0", arity = "1", description = "The text.")
  private @Getter @Setter String  text;

  @Option(names = { "-i", "-interpolate" }, required = false,
      description = "When set, interpolate the text value.  "
          + "(Default=${DEFAULT-VALUE})")
  private @Getter @Setter boolean interpolate = false;

  public Text()
  {
    super("text");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    //OpLogger.sysout("***************** TECTTINGIIG");
    
    if (isInterpolate())
    {
      //OpLogger.sysout("INTERPOLATING: '", getText(), "' vs '",
      //    JacksonUtil.toPrettyString(input), "'");
      return new TextNode(JacksonUtil.interpolate(getText(), input));
    }
    OpLogger.sysout("TEXTING: ", getText());
    //DEBUG("TEXTING: '", getText());
    return new TextNode(getText());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Text(), args);
  }
}
