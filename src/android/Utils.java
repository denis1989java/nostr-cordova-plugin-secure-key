package android;

import java.security.MessageDigest;

public class Utils {

  private final static MessageDigest sha256 = MessageDigest.getInstance("SHA-256");


  public ByteArray privkeyCreate (){
    ByteArray bytes = new ByteArray(32);
    random.nextBytes(bytes);
    return bytes
  }

  public pubkeyCreate(privKey: ByteArray) =
          secp256k1.pubKeyCompress(secp256k1.pubkeyCreate(privKey)).copyOfRange(1, 33)

}
