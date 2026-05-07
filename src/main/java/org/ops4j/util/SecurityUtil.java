package org.ops4j.util;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil
{
  public static SecretKey generateKey() throws NoSuchAlgorithmException
  {
    return generateKey(128);
  }

  public static SecretKey generateKey(int n) throws NoSuchAlgorithmException
  {
    return generateKey(n, "AES");
  }

  public static SecretKey generateKey(int n, String algorithm)
      throws NoSuchAlgorithmException
  {
    KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
    keyGenerator.init(n);
    SecretKey key = keyGenerator.generateKey();
    return key;
  }

  public static SecretKey getKeyFromPassword(String password, String salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException
  {
    SecretKeyFactory factory = SecretKeyFactory
        .getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),
        65536, 256);
    SecretKey secret = new SecretKeySpec(
        factory.generateSecret(spec).getEncoded(), "AES");
    return secret;
  }

  public static IvParameterSpec generateIv()
  {
    return generateIv(128);
  }

  public static IvParameterSpec generateIv(int bits)
  {
    byte[] iv = new byte[bits / 8];
    new SecureRandom().nextBytes(iv);
    return new IvParameterSpec(iv);
  }

  public static String encrypt(String algorithm, String input, SecretKey key,
      IvParameterSpec iv) throws NoSuchPaddingException,
      NoSuchAlgorithmException, InvalidAlgorithmParameterException,
      InvalidKeyException, BadPaddingException, IllegalBlockSizeException
  {

    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    byte[] cipherText = cipher.doFinal(input.getBytes());
    return Base64.getEncoder().encodeToString(cipherText);
  }

  public static String decrypt(String algorithm, String cipherText,
      SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException,
      NoSuchAlgorithmException, InvalidAlgorithmParameterException,
      InvalidKeyException, BadPaddingException, IllegalBlockSizeException
  {

    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.DECRYPT_MODE, key, iv);
    byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
    return new String(plainText);
  }

  public static SealedObject encryptObject(String algorithm,
      Serializable object, SecretKey key, IvParameterSpec iv)
      throws NoSuchPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException, IOException,
      IllegalBlockSizeException
  {

    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    SealedObject sealedObject = new SealedObject(object, cipher);
    return sealedObject;
  }

  public static Serializable decryptObject(String algorithm,
      SealedObject sealedObject, SecretKey key, IvParameterSpec iv)
      throws NoSuchPaddingException, NoSuchAlgorithmException,
      InvalidAlgorithmParameterException, InvalidKeyException,
      ClassNotFoundException, BadPaddingException, IllegalBlockSizeException,
      IOException
  {

    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.DECRYPT_MODE, key, iv);
    Serializable unsealObject = (Serializable) sealedObject.getObject(cipher);
    return unsealObject;
  }
}
