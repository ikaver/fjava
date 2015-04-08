package com.ikaver.aagarwal.common;

public class ArrayHelper {

  public static double [] createRandomArray(int size, double minValue, double maxValue) {
    double [] array = new double[size];
    for(int i = 0; i < size; ++i) {
      array[i] = MathHelper.randomBetween(minValue, maxValue);
    }
    return array;
  }
  
}
