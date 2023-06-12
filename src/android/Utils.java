package com.nostr.band;

import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import fr.acinq.secp256k1.Secp256k1;
import kotlin.collections.ArraysKt;
import kotlin.text.Charsets;

public class Utils {

  private static final MessageDigest sha256;
  private static final Secp256k1 secp256k1;
  private static final SecureRandom random;

  static {
    MessageDigest var10000 = null;
    try {
      var10000 = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    sha256 = var10000;
    secp256k1 = Secp256k1.Companion.get();
    random = new SecureRandom();
  }

  public static byte[] privkeyCreate() {
    byte[] bytes = new byte[20];
    new Random().nextBytes(bytes);
    return bytes;
  }

  public static byte[] pubkeyCreate(byte[] privKey) {
    byte[] var2 = secp256k1.pubKeyCompress(secp256k1.pubkeyCreate(privKey));
    byte var3 = 1;
    byte var4 = 33;
    return java.util.Arrays.copyOfRange(var2, var3, var4);
  }

  public final byte[] sign(byte[] data, byte[] privKey) {
    return secp256k1.signSchnorr(data, privKey, null);
  }

  public final String encrypt(String msg, byte[] privateKey, byte[] pubKey) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
    byte[] sharedSecret = this.getSharedSecret(privateKey, pubKey);
    return this.encrypt(msg, sharedSecret);
  }

  public final String encrypt(String msg, byte[] sharedSecret) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
    byte[] iv = new byte[16];
    random.nextBytes(iv);
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(1, (Key) (new SecretKeySpec(sharedSecret, "AES")), (AlgorithmParameterSpec) (new IvParameterSpec(iv)));
    String ivBase64 = Base64.toBase64String(iv);
    Charset var8 = Charsets.UTF_8;
    byte[] var10001 = msg.getBytes(var8);
    byte[] encryptedMsg = cipher.doFinal(var10001);
    String encryptedMsgBase64 = Base64.toBase64String(encryptedMsg);
    return encryptedMsgBase64 + "?iv=" + ivBase64;
  }

  public final String decrypt(String msg, byte[] privateKey, byte[] pubKey) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
    byte[] sharedSecret = this.getSharedSecret(privateKey, pubKey);
    return this.decrypt(msg, sharedSecret);
  }

  public final String decrypt(String msg, byte[] sharedSecret) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
    String[] parts = msg.split("\\?iv=");
    byte[] var5 = Base64.decode(parts[1]);
    byte[] var6 = Base64.decode(parts[0]);
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(2, (Key) (new SecretKeySpec(sharedSecret, "AES")), (AlgorithmParameterSpec) (new IvParameterSpec(var5)));
    byte[] var10000 = cipher.doFinal(var6);
    byte[] var13 = var10000;
    return new String(var13, Charsets.UTF_8);
  }

  public final byte[] getSharedSecret(byte[] privateKey, byte[] pubKey) {
    Secp256k1 var10000 = secp256k1;
    byte[] var10001 = Hex.decode("02");
    byte[] var3 = var10000.pubKeyTweakMul(ArraysKt.plus(var10001, pubKey), privateKey);
    byte var4 = 1;
    byte var5 = 33;
    return java.util.Arrays.copyOfRange(var3, var4, var5);
  }

  public final byte[] sha256(byte[] byteArray) {
    byte[] var10000 = sha256.digest(byteArray);
    return var10000;
  }


}
