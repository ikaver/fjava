package com.ikaver.aagarwal.seq;

import java.util.Arrays;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.problems.QuickSort;

public class SeqQuickSort implements QuickSort {

  public void sort(double[] array, int left, int right) {
    if(right - left <= Definitions.QUICKSORT_SEQ_THRESHOLD) {
      Arrays.sort(array, left, right+1);
      return;
    }
    int mid = partition(array, left, right);
    sort(array, left, mid-1);
    sort(array, mid+1, right);
  }


  private int partition(double [] array, int left, int right) {
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
