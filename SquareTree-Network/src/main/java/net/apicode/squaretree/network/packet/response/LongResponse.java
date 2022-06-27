package net.apicode.squaretree.network.packet.response;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;

public class LongResponse implements Response<Long> {

  private long value;
  private ResponseState responseState = ResponseState.TOTAL_ERROR;

  public LongResponse(long value) {
    this.value = value;
  }

  public ResponseState getState() {
    return responseState;
  }

  @Override
  public void setState(ResponseState responseState) {
    this.responseState = responseState;
  }

  @Override
  public Long getValue() {
    return value;
  }

  @Override
  public void setValue(Long value) {
    this.value = value;
  }

  @Override
  public void serialize(DataSerializer serializer) {
    serializer.writeLong(value);
    serializer.writeInt(responseState.getState());
  }

  @Override
  public void deserialize(DataDeserializer deserializer) {
    value = deserializer.readLong();
    responseState = ResponseState.getState(deserializer.readInt());
  }
}
