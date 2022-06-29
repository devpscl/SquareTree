package net.apicode.squaretree.server.config;

import java.io.File;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.SecurityInfo;

public class SettingConfiguration {

  private final ConfigurationFile cf;

  public SettingConfiguration() {
    cf = new ConfigurationFile(new File("config.yml"));
    if(!cf.existsFile()) {
      cf.createFile();
      cf.set("private-key", SecurityInfo.DEFAULT.getPrivateKey());
      cf.set("crypto-key", "null");
      cf.set("crypto-enabled", false);
      cf.set("host-address", "localhost");
      cf.set("port", ConnectionInfo.builder().getPort());
      cf.save();
    }
  }

  public String getHostAddress() {
    return (String) cf.get("host-address");
  }

  public int getPort() {
    return (int) cf.get("port");
  }

  public String getPrivateKey() {
    return (String) cf.get("private-key");
  }

  public boolean isCryptoEnabled() {
    return (boolean) cf.get("crypto-enabled");
  }

  public String getCryptoKey() {
    if(!isCryptoEnabled()) return null;
    return (String) cf.get("crypto-key");
  }


}
