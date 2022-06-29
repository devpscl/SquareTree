package net.apicode.squaretree.network.packet;

import java.util.Collection;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.util.SecurityInfo;
import net.apicode.squaretree.network.util.function.DoubleConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Protocol manager.
 */
public interface ProtocolManager {

  /**
   * Add packet listener.
   *
   * @param packetReceiver the packet receiver
   */
  void addPacketListener(PacketReceiver<?> packetReceiver);

  /**
   * Remove packet listener.
   *
   * @param packetReceiver the packet receiver
   */
  void removePacketListener(PacketReceiver<?> packetReceiver);

  /**
   * Add simple packet listener
   *
   * @param <T>         the packet type parameter
   * @param consumer    the consumer listener of packet and network node
   * @param packetClass the packet class
   * @return the packet receiver
   */
  <T extends Packet<?>> PacketReceiver<T> addPacketListener(@NotNull DoubleConsumer<T, NetworkNode> consumer,
      @NotNull Class<T> packetClass);

  /**
   * Gets all registered packet listeners.
   *
   * @param packetClass the packet class
   * @return the packet listeners
   */
  Collection<PacketReceiver<?>> getPacketListeners(Class<? extends Packet<?>> packetClass);

  /**
   * Gets packet class of packet link id
   *
   * @param type the type
   * @return the packet class
   * @throws NetworkException the network exception
   */
  Class<? extends Packet> getPacketClass(int type) throws NetworkException;

  /**
   * Register packet.
   * Packet must have the packet link annotation with special type id
   * @see PacketLink
   *
   * @param packetClass the packet class
   * @throws NetworkException the network exception
   */
  void registerPacket(Class<? extends Packet> packetClass) throws NetworkException;

  /**
   * Gets packet link type.
   *
   * @param packetClass the packet class
   * @return the packet type
   * @throws NetworkException the network exception
   */
  int getPacketType(Class<? extends Packet> packetClass) throws NetworkException;

  /**
   * Is packet registered boolean.
   *
   * @param type the type
   * @return the boolean
   */
  boolean isPacketRegistered(int type);

  /**
   * Is packet registered.
   *
   * @param packetClass the packet class
   * @return the boolean
   */
  boolean isPacketRegistered(Class<? extends Packet> packetClass);

  /**
   * Gets security info.
   *
   * @return the security info
   */
  SecurityInfo getSecurityInfo();

  /**
   * Create packet packet.
   * The packet must be registered
   *
   * @param id the type id
   * @return the packet
   * @throws NetworkException the network exception
   */
  Packet<?> createPacket(int id) throws NetworkException;
}
