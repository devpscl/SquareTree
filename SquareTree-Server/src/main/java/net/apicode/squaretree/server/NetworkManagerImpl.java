package net.apicode.squaretree.server;

import java.util.Collection;
import java.util.concurrent.Future;
import net.apicode.squaretree.network.BridgeServer;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;
import net.apicode.squaretree.network.util.function.DoubleConsumer;
import net.apicode.squaretree.server.api.NetworkManager;

class NetworkManagerImpl implements NetworkManager {

  private final BridgeServer bridgeServer;

  public NetworkManagerImpl(BridgeServer bridgeServer) {
    this.bridgeServer = bridgeServer;
  }

  @Override
  public <T extends Response<?>> T sendPacket(Packet<T> packet, NodeId nodeId) throws NetworkException {
    return bridgeServer.sendPacket(packet, nodeId);
  }

  @Override
  public <T extends Response<?>> Future<T> sendPacketAsync(Packet<T> packet, NodeId nodeId) {
    return bridgeServer.sendPacketAsync(packet, nodeId);
  }

  @Override
  public ConnectionInfo getConnectionInfo() {
    return bridgeServer.getConnectionInfo();
  }

  @Override
  public SecurityInfo getSecurityInfo() {
    return bridgeServer.getSecurityInfo();
  }

  @Override
  public <T extends Packet<?>> void addPacketListener(PacketReceiver<T> receiver) {
    bridgeServer.addPacketListener(receiver);
  }

  @Override
  public <T extends Packet<?>> void removePacketListener(PacketReceiver<T> receiver) {
    bridgeServer.removePacketListener(receiver);
  }

  @Override
  public <T extends Packet<?>> PacketReceiver<T> addPacketListener(DoubleConsumer<T, NodeId> receiver,
      Class<T> packetClass) {
    return bridgeServer.addPacketListener((a, b) -> receiver.accept(a, b.getId()), packetClass);
  }

  @Override
  public void closeNode(NodeId nodeId) {
    NetworkNode node = bridgeServer.getNode(nodeId);
    if(node != null) {
      node.getChannel().close();
    }
  }

  @Override
  public long ping(NodeId nodeId) throws NetworkException {
    return bridgeServer.ping(nodeId);
  }

  @Override
  public BridgeServer getHandle() {
    return bridgeServer;
  }

  @Override
  public Collection<NodeId> getNodeIds() {
    return bridgeServer.getNodeIds();
  }

  @Override
  public NodeId getServerId() {
    return bridgeServer.getNetworkNode().getId();
  }

  @Override
  public void registerPacket(Class<? extends Packet<?>> packet) throws NetworkException {
    bridgeServer.registerPacket(packet);
  }
}
