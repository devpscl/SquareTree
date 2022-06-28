package net.apicode.squaretree.network;

import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.apicode.squaretree.network.util.NodeId;

public class NodeMap {

  private final HashMap<NodeId, NetworkNode> networkNodeHashMap = new HashMap<>();
  private final HashMap<String, NodeId> nodeIdHashMap = new HashMap<>();
  private final HashMap<Channel, NetworkNode> channelNetworkNodeHashMap = new HashMap<>();
  private final List<NetworkNode> nodes = new ArrayList<>();

  public void addNode(NetworkNode networkNode) {
    channelNetworkNodeHashMap.put(networkNode.getChannel(), networkNode);
    nodes.add(networkNode);
  }

  public void registerNode(NetworkNode networkNode) {
    if(networkNode == null) throw new IllegalStateException("NodeId is not defined");
    String name = networkNode.getId().getName();
    nodeIdHashMap.put(name, networkNode.getId());
    networkNodeHashMap.put(networkNode.getId(), networkNode);
  }

  public void removeNodeId(NodeId nodeId) {
    nodeIdHashMap.remove(nodeId.getName());
    networkNodeHashMap.remove(nodeId);
  }

  public void removeNode(NetworkNode networkNode) {
    if(networkNode.getId() != null) {
      removeNodeId(networkNode.getId());
    }
    channelNetworkNodeHashMap.remove(networkNode.getChannel());
    nodes.remove(networkNode);
  }

  public NetworkNode getNode(NodeId nodeId) {
    if(networkNodeHashMap.containsKey(nodeId)) {
      return networkNodeHashMap.get(nodeId);
    }
    return null;
  }

  public NetworkNode getNode(Channel nodeId) {
    if(channelNetworkNodeHashMap.containsKey(nodeId)) {
      return channelNetworkNodeHashMap.get(nodeId);
    }
    return null;
  }

  public NodeId getNodeIdByName(String name) {
    if(nodeIdHashMap.containsKey(name)) {
      return nodeIdHashMap.get(name);
    }
    return null;
  }

  public Collection<NetworkNode> getNodes() {
    return Collections.unmodifiableCollection(nodes);
  }

  public Collection<NetworkNode> getRegisteredNodes() {
    return Collections.unmodifiableCollection(networkNodeHashMap.values());
  }


}
