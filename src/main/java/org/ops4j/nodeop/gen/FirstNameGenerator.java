package org.ops4j.nodeop.gen;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.FakerUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import picocli.CommandLine.Command;

@Command(name = "gen:first", mixinStandardHelpOptions = false, description = "Generate a first name."
    + "%n%nExample: gen:first")
public class FirstNameGenerator extends BaseNodeOp<FirstNameGenerator>
{
  public FirstNameGenerator()
  {
    name("gen:first");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(FakerUtil.faker().name().firstName());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new FirstNameGenerator(), args);
  }
}
