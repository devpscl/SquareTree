package net.apicode.squaretree.network.util;

import java.util.function.Consumer;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketType;
import net.apicode.squaretree.network.packet.response.Response;

public class PacketUtil {

  public static final IdGenerator PACKET_ID_GENERATOR = IdGenerator.builder()
      .withLowercaseCharacters()
      .withUppercaseCharacters()
      .withNumericCharacters()
      .withSymbolsCharacters()
      .length(8)
      .build();

  public static void createCallback(NetworkNode networkNode, Packet<?> packet) throws NetworkException {
    packet.setNodeInformation(packet.getTargetNode(), packet.getSenderNode());
    packet.setContainerType(PacketType.RESPONSE);
    networkNode.sendRawPacket(packet);
  }

  public static <T extends Response<?>> void createCallback(NetworkNode networkNode, Packet<T> packet,
      Consumer<T> consumer) throws NetworkException {
    consumer.accept(packet.getResponse());
    createCallback(networkNode, packet);
  }

  public static <T extends Response<?>> void createCallbackIfRequest(NetworkNode networkNode, Packet<T> packet,
      Consumer<T> consumer) throws NetworkException {
    if(packet.getContainerType() != PacketType.REQUEST) return;
    consumer.accept(packet.getResponse());
    createCallback(networkNode, packet);
  }

}
