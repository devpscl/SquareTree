package net.apicode.squaretree.coremodule.command;

import java.util.ArrayList;
import java.util.List;
import net.apicode.jbasedconsole.text.ConsoleStringBuilder;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.api.terminal.Terminal;
import net.apicode.squaretree.server.module.ModuleContext;

public class ModulesCommand implements Command {

  @Override
  public void command(String[] args, Terminal terminal) {
    List<String> list = new ArrayList<>();
    for (ModuleContext moduleContext : SquareTreeServer.getInstance().getModuleManager().getModuleContexts()) {
      list.add(moduleContext.getModule().getName());
    }
    terminal.writeLine("Modules: " + new ConsoleStringBuilder()
            .foreground(131).append(String.join(", ", list)).reset());
  }
}
