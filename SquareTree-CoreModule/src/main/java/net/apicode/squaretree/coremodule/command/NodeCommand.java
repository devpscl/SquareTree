package net.apicode.squaretree.coremodule.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.apicode.jbasedconsole.text.ConsoleColor;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.api.terminal.Terminal;

public class NodeCommand implements Command {

  @Override
  public List<String> getTabCompletions(String[] args) {
    if(args.length == 1) {
      List<String> list = new ArrayList<>();
      for (NodeId nodeId : SquareTreeServer.getInstance().getNetworkManager().getNodeIds()) {
        list.add(nodeId.toString());
      }
      return list;
    }
    if(args.length == 2) {
      return Arrays.asList("disconnect", "ping");
    }
    return null;
  }

  @Override
  public void command(String[] args, Terminal terminal) throws NetworkException {
    if(args.length <= 1) {
      printUsages("node <name:subname> disconnect", "node <name:subname> ping");
    } else {
      String name = args[0];
      String action = args[1];
      NodeId nodeId = parse(name);
      if(nodeId == null) {
        print("Invalid nodeid (name:subname)", ConsoleColor.BRIGHT_RED);
        return;
      }
      NetworkNode node = getNode(nodeId);
      if(node == null) {
        print("Node is not found", ConsoleColor.BRIGHT_RED);
        return;
      }
      if(action.equalsIgnoreCase("disconnect")) {
        node.getChannel().close();
        print("Node is closed!");
      } else if(action.equalsIgnoreCase("ping")) {
        print("ping: " + node.ping());
      }
    }
  }

  public NodeId parse(String s) {
    String[] split = s.split(":");
    if(split.length == 1) return null;
    return new NodeId(split[0], String.join(":", Arrays.copyOfRange(split, 1, split.length)));
  }

  public NetworkNode getNode(NodeId nodeId) {
    return SquareTreeServer.getInstance().getNetworkManager().getHandle().getNode(nodeId);
  }
}
