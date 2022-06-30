package net.apicode.squaretree.connector;

import net.apicode.squaretree.connector.handler.ConnectorHandler;
import net.apicode.squaretree.network.BridgeSocket;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;

public class SquareTreeConnector {

  private final ConnectionInfo connectionInfo;
  private final SecurityInfo securityInfo;
  private final NodeId nodeId;

  public SquareTreeConnector(ConnectionInfo connectionInfo, SecurityInfo securityInfo, NodeId nodeId) {
    this.connectionInfo = connectionInfo;
    this.securityInfo = securityInfo;
    this.nodeId = nodeId;
  }

  public NodeId getNodeId() {
    return nodeId;
  }

  public SecurityInfo getSecurityInfo() {
    return securityInfo;
  }

  public ConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }

  public SquareTreeConnection connect(ConnectorHandler connectorHandler) throws NetworkException {
    BridgeSocket socket;
    if(connectorHandler == null) {
      socket = new BridgeSocket(connectionInfo, securityInfo, nodeId);
    } else {
      socket = new BridgeSocket(connectionInfo, securityInfo, nodeId, new HandlerAdapter(connectorHandler));
    }
    return new SquareTreeConnection(socket);
  }
}
