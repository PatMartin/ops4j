package org.ops4j.nodeop.gen;

import java.security.SecureRandom;
import java.util.Random;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.StringUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "random:text", mixinStandardHelpOptions = false,
    description = "Generate a first name." + "%n%nExample: random:text")
public class RandomTextGenerator extends BaseNodeOp<RandomTextGenerator>
{
  @Option(names = { "-min", "-min-length" }, required = false,
      description = "The minimum length of the random text.")
  private @Getter @Setter int    min     = 8;

  @Option(names = { "-max", "-max-length" }, required = false,
      description = "The maximum length of the random text.")
  private @Getter @Setter int    max     = 20;

  @Option(names = { "-charset" }, required = false,
      description = "The maximum length of the random text.")
  private @Getter
  @Setter String                 charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  private Random                 random  = null;

  public RandomTextGenerator()
  {
    name("random:text");
  }

  public RandomTextGenerator create()
  {
    return new RandomTextGenerator();
  }

  public Random random()
  {
    if (random == null)
    {
      random = new SecureRandom();
    }
    return random;
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    return new TextNode(
        StringUtil.randomString(getMin(), getMax(), getCharset()));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new RandomTextGenerator(), args);
  }
}
