package net.apicode.squaretree.server.api.event;

import net.apicode.squaretree.network.util.NodeId;

public class NetworkNodeCloseEvent extends Event {

  private final NodeId nodeId;

  public NetworkNodeCloseEvent(NodeId nodeId) {
    this.nodeId = nodeId;
  }
}
