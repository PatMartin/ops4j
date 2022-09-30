package org.ops4j.nodeop.gen;

import org.ops4j.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.FakerUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import picocli.CommandLine.Command;

@Command(name = "gen:last", mixinStandardHelpOptions = false, description = "Generate a last name."
    + "%n%nExample: gen:last")
public class LastNameGenerator extends BaseNodeOp<LastNameGenerator>
{
  public LastNameGenerator()
  {
    name("gen:last");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(FakerUtil.faker().name().firstName());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new LastNameGenerator(), args);
  }
}
