package org.ops4j.nodeop.gen;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.FakerUtil;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.javafaker.Address;
import com.github.javafaker.Demographic;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.PhoneNumber;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "gen-city", mixinStandardHelpOptions = false,
    description = "Generate a city.")
public class CityGenerator extends BaseNodeOp<CityGenerator>
{
  public CityGenerator()
  {
    super("gen-city");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(FakerUtil.faker().address().city());
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new CityGenerator(), args);
  }
}
