package net.apicode.squaretree.server.api;

import net.apicode.squaretree.server.module.ModuleContext;

public abstract class Module {

  private ModuleContext context;

  public void start() {}

  public void stop() {}

  public void preStart() {}

  public abstract String getName();

  public ModuleContext getContext() {
    return context;
  }
}
