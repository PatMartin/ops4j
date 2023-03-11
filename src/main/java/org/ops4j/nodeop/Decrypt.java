package org.ops4j.nodeop;

import java.io.IOException;
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
import org.ops4j.log.OpLogger;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(NodeOp.class)
@Command(name = "decrypt", mixinStandardHelpOptions = false,
    description = "Create a text node with optional interpolation.")
public class Decrypt extends BaseNodeOp<Decrypt>
{

  @Option(names = { "-algorithm" }, required = false, defaultValue = "AES",
      description = "The cipher algorithm.  (Default=${DEFAULT-VALUE})")
  private @Getter @Setter Ops4J.AlgorithmType algorithm = null;

  private Cipher                              cipher;

  public Decrypt() throws OpsException
  {
    super("decrypt");
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

      cipher.init(Cipher.DECRYPT_MODE, k);
    }
    catch(NoSuchAlgorithmException | InvalidKeyException
        | NoSuchPaddingException ex)
    {
      throw new OpsException(ex);
    }
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    //OpLogger.syserr("DECRYPTING: " + getTarget(input));
    JsonNode target = getTarget(input);
    if (target.isTextual())
    {
      String text = getTarget(input).asText();
      //OpLogger.syserr("TEXT: ", text);
      try
      {
        //OpLogger.syserr("DECODED-TEXT: ",
        //    new String(Base64.getDecoder().decode(text)));
        return JacksonUtil.mapper()
            .readTree(cipher.doFinal(Base64.getDecoder().decode(text)));
      }
      catch(IllegalBlockSizeException | BadPaddingException | IOException ex)
      {
        ex.printStackTrace();
      }
    }
    return NullNode.getInstance();
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Decrypt(), args);
  }
}
