package com.ikaver.aagarwal.fjava;

public class FJava {
  
  private Thread [] dequeThreads;
  private Thread [] runnerThreads;
  private int poolSize;
  
  public FJava(int poolSize) {
    this.setup(poolSize);
  }

  
  private void setup(int poolSize) {
    if(poolSize <= 0) throw new IllegalArgumentException("Pool size should be > 0");
    this.poolSize = poolSize;
    this.dequeThreads = new Thread[this.poolSize];
    this.runnerThreads = new Thread[this.poolSize];
    
    for(int i = 0; i < this.poolSize; ++i) {
      this.dequeThreads[i] = new Thread("Deque " + i);
      this.runnerThreads[i] = new Thread("Runner " + i);
    }    
  }
}
