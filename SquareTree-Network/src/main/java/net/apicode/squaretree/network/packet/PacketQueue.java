package net.apicode.squaretree.network.packet;

import java.util.HashMap;
import net.apicode.squaretree.network.NetworkException;

public class PacketQueue {

  private volatile HashMap<String, Entry> map = new HashMap<>();

  public void forwardPacket(Packet<?> packet) {
    if(isQueueWaiting(packet.getPacketId())) {
      Entry queueEntry = getQueueEntry(packet.getPacketId());
      queueEntry.setResult(packet);
    }
  }

  public boolean isQueueWaiting(String packetId) {
    return map.containsKey(packetId);
  }

  public Entry getQueueEntry(String packetId) {
    return map.get(packetId);
  }

  public void openEntry(String packetId) {
    Entry entry = new Entry();
    map.put(packetId, entry);
  }

  public synchronized Packet<?> waitOfPacket(String packetId) throws NetworkException {
    if(!isQueueWaiting(packetId)) throw new NetworkException("No entry is open");
    Entry entry = getQueueEntry(packetId);
    entry.waitOfPacket();
    Packet<?> result = entry.getResult();
    map.remove(packetId);
    return result;
  }

  public static class Entry {

    private Packet<?> result = null;
    private boolean waiting = false;

    public Packet<?> getResult() {
      return result;
    }

    public synchronized void setResult(Packet<?> result) {
      this.result = result;
      if(!waiting) {
        try {
          wait();
        } catch (InterruptedException e) {
          throw new IllegalStateException(e);
        }
      }
      notifyAll();
    }

    public boolean isReceived() {
      return result != null;
    }

    public boolean isWaiting() {
      return waiting;
    }

    public synchronized Packet<?> waitOfPacket() throws NetworkException {
      waiting = true;
      notifyAll();
      try {
        while (!isReceived()) {
          wait();
        }
      } catch (InterruptedException e) {
        throw new NetworkException("Failed to wait of packet", e);
      }
      return result;
    }

  }

}
