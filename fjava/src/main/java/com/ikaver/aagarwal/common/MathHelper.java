package com.ikaver.aagarwal.common;

public class MathHelper {
  
  public static long randomLongBetween(long min, long max) {
    return min + (long)Math.floor((Math.random() * (max-min)));
  }
  
  public static double randomBetween(double min, double max) {
    return min + Math.random() * (max-min);
  }
  
  public static float randomBetween(float min, float max) {
    return min + (float)Math.random() * (max-min);
  }
}
