package net.apicode.squaretree.network.packet.response;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;

public class PingResponse implements Response<Long> {


  @Override
  public void serialize(DataSerializer serializer) {

  }

  @Override
  public void deserialize(DataDeserializer deserializer) {

  }

  @Override
  public Long getValue() {
    return null;
  }

  @Override
  public ResponseState getState() {
    return null;
  }

  @Override
  public void setState(ResponseState state) {

  }

  @Override
  public void setValue(Long value) {

  }
}
