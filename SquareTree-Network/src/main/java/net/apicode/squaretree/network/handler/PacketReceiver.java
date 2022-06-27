package net.apicode.squaretree.network.handler;

import net.apicode.squaretree.network.packet.Packet;

public interface PacketReceiver<T extends Packet<?>> {

  Class<T> getPacketClass();

  void receive(T packet);

}
