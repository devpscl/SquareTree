package net.apicode.squaretree.coremodule;

import net.apicode.squaretree.coremodule.command.ClearCommand;
import net.apicode.squaretree.coremodule.command.ModuleCommand;
import net.apicode.squaretree.coremodule.command.ModulesCommand;
import net.apicode.squaretree.coremodule.command.NodeCommand;
import net.apicode.squaretree.coremodule.command.NodeListCommand;
import net.apicode.squaretree.server.api.Module;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.api.terminal.Terminal;

public class CoreModule extends Module {

  @Override
  public void preStart() {
    Terminal terminal = SquareTreeServer.getInstance().getTerminal();
    terminal.registerCommand(new ModulesCommand(), "modules", "modl");
    terminal.registerCommand(new ModuleCommand(), "module", "mod");
    terminal.registerCommand(new NodeCommand(), "node", "n");
    terminal.registerCommand(new NodeListCommand(), "nodelist", "nodes");
    terminal.registerCommand(new ClearCommand(), "clear", "cls");
  }

  @Override
  public void start() {

  }

  @Override
  public String getName() {
    return "CoreModule";
  }
}
