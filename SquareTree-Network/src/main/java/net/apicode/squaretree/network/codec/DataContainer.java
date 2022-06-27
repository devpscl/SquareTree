package net.apicode.squaretree.network.codec;

public interface DataContainer {

  default int getBufferCapacity() {
    return 512;
  }

  void serialize(DataSerializer serializer);

  void deserialize(DataDeserializer deserializer);

}
