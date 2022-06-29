package net.apicode.squaretree.server.api.event;

import net.apicode.squaretree.network.BridgeNetwork;
import net.apicode.squaretree.network.BridgeServer;

public class NetworkOpenEvent extends Event {

  private final BridgeServer server;

  public NetworkOpenEvent(BridgeServer server) {
    this.server = server;
  }

  public BridgeServer getServer() {
    return server;
  }
}
