package net.apicode.squaretree.network.codec.converter;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;

public interface DataConverter<T> {

  DataSerializer serialize(T value) throws Exception;

  T deserialize(DataDeserializer deserializer) throws Exception;

}
