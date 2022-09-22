package org.ops4j.nodeop.gen;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.FakerUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import picocli.CommandLine.Command;

@Command(name = "gen:name", mixinStandardHelpOptions = false, description = "Generate a full name.%n"
    + "%nExample: gen:name")
public class NameGenerator extends BaseNodeOp<NameGenerator>
{
  public NameGenerator()
  {
    name("gen:name");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(FakerUtil.faker().name().fullName());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new NameGenerator(), args);
  }
}
