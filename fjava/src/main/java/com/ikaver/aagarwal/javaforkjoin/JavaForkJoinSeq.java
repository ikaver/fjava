package com.ikaver.aagarwal.javaforkjoin;

import java.util.concurrent.RecursiveAction;

public class JavaForkJoinSeq extends RecursiveAction {

  private static final long serialVersionUID = 3830465052704885304L;
  private final RecursiveAction [] tasks;
  
  public JavaForkJoinSeq(RecursiveAction ... tasks) {
    this.tasks = tasks;
  }

  @Override
  public void compute() {
    for(int i = 0; i < tasks.length; ++i) {
      tasks[i].invoke();
    }
  }

}
