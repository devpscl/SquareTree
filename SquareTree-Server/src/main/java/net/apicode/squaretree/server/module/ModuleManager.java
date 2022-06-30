package net.apicode.squaretree.server.module;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import net.apicode.squaretree.server.api.Module;

/**
 * The type Module manager.
 */
public class ModuleManager {

  private final List<ModuleContext> moduleContexts = new ArrayList<>();
  private final HashMap<String, ModuleContext> moduleContextHashMap = new HashMap<>();

  /**
   * Gets module names.
   *
   * @return the module names sez
   */
  public Set<String> getModuleNames() {
    return moduleContextHashMap.keySet();
  }

  /**
   * Gets module context of name.
   *
   * @param moduleName the module name
   * @return the module context
   */
  public ModuleContext getModuleContext(String moduleName) {
    if(moduleContextHashMap.containsKey(moduleName)) {
      return moduleContextHashMap.get(moduleName);
    }
    return null;
  }

  /**
   * Gets module contexts.
   *
   * @return the module contexts
   */
  public List<ModuleContext> getModuleContexts() {
    return Collections.unmodifiableList(moduleContexts);
  }

  /**
   * Register all modules in directory.
   *
   * @param directory the directory
   */
  public void registerAllModules(File directory) {
    File[] files = directory.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".jar"));
    assert files != null;
    for (File file : files) {
      try {
        registerModule(file);
      } catch (ModuleException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Register module.
   *
   * @param file the jar file
   * @return the module context
   * @throws ModuleException the module exception
   */
  public ModuleContext registerModule(File file) throws ModuleException {
    if(!file.exists()) throw new ModuleException("File " + file.getName() + " is not exists");
    if(!file.getName().endsWith(".jar")) {
      throw new ModuleException("Invalid Jarfile of "  + file.getName());
    }
    try (JarFile jarFile = new JarFile(file)) {
      String mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
      if(mainClass == null) {
        throw new ModuleException("Main-Class of "  + file.getName() + " is not set");
      }
      URLClassLoader classLoader
          = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
      Class<?> clazz = Class.forName(mainClass, true, classLoader);
      if(!Module.class.isAssignableFrom(clazz)) {
        throw new ModuleException("Main-Class is not assignable from module at "  + file.getName());
      }
      Object o = clazz.newInstance();
      if(!(o instanceof Module)) {
        throw new ModuleException("Main-Class is not assignable from module at "  + file.getName());
      }
      Module module = (Module) o;
      if(module.getName() == null) {
        throw new ModuleException("Invalid name of "  + file.getName());
      }
      if(getModuleContext(module.getName()) != null) {
        throw new ModuleException("Name of "  + file.getName() + " is already in use");
      }

      ModuleContext moduleContext = new ModuleContext(module, file, classLoader);

      Field contextField = Module.class.getDeclaredField("context");
      contextField.setAccessible(true);
      contextField.set(moduleContext.getModule(), moduleContext);

      moduleContexts.add(moduleContext);
      moduleContextHashMap.put(module.getName(), moduleContext);

      return moduleContext;
    } catch (IOException e) {
      throw new ModuleException("Invalid Jarfile of "  + file.getName());
    } catch (ClassNotFoundException e) {
      throw new ModuleException("Invalid Main-Class of "  + file.getName());
    } catch (InstantiationException | IllegalAccessException e) {
      throw new ModuleException("Failed to create module "  + file.getName(), e);
    } catch (NoSuchFieldException e) {
      throw new ModuleException("Failed to load module "  + file.getName(), e);
    }
  }

  public void preStartModule(ModuleContext context) throws ModuleException {
    try {
      context.getModule().preStart();
    } catch (Exception e) {
      throw new ModuleException("Failed to load module", e);
    }
  }

  public void startModule(ModuleContext context) throws ModuleException {
    try {
      context.getModule().start();
    } catch (Exception e) {
      throw new ModuleException("Failed to start module", e);
    }
  }

  /**
   * Unregister module.
   *
   * @param context the context
   * @throws ModuleException the module exception
   */
  public void unregisterModule(ModuleContext context) throws ModuleException {
    try {
      moduleContexts.remove(context);
      moduleContextHashMap.remove(context.getModule().getName());
      context.getModule().stop();
      context.getClassLoader().close();
    } catch (Exception e) {
      throw new ModuleException("Failed to unregister module", e);
    }
  }

}
