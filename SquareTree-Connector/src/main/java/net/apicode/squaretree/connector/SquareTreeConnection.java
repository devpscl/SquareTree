package net.apicode.squaretree.connector;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import net.apicode.squaretree.network.BridgeSocket;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketLink;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;

/**
 * The type Square tree connection.
 */
public class SquareTreeConnection {

  private final BridgeSocket socket;

  /**
   * Instantiates a new Square tree connection.
   *
   * @param socket the socket
   */
  public SquareTreeConnection(BridgeSocket socket) {
    this.socket = socket;
  }

  /**
   * Gets connection info.
   *
   * @return the connection info
   */
  public ConnectionInfo getConnectionInfo() {
    return socket.getConnectionInfo();
  }

  /**
   * Gets security info.
   *
   * @return the security info
   */
  public SecurityInfo getSecurityInfo() {
    return socket.getSecurityInfo();
  }

  /**
   * Gets node id.
   *
   * @return the node id
   */
  public NodeId getNodeId() {
    return socket.getNetworkNode().getId();
  }

  /**
   * Send packet to server.
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the response of packet
   * @throws NetworkException the network exception
   */
  public <T extends Response<?>> T sendPacket(Packet<T> packet) throws NetworkException {
    return socket.sendPacket(packet);
  }

  /**
   * Send packet to specific target. If target no exists will be return a error response
   *
   * @param <T>    the response type
   * @param packet the packet
   * @param nodeId the node id
   * @return the response of packet
   * @throws NetworkException the network exception
   */
  public <T extends Response<?>> T sendPacket(Packet<T> packet, NodeId nodeId) throws NetworkException {
    return socket.sendPacket(packet, nodeId);
  }

  /**
   * Send packet asynchronous to server.
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the future of response packet
   * @throws NetworkException the network exception
   */
  public <T extends Response<?>> Future<T> sendPacketAsync(Packet<T> packet) throws NetworkException {
    return socket.sendPacketAsync(packet);
  }

  /**
   * Send packet asynchronous to specific target. If target no exists will be return error response
   *
   * @param <T>    the response type
   * @param packet the packet
   * @param nodeId the node id
   * @return the future of response packet
   * @throws NetworkException the network exception
   */
  public <T extends Response<?>> Future<T> sendPacketAsync(Packet<T> packet, NodeId nodeId) throws NetworkException {
    return socket.sendPacketAsync(packet, nodeId);
  }

  /**
   * Add packet listener.
   *
   * @param <T>      the type parameter
   * @param receiver the receiver
   */
  public <T extends Packet<?>> void addPacketListener(PacketReceiver<T> receiver) {
    socket.addPacketListener(receiver);
  }

  /**
   * Remove packet listener.
   *
   * @param <T>      the type parameter
   * @param receiver the receiver
   */
  public <T extends Packet<?>> void removePacketListener(PacketReceiver<T> receiver) {
    socket.removePacketListener(receiver);
  }

  /**
   * Add simple packet listener.
   *
   * @param <T>         the type parameter
   * @param receiver    the receiver
   * @param packetClass the packet class
   * @return the packet receiver
   */
  public <T extends Packet<?>> PacketReceiver<T> addPacketListener(Consumer<T> receiver, Class<T> packetClass) {
    return socket.addPacketListener((a, b) -> receiver.accept(a), packetClass);
  }

  /**
   * Register packet.
   * Packet must have the packet link annotation with special type id
   * @see PacketLink
   *
   * @param packetClass the packet class
   * @throws NetworkException the network exception
   */
  public void registerPacket(Class<? extends Packet<?>> packetClass) throws NetworkException {
    socket.registerPacket(packetClass);
  }

  /**
   * Close connection from server.
   */
  public void close() {
    socket.close();
  }

  /**
   * Check ping of sending and receiving packet to bridge server
   *
   * @return the ping as long
   * @throws NetworkException the network exception
   */
  public long ping() throws NetworkException {
    return socket.ping();
  }

  /**
   * Gets network socket.
   *
   * @return the handle
   */
  public BridgeSocket getHandle() {
    return socket;
  }
}
