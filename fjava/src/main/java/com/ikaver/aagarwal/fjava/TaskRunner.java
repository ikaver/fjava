package com.ikaver.aagarwal.fjava;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.fjava.deques.TaskRunnerDeque;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * A Task Runner is an entity that runs tasks. 
 * Each Task Runner owns a work stealing deque. The Task Runner simply gets 
 * work from the deque, and inserts new work back into the deque. 
 */
public class TaskRunner implements Runnable {

  /**
   * The deque of this task runner.
   */
  private TaskRunnerDeque deque;
  /**
   * The id of this task runner.
   */
  private int taskRunnerID;
  /**
   * The root task, we should keep executing tasks from our deque until 
   * the rootTask is done.
   */
  private FJavaTask rootTask;
  /**
   * Stopwatch for statistics.
   */
  private FastStopwatch getTaskStopwatch;
  
  
  /**
   * Creates a task runner, with the given deque and ID
   * @param deque The deque of this task runner
   * @param taskRunnerID The id of this task runner
   */
  TaskRunner(TaskRunnerDeque deque, int taskRunnerID) {
    this.deque = deque;
    this.taskRunnerID = taskRunnerID;
    this.getTaskStopwatch = new FastStopwatch();
  }
  
  void setRootTask(FJavaTask task) {
    this.rootTask = task;
  }
  
  void addTask(FJavaTask task) {
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
  void syncTask(FJavaTask parentTask) {
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
  
  void tryLoadBalance() {
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
