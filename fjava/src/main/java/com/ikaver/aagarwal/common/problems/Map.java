package com.ikaver.aagarwal.common.problems;

public interface Map<T> {
  public void map(T [] array, T [] result, MapFunction<T> func);
}
