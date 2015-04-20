package com.ikaver.aagarwal.common;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class FastStopwatch {
  
  private Stopwatch stopwatch;
  
  public FastStopwatch() { }
  
  public void start() {
    if(FJavaConf.getInstance().shouldTrackStats()) {
      stopwatch = Stopwatch.createStarted();
    }
  }
  
  public long end() {
    if(FJavaConf.getInstance().shouldTrackStats()) {
      return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    } else {
    	return 0;
    }
  }

}
