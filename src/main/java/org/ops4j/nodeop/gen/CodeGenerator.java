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
import com.github.javafaker.Code;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(NodeOp.class) @Command(name = "gen-code",
    mixinStandardHelpOptions = false, description = "Generate codes.")
public class CodeGenerator extends BaseNodeOp<CodeGenerator>
{
  public CodeGenerator()
  {
    super("gen-code");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    Code code = FakerUtil.faker().code();
    ObjectNode node = JacksonUtil.createObjectNode();
    node.set("asin", new TextNode(code.asin()));
    node.set("ean13", new TextNode(code.ean13()));
    node.set("ean8", new TextNode(code.ean8()));
    node.set("gtin13", new TextNode(code.gtin13()));
    node.set("gtin8", new TextNode(code.gtin8()));
    node.set("imei", new TextNode(code.imei()));

    node.set("isbn10", new TextNode(code.isbn10()));
    node.set("isbn13", new TextNode(code.isbn13()));

    return node;
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new CodeGenerator(), args);
  }
}
