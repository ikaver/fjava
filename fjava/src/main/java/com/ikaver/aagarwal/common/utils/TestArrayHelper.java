package com.ikaver.aagarwal.common.utils;

public class TestArrayHelper {

  public static double [] createRandomArray(int size, double minValue, double maxValue) {
    double [] array = new double[size];
    for(int i = 0; i < size; ++i) {
      array[i] = TestRandomHelper.randomBetween(minValue, maxValue);
    }
    return array;
  }
  
  public static int [] createRandomAray(int size, int minValue, int maxValue) {
	  int [] array = new int[size];
	  for(int i = 0; i < size; i++) {
		  array[i] = TestRandomHelper.randomBetween(minValue, maxValue);
	  }

	  return array;
  }

  public static long [] createRandomArray(int size, long minValue, long maxValue) {
    long [] array = new long[size];
    for(int i = 0; i < size; ++i) {
      array[i] = TestRandomHelper.randomLongBetween(minValue, maxValue);
    }
    return array;
  }
  
  public static float [][] createRandomMatrix(int rows, int cols, float minValue, float maxValue) {
    float [][] m = new float[rows][cols];
    for(int i = 0; i < rows; ++i) 
      for(int j = 0; j < cols; ++j)
        m[i][j] = TestRandomHelper.randomBetween(minValue, maxValue);
    return m;
  }
  
}
