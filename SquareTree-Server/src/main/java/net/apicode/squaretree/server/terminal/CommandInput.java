package net.apicode.squaretree.server.terminal;

import net.apicode.squaretree.server.util.StringUtil;

public class CommandInput {

  private String string;

  public CommandInput() {
    string = StringUtil.EMPTY_STRING;
  }

  public CommandInput(String string) {
    this.string = string;
  }

  public void append(char character) {
    string += character;
  }

  public void erase() {
    if(string.length() > 0) {
      string = string.substring(0, string.length()-1);
    }
  }

  public int getLength() {
    return string.length();
  }

  public void set(String string) {
    this.string = string;
  }

  public CommandInput clone() {
    return new CommandInput(string);
  }

  @Override
  public String toString() {
    return string;
  }

  public String getString() {
    return string;
  }
}
