package net.apicode.squaretree.server.module;

import java.io.File;
import java.net.URLClassLoader;
import net.apicode.squaretree.server.api.Module;

public class ModuleContext {

  private final Module module;
  private final File file;
  private final URLClassLoader classLoader;

  public ModuleContext(Module module, File file, URLClassLoader classLoader) {
    this.module = module;
    this.file = file;
    this.classLoader = classLoader;
  }

  public File getFile() {
    return file;
  }

  public Module getModule() {
    return module;
  }

  public URLClassLoader getClassLoader() {
    return classLoader;
  }

}
