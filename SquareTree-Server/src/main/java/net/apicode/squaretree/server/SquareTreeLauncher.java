package net.apicode.squaretree.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import net.apicode.jbasedconsole.platform.Console;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.terminal.SquareTreeTerminal;
import net.apicode.squaretree.server.util.Logger;
import net.apicode.squaretree.server.util.StringUtil;
import org.jetbrains.annotations.Nullable;

public class SquareTreeLauncher {

  private final SquareTreeTerminal terminal;
  private final Logger logger;

  SquareTreeLauncher() throws IOException {
    logger = new Logger(new File("logs"));
    terminal = new SquareTreeTerminal(logger, Console.getConsole());
    initErrorOutput();
    load(terminal);
    terminal.enableCommandInput();
  }

  private void initErrorOutput() {
    System.setErr(new PrintStream(System.err) {
      @Override
      public void println(@Nullable Object x) {
        if(x instanceof Throwable) {
          terminal.printError(StringUtil.toString((Throwable) x));
        } else if(x instanceof String) {
          terminal.printError((String) x);
        }
      }
    });
  }

  private void load(SquareTreeTerminal terminal) {
    terminal.writeLine("\n"
        + " $$$$$$\\                                                 $$$$$$$$\\                            \n"
        + "$$  __$$\\                                                \\__$$  __|                           \n"
        + "$$ /  \\__| $$$$$$\\  $$\\   $$\\  $$$$$$\\   $$$$$$\\   $$$$$$\\  $$ | $$$$$$\\   $$$$$$\\   $$$$$$\\  \n"
        + "\\$$$$$$\\  $$  __$$\\ $$ |  $$ | \\____$$\\ $$  __$$\\ $$  __$$\\ $$ |$$  __$$\\ $$  __$$\\ $$  __$$\\ \n"
        + " \\____$$\\ $$ /  $$ |$$ |  $$ | $$$$$$$ |$$ |  \\__|$$$$$$$$ |$$ |$$ |  \\__|$$$$$$$$ |$$$$$$$$ |\n"
        + "$$\\   $$ |$$ |  $$ |$$ |  $$ |$$  __$$ |$$ |      $$   ____|$$ |$$ |      $$   ____|$$   ____|\n"
        + "\\$$$$$$  |\\$$$$$$$ |\\$$$$$$  |\\$$$$$$$ |$$ |      \\$$$$$$$\\ $$ |$$ |      \\$$$$$$$\\ \\$$$$$$$\\ \n"
        + " \\______/  \\____$$ | \\______/  \\_______|\\__|       \\_______|\\__|\\__|       \\_______| \\_______|\n"
        + "                $$ |                                                                          \n"
        + "                $$ |                                                                          \n"
        + "                \\__|                                                                          \n\n");
    SquareTree squareTree;
    try {
      squareTree = new SquareTree(logger, terminal);
      SquareTreeServer.setInstance(squareTree);
      squareTree.startup();
    } catch (NetworkException e) {
      e.printStackTrace();
    }

  }

}
