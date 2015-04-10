package com.ikaver.aagarwal.common;

public class ArrayHelper {

  public static double [] createRandomArray(int size, double minValue, double maxValue) {
    double [] array = new double[size];
    for(int i = 0; i < size; ++i) {
      array[i] = MathHelper.randomBetween(minValue, maxValue);
    }
    return array;
  }
  
  public static long [] createRandomArray(int size, long minValue, long maxValue) {
    long [] array = new long[size];
    for(int i = 0; i < size; ++i) {
      array[i] = MathHelper.randomLongBetween(minValue, maxValue);
    }
    return array;
  }
  
  public static float [][] createRandomMatrix(int rows, int cols, float minValue, float maxValue) {
    float [][] m = new float[rows][cols];
    for(int i = 0; i < rows; ++i) 
      for(int j = 0; j < cols; ++j)
        m[i][j] = MathHelper.randomBetween(minValue, maxValue);
    return m;
  }
  
}
