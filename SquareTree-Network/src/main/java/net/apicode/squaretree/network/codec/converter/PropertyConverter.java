package net.apicode.squaretree.network.codec.converter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;

public class PropertyConverter implements DataConverter<Map<String, String>>{

  @Override
  public DataSerializer serialize(Map<String, String> value) {
    int size = value.size();
    DataSerializer serializer = new DataSerializer(size*64);
    serializer.writeInt(size);
    for (Entry<String, String> entry : value.entrySet()) {
      serializer.writeString(entry.getKey(), StandardCharsets.UTF_8);
      serializer.writeString(entry.getValue(), StandardCharsets.UTF_8);
    }
    return serializer;
  }

  @Override
  public Map<String, String> deserialize(DataDeserializer deserializer) {
    int size = deserializer.readInt();
    Map<String, String> map = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      String key = deserializer.readString(StandardCharsets.UTF_8);
      String value = deserializer.readString(StandardCharsets.UTF_8);
      map.put(key, value);
    }
    return map;
  }
}
