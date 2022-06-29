package net.apicode.squaretree.server.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtil {

  public static final String EMPTY_STRING = "";

  public static String toString(Throwable throwable) {
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    throwable.printStackTrace(printWriter);
    printWriter.close();
    return writer.toString();
  }


}
