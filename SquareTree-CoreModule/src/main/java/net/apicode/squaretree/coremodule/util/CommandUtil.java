package net.apicode.squaretree.coremodule.util;

import net.apicode.jbasedconsole.text.ConsoleStringBuilder;

public class CommandUtil {

  public static final String INVALID_COMMAND_USAGE = new ConsoleStringBuilder()
      .foreground(160)
      .append("Invalid command usage")
      .reset()
      .toConsoleString();

}
