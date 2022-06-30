package net.apicode.squaretree.server.command;

import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.api.terminal.Terminal;

public class ExitCommand implements Command {

  private final SquareTreeServer squareTree;

  public ExitCommand(SquareTreeServer squareTree) {
    this.squareTree = squareTree;
  }

  @Override
  public void command(String[] args, Terminal terminal) {
    terminal.writeLine("shutdown...");
    squareTree.shutdown();
  }

}
