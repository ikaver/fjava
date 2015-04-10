package com.ikaver.aagarwal.seq;

import com.ikaver.aagarwal.common.problems.Map;
import com.ikaver.aagarwal.common.problems.MapFunction;

public class SeqMap<T> implements Map<T> {
  
  public void map(T [] input, T [] result, MapFunction<T> mapFunc) {
    int length = input.length;
    for(int i = 0; i < length; ++i) {
      result[i] = mapFunc.map(input[i]);
    }
  }

}
