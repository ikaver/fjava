package com.ikaver.aagarwal.fjavaexamples;

import java.util.ArrayList;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.problems.Map;
import com.ikaver.aagarwal.common.problems.MapFunction;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;


public class FJavaMap<T, V> extends FJavaTask implements Map<T, V> {

  private MapFunction<T, V> mapFunc;
  private T [] array;
  private V [] result;
  private int left;
  private int right;
  
  private FJavaPool pool;
  
  public FJavaMap(FJavaPool pool) {
    this.pool = pool;
  }
  
  public FJavaMap(T [] array, V [] result, 
      MapFunction<T, V> mapFunc, int left, int right) {
    this.array = array;
    this.result = result;
    this.mapFunc = mapFunc;
    this.left = left;
    this.right = right;
  }
  
  public FJavaMap(T [] array, V [] result, 
      MapFunction<T, V> mapFunc, int left, int right, FJavaTask parent) {
    super(parent);
    this.array = array;
    this.result = result;
    this.mapFunc = mapFunc;
    this.left = left;
    this.right = right;
  }
  
  public void map(T [] array, V [] result, MapFunction<T, V> mapFunc) {
    this.pool.run(new FJavaMap<T, V>(array, result, mapFunc, 0, array.length-1));
  }
  
  @Override
  public void compute() {
    if(right - left <= Definitions.FILTER_SEQ_THRESHOLD) {
      for(int i = left; i <= right; ++i) {
        this.result[i] = this.mapFunc.map(this.array[i]);
      }
      return;
    }
    else {
      ArrayList<FJavaMap<T, V>> tasks = new ArrayList<FJavaMap<T, V>>();
      while(left <= right - Definitions.FILTER_SEQ_THRESHOLD) {
        int mid = (right+left)/2;
        tasks.add(new FJavaMap<T, V>(array, result, mapFunc, left, mid, this));
        left = mid+1;
      }

      for(int i = 0; i < tasks.size(); ++i) {
        tasks.get(i).fork(true);
      }
      new FJavaMap<T, V>(array, result, mapFunc, left, right, this).fork(false);
      sync();
    }
  }

}
