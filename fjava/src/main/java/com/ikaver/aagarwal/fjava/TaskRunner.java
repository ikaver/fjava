package com.ikaver.aagarwal.fjava;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.fjava.deques.TaskRunnerDeque;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;



public class TaskRunner implements Runnable {

  private TaskRunnerDeque deque;
  private int taskRunnerID;
  private FJavaTask rootTask;
    
  private FastStopwatch getTaskStopwatch;
  
  /* Statistics */
  
  public TaskRunner(TaskRunnerDeque deque, int taskRunnerID) {
    this.deque = deque;
    this.taskRunnerID = taskRunnerID;
    this.getTaskStopwatch = new FastStopwatch();
  }
  
  public void setRootTask(FJavaTask task) {
    this.rootTask = task;
  }
  
  public void addTask(FJavaTask task) {
    if(FJavaConf.shouldTrackStats()) { 
      StatsTracker.getInstance().onTaskCreated(this.taskRunnerID);
    }
    this.deque.addTask(task);
  }
  
  /**
   * This method can be invoked only by {@code FJavaTask#sync()}. We continue to 
   * fetch a new piece of work to execute until all the children of the task 
   * finish execution; the control then passes over to the parent task which had invoked 
   * sync.
   * 
   * @param parentTask is the task which we want to sync on. 
   */
  public void syncTask(FJavaTask parentTask) {
    int triesBeforeSteal = 0;
    while(true) {
      if(parentTask.areAllChildsDone()) {
        return;
      }
      else {
        this.getTaskStopwatch.start();

        FJavaTask task = deque.getTask(parentTask);
        if(FJavaConf.shouldTrackStats()) {
          StatsTracker.getInstance().onGetTaskTime(
          		this.taskRunnerID, this.getTaskStopwatch.end());
        }
        if(task == null) {
          ++triesBeforeSteal;
          continue;
        }
        if(FJavaConf.shouldTrackStats()) {
          StatsTracker.getInstance().onTaskAcquired(
          		this.taskRunnerID, triesBeforeSteal);
          triesBeforeSteal = 0;
        }
        task.execute(this);
        this.notifyTaskDone(task);
      }
    }
  }
  
  @Override
  public void run() {
    int triesBeforeSteal = 1;
    while(!this.rootTask.isDone()) {    
      this.getTaskStopwatch.start();
      FJavaTask task = deque.getTask(null);

      if(FJavaConf.shouldTrackStats()) {
        StatsTracker.getInstance().onGetTaskTime(
        		this.taskRunnerID, this.getTaskStopwatch.end());
      }

      if(task == null) {
        ++triesBeforeSteal;
        continue;
      }

      if(FJavaConf.shouldTrackStats()) {
        StatsTracker.getInstance().onTaskAcquired(
        		this.taskRunnerID, triesBeforeSteal);
      }

      triesBeforeSteal = 1;
      task.execute(this);
      this.notifyTaskDone(task);
    }
  }
  
  public void tryLoadBalance() {
    this.deque.tryLoadBalance();
  } 
  
  private void notifyTaskDone(FJavaTask task) {
    if(FJavaConf.shouldTrackStats()) {
      StatsTracker.getInstance().onTaskCompleted(this.taskRunnerID);
    }
   }
  
   public int getTaskRunnerID() {
  	 return this.taskRunnerID;
   }
}
