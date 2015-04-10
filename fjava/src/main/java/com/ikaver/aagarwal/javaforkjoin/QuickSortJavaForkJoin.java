package com.ikaver.aagarwal.javaforkjoin;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.problems.QuickSort;

public class QuickSortJavaForkJoin extends RecursiveAction implements QuickSort {

  private static final long serialVersionUID = 7126254235720159895L;
  private double [] array;
  private int left;
  private int right;
  
  private ForkJoinPool pool;
  
  public QuickSortJavaForkJoin(ForkJoinPool pool) { 
    this.pool = pool;
  }
  
  public QuickSortJavaForkJoin(double [] array, int left, int right) {
    this.array = array;
    this.left = left;
    this.right = right;
  }
  
  @Override
  protected void compute() {
    if(right <= left) return;
    
    if(right - left <= Definitions.QUICKSORT_SEQ_THRESHOLD) {
      Arrays.sort(array, left, right+1);
      return;
    }
    int mid = partition();
    invokeAll(
        new QuickSortJavaForkJoin(array, left, mid-1),
        new QuickSortJavaForkJoin(array, mid+1, right)
    );
  }
  
  private int partition() {
    int i = left, j = right+1;
    double tmp;
    double pivot = array[left];
   
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

  public void sort(double[] array, int left, int right) {
    pool.invoke(new QuickSortJavaForkJoin(array, left, right));
  }

}
