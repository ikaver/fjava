package com.ikaver.aagarwal.fjava;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;



public class TaskRunner implements Runnable {

  private Thread thread;
  private TaskRunnerDeque deque;
  private int taskRunnerID;
  private volatile boolean shouldShutdown;
    
  private Logger log;
  private FastStopwatch getTaskStopwatch;
  private FastStopwatch runTaskStopwatch;
  
  /* Statistics */
  
  public TaskRunner(TaskRunnerDeque deque, int taskRunnerID) {
    this.deque = deque;
    this.taskRunnerID = taskRunnerID;
    this.thread = new Thread(this, "Task Runner " + this.taskRunnerID);
    
    this.log = LogManager.getLogger();
    this.getTaskStopwatch = new FastStopwatch();
    this.runTaskStopwatch = new FastStopwatch();
  }
  
  public void addTask(FJavaTask task) {
    if(Definitions.TRACK_STATS) 
      StatsTracker.getInstance().onTaskCreated(this.taskRunnerID);
    this.deque.addTask(task);
  }
  
  public void startRunning() {
    this.thread.start();
  }
  
  public void setShouldShutdown(boolean shouldShutdown) {
    this.shouldShutdown = shouldShutdown;
  }
  
  public void syncTask(FJavaTask parentTask) {
    int triesBeforeSteal = 1;
    while(true) {
      if(parentTask.areAllChildsDone()) {
        return;
      }
      else {
        this.getTaskStopwatch.start();
        FJavaTask task = deque.getTask(parentTask);
        if(Definitions.TRACK_STATS)
          StatsTracker.getInstance().onGetTaskTime(this.taskRunnerID, this.getTaskStopwatch.end());
        if(task == null) {
          ++triesBeforeSteal;
          continue;
        }
        if(Definitions.TRACK_STATS)
          StatsTracker.getInstance().onTaskAcquired(this.taskRunnerID, triesBeforeSteal);
        triesBeforeSteal = 0;
        this.runTaskStopwatch.start();
        task.run(this);
        if(Definitions.TRACK_STATS)
          StatsTracker.getInstance().onRunTaskTime(this.taskRunnerID, this.runTaskStopwatch.end());
        this.notifyTaskDone(task);
      }
    }
  }
  
  public void run() {
    int triesBeforeSteal = 1;
    while(!this.shouldShutdown) {    
      this.getTaskStopwatch.start();
      FJavaTask task = deque.getTask(null);
      if(Definitions.TRACK_STATS)
        StatsTracker.getInstance().onGetTaskTime(this.taskRunnerID, this.getTaskStopwatch.end());
      if(task == null) {
        ++triesBeforeSteal;
        continue;
      }
      if(Definitions.TRACK_STATS)
        StatsTracker.getInstance().onTaskAcquired(this.taskRunnerID, triesBeforeSteal);
      triesBeforeSteal = 1;
      this.runTaskStopwatch.start();
      task.run(this);
      if(Definitions.TRACK_STATS)
        StatsTracker.getInstance().onRunTaskTime(this.taskRunnerID, this.runTaskStopwatch.end());
      this.notifyTaskDone(task);
    }
    log.warn("TR {} shutting down", this.taskRunnerID);
  }
    
  
  private void notifyTaskDone(FJavaTask task) {
    if(Definitions.TRACK_STATS) 
      StatsTracker.getInstance().onTaskCompleted(this.taskRunnerID);
  }
}
