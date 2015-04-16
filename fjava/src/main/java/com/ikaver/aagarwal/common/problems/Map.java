package com.ikaver.aagarwal.common.problems;

public interface Map<T, V> {
  public void map(T [] array, V [] result, MapFunction<T, V> func);
}
