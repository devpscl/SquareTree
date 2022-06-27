package net.apicode.squaretree.network.packet.response;

import net.apicode.squaretree.network.codec.DataContainer;
import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;

public interface Response<T> extends DataContainer {

  T getValue();

  ResponseState getState();

  void setState(ResponseState state);

  void setValue(T value);

}
