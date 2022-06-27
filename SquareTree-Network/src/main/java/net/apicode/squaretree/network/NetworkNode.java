package net.apicode.squaretree.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketType;
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

  public <T extends Response<VT>, VT> T sendPacket(Packet<T> packet, NodeId nodeId) {
    packet.setContainerType(PacketType.REQUEST);
    packet.setNodeInformation(id, nodeId);
    channel.writeAndFlush(packet);
    return null;
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
