package net.apicode.squaretree.network;

import io.netty.channel.Channel;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.util.NodeId;

public class NetworkNode {

  private final BridgeNetwork bridgeNetwork;
  private final Channel channel;
  private final boolean server;
  private NodeId id;

  public NetworkNode(BridgeNetwork bridgeNetwork, Channel channel, NodeId id) {
    this.bridgeNetwork = bridgeNetwork;
    this.channel = channel;
    this.server = (bridgeNetwork instanceof BridgeServer) && id != null;
  }

  public boolean isRegistered() {
    return id != null;
  }

  public void register(NodeId nodeId) throws NetworkException {
    if(isRegistered()) {
      throw new NetworkException("Node is already registered");
    }
    this.id = nodeId;
  }


  public <T extends Response<VT>, VT> T sendPacket(Packet<T> packet) throws NetworkException {
    if(server) {
      return bridgeNetwork.sendPacket(packet, id);
    }
    return sendPacket(packet);
  }
  public <T extends Response<VT>, VT> T sendPacket(Packet<T> packet, NodeId nodeId) throws NetworkException {
    if(server) {
      return sendPacket(packet);
    }
    return bridgeNetwork.sendPacket(packet, nodeId);
  }

  @Deprecated
  public void sendRawPacket(Packet<?> packet) throws NetworkException {
    bridgeNetwork.sendPacket(packet, channel);
  }

  public boolean isServerNode() {
    return server;
  }

  public Channel getChannel() {
    return channel;
  }

  public NodeId getId() {
    return id;
  }
}
