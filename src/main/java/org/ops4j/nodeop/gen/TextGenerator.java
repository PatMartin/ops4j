package org.ops4j.nodeop.gen;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.FakerUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "gen-text", mixinStandardHelpOptions = false,
    description = "Generate text." + "%n%nExample: gen-text")
public class TextGenerator extends BaseNodeOp<TextGenerator>
{
  @Option(names = { "-p", "-pattern" }, required = false,
      description = "A "
          + "pattern which will be used to generate the text.%n%nExamples:%n%n"
          + "###-##-#### = Generate a SSN.%n"
          + "@@@@ ######       = Generate 4 letters, a space, then 6 digits")
  private @Getter @Setter String pattern = "????????";

  public TextGenerator()
  {
    super("gen-text");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    // System.out.println("REPLACED: '" + getPattern() + "' WITH '"
    // + getPattern().replace('@', '?') + "'");
    return new TextNode(
        FakerUtil.faker().bothify(getPattern().replace('@', '?')));
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new TextGenerator(), args);
  }
}
