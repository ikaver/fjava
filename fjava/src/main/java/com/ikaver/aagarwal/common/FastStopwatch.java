package com.ikaver.aagarwal.common;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class FastStopwatch {
  
  private Stopwatch stopwatch;
  
  public FastStopwatch() { }
  
  public void start() {
    if(Definitions.TRACK_STATS)
      stopwatch = Stopwatch.createStarted();
  }
  
  public long end() {
    if(Definitions.TRACK_STATS)
      return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    else return 0;
  }

}
