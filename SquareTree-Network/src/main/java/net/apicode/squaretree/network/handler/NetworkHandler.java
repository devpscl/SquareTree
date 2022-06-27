package net.apicode.squaretree.network.handler;

import net.apicode.squaretree.network.BridgeNetwork;
import net.apicode.squaretree.network.NetworkNode;

public interface NetworkHandler {

  void nodePreConnect(NetworkNode networkNode);

  void nodeClose(NetworkNode networkNode);

  void nodeConnect(NetworkNode networkNode);

  void throwException(NetworkNode networkNode, Throwable throwable);

  void networkOpen(BridgeNetwork network);

  void networkClose(BridgeNetwork network);

}
