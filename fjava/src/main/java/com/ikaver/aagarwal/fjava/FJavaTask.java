package com.ikaver.aagarwal.fjava;

import java.util.ArrayList;

public abstract class FJavaTask {
  
  private TaskRunner runner;
  private ArrayList<FJavaTask> childTasks;
  private volatile boolean isDone; //TODO: is it necessary to be volatile?
  
  public FJavaTask() {
    this.childTasks = new ArrayList<FJavaTask>();
    this.isDone = false;
  }
  
  public abstract void compute();
  
  void execute(TaskRunner runner) {
    this.runner = runner;
    this.compute();
    this.setIsDone(true);
  }
  
  boolean isDone() {
    return this.isDone;
  }
  
  void setIsDone(boolean done) {
    if(this.isDone && done == false) 
      throw new IllegalStateException("Cannot 'undo' a done task");
    this.isDone = done;
    this.childTasks.clear();
  }
  
  public void runAsync(FJavaTask parent) {
    parent.addChild(this);
    this.runner = parent.runner;
    this.runner.addTask(this);
  }
  
  public void runSync(FJavaTask parent) {
    this.runner = parent.runner;
    this.compute();
    this.setIsDone(true);
  }
  
  public boolean areAllChildsDone() {
    for(int i = 0; i < childTasks.size(); ++i) {
      if(!childTasks.get(i).isDone()) {
        return false;
      }
      else {
        childTasks.remove(i);
        i = -1;
      }
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
