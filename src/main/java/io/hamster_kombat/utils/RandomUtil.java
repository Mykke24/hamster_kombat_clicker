package io.hamster_kombat.utils;

import java.util.Random;

public class RandomUtil {

  private static final Random random = new Random();

  public static int randomSleepTime() {
    return random.nextInt(30) + 30;
  }

  public static int randomCount() {
    return random.nextInt(5);
  }

}
