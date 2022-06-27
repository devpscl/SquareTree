package net.apicode.squaretree.network.protocol;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketLink;
import net.apicode.squaretree.network.packet.response.LongResponse;

@PacketLink(type = 1000)
public class PacketNetworkPing extends Packet<LongResponse> {

  private long timeAtSend = 0L;

  public long getTimeAtSend() {
    return timeAtSend;
  }

  @Override
  public void serialize(DataSerializer serializer) {
    serializer.writeLong(System.currentTimeMillis());
  }

  @Override
  public void deserialize(DataDeserializer deserializer) {
    timeAtSend = deserializer.readLong();
  }

  @Override
  public LongResponse getDefaultResponse() {
    return new LongResponse(0L);
  }
}
