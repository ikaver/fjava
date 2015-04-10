package com.ikaver.aagarwal.common;

import java.util.concurrent.RecursiveAction;

public class SeqJavaForkJoin extends RecursiveAction {

  private static final long serialVersionUID = -7006504153266531640L;
  private RecursiveAction [] tasks;
  
  public SeqJavaForkJoin(RecursiveAction [] tasks) {
    this.tasks = tasks;
  }
  
  @Override
  protected void compute() {
    for(int i = 0; i < tasks.length; ++i) {
      tasks[i].invoke();
    }
  }

  
  
}
