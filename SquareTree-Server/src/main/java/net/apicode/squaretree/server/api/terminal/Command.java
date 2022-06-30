package net.apicode.squaretree.server.api.terminal;

import java.util.ArrayList;
import java.util.List;
import net.apicode.jbasedconsole.text.ConsoleDecoration;
import net.apicode.jbasedconsole.text.ConsoleStringBuilder;
import net.apicode.jbasedconsole.text.ForegroundColor;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.terminal.SquareTreeTerminal;

public interface Command {

  void command(String[] args, Terminal terminal) throws Exception;


  default List<String> getTabCompletions(String[] args) {
    return new ArrayList<>();
  }

  default void printError(Throwable throwable) {
    SquareTreeServer.getInstance().getTerminal()
        .printError("Error at command " + getClass().getName(), throwable);
  }

  default void print(String message) {
    SquareTreeServer.getInstance().getTerminal().writeLine(message);
  }

  default void print(String message, ForegroundColor foregroundColor) {
    SquareTreeServer.getInstance().getTerminal().writeLine(
        foregroundColor.getFGColor() + message + ConsoleDecoration.RESET);
  }

  default void printf(String message, Object...objects) {
    SquareTreeServer.getInstance().getTerminal().writeLine(String.format(message, objects));
  }

  default void printUsages(String...usages) {
    if(usages.length == 0) {
      print("No usages.");
    } else {
      print("Usages:");
      for (String usage : usages) {
        print("  " + usage);
      }
    }
  }

  default void printInvalidUsage() {
    SquareTreeServer.getInstance().getTerminal()
        .writeLine(new ConsoleStringBuilder()
            .foreground(196)
            .append("Invalid command usage")
            .toConsoleString());
  }

}
