package net.apicode.squaretree.network.packet;

import net.apicode.squaretree.network.codec.DataContainer;
import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.PacketUtil;

/**
 * The packet must be use as inheritance class and must have a Packet link
 * @see PacketLink required
 *
 * @param <T> the response type parameter
 */
public abstract class Packet<T extends Response<?>> implements DataContainer {

  private String packetId = PacketUtil.PACKET_ID_GENERATOR.generateIdToString();
  private NodeId sender;
  private NodeId target;
  private PacketType packetType = PacketType.REQUEST;
  private final T response = getDefaultResponse();

  /**
   * Default empty constructor of packet
   */
  public Packet() {

  }

  /**
   * Gets response
   *
   * @return the response
   */
  public T getResponse() {
    return response;
  }

  /**
   * Gets packet id.
   *
   * @return the packet id
   */
  public String getPacketId() {
    return packetId;
  }

  /**
   * Gets sender node.
   *
   * @return the sender node
   */
  public NodeId getSenderNode() {
    return sender;
  }

  /**
   * Gets target node.
   *
   * @return the target node
   */
  public NodeId getTargetNode() {
    return target;
  }

  /**
   * Sets packet id.
   *
   * @param packetId the packet id
   */
  public void setPacketId(String packetId) {
    this.packetId = packetId;
  }

  /**
   * Sets sender and target of packet.
   *
   * @param sender the sender
   * @param target the target
   */
  public void setNodeInformation(NodeId sender, NodeId target) {
    this.sender = sender;
    this.target = target;
  }

  public abstract void serialize(DataSerializer serializer);

  public abstract void deserialize(DataDeserializer deserializer);

  /**
   * Gets default response.
   *
   * @return the default response
   */
  public abstract T getDefaultResponse();

  /**
   * Gets container type.
   *
   * @return the container type
   */
  public PacketType getContainerType() {
    return packetType;
  }

  /**
   * Sets container type.
   *
   * @param packetType the packet type
   */
  public void setContainerType(PacketType packetType) {
    this.packetType = packetType;
  }
}
