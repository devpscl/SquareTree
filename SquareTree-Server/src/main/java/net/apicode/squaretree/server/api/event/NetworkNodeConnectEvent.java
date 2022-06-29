package net.apicode.squaretree.server.api.event;

import net.apicode.squaretree.network.util.NodeId;

public class NetworkNodeConnectEvent extends Event {

  private final NodeId nodeId;

  public NetworkNodeConnectEvent(NodeId nodeId) {
    this.nodeId = nodeId;
  }

  public NodeId getNodeId() {
    return nodeId;
  }
}
