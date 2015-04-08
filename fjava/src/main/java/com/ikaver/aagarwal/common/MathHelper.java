package com.ikaver.aagarwal.common;

public class MathHelper {
  
  public static int randomIntBetween(int min, int max) {
    return min + (int)Math.floor((Math.random() * (max-min)));
  }
  
  public static double randomBetween(double min, double max) {
    return min + Math.random() * (max-min);
  }
}
