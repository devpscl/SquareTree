package net.apicode.squaretree.network.codec.converter;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;
import net.apicode.squaretree.network.util.NodeId;

public class NodeIdConverter implements DataConverter<NodeId> {

  @Override
  public DataSerializer serialize(NodeId value) throws Exception {
    DataSerializer dataSerializer = new DataSerializer(128);
    dataSerializer.writeString(value.getSubName());
    dataSerializer.writeString(value.getName());
    return dataSerializer;
  }

  @Override
  public NodeId deserialize(DataDeserializer deserializer) throws Exception {
    String subName = deserializer.readString();
    String name = deserializer.readString();
    return new NodeId(name, subName);
  }
}
