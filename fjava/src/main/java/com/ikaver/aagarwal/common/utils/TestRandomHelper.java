package com.ikaver.aagarwal.common.utils;

import java.util.Random;

public class TestRandomHelper {
  
  private static Random random = new Random(1333331);
  
  public static long randomLongBetween(long min, long max) {
    return min + (long)(Math.floor((random.nextDouble() * (max-min))));
  }
  
  public static double randomBetween(double min, double max) {
    return min + random.nextDouble() * (max-min);
  }
  
  public static float randomBetween(float min, float max) {
    return min + (float)random.nextDouble() * (max-min);
  }
  
  /**
   * Generates a random number between max and min inclusive.
   * @param min is the minimum value of the range.
   * @param max is the maximum value of the range
   * @return a random number between [min, max]
   */
  public static int randomBetween(int min, int max) {
    return min + (int)(random.nextDouble() * (max - min));
  }
}
