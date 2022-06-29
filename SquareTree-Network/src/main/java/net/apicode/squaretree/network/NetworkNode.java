package net.apicode.squaretree.network;

import io.netty.channel.Channel;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.response.PingResponse;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.protocol.PacketNetworkPing;
import net.apicode.squaretree.network.util.NodeId;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * The network node
 */
public class NetworkNode {

  private final BridgeNetwork bridgeNetwork;
  private final Channel channel;
  private final boolean server;
  private NodeId id;

  /**
   * Instantiates a new Network node.
   *
   * @param bridgeNetwork the bridge network
   * @param channel       the channel
   * @param id            the id
   */
  @Internal
  public NetworkNode(BridgeNetwork bridgeNetwork, Channel channel, NodeId id) {
    this.bridgeNetwork = bridgeNetwork;
    this.channel = channel;
    this.server = (bridgeNetwork instanceof BridgeServer) && id != null;
    this.id = id;
  }

  /**
   * Is the nodeid by register packet registered
   *
   * @return the boolean if id set
   */
  public boolean isRegistered() {
    return id != null;
  }

  /**
   * Register node id.
   *
   * @param nodeId the node id
   * @throws NetworkException the network exception
   */
  public void register(NodeId nodeId) throws NetworkException {
    if(isRegistered()) {
      throw new NetworkException("Node is already registered");
    }
    this.id = nodeId;
  }

  /**
   * Check ping of sending and receiving packet to target network
   *
   * @return the ping as long
   * @throws NetworkException the network exception
   */
  public long ping() throws NetworkException {
    if(bridgeNetwork instanceof BridgeServer) {
      return ((BridgeServer) bridgeNetwork).ping(id);
    }
    if(bridgeNetwork instanceof BridgeSocket) {
      return ((BridgeSocket) bridgeNetwork).ping();
    }
    throw new UnsupportedOperationException();
  }


  /**
   * Send packet via network.
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the response of packet
   * @throws NetworkException the network exception
   */
  public <T extends Response<?>> T sendPacket(Packet<T> packet) throws NetworkException {
    return bridgeNetwork.sendPacket(packet, id);
  }

  /**
   * Send packet to target node id.
   *
   * @param <T>    the response type
   * @param packet the packet
   * @param nodeId the node id
   * @return the response of packet
   * @throws NetworkException the network exception
   */
  public <T extends Response<?>> T sendPacket(Packet<T> packet, NodeId nodeId) throws NetworkException {
    if(server) {
      return sendPacket(packet);
    }
    return bridgeNetwork.sendPacket(packet, nodeId);
  }

  /**
   * Send raw packet.
   *
   * @param packet the packet
   * @throws NetworkException the network exception
   */
  @Deprecated
  public void sendRawPacket(Packet<?> packet) throws NetworkException {
    bridgeNetwork.sendPacket(packet, channel);
  }

  /**
   * Is node equals to main server node.
   *
   * @return the boolean
   */
  public boolean isServerNode() {
    return server;
  }

  /**
   * Gets channel of node.
   *
   * @return the channel
   */
  public Channel getChannel() {
    return channel;
  }

  /**
   * Gets node id.
   * Node must be registered
   *
   * @return the id
   */
  public NodeId getId() {
    return id;
  }
}
