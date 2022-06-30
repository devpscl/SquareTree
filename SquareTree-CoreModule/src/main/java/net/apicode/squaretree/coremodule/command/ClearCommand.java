package net.apicode.squaretree.coremodule.command;

import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.api.terminal.Terminal;

public class ClearCommand implements Command {

  @Override
  public void command(String[] args, Terminal terminal) throws Exception {
    terminal.clear();
  }
}
