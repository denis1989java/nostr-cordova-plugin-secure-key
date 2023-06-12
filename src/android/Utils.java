package com.nostr.band;

import java.util.Random;

public class Utils {

  public byte[] privkeyCreate() {
    byte[] bytes = new byte[20];
    new Random().nextBytes(bytes);
    return bytes;
  }
}
