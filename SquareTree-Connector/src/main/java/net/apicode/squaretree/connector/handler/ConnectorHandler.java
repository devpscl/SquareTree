package net.apicode.squaretree.connector.handler;

import net.apicode.squaretree.connector.SquareTreeConnection;
import net.apicode.squaretree.network.BridgeSocket;

public interface ConnectorHandler {

  void onConnect(BridgeSocket socket);

  void onClose(BridgeSocket socket);

  void onError(Throwable throwable);

}
