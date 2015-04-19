package com.ikaver.aagarwal.fjava.stats;

import com.custardsource.parfait.MonitoredCounter;

public class TaskRunnerStats {

  public final MonitoredCounter totalTasksCompleted; 
  public final MonitoredCounter totalTasksCreated; 
  public final MonitoredCounter totalTriesBeforeAcquireTask;
  
  public final MonitoredCounter getTaskTime;
  public final MonitoredCounter runTaskTime;
  
  public TaskRunnerStats(int idx) {
    this.totalTasksCompleted = new MonitoredCounter(
        StatsTracker.getStatisticName("TR.tasks_completed", "", idx),
        "# of tasks completed by TR");
    this.totalTasksCreated = new MonitoredCounter(
        StatsTracker.getStatisticName("TR.tasks_created", "", idx),
        "# of tasks created by TR");
    this.totalTriesBeforeAcquireTask = new MonitoredCounter(
        StatsTracker.getStatisticName("TR.failed_to_acquire_task", "", idx),
        "# of failed attempts to acquire task by TR");
    
    this.getTaskTime = new MonitoredCounter(
        StatsTracker.getStatisticName("TR.time.get_task", "tr_time", idx),
        "Amount of time spend on get task");
    this.runTaskTime = new MonitoredCounter(
        StatsTracker.getStatisticName("TR.time.run_task", "tr_time", idx),
        "Amount of time running tasks");
  }
  
}
