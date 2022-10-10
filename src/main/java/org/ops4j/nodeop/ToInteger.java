package org.ops4j.nodeop;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "to:int", mixinStandardHelpOptions = false,
    description = "Converts a text node to an integer.")
public class ToInteger extends BaseNodeOp<ToInteger>
{
  @Parameters(index = "0", arity = "0..1",
      description = "The location of the value to convert.")
  private @Getter @Setter String location = null;

  public ToInteger()
  {
    name("to:int");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    if (location == null)
    {
      return new IntNode(Integer.parseInt(input.asText()));
    }
    else
    {
      JsonNode src = input.at(getLocation());
      return new IntNode(Integer.parseInt(src.textValue()));
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new ToInteger(), args);
  }
}
