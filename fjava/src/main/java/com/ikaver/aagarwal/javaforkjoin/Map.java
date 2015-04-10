package com.ikaver.aagarwal.javaforkjoin;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.MapFunction;

public class Map<T> extends RecursiveAction {

  private static final long serialVersionUID = -3740242580579985222L;
  private MapFunction<T> mapFunc;
  private T [] array;
  private T [] result;
  private int left;
  private int right;
  
  public Map(T [] array, T [] result, 
      MapFunction<T> mapFunc, int left, int right) {
    this.array = array;
    this.result = result;
    this.mapFunc = mapFunc;
    this.left = left;
    this.right = right;
  }
  
  @Override
  protected void compute() {
    if(right - left <= Definitions.FILTER_SEQ_THRESHOLD) {
      for(int i = left; i <= right; ++i) {
        this.result[i] = this.mapFunc.map(this.array[i]);
      }
      return;
    }
    else {
      ArrayList<Map<T>> tasks = new ArrayList<Map<T>>();
      while(left <= right - Definitions.FILTER_SEQ_THRESHOLD) {
        int mid = (right+left)/2;
        tasks.add(new Map<T>(array, result, mapFunc, left, mid));
        left = mid+1;
      }

      tasks.add(new Map<T>(array, result, mapFunc, left, right));
      invokeAll(tasks);
    }
  }

}
