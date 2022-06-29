package net.apicode.squaretree.connector;

import net.apicode.squaretree.connector.handler.ConnectorHandler;
import net.apicode.squaretree.network.BridgeNetwork;
import net.apicode.squaretree.network.BridgeSocket;
import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.handler.NetworkHandler;

public class HandlerAdapter implements NetworkHandler {

  private final ConnectorHandler connectorHandler;

  public HandlerAdapter(ConnectorHandler connectorHandler) {
    this.connectorHandler = connectorHandler;
  }

  @Override
  public void nodePreConnect(NetworkNode networkNode) throws Exception {
    //ignored
  }

  @Override
  public void nodeClose(NetworkNode networkNode) throws Exception {
    //ignored
  }

  @Override
  public void nodeConnect(NetworkNode networkNode) throws Exception {
    //ignored
  }

  @Override
  public void throwException(NetworkNode networkNode, Throwable throwable) {
    connectorHandler.onError(throwable);
  }

  @Override
  public void networkOpen(BridgeNetwork network) throws Exception {
    connectorHandler.onConnect((BridgeSocket) network);
  }

  @Override
  public void networkClose(BridgeNetwork network) throws Exception {
    connectorHandler.onClose((BridgeSocket) network);
  }
}
