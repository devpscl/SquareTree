package net.apicode.squaretree.server.api;

import java.util.Collection;
import java.util.concurrent.Future;
import net.apicode.squaretree.network.BridgeServer;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketLink;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;
import net.apicode.squaretree.network.util.function.DoubleConsumer;
import net.apicode.squaretree.server.api.terminal.Terminal;

/**
 * The interface Network manager.
 */
public interface NetworkManager {

  /**
   * Send packet to specific target.
   * If target no exists will be return a error response
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the response of packet
   */
  <T extends Response<?>> T sendPacket(Packet<T> packet, NodeId nodeId) throws NetworkException;

  /**
   * Send packet asynchronous to specific target.
   * If target no exists will be return error response
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the future of response packet
   */
  <T extends Response<?>> Future<T> sendPacketAsync(Packet<T> packet, NodeId nodeId) throws NetworkException;

  /**
   * Gets connection info.
   *
   * @return the connection info
   */
  ConnectionInfo getConnectionInfo();

  /**
   * Gets security info.
   *
   * @return the security info
   */
  SecurityInfo getSecurityInfo();

  /**
   * Add packet listener.
   *
   * @param <T>      the type parameter
   * @param receiver the receiver
   */
  <T extends Packet<?>> void addPacketListener(PacketReceiver<T> receiver);

  /**
   * Remove packet listener.
   *
   * @param <T>      the type parameter
   * @param receiver the receiver
   */
  <T extends Packet<?>> void removePacketListener(PacketReceiver<T> receiver);

  /**
   * Add simple packet listener.
   *
   * @param <T>         the type parameter
   * @param receiver    the receiver
   * @param packetClass the packet class
   * @return the packet receiver
   */
  <T extends Packet<?>> PacketReceiver<T> addPacketListener(DoubleConsumer<T, NodeId> receiver, Class<T> packetClass);

  /**
   * Close node.
   * Kick a node from server
   *
   * @param nodeId the node id
   */
  void closeNode(NodeId nodeId);

  /**
   * Check ping of sending and receiving packet to bridge socket
   *
   * @return the ping as long
   * @throws NetworkException the network exception
   */
  long ping(NodeId nodeId) throws NetworkException;

  /**
   * Gets network server.
   *
   * @return the handle
   */
  BridgeServer getHandle();

  /**
   * @return collection of node ids
   */
  Collection<NodeId> getNodeIds();

  /**
   * Gets current server id.
   *
   * @return the node id
   */
  NodeId getServerId();

  /**
   * Register packet.
   * Packet must have the packet link annotation with special type id
   * @see PacketLink
   *
   * @param packetClass the packet class
   * @throws NetworkException the network exception
   */
  void registerPacket(Class<? extends Packet<?>> packetClass) throws NetworkException;

  static NetworkManager getInstance() {
    return SquareTreeServer.getInstance().getNetworkManager();
  }

}
