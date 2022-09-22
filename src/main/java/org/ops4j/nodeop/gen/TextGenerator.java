package org.ops4j.nodeop.gen;

import org.ops4j.BaseNodeOp;
import org.ops4j.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.FakerUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "gen:first", mixinStandardHelpOptions = false, description = "Generate a first name."
    + "%n%nExample: gen:text")
public class TextGenerator extends BaseNodeOp<TextGenerator>
{
  @Option(names = { "-p", "-pattern" }, required = false, description = "A "
      + "pattern which will be used to generate the text.%n%nExamples:%n%n"
      + "###-##-#### = Generate a SSN.%n"
      + "@@@@ ######       = Generate 4 letters, a space, then 6 digits")
  private @Getter @Setter String pattern = "????????";

  public TextGenerator()
  {
    name("gen:text");
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
