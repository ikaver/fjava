package com.ikaver.aagarwal.fjava;


public class TaskRunner implements Runnable {

  private Thread thread;
  private TaskRunnerDeque deque;
  private int taskRunnerID;
  
  public TaskRunner(TaskRunnerDeque deque, int taskRunnerID) {
    this.deque = deque;
    this.taskRunnerID = taskRunnerID;
    this.thread = new Thread(this, "Task Runner " + this.taskRunnerID);
  }
  
  public void addTask(FJavaTask task) {
    this.deque.addTask(task);
  }
  
  public void startRunning() {
    this.thread.start();
  }
  
  public void syncTask(FJavaTask parentTask) {
    while(true) {
      if(parentTask.areAllChildsDone()) {
        return;
      }
      else {
        FJavaTask task = deque.getTask();
        if(task == null) throw new NullPointerException("Task from deque is null");
        task.run(this);
      }
    }
  }

  public void run() {
    while(true) { //TODO: while not finished running all tasks, according to the pool?
      //TODO: measure time waiting for task?
      FJavaTask task = deque.getTask();
      if(task == null) throw new NullPointerException("Task from deque is null");
      task.run(this);
    }
  }
    
}
