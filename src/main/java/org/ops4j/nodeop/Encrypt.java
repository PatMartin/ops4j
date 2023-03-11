package org.ops4j.nodeop;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.ops4j.Ops4J;
import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "encrypt", mixinStandardHelpOptions = false,
    description = "Create a text node with optional interpolation.")
public class Encrypt extends BaseNodeOp<Encrypt>
{

  @Option(names = { "-size" }, required = false, defaultValue = "128",
      description = "The size of the key to generate.  "
          + "(Default=${DEFAULT-VALUE})")
  private @Getter @Setter int                 keySize   = 128;

  @Option(names = { "-algorithm" }, required = false, defaultValue = "AES",
      description = "The cipher algorithm.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter Ops4J.AlgorithmType algorithm = null;

  private Cipher                              cipher;

  public Encrypt() throws OpsException
  {
    super("encrypt");
    setDefaultView("DEFAULT.ENCRYPTION");
    byte[] key = Base64.getDecoder().decode(config().getString("key"));
    String alg;
    if (getAlgorithm() != null)
    {
      alg = "" + getAlgorithm();
    }
    else
    {
      alg = config().getString("algorithm");
    }
    try
    {
      cipher = Cipher.getInstance(alg);
      SecretKeySpec k = new SecretKeySpec(key, alg);

      cipher.init(Cipher.ENCRYPT_MODE, k);
    }
    catch(NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException ex)
    {
      throw new OpsException(ex);
    }

    // now send encryptedData to Bob...
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    try
    {
      return new TextNode(Base64.getEncoder().encodeToString(
          cipher.doFinal(JacksonUtil.toString(getTarget(input)).getBytes())));
    }
    catch(IllegalBlockSizeException | BadPaddingException | OpsException ex)
    {
      return NullNode.getInstance();
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Encrypt(), args);
  }
}
