package com.ikaver.aagarwal.fjava;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;



public class TaskRunner implements Runnable {

  private Thread thread;
  private TaskRunnerDeque deque;
  private int taskRunnerID;
  private volatile boolean shouldShutdown;
    
  private Logger log;
  
  /* Statistics */
  
  public TaskRunner(TaskRunnerDeque deque, int taskRunnerID) {
    this.deque = deque;
    this.taskRunnerID = taskRunnerID;
    this.thread = new Thread(this, "Task Runner " + this.taskRunnerID);
    log = LogManager.getLogger();
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
    while(true) {
      if(parentTask.areAllChildsDone()) {
        return;
      }
      else {
        FJavaTask task = deque.getTask(parentTask);
        if(task == null) {
          continue;
        }
        task.run(this);
        this.notifyTaskDone(task);
      }
    }
  }
  
  public void run() {
    while(!this.shouldShutdown) { //TODO: while not finished running all tasks, according to the pool?
      //TODO: measure time waiting for task?
      
      FJavaTask task = deque.getTask(null);
      if(task == null) {
        continue;
      }
      task.run(this);
      this.notifyTaskDone(task);
    }
    log.warn("TR {} shutting down", this.taskRunnerID);
  }
    
  
  private void notifyTaskDone(FJavaTask task) {
    if(Definitions.TRACK_STATS) 
      StatsTracker.getInstance().onTaskCompleted(this.taskRunnerID);
  }
}
