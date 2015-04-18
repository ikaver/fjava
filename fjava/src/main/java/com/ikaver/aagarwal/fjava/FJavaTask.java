package com.ikaver.aagarwal.fjava;

public abstract class FJavaTask implements Runnable {
  
  private TaskRunner runner;

  public void run(TaskRunner runner) {
    if(runner != null) throw new IllegalStateException("Task runner of this task was already set!");
    this.runner = runner;
    //TODO: actually run here
  }
  
  public void onNewTask(FJavaTask task) {
    this.runner.onNewTask(task, this);
  }
  
  public void sync() {
    //TODO: Wait for onChildCompleted? Recursive?
  }
  
  public void onChildCompleted(FJavaTask task) {
    //TODO: increment child completed count here
  }
}
