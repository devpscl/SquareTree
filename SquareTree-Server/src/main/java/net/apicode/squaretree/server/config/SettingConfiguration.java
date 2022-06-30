package net.apicode.squaretree.server.config;

import com.google.gson.JsonPrimitive;
import java.io.File;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.SecurityInfo;

public class SettingConfiguration {

  private final ConfigurationFile cf;

  public SettingConfiguration() {
    cf = new ConfigurationFile(new File("config.json"));
    if(!cf.existsFile()) {
      cf.createFile();
      cf.set("private-key", new JsonPrimitive(SecurityInfo.DEFAULT.getPrivateKey()));
      cf.set("crypto-key", new JsonPrimitive("null"));
      cf.set("crypto-enabled", new JsonPrimitive(false));
      cf.set("host-address", new JsonPrimitive("localhost"));
      cf.set("port", new JsonPrimitive(ConnectionInfo.builder().getPort()));
      cf.save();
    }
  }

  public String getHostAddress() {
    return cf.get("host-address").getAsString();
  }

  public int getPort() {
    return cf.get("port").getAsInt();
  }

  public String getPrivateKey() {
    return cf.get("private-key").getAsString();
  }

  public boolean isCryptoEnabled() {
    return cf.get("crypto-enabled").getAsBoolean();
  }

  public String getCryptoKey() {
    if(!isCryptoEnabled()) return null;
    return (String) cf.get("crypto-key").getAsString();
  }


}
