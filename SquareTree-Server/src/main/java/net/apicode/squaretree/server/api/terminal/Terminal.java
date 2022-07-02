package net.apicode.squaretree.server.api.terminal;

import java.util.List;
import java.util.function.Consumer;
import net.apicode.jbasedconsole.platform.Console;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.terminal.CommandInput;

public interface Terminal {

  void writeLine(String data);

  void printInfo(String message);

  void printWarning(String message);

  void printError(String message);

  void printError(String message, Throwable throwable);

  void clear();

  void registerCommand(Command command, String...commandPrefixes);

  void unregisterCommand(String command);

  Command getCommand(String command);

  CommandInput getCurrentInput();

  List<CommandInput> getHistory();

  void executeTask(Consumer<Console> consoleTask);

  boolean dispatchCommand(String input);

  void updateCommandLine();

  boolean isCommandLineAvailable();

  static Terminal getInstance() {
    return SquareTreeServer.getInstance().getTerminal();
  }

}
