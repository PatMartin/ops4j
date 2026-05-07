package org.ops4j.groovy.op;

import java.util.Map;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class) @Command(name = "eval",
    mixinStandardHelpOptions = false, description = "Evaluate an expression.")
public class Eval extends BaseNodeOp<Eval>
{
  @Parameters(index = "0", arity = "1",
      description = "The expression to evaluate.")
  private @Getter @Setter String expression;

  private String                 script = "import static java.lang.Math.*;\n%s";

  public Eval()
  {
    super("eval");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    Binding binding = new Binding();
    if (input != null && input.isObject())
    {
      ObjectNode onode = (ObjectNode) input;
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> bindingMap = mapper.convertValue(onode,
          new TypeReference<Map<String, Object>>()
          {
          });
      for (String name : bindingMap.keySet())
      {
        binding.setVariable(name, bindingMap.get(name));
      }
    }
    else
    {
      binding.setVariable("input", input);
    }
    GroovyShell shell = new GroovyShell(binding);
    String thisScript = String.format(script, getExpression());
    TRACE("SCRIPT: '", thisScript, "'");
    Object result = shell.evaluate(thisScript);
    DEBUG("EVAL RETURN: '", result, "' = ",
        (result != null) ? result.getClass().getName() : "N/A");

    return toJsonNode(result);
  }

  private JsonNode toJsonNode(Object obj) throws OpsException
  {
    if (obj == null)
    {
      return NullNode.getInstance();
    }
    if (obj instanceof GString)
    {
      return new TextNode("" + obj);
    }
    else
    {
      return JacksonUtil.toJsonNode(obj);
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Eval(), args);
  }
}
