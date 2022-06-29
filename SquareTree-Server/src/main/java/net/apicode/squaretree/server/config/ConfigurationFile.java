package net.apicode.squaretree.server.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class ConfigurationFile {

  private final File file;
  private LinkedHashMap<String, Object> map = new LinkedHashMap<>();
  private final Yaml yaml;

  public ConfigurationFile(File file) {
    this.file = file;
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    yaml = new Yaml(options);

    if(file.exists()) {
      load();
    }
  }

  public boolean existsFile() {
    return file.exists();
  }

  public void createFile() {
    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void set(String key, Object value) {
    map.put(key, value);
  }

  public Object get(String key) {
    return map.get(key);
  }

  public Set<String> keys() {
    return map.keySet();
  }

  public boolean containsKey(String key) {
    return map.containsKey(key);
  }

  public File getFile() {
    return file;
  }

  public void load(){
    try {
      map = yaml.load(new FileReader(file));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Failed to load file", e);
    }
  }

  public void save() {
    if(existsFile()) {
      try {
        PrintWriter writer = new PrintWriter(file);
        yaml.dump(map, writer);
      } catch (IOException e) {
        throw new RuntimeException("Failed to save file", e);
      }
    }
  }
}
