package net.apicode.squaretree.server.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import net.apicode.jbasedconsole.platform.Console;
import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.util.StringUtil;

public class TabCompleter {

  private boolean tabbing = false;
  private final SquareTreeTerminal terminal;
  private List<String> completions = new ArrayList<>();
  private volatile int pointer = -1;

  TabCompleter(SquareTreeTerminal terminal) {
    this.terminal = terminal;
  }

  public synchronized boolean tab() {
    CommandInput currentInput = terminal.getCurrentInput();
    String[] array = currentInput.getString().split(" ");
    if(currentInput.getString().endsWith(" ")) {
      List<String> arguments = new ArrayList<>(Arrays.asList(array));
      arguments.add(StringUtil.EMPTY_STRING);
      array = arguments.toArray(new String[0]);
    }
    if(!tabbing) {
      completions = getCompletions(currentInput.getString());
      if(completions.isEmpty()) {
        return false;
      }
      tabbing = true;
    }
    if(pointer >= completions.size()-1) {
      pointer = -1;
    }
    pointer++;
    String suggest = completions.get(pointer);
    array[array.length-1] = suggest;
    terminal.setInput(String.join(" ", array));
    terminal.updateCommandLine();
    return true;
  }

  public void endTab() {
    tabbing = false;
    pointer = -1;
    completions = new ArrayList<>();
  }

  public boolean isTabbing() {
    return tabbing;
  }

  public List<String> getCompletions(String input) {
    String[] array = input.split(" ");
    if(input.endsWith(" ")) {
      List<String> arguments = new ArrayList<>(Arrays.asList(array));
      arguments.add(StringUtil.EMPTY_STRING);
      array = arguments.toArray(new String[0]);
    }
    SortedSet<String> list = new TreeSet<>();
    if(array.length == 1) {
      for (String cmd : terminal.getCommandMap().keySet()) {
        if(cmd.startsWith(array[0].toLowerCase().replace(' ', '#'))) {
          list.add(cmd);
        }
      }
    } else {
      if(terminal.getCommandMap().containsKey(array[0].toLowerCase())) {
        Command command = terminal.getCommandMap().get(array[0].toLowerCase());
        String[] args = Arrays.copyOfRange(array, 1, array.length);
        List<String> tabCompletions = command.getTabCompletions(args);
        if(tabCompletions == null) return new ArrayList<>();
        for (String tabCompletion : tabCompletions) {
          if(tabCompletion.startsWith(array[array.length-1].toLowerCase().replace(' ', '#'))) {
            list.add(tabCompletion);
          }
        }
      }
    }
    return new ArrayList<>(list);
  }



}
