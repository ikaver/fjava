package com.ikaver.aagarwal.fjava;


public class TaskRunner implements Runnable {

  private ReceiverInitiatedDeque deque;
  
  public TaskRunner(ReceiverInitiatedDeque deque) {
    this.deque = deque;
  }

  public void run() {
    while(true) {
      //TODO: measure time waiting for task?
      FJavaTask task = deque.getTask();
      if(task == null) throw new NullPointerException("Task from deque is null");
      task.run(this);
    }
  }
  
  public void onNewTask(FJavaTask task, FJavaTask parent) {
    //TODO: should be called on this task runner's thread.
    //TODO; increment task added counter here?
    
    //TODO: associate task to parent
    this.deque.addTask(task);
  }
    
}
