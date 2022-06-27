package net.apicode.squaretree.network.packet;

public enum PacketType {

  REQUEST(1),
  RESPONSE(2);

  private final int id;

  PacketType(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public static PacketType getType(int id) {
    if(id == 1) return REQUEST;
    if(id == 2) return RESPONSE;
    throw new IllegalArgumentException("Invalid packet type " + id);
  }
}
