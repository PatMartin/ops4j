package org.ops4j.nodeop;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.SecurityUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "gen:key", mixinStandardHelpOptions = false,
    description = "Create a text node with optional interpolation.")
public class GenKey extends BaseNodeOp<GenKey>
{
  @Option(names = { "-size" }, required = false,
      description = "The size of the key to generate.  "
          + "(Default=${DEFAULT-VALUE})")
  private @Getter @Setter Integer keySize   = null;

  @Option(names = { "-algorithm" }, required = false,
      description = "The cipher algorithm.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter String  algorithm = null;

  public GenKey()
  {
    super("gen:key");
    setDefaultView("DEFAULT.ENCRYPTION");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    try
    {
      DEBUG("key-size=", fallback(getKeySize(), config().getInt("keySize")),
          ", algorithm='",
          fallback(getAlgorithm(), config().getString("algorithm")), "'");
      SecretKey key = SecurityUtil.generateKey(
          fallback(getKeySize(), config().getInt("keySize")),
          fallback(getAlgorithm(), config().getString("algorithm")));
      return new TextNode(Base64.getEncoder().encodeToString(key.getEncoded()));
    }
    catch(NoSuchAlgorithmException ex)
    {
      return NullNode.getInstance();
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new GenKey(), args);
  }
}
