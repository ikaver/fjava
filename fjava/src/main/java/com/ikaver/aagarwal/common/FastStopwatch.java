package com.ikaver.aagarwal.common;


public class FastStopwatch {
  
  private long startTime;
  
  public FastStopwatch() { }
  
  public void start() {
    startTime = System.nanoTime();
  }
  
  public long end() {
    if (FJavaConf.shouldTrackStats()) {
      return System.nanoTime() - startTime;
    } else {
    	return 0;
    }
  }

}
