package com.ikaver.aagarwal.fjavaexamples;

import com.ikaver.aagarwal.fjava.FJavaTask;

public class FJavaSeq extends FJavaTask {
  
  private final FJavaTask [] tasks;
  
  public FJavaSeq(FJavaTask ... tasks) {
    this.tasks = tasks;
  }

  @Override
  public void compute() {
    for(int i = 0; i < tasks.length; ++i) {
      tasks[i].runSync(this);
      this.tryLoadBalance();
    }
  }

  
  
}
