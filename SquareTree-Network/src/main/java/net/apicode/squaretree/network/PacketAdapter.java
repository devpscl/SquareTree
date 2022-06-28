package net.apicode.squaretree.network;

import io.netty.channel.Channel;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketQueue;
import net.apicode.squaretree.network.util.NodeId;

public interface PacketAdapter {

  <T> T sendPacket(Packet<?> packet) throws NetworkException;

  <T> T sendPacket(Packet<?> packet, NodeId target) throws NetworkException;

  void sendPacket(Packet<?> packet, Channel channel) throws NetworkException;

  PacketQueue getWaitingQueue();


}
