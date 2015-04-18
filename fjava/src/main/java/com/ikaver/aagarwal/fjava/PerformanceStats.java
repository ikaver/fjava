package com.ikaver.aagarwal.fjava;

import com.custardsource.parfait.MonitoredCounter;


public class PerformanceStats {
  
  public static final MonitoredCounter totalSteals = new MonitoredCounter("steals.total", "# of total steals");


}
