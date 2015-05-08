package com.ikaver.aagarwal.fjavaexamples;

import java.util.ArrayList;

import com.ikaver.aagarwal.common.FJavaConf;
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
  
  public static final int ITERATIONS_FOR_BALANCE = 250;
  
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
    
  public void map(T [] array, V [] result, MapFunction<T, V> mapFunc) {
    this.pool.run(new FJavaMap<T, V>(array, result, mapFunc, 0, array.length-1));
  }
  
  @Override
  public void compute() {
    int count = 0;
   
    if(right - left <= FJavaConf.getMapSequentialThreshold()) {
      for(int i = left; i <= right; ++i) {
        this.result[i] = this.mapFunc.map(this.array[i]);
        ++count;
        if(count == ITERATIONS_FOR_BALANCE) {
          this.tryLoadBalance();
          count = 0;
        }
      }
      return;
    }
    else {
      ArrayList<FJavaMap<T, V>> tasks = new ArrayList<FJavaMap<T, V>>();
      while(left <= right - FJavaConf.getMapSequentialThreshold()) {
        int mid = (right+left)/2;
        tasks.add(new FJavaMap<T, V>(array, result, mapFunc, left, mid));
        left = mid+1;
      }

      for(int i = 0; i < tasks.size(); ++i) {
        tasks.get(i).runAsync(this);
      }
      new FJavaMap<T, V>(array, result, mapFunc, left, right).runSync(this);
      sync();
    }
  }

}
