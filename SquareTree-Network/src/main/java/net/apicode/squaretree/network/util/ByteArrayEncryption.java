package net.apicode.squaretree.network.util;

import java.nio.charset.StandardCharsets;

public class ByteArrayEncryption {

  public static byte[] xor(byte[] bytes, String key) {
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    byte[] output = new byte[bytes.length];

    for (int i = 0; i < bytes.length; i++) {
      output[i] = (byte) (bytes[i] ^ keyBytes[i % keyBytes.length]);
    }
    return output;
  }

}
