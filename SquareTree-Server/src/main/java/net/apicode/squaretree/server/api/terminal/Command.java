package net.apicode.squaretree.server.api.terminal;

public interface Command {

  void command(String[] args, Terminal terminal);

}
