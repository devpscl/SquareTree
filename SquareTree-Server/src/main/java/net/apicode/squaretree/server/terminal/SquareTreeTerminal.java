package net.apicode.squaretree.server.terminal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import net.apicode.jbasedconsole.platform.Console;
import net.apicode.jbasedconsole.platform.input.ConsoleInput;
import net.apicode.jbasedconsole.platform.input.InputReader;
import net.apicode.jbasedconsole.platform.input.InputType;
import net.apicode.jbasedconsole.system.SystemInformation;
import net.apicode.jbasedconsole.text.ConsoleStringBuilder;
import net.apicode.jbasedconsole.text.ConsoleWritable;
import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.api.terminal.Terminal;
import net.apicode.squaretree.server.util.Logger;
import net.apicode.squaretree.server.util.StringUtil;

public class SquareTreeTerminal implements Terminal {

  private static final SimpleDateFormat terminalDateFormat = new SimpleDateFormat("HH:mm:ss");

  private final Console console;
  private final HashMap<String, Command> commandHashMap = new HashMap<>();
  private final List<CommandInput> history = new ArrayList<>();
  private final CommandInput input = new CommandInput();
  private int historyPointer = 0;
  private boolean commandInputAvailable = false;
  private boolean consoleInUse = false;
  private final InputReader inputReader;
  private final String prefix;
  private final Logger logger;

  public SquareTreeTerminal(Logger logger, Console console) {
    this.console = console;
    this.logger = logger;
    console.setTitle("SquareTree");
    inputReader = new InputReader(this::handleInput);
    SystemInformation current = SystemInformation.current();
    prefix = new ConsoleStringBuilder()
        .foreground(76)
        .append(current.getUserName())
        .append("@sqt")
        .foreground(69)
        .append("~ ")
        .reset()
        .toConsoleString();

  }

  public void enableCommandInput() {
    commandInputAvailable = true;
    updateCommandLine();
  }

  public InputReader getInputReader() {
    return inputReader;
  }

  private void handleInput(ConsoleInput consoleInput) {
    if(!isCommandLineAvailable()) return;
    InputType type = consoleInput.getType();
    if(type == InputType.LETTER || type == InputType.NORMAL_SYMBOL || type == InputType.NUMBER) {
      input(consoleInput.getInput());
    } else if(type == InputType.KEY_SPACE) {
      input(' ');
    } else if(type == InputType.KEY_DELETE) {
      erase();
    } else if(type == InputType.KEY_ARROW_UP) {
      up();
    } else if(type == InputType.KEY_ARROW_DOWN) {
      down();
    } else if(type == InputType.KEY_ENTER) {
      if(!input.getString().trim().isEmpty()) {
        commandInputAvailable = false;
        CommandInput commandInput = input.clone();
        if(historyPointer < history.size()) {
          commandInput = history.get(historyPointer);
        }
        history.add(commandInput.clone());
        historyPointer = history.size();
        input.set(StringUtil.EMPTY_STRING);
        boolean state = dispatchCommand(commandInput.getString());
        if(!state) {
          printInfo("Command not found.");
        }
        commandInputAvailable = true;
        updateCommandLine();
      }
    }
  }

  private void up() {
    if(historyPointer > 0) {
      historyPointer--;
    }
    updateCommandLine();
  }

  private void down() {
    if(historyPointer < history.size()) {
      historyPointer++;
    }
    updateCommandLine();
  }

  private void input(char c) {
    if(historyPointer >= history.size()) {
      input.append(c);
      updateCommandLine();
    } else {
      input.set(history.get(historyPointer).toString());
      historyPointer = history.size();
      input(c);
    }
  }

  private void erase() {
    if(historyPointer >= history.size()) {
      input.erase();
      updateCommandLine();
    } else {
      input.set(history.get(historyPointer).toString());
      historyPointer = history.size();
      erase();
    }
  }

  public String getPrefix(String state) {
    return new ConsoleStringBuilder()
        .append("[")
        .append(terminalDateFormat.format(new Date()))
        .append(" ")
        .append(state)
        .reset()
        .append("]: ")
        .toConsoleString();
  }

  @Override
  public synchronized void writeLine(String data) {
    console.clearLine();
    console.moveCursorLeft(9999);
    console.writeLine(data);
    updateCommandLine();
  }

  @Override
  public void printInfo(String message) {
    if(consoleInUse) return;
    writeLine(getPrefix(new ConsoleStringBuilder()
        .foreground(14)
        .append("INFO")
        .reset()
        .toConsoleString()) + message);
    logger.logInfo(ConsoleWritable.convertToPlainString(message));
  }

  @Override
  public void printWarning(String message) {
    if(consoleInUse) return;
    writeLine(getPrefix(new ConsoleStringBuilder()
        .foreground(226)
        .append("WARN")
        .reset()
        .toConsoleString()) + message);
    logger.logWarning(ConsoleWritable.convertToPlainString(message));
  }

  @Override
  public void printError(String message) {
    writeLine(getPrefix(new ConsoleStringBuilder()
        .foreground(160)
        .append("ERROR")
        .reset()
        .toConsoleString()) + message);
    logger.logError(ConsoleWritable.convertToPlainString(message));
  }

  @Override
  public void printError(String message, Throwable throwable) {
    printError(message);
    printError(StringUtil.toString(throwable));
  }

  @Override
  public void clear() {
    console.systemClear();
    updateCommandLine();
  }

  @Override
  public void registerCommand(Command command, String... commandPrefixes) {
    for (String commandPrefix : commandPrefixes) {
      commandHashMap.put(commandPrefix.toLowerCase(), command);
    }
  }

  @Override
  public void unregisterCommand(String command) {
    commandHashMap.remove(command.toLowerCase());
  }

  @Override
  public Command getCommand(String command) {
    if(commandHashMap.containsKey(command.toLowerCase())) {
      return commandHashMap.get(command.toLowerCase());
    }
    return null;
  }

  @Override
  public CommandInput getCurrentInput() {
    return input;
  }

  @Override
  public List<CommandInput> getHistory() {
    return history;
  }

  @Override
  public void executeTask(Consumer<Console> consoleTask) {
    consoleInUse = true;
    console.clearLine();
    consoleTask.accept(console);
    consoleInUse = false;
  }

  @Override
  public void updateCommandLine() {
    if(isCommandLineAvailable()) {
      CommandInput cmdInput = input;
      if(historyPointer < history.size()) {
        cmdInput = history.get(historyPointer);
      }
      console.clearLine();
      console.moveCursorLeft(9999);
      console.write(prefix);
      console.write(cmdInput.getString());
    }
  }

  @Override
  public boolean isCommandLineAvailable() {
    return commandInputAvailable && !consoleInUse;
  }

  @Override
  public boolean dispatchCommand(String input) {
    if(input.trim().isEmpty()) return false;
    logger.log(input, "COMMAND");
    String[] array = input.split(" ");
    String cmd = array[0];
    Command command = getCommand(cmd);
    if(command == null) {
      return false;
    }
    command.command(Arrays.copyOfRange(array, 1, array.length), this);
    return true;
  }
}
