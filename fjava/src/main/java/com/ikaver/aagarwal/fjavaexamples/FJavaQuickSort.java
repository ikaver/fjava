package com.ikaver.aagarwal.fjavaexamples;

import java.util.Arrays;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.problems.QuickSort;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

public class FJavaQuickSort extends FJavaTask implements QuickSort {

  private long [] array;
  private int left;
  private int right;
  
  private FJavaPool pool;
  
  public FJavaQuickSort(FJavaPool pool) { 
    this.pool = pool;
  }
  
  public FJavaQuickSort(long [] array, int left, int right) {
    this.array = array;
    this.left = left;
    this.right = right;
  }
  
  @Override
  public void compute() {
    if(right <= left) return;
    
    if(right - left <= Definitions.QUICKSORT_SEQ_THRESHOLD) {
      Arrays.sort(array, left, right+1);
      return;
    }
    int mid = partition();
    new FJavaQuickSort(array, left, mid-1).runAsync(this);
    new FJavaQuickSort(array, mid+1, right).runSync(this);
    sync();
  }
  
  private int partition() {
    int i = left, j = right+1;
    long tmp;
    long pivot = array[left];
   
    while (true) {
      while(array[++i] <= pivot) 
        if(i == right) break;
      while(array[--j] >= pivot) 
        if(j == left) break;
      if(i >= j) break;
      tmp = array[i];
      array[i] = array[j];
      array[j] = tmp;
    }

    tmp = array[j];
    array[j] = pivot;
    array[left] = tmp;
    
    return j;
  }

  public void sort(long[] array, int left, int right) {
    FJavaQuickSort task = new FJavaQuickSort(array, left, right);
    pool.run(task);
  }

}
