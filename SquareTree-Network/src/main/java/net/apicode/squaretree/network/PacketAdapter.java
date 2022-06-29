package net.apicode.squaretree.network;

import io.netty.channel.Channel;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketQueue;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.util.NodeId;

public interface PacketAdapter {

  /**
   * Send packet.
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the response of packet
   */
  <T extends Response<?>> T sendPacket(Packet<T> packet) throws NetworkException;

  /**
   * Send packet to specific target.
   * If target no exists will be return a error response
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the response of packet
   */
  <T extends Response<?>> T sendPacket(Packet<T> packet, NodeId target) throws NetworkException;

  /**
   * Send packet to channel without standard packet setup
   * The packet must have sender and target
   *
   * @param packet the packet
   * @param channel the target channel
   */
  void sendPacket(Packet<?> packet, Channel channel) throws NetworkException;

  /**
   * The packetqueue contains the waiting packets
   *
   * @return PacketQueue
   */
  PacketQueue getWaitingQueue();


}
