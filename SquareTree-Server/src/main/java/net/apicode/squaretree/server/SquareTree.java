package net.apicode.squaretree.server;

import java.io.File;
import net.apicode.squaretree.network.BridgeServer;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.SecurityInfo;
import net.apicode.squaretree.server.api.NetworkManager;
import net.apicode.squaretree.server.api.SquareTreeServer;
import net.apicode.squaretree.server.api.event.EventManager;
import net.apicode.squaretree.server.api.terminal.Terminal;
import net.apicode.squaretree.server.command.ExitCommand;
import net.apicode.squaretree.server.config.SettingConfiguration;
import net.apicode.squaretree.server.module.ModuleContext;
import net.apicode.squaretree.server.module.ModuleManager;
import net.apicode.squaretree.server.terminal.SquareTreeTerminal;
import net.apicode.squaretree.server.util.Logger;

class SquareTree extends SquareTreeServer {

  private final Logger logger;
  private final SquareTreeTerminal terminal;
  private final NetworkManagerImpl networkManager;
  private final ModuleManager moduleManager;
  private final EventManager eventManager;
  private final SettingConfiguration settingConfiguration;
  private final BridgeServer bridgeServer;
  private final File moduleDirectory = new File("modules");

  public SquareTree(Logger logger, SquareTreeTerminal terminal) throws NetworkException {
    if(!moduleDirectory.exists()) {
      moduleDirectory.mkdir();
    }
    this.logger = logger;
    this.terminal = terminal;
    terminal.writeLine("loading settings...");
    this.settingConfiguration = new SettingConfiguration();
    this.moduleManager = new ModuleManager();
    this.eventManager = new EventManager();
    ConnectionInfo connectionInfo = ConnectionInfo.builder()
        .setPort(settingConfiguration.getPort())
        .setAddress(settingConfiguration.getHostAddress());
    SecurityInfo securityInfo = new SecurityInfo(settingConfiguration.getPrivateKey(),
        settingConfiguration.getCryptoKey());
    this.bridgeServer = new BridgeServer(connectionInfo, securityInfo);
    this.networkManager = new NetworkManagerImpl(bridgeServer);
  }

  public void startup() {
    terminal.printInfo("registering modules...");
    for (File file : moduleDirectory.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".jar"))) {
      try {
        ModuleContext moduleContext = moduleManager.registerModule(file);
        moduleContext.getModule().preStart();
      } catch (Exception e) {
        terminal.printError("Failed to load " + file.getName(), e);
      }
    }
    terminal.printInfo("connecting to server ...");
    try {
      bridgeServer.addHandler(new DefaultNetworkHandler(this));
      bridgeServer.start();
      bridgeServer.scheduleAsync();
      terminal.printInfo("Server started at " + bridgeServer.getConnectionInfo().getNaming());
    } catch (NetworkException e) {
      terminal.printError("Failed to start server", e);
    }
    terminal.printInfo("starting modules...");
    for (ModuleContext moduleContext : moduleManager.getModuleContexts()) {
      moduleContext.getModule().start();
      terminal.printInfo(moduleContext.getModule().getName() + " is enable!");
    }
    terminal.registerCommand(new ExitCommand(this), "exit", "shutdown", "stop");
  }

  @Override
  public void shutdown() {
    bridgeServer.close();
    for (ModuleContext moduleContext : moduleManager.getModuleContexts()) {
      moduleContext.getModule().stop();
      terminal.printInfo(moduleContext.getModule().getName() + " is disabled!");
    }
    System.exit(1);
  }

  @Override
  public File getModuleDirectory() {
    return moduleDirectory;
  }

  public BridgeServer getBridgeServer() {
    return bridgeServer;
  }

  @Override
  public Logger getLogger() {
    return logger;
  }

  @Override
  public Terminal getTerminal() {
    return terminal;
  }

  @Override
  public NetworkManager getNetworkManager() {
    return networkManager;
  }

  @Override
  public ModuleManager getModuleManager() {
    return moduleManager;
  }

  @Override
  public EventManager getEventManager() {
    return eventManager;
  }

  @Override
  public SettingConfiguration getSettings() {
    return settingConfiguration;
  }
}
