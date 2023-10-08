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
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class)
@Command(name = "gen:address", mixinStandardHelpOptions = false,
    description = "Generate an address.")
public class AddressGenerator extends BaseNodeOp<AddressGenerator>
{
  public AddressGenerator()
  {
    super("gen:address");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    Address address = FakerUtil.faker().address();
    ObjectNode node = JacksonUtil.createObjectNode();
    node.set("street", new TextNode(address.streetAddress()));
    node.set("city", new TextNode(address.city()));
    node.set("zip", new TextNode(address.zipCode()));
    node.set("state", new TextNode(address.stateAbbr()));

    return node;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new AddressGenerator(), args);
  }
}
