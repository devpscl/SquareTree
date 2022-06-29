package net.apicode.squaretree.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Logger {

  private final File loggingFile;
  private final SimpleDateFormat loggingFileDateFormat = new SimpleDateFormat("dd-MM-yyyy");
  private final SimpleDateFormat loggingContentDateFormat = new SimpleDateFormat("HH:mm:ss");
  private final FileWriter fileWriter;

  public Logger(File loggerDirectory) throws IOException {
    if(!loggerDirectory.exists()) {
      loggerDirectory.mkdir();
    }
    loggingFile = new File(loggerDirectory, "latest.log");
    if(loggingFile.exists()) {
      int count = 0;
      File targetFile;
      while ((targetFile = new File(loggerDirectory,
          loggingFileDateFormat.format(new Date()) + "_" + count + ".log.gz"))
          .exists()) {
        count++;
      }
      archiveFile(loggingFile, targetFile.getAbsolutePath());
      clearFile(loggingFile);
    } else {
      loggingFile.createNewFile();
    }
    fileWriter = new FileWriter(loggingFile, false);
  }

  public void log(String message, String state) {
    String prefix = "[" + loggingContentDateFormat.format(new Date()) + " " + state.toUpperCase(Locale.ROOT) + "]: ";
    try {
      fileWriter.write(prefix + message + "\n");
      fileWriter.flush();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to log", e);
    }
  }

  public void logInfo(String message, Object...objects) {
    log(String.format(message, objects), "INFO");
  }

  public void logWarning(String message, Object...objects) {
    log(String.format(message, objects), "WARN");
  }

  public void logError(String message, Object...objects) {
    log(String.format(message, objects), "ERR");
  }

  public void logError(String message, Throwable throwable, Object...objects) {
    logError(message, objects);
    logError(StringUtil.toString(throwable));
  }

  public File getLoggingFile() {
    return loggingFile;
  }

  private static void archiveFile(File loggingFile, String targetName) throws IOException {
    try(FileOutputStream fos = new FileOutputStream(targetName);
        FileInputStream fis = new FileInputStream(loggingFile)) {
      GZIPOutputStream zos = new GZIPOutputStream(fos);
      byte[] buffer = new byte[1024];
      int length;
      while ((length = fis.read(buffer)) >= 0) {
        zos.write(buffer, 0, length);
      }
      zos.close();
    }
  }

  private static void clearFile(File loggingFile) throws IOException {
    PrintWriter writer = new PrintWriter(loggingFile);
    writer.print("");
    writer.close();
  }


}
