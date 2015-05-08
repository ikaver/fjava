package com.ikaver.aagarwal.fjava.deques;

import com.ikaver.aagarwal.fjava.FJavaTask;

public class StolenTasksStorage {
  
  private final FJavaTask [] tasks;
  
  public StolenTasksStorage(int size) {
    this.tasks = new FJavaTask[size];
  }
  
  public FJavaTask [] getStolenTasks() {
    return this.tasks;
  }
  
  public int getSize() {
    return this.tasks.length;
  }
  
}
