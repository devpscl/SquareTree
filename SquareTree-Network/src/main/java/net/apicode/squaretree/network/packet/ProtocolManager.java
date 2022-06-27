package net.apicode.squaretree.network.packet;

import java.util.Collection;
import java.util.function.Consumer;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.util.SecurityInfo;

public interface ProtocolManager {

  void addPacketListener(PacketReceiver<?> packetReceiver);

  void removePacketListener(PacketReceiver<?> packetReceiver);

  <T extends Packet<?>> PacketReceiver<T> addPacketListener(Consumer<T> consumer, Class<T> packetClass);

  Collection<PacketReceiver<?>> getPacketListeners(Class<? extends Packet<?>> packetClass);


  Class<? extends Packet> getPacketClass(int type) throws NetworkException;

  void registerPacket(Class<? extends Packet> packetClass) throws NetworkException;

  int getPacketType(Class<? extends Packet> packetClass) throws NetworkException;

  boolean isPacketRegistered(int type);

  boolean isPacketRegistered(Class<? extends Packet> packetClass);

  SecurityInfo getSecurityInfo();

  Packet<?> createPacket(int id) throws NetworkException;
}
