package net.apicode.squaretree.network.handler;

import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.util.NodeId;

public interface PacketReceiver<T extends Packet<?>> {

  Class<T> getPacketClass();

  void receive(T packet, NetworkNode nodeId);

  default void input(Packet<?> packet, NetworkNode nodeId) {
    if(!getPacketClass().isInstance(packet)) return;
    T receivingPacket = (T) packet;
    receive(receivingPacket, nodeId);
  }
}
