package net.apicode.squaretree.network.util;

public class PacketUtil {

  public static final IdGenerator PACKET_ID_GENERATOR = IdGenerator.builder()
      .withLowercaseCharacters()
      .withUppercaseCharacters()
      .withNumericCharacters()
      .withSymbolsCharacters()
      .length(8)
      .build();

}
