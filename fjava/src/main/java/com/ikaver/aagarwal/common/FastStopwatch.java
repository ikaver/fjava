package com.ikaver.aagarwal.common;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class FastStopwatch {
  
  private Stopwatch stopwatch;
  
  public FastStopwatch() { }
  
  public void start() {
    if (FJavaConf.shouldTrackStats()) {
      stopwatch = Stopwatch.createStarted();
    }
  }
  
  public long end() {
    if (FJavaConf.shouldTrackStats()) {
      return stopwatch.elapsed(TimeUnit.NANOSECONDS);
    } else {
    	return 0;
    }
  }

}
