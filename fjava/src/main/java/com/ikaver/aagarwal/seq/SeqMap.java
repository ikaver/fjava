package com.ikaver.aagarwal.seq;

import com.ikaver.aagarwal.common.MapFunction;

public class SeqMap<T> {
  
  public void map(T [] input, T [] result, MapFunction<T> mapFunc) {
    int length = input.length;
    for(int i = 0; i < length; ++i) {
      result[i] = mapFunc.map(input[i]);
    }
  }

}
