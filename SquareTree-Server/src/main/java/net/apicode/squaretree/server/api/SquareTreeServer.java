package net.apicode.squaretree.server.api;

import java.io.File;
import net.apicode.squaretree.server.api.event.EventManager;
import net.apicode.squaretree.server.api.terminal.Terminal;
import net.apicode.squaretree.server.config.ConfigurationFile;
import net.apicode.squaretree.server.config.SettingConfiguration;
import net.apicode.squaretree.server.module.ModuleManager;
import net.apicode.squaretree.server.util.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * The type Square tree server.
 */
public abstract class SquareTreeServer {

  private static SquareTreeServer instance;

  @Internal
  public static void setInstance(SquareTreeServer server) {
    if(server == null) throw new NullPointerException();
    if(instance != null) throw new IllegalStateException("Instance already set");
    instance = server;
  }

  /**
   * Gets instance of square tree
   *
   * @return the instance
   */
  public static SquareTreeServer getInstance() {
    if(instance == null) throw new IllegalStateException("Instance not set");
    return instance;
  }

  /**
   * Gets logger.
   *
   * @return the logger
   */
  public abstract Logger getLogger();

  /**
   * Gets terminal.
   *
   * @return the terminal
   */
  public abstract Terminal getTerminal();

  /**
   * Gets network manager.
   *
   * @return the network manager
   */
  public abstract NetworkManager getNetworkManager();

  /**
   * Gets module manager.
   *
   * @return the module manager
   */
  public abstract ModuleManager getModuleManager();

  /**
   * Gets event manager to manage listeners
   *
   * @return the event manager
   */
  public abstract EventManager getEventManager();

  /**
   * Gets settings of application.
   *
   * @return the settings
   */
  public abstract SettingConfiguration getSettings();

  /**
   * Shutdown application.
   */
  public abstract void shutdown();

  /**
   * Gets module directory.
   *
   * @return the module directory
   */
  public abstract File getModuleDirectory();

  /**
   * Create custom configuration file.
   *
   * @param file the file
   * @param name the name
   * @return the configuration file
   */
  public ConfigurationFile createConfiguration(File file, String name) {
    return createConfiguration(new File(file, name));
  }

  /**
   * Create custom configuration file.
   *
   * @param name the name
   * @return the configuration file
   */
  public ConfigurationFile createConfiguration(String name) {
    return createConfiguration(new File(name));
  }

  /**
   * Create custom configuration file.
   *
   * @param file the file
   * @return the configuration file
   */
  public ConfigurationFile createConfiguration(File file) {
    return new ConfigurationFile(file);
  }

}
