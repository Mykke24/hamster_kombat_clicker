package io.hamster_kombat.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {

  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public static void log(String message) {
    String timeStampedMessage = LocalDateTime.now().format(dtf) + ":" + message;
    System.out.println(timeStampedMessage);
  }

  public static void error(String message) {
    String timeStampedMessage = LocalDateTime.now().format(dtf) + ":" + message;
    System.err.println(timeStampedMessage);
  }

}
