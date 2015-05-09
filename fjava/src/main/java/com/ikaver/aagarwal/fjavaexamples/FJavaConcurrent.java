package com.ikaver.aagarwal.fjavaexamples;

import com.ikaver.aagarwal.fjava.FJavaTask;

public class FJavaConcurrent extends FJavaTask {
  
  private final FJavaTask [] tasks;
  
  public FJavaConcurrent(FJavaTask ... tasks) {
    this.tasks = tasks;
  }

  @Override
  public void compute() {
    for(int i = 0; i < tasks.length-1; ++i) {
      tasks[i].runAsync(this);
    }
    tasks[tasks.length-1].runSync(this);
    this.sync();
  }
}
