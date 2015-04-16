package com.ikaver.aagarwal.javaforkjoin;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.problems.Map;
import com.ikaver.aagarwal.common.problems.MapFunction;


public class MapJavaForkJoin<T, V> extends RecursiveAction implements Map<T, V> {

  private static final long serialVersionUID = -3740242580579985222L;
  private MapFunction<T, V> mapFunc;
  private T [] array;
  private V [] result;
  private int left;
  private int right;
  
  private ForkJoinPool pool;
  
  public MapJavaForkJoin(ForkJoinPool pool) {
    this.pool = pool;
  }
  
  public MapJavaForkJoin(T [] array, V [] result, 
      MapFunction<T, V> mapFunc, int left, int right) {
    this.array = array;
    this.result = result;
    this.mapFunc = mapFunc;
    this.left = left;
    this.right = right;
  }
  
  public void map(T [] array, V [] result, MapFunction<T, V> mapFunc) {
    this.pool.invoke(new MapJavaForkJoin<T, V>(array, result, mapFunc, 0, array.length-1));
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
      ArrayList<MapJavaForkJoin<T, V>> tasks = new ArrayList<MapJavaForkJoin<T, V>>();
      while(left <= right - Definitions.FILTER_SEQ_THRESHOLD) {
        int mid = (right+left)/2;
        tasks.add(new MapJavaForkJoin<T, V>(array, result, mapFunc, left, mid));
        left = mid+1;
      }

      tasks.add(new MapJavaForkJoin<T, V>(array, result, mapFunc, left, right));
      invokeAll(tasks);
    }
  }

}
