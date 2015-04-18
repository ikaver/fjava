package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;

public class FJavaPool {
  
  private TaskRunner [] taskRunners;
  private int poolSize;
  private boolean isRunning;
  
  public FJavaPool(int poolSize) {
    this.setup(poolSize);
  }

  public synchronized void run(FJavaTask task) {
    if(this.isRunning) throw new IllegalStateException("This pool is already running a task!");
    this.isRunning = true;
    this.taskRunners[0].addTask(task);
    for(int i = 0; i < this.poolSize; ++i) {
      this.taskRunners[i].startRunning();
    }
  }
  
  private void setup(int poolSize) {
    if(poolSize <= 0) throw new IllegalArgumentException("Pool size should be > 0");
    this.poolSize = poolSize;
    this.taskRunners = new TaskRunner[this.poolSize];
    this.isRunning = false;
    TaskRunnerDeque [] deques = this.getDeques(this.poolSize);
    
    for(int i = 0; i < this.poolSize; ++i) {
      this.taskRunners[i] = new TaskRunner(deques[i], i);
    }

  }

  
  //TODO: move to factory
  private TaskRunnerDeque [] getDeques(int size) {
    TaskRunnerDeque [] deques = new TaskRunnerDeque[size];
    AtomicInteger [] status = new AtomicInteger[size];
    AtomicInteger [] requestCells = new AtomicInteger[size];
    FJavaTask [] responseCells = new FJavaTask[size];
    FJavaTask emptyTask = new EmptyFJavaTask();
    
    for(int i = 0; i < size; ++i) {
      status[i].set(ReceiverInitiatedDeque.INVALID_STATUS);
      requestCells[i].set(ReceiverInitiatedDeque.EMPTY_REQUEST);
      responseCells[i] = emptyTask;
      deques[i] = new ReceiverInitiatedDeque(status, requestCells, responseCells, i, emptyTask);
    }
  
    return deques;
  }
  
}
