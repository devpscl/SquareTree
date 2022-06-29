package net.apicode.squaretree.network;

import com.sun.javafx.collections.UnmodifiableListSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.apicode.squaretree.network.handler.NetworkHandler;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketLink;
import net.apicode.squaretree.network.packet.PacketQueue;
import net.apicode.squaretree.network.packet.ProtocolManager;
import net.apicode.squaretree.network.protocol.PacketNetworkPing;
import net.apicode.squaretree.network.protocol.PacketNetworkRegister;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.PrioritizedList;
import net.apicode.squaretree.network.util.SecurityInfo;
import net.apicode.squaretree.network.util.function.DoubleConsumer;
import net.apicode.squaretree.network.util.function.ExceptionConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The type Bridge network.
 */
public abstract class BridgeNetwork implements ProtocolManager, PacketAdapter {

  private final HashMap<Integer, Class<? extends Packet>> packets = new HashMap<>();
  private final HashMap<Class<? extends Packet>, Integer> packetLinkers = new HashMap<>();
  private final HashMap<Class<? extends Packet>, PrioritizedList<PacketReceiver<?>>> packetListeners = new HashMap<>();
  private final ConnectionInfo connectionInfo;
  private final SecurityInfo securityInfo;
  private final PrioritizedList<NetworkHandler> networkHandlers = new PrioritizedList<>();
  private final PacketQueue queue = new PacketQueue();

  /**
   * Instantiates a new Bridge network.
   *
   * @param connectionInfo the connection info
   * @param securityInfo   the security info
   * @throws NetworkException the network exception
   */
  public BridgeNetwork(@NotNull ConnectionInfo connectionInfo, @NotNull SecurityInfo securityInfo)
      throws NetworkException {
    this.connectionInfo = connectionInfo;
    this.securityInfo = securityInfo;
    registerPacket(PacketNetworkRegister.class);
    registerPacket(PacketNetworkPing.class);
  }

  /**
   * Gets network node of this instance.
   *
   * @return the network node
   */
  public abstract NetworkNode getNetworkNode();

  @Override
  public void addPacketListener(@NotNull PacketReceiver<?> packetReceiver) {
    PrioritizedList<PacketReceiver<?>> list;
    if(packetListeners.containsKey(packetReceiver.getPacketClass())) {
      list = packetListeners.get(packetReceiver.getPacketClass());
    } else list = new PrioritizedList<>();
    list.add(packetReceiver);
    packetListeners.put(packetReceiver.getPacketClass(), list);
  }

  @Override
  public void removePacketListener(@NotNull PacketReceiver<?> packetReceiver) {
    if(packetListeners.containsKey(packetReceiver.getPacketClass())) {
      PrioritizedList<PacketReceiver<?>> list = packetListeners.get(packetReceiver.getPacketClass());
      list.remove(packetReceiver);
      packetListeners.put(packetReceiver.getPacketClass(), list);
    }
  }

  @Override
  public Collection<PacketReceiver<?>> getPacketListeners(Class<? extends Packet<?>> packetClass) {
    if(packetListeners.containsKey(packetClass)) {
      PrioritizedList<PacketReceiver<?>> packetReceivers = packetListeners.get(packetClass);
      return Collections.unmodifiableCollection(packetReceivers);
    }
    return new UnmodifiableListSet<>(new ArrayList<>());
  }

  /**
   * Add network handler listener.
   *
   * @param networkHandler the network handler
   */
  public void addHandler(@NotNull NetworkHandler networkHandler) {
    networkHandlers.add(networkHandler);
  }

  /**
   * Remove network handler listener.
   *
   * @param networkHandler the network handler
   */
  public void removeHandler(@NotNull NetworkHandler networkHandler) {
    networkHandlers.remove(networkHandler);
  }

  /**
   * Execute foreach network handlers.
   *
   * @param handlerConsumer the handler consumer
   */
  public void foreachHandler(@NotNull ExceptionConsumer<NetworkHandler> handlerConsumer) {
    for (NetworkHandler networkHandler : networkHandlers) {
      try {
        handlerConsumer.accept(networkHandler);
      } catch (Throwable throwable) {
        networkHandlers.forEach(h -> networkHandler.throwException(null, throwable));
      }
    }
  }

  /**
   * Gets all registered network handlers.
   *
   * @return the handlers as unmodifiable collection
   */
  public @NotNull  Collection<NetworkHandler> getHandlers() {
    return Collections.unmodifiableCollection(networkHandlers);
  }

  public <T extends Packet<?>> PacketReceiver<T> addPacketListener(@NotNull DoubleConsumer<T, NetworkNode> consumer,
      @NotNull Class<T> packetClass) {
    PacketReceiver<T> receiver = new PacketReceiver<T>() {
      @Override
      public Class<T> getPacketClass() {
        return packetClass;
      }

      @Override
      public void receive(T packet, NetworkNode nodeId) {
        consumer.accept(packet, nodeId);
      }
    };
    addPacketListener(receiver);
    return receiver;
  }

  @Override
  public @NotNull  SecurityInfo getSecurityInfo() {
    return securityInfo;
  }

  public boolean isPacketRegistered(@NotNull Class<? extends Packet> packetClass) {
    return packets.containsValue(packetClass);
  }

  public boolean isPacketRegistered(int type) {
    return packets.containsKey(type);
  }

  public int getPacketType(@NotNull Class<? extends Packet> packetClass) throws NetworkException {
    if(packetLinkers.containsKey(packetClass)) {
      return packetLinkers.get(packetClass);
    }
    if(!packetClass.isAnnotationPresent(PacketLink.class)) {
      throw new NetworkException("Packet has no linked type (PacketLink annotation)");
    }
    PacketLink link = packetClass.getAnnotation(PacketLink.class);
    int type = link.type();
    packetLinkers.put(packetClass, type);
    return type;
  }

  @Override
  public Class<? extends Packet> getPacketClass(int type) throws NetworkException {
    if(!isPacketRegistered(type)) {
      throw new NetworkException("Packet type " + type + " is not defined");
    }
    return packets.get(type);
  }

  public void registerPacket(@NotNull Class<? extends Packet> packet) throws NetworkException {
    if(isPacketRegistered(getPacketType(packet))) {
      throw new NetworkException("Packet type is already defined");
    }
    packets.put(getPacketType(packet), packet);
  }

  @Override
  public Packet<?> createPacket(int id) throws NetworkException {
    Class<? extends Packet> packetClass = getPacketClass(id);
    try {
      return packetClass.newInstance();
    } catch (InstantiationException e) {
      throw new NetworkException("Failed to find empty public constructor", e);
    } catch (IllegalAccessException e) {
      throw new NetworkException("Failed to access constructor", e);
    }
  }

  @Override
  public PacketQueue getWaitingQueue() {
    return queue;
  }

  /**
   * Gets connection info.
   *
   * @return the connection info
   */
  public ConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }

}
