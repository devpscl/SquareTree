package net.apicode.squaretree.network.protocol;

import net.apicode.squaretree.network.codec.DataDeserializer;
import net.apicode.squaretree.network.codec.DataSerializer;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketLink;
import net.apicode.squaretree.network.packet.response.RegisterResponse;

@PacketLink(type = 1001)
public class PacketNetworkRegister extends Packet<RegisterResponse> {

  private String privateKey;

  public PacketNetworkRegister() {

  }

  public PacketNetworkRegister(String privateKey) {
    this.privateKey = privateKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  @Override
  public void serialize(DataSerializer serializer) {
    serializer.writeString(privateKey);
  }

  @Override
  public void deserialize(DataDeserializer deserializer) {
    privateKey = deserializer.readString();
  }

  @Override
  public RegisterResponse getDefaultResponse() {
    return new RegisterResponse();
  }
}
