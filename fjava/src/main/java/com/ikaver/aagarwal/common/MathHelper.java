package com.ikaver.aagarwal.common;

public class MathHelper {
  
  public static long randomLongBetween(long min, long max) {
    return min + (long)(Math.floor((Math.random() * (max-min))));
  }
  
  public static double randomBetween(double min, double max) {
    return min + Math.random() * (max-min);
  }
  
  public static float randomBetween(float min, float max) {
    return min + (float)Math.random() * (max-min);
  }
  
  /**
   * Generates a random number between max and min inclusive.
   * @param min is the minimum value of the range.
   * @param max is the maximum value of the range
   * @return a random number between [min, max]
   */
  public static int randomBetween(int min, int max) {
	  return min + (int)(Math.random() * (max - min));
  }
}
