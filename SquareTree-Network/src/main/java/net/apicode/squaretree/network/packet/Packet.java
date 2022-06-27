package net.apicode.squaretree.network.packet;

import net.apicode.squaretree.network.codec.DataContainer;
import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.PacketUtil;

public abstract class Packet<T extends Response<?>> implements DataContainer {

  private String packetId = PacketUtil.PACKET_ID_GENERATOR.generateIdToString();
  private NodeId sender;
  private NodeId target;
  private PacketType packetType = PacketType.REQUEST;
  private final T response = getDefaultResponse();

  public Packet() {

  }

  public T getResponse() {
    return response;
  }

  public String getPacketId() {
    return packetId;
  }

  public NodeId getSenderNode() {
    return sender;
  }

  public NodeId getTargetNode() {
    return target;
  }

  public void setPacketId(String packetId) {
    this.packetId = packetId;
  }

  public void setNodeInformation(NodeId sender, NodeId target) {
    this.sender = sender;
    this.target = target;
  }

  public abstract void serialize(DataSerializer serializer);

  public abstract void deserialize(DataDeserializer deserializer);

  public abstract T getDefaultResponse();

  public PacketType getContainerType() {
    return packetType;
  }

  public void setContainerType(PacketType packetType) {
    this.packetType = packetType;
  }
}
