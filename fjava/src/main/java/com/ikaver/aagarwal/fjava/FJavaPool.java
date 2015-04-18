package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

public class FJavaPool {
  
  private TaskRunner [] taskRunners;
  private int poolSize;
  private boolean isRunning;
    
  public FJavaPool() {
    this(Runtime.getRuntime().availableProcessors());
  }
  
  public FJavaPool(int poolSize) {
    this.setup(poolSize);
  }

  public synchronized void run(FJavaTask task) {
    //TODO: record total running time
    if(this.isRunning) 
      throw new IllegalStateException("This pool is already running a task!");
    this.isRunning = true;
    this.taskRunners[0].addTask(task);
    for(int i = 0; i < this.poolSize; ++i) {
      this.taskRunners[i].startRunning();
    }
    while(!task.isDone()) {
      //TODO: remove busy waiting
    }
    LogManager.getLogger().warn("SHUTTING DOWN WORKERS");
    for(int i = 0; i < this.poolSize; ++i) {
      this.taskRunners[i].setShouldShutdown(true);
    }
    if(Definitions.TRACK_STATS) 
      StatsTracker.getInstance().printStats();
  }
  
  private void setup(int poolSize) {    
    if(poolSize <= 0) 
      throw new IllegalArgumentException("Pool size should be > 0");

    if(Definitions.TRACK_STATS) StatsTracker.getInstance().setup(poolSize);

    
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
    RefInt [] status = new RefInt[size];
    AtomicInteger [] requestCells = new AtomicInteger[size];
    FJavaTaskRef [] responseCells = new FJavaTaskRef[size];
    FJavaTask emptyTask = new EmptyFJavaTask(null);
    
    for(int i = 0; i < size; ++i) {
      status[i] = new RefInt(ReceiverInitiatedDeque.INVALID_STATUS);
      requestCells[i] = new AtomicInteger(ReceiverInitiatedDeque.EMPTY_REQUEST);
      responseCells[i] = new FJavaTaskRef(emptyTask);
    }
    
    for(int i = 0; i < size; ++i) {
      deques[i] = new ReceiverInitiatedDeque(status, requestCells, responseCells, i, emptyTask);
    }
  
    return deques;
  }
  
}
