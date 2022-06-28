package net.apicode.squaretree.network.packet.response;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;

public class EmptyResponse implements Response<Void>{

  private ResponseState state = ResponseState.TOTAL_ERROR;

  @Override
  public void serialize(DataSerializer serializer) {
    serializer.writeInt(state.getState());
  }

  @Override
  public void deserialize(DataDeserializer deserializer) {
    state = ResponseState.getState(deserializer.readInt());
  }

  @Override
  public Void getValue() {
    return null;
  }

  @Override
  public ResponseState getState() {
    return state;
  }

  @Override
  public void setState(ResponseState state) {
    this.state = state;
  }

  @Override
  public void setValue(Void value) {

  }
}
