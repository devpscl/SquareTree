package net.apicode.squaretree.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.converter.NodeIdConverter;
import net.apicode.squaretree.network.util.NodeId;

public class PacketDecoder extends ByteToMessageDecoder {

  private final ProtocolManager protocolManager;

  public PacketDecoder(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  /*
   * 0 packetid
   * 1 packet type
   * 2 container type
   * 3 sender
   * 4 target
   * 5 data
   * 6 bool (has response)
   *!7 response (nullable)
   * */

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
      throws Exception {
    NodeIdConverter nodeIdConverter = new NodeIdConverter();

    int size = byteBuf.readInt();
    byte[] bytes = new byte[size];
    byteBuf.readBytes(bytes);

    DataDeserializer deserializer = new DataDeserializer(bytes);

    String packetId = deserializer.readString();
    int packetType = deserializer.readInt();
    int containerTypeId = deserializer.readInt();

    Packet packet = protocolManager.createPacket(packetType);

    PacketType containerType = PacketType.getType(containerTypeId);
    NodeId sender = deserializer.readData(nodeIdConverter);
    NodeId target = deserializer.readData(nodeIdConverter);

    packet.deserialize(deserializer.readContainer());

    packet.setContainerType(containerType);
    packet.setNodeInformation(sender, target);
    packet.setPacketId(packetId);

    boolean hasResponse = deserializer.readBoolean();
    if(hasResponse && packet.getResponse() != null) {
      packet.getResponse().deserialize(deserializer);
    }
    list.add(packet);
  }
}
