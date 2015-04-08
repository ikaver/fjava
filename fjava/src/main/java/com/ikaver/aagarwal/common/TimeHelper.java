package com.ikaver.aagarwal.common;

public class TimeHelper {
  
  public static void printTimes(long origTime, long newTime) {
    System.out.printf("Orig time: %d - Final time: %d - Speed up: %f\n",
        origTime, newTime, (double)(origTime) / newTime
        );
  }

}
