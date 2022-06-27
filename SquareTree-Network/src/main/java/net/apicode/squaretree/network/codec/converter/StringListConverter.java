package net.apicode.squaretree.network.codec.converter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;

public class StringListConverter implements DataConverter<List<String>> {

  @Override
  public DataSerializer serialize(List<String> value) {
    int size = value.size();
    DataSerializer serializer = new DataSerializer(size*32);
    serializer.writeInt(size);
    for (String s : value) {
      serializer.writeString(s, StandardCharsets.UTF_8);
    }
    return serializer;
  }

  @Override
  public List<String> deserialize(DataDeserializer deserializer) {
    int size = deserializer.readInt();
    List<String> list = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      list.add(deserializer.readString(StandardCharsets.UTF_8));
    }
    return list;
  }
}
