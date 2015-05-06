package com.ikaver.aagarwal.fjava.stats;



public class TaskRunnerStats {

  public final CounterStat totalTasksCompleted; 
  public final CounterStat totalTasksCreated; 
  public final CounterStat totalTriesBeforeAcquireTask;
  
  public final CounterStat getTaskTime;
  public final CounterStat computeTime;

  public TaskRunnerStats(int idx, CounterStatFactory factory) {
    this.totalTasksCompleted = factory.createCounter(
        StatsTracker.getStatisticName("TR.tasks_completed", "", idx),
        "# of tasks completed by TR");
    this.totalTasksCreated = factory.createCounter(
        StatsTracker.getStatisticName("TR.tasks_created", "", idx),
        "# of tasks created by TR");
    this.totalTriesBeforeAcquireTask = factory.createCounter(
        StatsTracker.getStatisticName("TR.failed_to_acquire_task", "", idx),
        "# of failed attempts to acquire task by TR");
    
    this.getTaskTime = factory.createCounter(
        StatsTracker.getStatisticName("TR.time.get_task", "tr_time", idx),
        "Amount of time spend on get task");
    this.computeTime = factory.createCounter(
        StatsTracker.getStatisticName("TR.time.compute_task", "tr_time", idx),
        "Amount of time actually doing work");
  }
  
}
