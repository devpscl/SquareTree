package net.apicode.squaretree.network.codec.converter;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;
import net.apicode.squaretree.network.util.NodeId;

public class NodeIdConverter implements DataConverter<NodeId> {

  @Override
  public DataSerializer serialize(NodeId value) throws Exception {
    DataSerializer dataSerializer = new DataSerializer(128);
    dataSerializer.writeString(value.getSessionId());
    dataSerializer.writeString(value.getName());
    return dataSerializer;
  }

  @Override
  public NodeId deserialize(DataDeserializer deserializer) throws Exception {
    String sessionId = deserializer.readString();
    String signature = deserializer.readString();
    return new NodeId(sessionId, signature);
  }
}
