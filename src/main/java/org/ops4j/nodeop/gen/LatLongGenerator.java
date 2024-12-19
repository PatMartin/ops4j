package org.ops4j.nodeop.gen;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.FakerUtil;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Address;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class) @Command(name = "gen-lat-long",
    mixinStandardHelpOptions = false, description = "Generate an address.")
public class LatLongGenerator extends BaseNodeOp<LatLongGenerator>
{
  public LatLongGenerator()
  {
    super("gen-lat-long");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    Address address = FakerUtil.faker().address();
    ObjectNode node = JacksonUtil.createObjectNode();
    node.put("latitude", address.latitude());
    node.put("longitude", address.longitude());
    return node;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new LatLongGenerator(), args);
  }
}
