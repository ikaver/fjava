package com.ikaver.aagarwal.fjava;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;



public class TaskRunner implements Runnable {

  private Thread thread;
  private TaskRunnerDeque deque;
  private int taskRunnerID;
  private volatile boolean shouldShutdown;
    
  /* Statistics */
  
  public TaskRunner(TaskRunnerDeque deque, int taskRunnerID) {
    this.deque = deque;
    this.taskRunnerID = taskRunnerID;
    this.thread = new Thread(this, "Task Runner " + this.taskRunnerID);
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
        //LogManager.getLogger().info("TR {} on SYNC {} looking for task", this.taskRunnerID, parentTask);
        FJavaTask task = deque.getTask();
        if(task == null) {
          //System.out.println("TR " + this.taskRunnerID + " running SYNC " + parentTask + " GOT NULL ");
          continue;
        }
        //LogManager.getLogger().info("TR {} on SYNC {} got task {}", this.taskRunnerID, parentTask, task);
        task.run(this);
        this.notifyTaskDone(task);
      }
    }
  }
  


  public void run() {
    while(!this.shouldShutdown) { //TODO: while not finished running all tasks, according to the pool?
      //TODO: measure time waiting for task?
      FJavaTask task = deque.getTask();
      if(task == null) {
        continue;
      }
      //LogManager.getLogger().info("TR {} got task {}", this.taskRunnerID, task);
      task.run(this);
      this.notifyTaskDone(task);
    }
  }
    
  
  private void notifyTaskDone(FJavaTask task) {
    if(Definitions.TRACK_STATS) 
      StatsTracker.getInstance().onTaskCompleted(this.taskRunnerID);
  }
}
