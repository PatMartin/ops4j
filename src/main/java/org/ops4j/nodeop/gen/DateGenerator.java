package org.ops4j.nodeop.gen;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.FakerUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "gen:date", mixinStandardHelpOptions = false,
    description = "Generate a date.%n" + "%nExample: gen:date")
public class DateGenerator extends BaseNodeOp<DateGenerator>
{
  public DateGenerator()
  {
    name("gen:date");
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
