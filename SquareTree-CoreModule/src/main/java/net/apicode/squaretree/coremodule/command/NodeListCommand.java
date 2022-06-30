package net.apicode.squaretree.coremodule.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.apicode.jbasedconsole.text.ConsoleStringBuilder;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.api.terminal.Terminal;
import net.apicode.squaretree.server.module.ModuleContext;
import net.apicode.squaretree.server.terminal.SquareTreeTerminal;

public class NodeListCommand implements Command {


  @Override
  public void command(String[] args, Terminal terminal) throws Exception {
    Collection<NodeId> nodeIds = SquareTreeServer.getInstance().getNetworkManager().getNodeIds();
    List<String> list = new ArrayList<>();
    for (NodeId node : nodeIds) {
      list.add(node.toString());
    }
    terminal.writeLine("Nodes: " + new ConsoleStringBuilder()
        .foreground(131).append(String.join(", ", list)).reset());
  }
}
