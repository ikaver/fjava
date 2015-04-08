package com.ikaver.aagarwal.seq;

import java.util.Arrays;

import com.ikaver.aagarwal.common.Definitions;

public class SeqQuickSort {

  private double [] array;
  private int left;
  private int right;

  public SeqQuickSort(double [] array, int left, int right) {
    this.array = array;
    this.left = left;
    this.right = right;
  }

  public void compute() {
    quicksort(this.left, this.right);
  }
  
  private void quicksort(int left, int right) {
    if(right - left <= Definitions.QUICKSORT_SEQ_THRESHOLD) {
      Arrays.sort(array, left, right+1);
      return;
    }
    int mid = partition(left, right);
    quicksort(left, mid-1);
    quicksort(mid+1, right);
  }

  private int partition(int left, int right) {
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

}
