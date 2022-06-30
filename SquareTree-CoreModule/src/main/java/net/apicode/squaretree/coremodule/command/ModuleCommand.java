package net.apicode.squaretree.coremodule.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.apicode.jbasedconsole.text.ConsoleColor;
import net.apicode.jbasedconsole.text.ConsoleStringBuilder;
import net.apicode.squaretree.coremodule.util.CommandUtil;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.api.terminal.Command;
import net.apicode.squaretree.server.api.terminal.Terminal;
import net.apicode.squaretree.server.module.ModuleContext;
import net.apicode.squaretree.server.module.ModuleException;
import net.apicode.squaretree.server.module.ModuleManager;

public class ModuleCommand implements Command {

  private final ModuleManager moduleManager = SquareTreeServer.getInstance().getModuleManager();

  @Override
  public List<String> getTabCompletions(String[] args) {
    if(args.length == 1) {
      return Arrays.asList("load", "unload", "status");
    } else if(args.length == 2) {
      if(args[0].equalsIgnoreCase("load")) {
        List<String> filenames = new ArrayList<>();
        for (File file : SquareTreeServer.getInstance().getModuleDirectory()
            .listFiles(pathname -> pathname.getName().endsWith(".jar"))) {
          filenames.add(file.getName());
        }
        return filenames;
      } else if(args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("status")) {
        return new ArrayList<>(SquareTreeServer.getInstance().getModuleManager().getModuleNames());
      }
    }
    return null;
  }

  @Override
  public void command(String[] args, Terminal terminal) {
    if(args.length <= 1) {
      printUsages("module load <filename>", "module unload <name>", "module status <name>");
    } else {
      String action = args[0];
      String module = args[1];
      if(action.equalsIgnoreCase("load")) {
        File file = new File(SquareTreeServer.getInstance().getModuleDirectory(), module);
        if(!file.exists()) {
          print("Module file not found", ConsoleColor.BRIGHT_RED);
          return;
        }
        if(isLoaded(file.getName())) {
          print("Module is already loaded", ConsoleColor.BRIGHT_RED);
          return;
        }
        try {
          ModuleContext moduleContext = moduleManager.registerModule(file);
          moduleManager.preStartModule(moduleContext);
          moduleManager.startModule(moduleContext);
          print("Module is loaded!");
        } catch (ModuleException e) {
          print(e.getMessage(), ConsoleColor.BRIGHT_RED);
        }
      } else if(action.equalsIgnoreCase("unload")) {
        ModuleContext moduleContext = moduleManager.getModuleContext(module);
        if(moduleContext == null) {
          print("Module is not loaded", ConsoleColor.BRIGHT_RED);
        } else {
          try {
            moduleManager.unregisterModule(moduleContext);
            terminal.writeLine("Module is unloaded!");
          } catch (ModuleException e) {
            print(e.getMessage(), ConsoleColor.BRIGHT_RED);
          }
        }
      } else if(action.equalsIgnoreCase("status")) {
        ModuleContext moduleContext = moduleManager.getModuleContext(module);
        if(moduleContext == null) {
          print("Module is not loaded!", ConsoleColor.from256Color(196));
        } else {
          print("Module is loaded", ConsoleColor.from256Color(10));
        }
      } else {
        printInvalidUsage();
      }
    }
  }

  private boolean isLoaded(String filename) {
    for (ModuleContext moduleContext : moduleManager.getModuleContexts()) {
      if (moduleContext.getFile().getName().equalsIgnoreCase(filename)) {
        return true;
      }
    }
    return false;
  }
}
