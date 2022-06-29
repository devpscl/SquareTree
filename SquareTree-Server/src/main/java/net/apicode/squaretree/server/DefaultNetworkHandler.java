package net.apicode.squaretree.server;

import net.apicode.squaretree.network.BridgeNetwork;
import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.handler.NetworkHandler;
import net.apicode.squaretree.server.api.event.NetworkNodeCloseEvent;
import net.apicode.squaretree.server.api.event.NetworkNodeConnectEvent;
import net.apicode.squaretree.server.api.event.NetworkNodePreConnectEvent;
import net.apicode.squaretree.server.api.event.NetworkOpenEvent;

public class DefaultNetworkHandler implements NetworkHandler {

  private final SquareTree squareTree;

  public DefaultNetworkHandler(SquareTree squareTree) {
    this.squareTree = squareTree;
  }

  @Override
  public void nodePreConnect(NetworkNode networkNode) throws Exception {
    squareTree.getEventManager().call(new NetworkNodePreConnectEvent(networkNode));
  }

  @Override
  public void nodeClose(NetworkNode networkNode) throws Exception {
    if(networkNode.isRegistered()) {
      squareTree.getEventManager().call(new NetworkNodeCloseEvent(networkNode.getId()));
      squareTree.getTerminal().printInfo("Disconnect: " + networkNode.getId());
    }
  }

  @Override
  public void nodeConnect(NetworkNode networkNode) throws Exception {
    squareTree.getEventManager().call(new NetworkNodeConnectEvent(networkNode.getId()));
    squareTree.getTerminal().printInfo("Connect: " + networkNode.getId());
  }

  @Override
  public void throwException(NetworkNode networkNode, Throwable throwable) {
    String id;
    if(networkNode.isRegistered()) {
      id = networkNode.getId().toString();
    } else {
      id = "!" + networkNode.getChannel().id().asShortText();
    }
    squareTree.getTerminal().printError("Error at " + id, throwable);
  }

  @Override
  public void networkOpen(BridgeNetwork network) throws Exception {
    squareTree.getEventManager().call(new NetworkOpenEvent(squareTree.getBridgeServer()));
  }

  @Override
  public void networkClose(BridgeNetwork network) throws Exception {
    squareTree.getTerminal().printWarning("Server is closed");
  }
}
