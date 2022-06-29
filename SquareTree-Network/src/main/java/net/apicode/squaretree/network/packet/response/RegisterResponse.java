package net.apicode.squaretree.network.packet.response;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;
import net.apicode.squaretree.network.packet.response.RegisterResponse.Result;

public class RegisterResponse implements Response<Result> {

  private ResponseState responseState = ResponseState.TOTAL_ERROR;
  private Result result = Result.ERROR;

  @Override
  public void serialize(DataSerializer serializer) {
    serializer.writeInt(responseState.getState());
    serializer.writeByte(result.getId());
  }

  @Override
  public void deserialize(DataDeserializer deserializer) {
    responseState = ResponseState.getState(deserializer.readInt());
    result = Result.getResult(deserializer.readByte());
  }

  @Override
  public Result getValue() {
    return result;
  }

  @Override
  public ResponseState getState() {
    return responseState;
  }

  @Override
  public void setState(ResponseState state) {
    this.responseState = state;
  }

  @Override
  public void setValue(Result value) {
    this.result = value;
  }

  public enum Result {

    SUCCESSFUL(0x0),
    INVALID_PRIVATE_KEY(0x1),
    NODE_ALREADY_LOADED(0x2),
    ERROR(0x3);

    private final int value;

    Result(int value) {
      this.value = value;
    }

    public byte getId() {
      return (byte) value;
    }

    public static Result getResult(byte b) {
      for (Result value : values()) {
        if(value.getId() == b) return value;
      }
      return ERROR;
    }

  }

}
