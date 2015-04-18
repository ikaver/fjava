package com.ikaver.aagarwal.fjava.stats;

import com.custardsource.parfait.MonitoredCounter;

public class TaskRunnerStats {

  public final MonitoredCounter totalTasksCompleted; 
  public final MonitoredCounter totalTasksCreated; 
  
  public TaskRunnerStats(int idx) {
    this.totalTasksCompleted = new MonitoredCounter("TR.tasks_completed#" + idx, 
        "# of tasks completed by TR");
    this.totalTasksCreated = new MonitoredCounter("TR.tasks_created#" + idx, 
        "# of tasks created by TR");
  }
  
}
