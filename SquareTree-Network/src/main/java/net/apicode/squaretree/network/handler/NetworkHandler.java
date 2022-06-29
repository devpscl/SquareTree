package net.apicode.squaretree.network.handler;

import net.apicode.squaretree.network.BridgeNetwork;
import net.apicode.squaretree.network.NetworkNode;

/**
 * The interface Network handler.
 */
public interface NetworkHandler {

  /**
   * <p style="color:red">Only at server</p>
   * Node connect before register event
   * <b>The node id is null!</b>
   *
   * @param networkNode the network node
   */
  void nodePreConnect(NetworkNode networkNode) throws Exception;

  /**
   * Node close event.
   *
   * @param networkNode the network node
   */
  void nodeClose(NetworkNode networkNode) throws Exception;

  /**
   * <p style="color:red">Only at server</p>
   * Node connect (register) event.
   * The node has an own node id
   *
   * @param networkNode the network node
   */
  void nodeConnect(NetworkNode networkNode) throws Exception;

  /**
   * Throw exception event.
   * handle all errors in network
   *
   * @param networkNode the network node
   * @param throwable   the throwable
   */
  void throwException(NetworkNode networkNode, Throwable throwable);

  /**
   * Network open event.
   *
   * @param network the network
   */
  void networkOpen(BridgeNetwork network) throws Exception;

  /**
   * Network close event.
   *
   * @param network the network
   */
  void networkClose(BridgeNetwork network) throws Exception;

}
