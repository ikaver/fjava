package com.ikaver.aagarwal.fjava;

import java.util.ArrayList;

public abstract class FJavaTask {
  
  private TaskRunner runner;
  private ArrayList<FJavaTask> childTasks;
  private volatile boolean isDone; //TODO: is it necessary to be volatile?
  
  public FJavaTask() {
    this(null);
  }
  
  public FJavaTask(FJavaTask parent) {
    if(parent != null) {
      parent.addChild(this);
      this.runner = parent.runner;
    }
    else {
      this.runner = null;
    }
    this.childTasks = new ArrayList<FJavaTask>();
    this.isDone = false;
  }
  
  public abstract void compute();
  
  void run(TaskRunner runner) {
    this.runner = runner;
    this.compute();
    this.isDone = true;
  }
  
  boolean isDone() {
    return this.isDone;
  }
  
  void setIsDone(boolean done) {
    if(this.isDone && done == false) 
      throw new IllegalStateException("Cannot 'undo' a done task");
    this.isDone = done;
  }
  
  public void fork() {
    this.runner.addTask(this);
  }
  
  public boolean areAllChildsDone() {
    for(int i = 0; i < childTasks.size(); ++i) {
      if(!childTasks.get(i).isDone()) return false;
    }
    return true;
  }
  
  public void sync() {
    this.runner.syncTask(this);
  }
    
  private void addChild(FJavaTask task) {
    this.childTasks.add(task);
  }
}
