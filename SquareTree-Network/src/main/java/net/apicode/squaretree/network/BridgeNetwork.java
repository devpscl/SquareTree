package net.apicode.squaretree.network;

import com.sun.javafx.collections.UnmodifiableListSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;
import net.apicode.squaretree.network.handler.NetworkHandler;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketLink;
import net.apicode.squaretree.network.packet.ProtocolManager;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.PrioritizedList;
import net.apicode.squaretree.network.util.SecurityInfo;
import org.jetbrains.annotations.NotNull;

public class BridgeNetwork implements ProtocolManager {

  private final HashMap<Integer, Class<? extends Packet>> packets = new HashMap<>();
  private final HashMap<Class<? extends Packet>, Integer> packetLinkers = new HashMap<>();
  private final HashMap<Class<? extends Packet>, PrioritizedList<PacketReceiver<?>>> packetListeners = new HashMap<>();
  private final ConnectionInfo connectionInfo;
  private final SecurityInfo securityInfo;
  private final PrioritizedList<NetworkHandler> networkHandlers = new PrioritizedList<>();

  public BridgeNetwork(@NotNull ConnectionInfo connectionInfo, @NotNull SecurityInfo securityInfo) {
    this.connectionInfo = connectionInfo;
    this.securityInfo = securityInfo;
  }

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
  public Collection<PacketReceiver<?>> getPacketListeners(@NotNull Class<? extends Packet<?>> packetClass) {
    if(packetListeners.containsKey(packetClass)) {
      PrioritizedList<PacketReceiver<?>> packetReceivers = packetListeners.get(packetClass);
      return Collections.unmodifiableCollection(packetReceivers);
    }
    return new UnmodifiableListSet<>(new ArrayList<>());
  }

  public void addHandler(@NotNull NetworkHandler networkHandler) {
    networkHandlers.add(networkHandler);
  }

  public void removeHandler(@NotNull NetworkHandler networkHandler) {
    networkHandlers.remove(networkHandler);
  }

  public void foreachHandler(@NotNull Consumer<NetworkHandler> handlerConsumer) {
    for (NetworkHandler networkHandler : networkHandlers) {
      try {
        handlerConsumer.accept(networkHandler);
      } catch (Throwable throwable) {
        networkHandlers.forEach(h -> networkHandler.throwException(null, throwable));
      }
    }
  }

  public @NotNull  Collection<NetworkHandler> getHandlers() {
    return Collections.unmodifiableCollection(networkHandlers);
  }

  public <T extends Packet<?>> PacketReceiver<T> addPacketListener(@NotNull Consumer<T> consumer,
      @NotNull Class<T> packetClass) {
    PacketReceiver<T> receiver = new PacketReceiver<T>() {
      @Override
      public Class<T> getPacketClass() {
        return packetClass;
      }

      @Override
      public void receive(T packet) {
        consumer.accept(packet);
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

  public ConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }

}
