package net.apicode.squaretree.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Set;

public class ConfigurationFile {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private final File file;
  private JsonObject object = new JsonObject();

  public ConfigurationFile(File file) {
    this.file = file;


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

  public void set(String key, JsonElement value) {
    object.add(key, value);
  }

  public JsonObject getAsJsonObject() {
    return object;
  }

  public JsonElement get(String key) {
    if(!containsKey(key)) return null;
    return object.get(key);
  }

  public Set<String> keys() {
    return object.keySet();
  }

  public boolean containsKey(String key) {
    return object.has(key);
  }

  public File getFile() {
    return file;
  }

  public void load(){
    try {
      JsonElement jsonElement = JsonParser.parseReader(new FileReader(file));
      object = jsonElement.getAsJsonObject();
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Failed to load file", e);
    }
  }

  public void save() {
    if(existsFile()) {
      String output = gson.toJson(object);
      try(PrintWriter writer = new PrintWriter(file)) {
        writer.write(output);
      } catch (IOException e) {
        throw new RuntimeException("Failed to save file", e);
      }
    }
  }
}
