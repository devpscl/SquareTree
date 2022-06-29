package net.apicode.squaretree.server.api.event;

import net.apicode.squaretree.network.NetworkNode;

public class NetworkNodePreConnectEvent extends Event {

  private final NetworkNode node;

  public NetworkNodePreConnectEvent(NetworkNode node) {
    this.node = node;
  }

  public NetworkNode getNode() {
    return node;
  }
}
