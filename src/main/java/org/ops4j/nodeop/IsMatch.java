package org.ops4j.nodeop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "match", mixinStandardHelpOptions = false,
    description = "Returns a boolean true node on match, false otherwise.")
public class IsMatch extends BaseNodeOp<IsMatch>
{
  @Option(names = { "-pattern" }, description = "The pattern to match.")
  private @Getter @Setter String pattern;

  private Pattern                compiled = null;

  public IsMatch()
  {
    super("match");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    JsonNode srcNode = getTarget(input);
    if (srcNode == null)
    {
      DEBUG("SRC is null");
      return BooleanNode.FALSE;
    }

    if (getPattern() == null)
    {
      DEBUG("PATTERN is null");
      return input;
    }

    if (compiled == null)
    {
      DEBUG("Compiling: /.*", getPattern(), ".*/");
      compiled = Pattern.compile(".*" + getPattern() + ".*");
    }

    switch (srcNode.getNodeType())
    {
      case NUMBER:
      case STRING:
      {
        Matcher matcher = compiled.matcher(srcNode.asText());
        DEBUG("MATCHES: '", getPattern(), "' vs '", srcNode.asText(), "'");
        return (matcher.matches()) ? BooleanNode.TRUE : BooleanNode.FALSE;
      }
      default:
      {
        DEBUG("UNSUPPORTED-NODE-TYPE: '", srcNode.getNodeType());
        return BooleanNode.FALSE;
      }
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new IsMatch(), args);
  }
}
