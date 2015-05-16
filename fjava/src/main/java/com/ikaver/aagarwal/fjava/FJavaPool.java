package com.ikaver.aagarwal.fjava;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.fjava.deques.TaskRunnerDeque;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * An FJavaPool simply manages a group of Task Runners. 
 * The Fork Join Pool is the entry point of the client to our system. 
 * The client simply submits a task to the fork join pool, and the pool gives 
 * tasks to any Task Runner, and afterwards waits for all of the task 
 * runners to finish.
 */
public class FJavaPool {

  /**
   * The threads of the task runners.
   * threads[i] is the thread of taskRunners[i].
   */
  private Thread [] threads;
  /**
   * The task runners. 
   * The size of the array matches the size of the pool.
   * Each task runner runs on its own thread.
   */
  private TaskRunner[] taskRunners;
  /**
   * The pool size. (poolSize) physical threads are created for this pool.
   */
  private int poolSize;
  /**
   * True iff the pool is currently running.
   */
  private boolean isRunning;

  private FJavaTask rootTask;


  /**
   * Creates an FJavaPool. The pool is ready to run tasks after the constructor
   * is called.
   * @param poolSize The size of the pool.
   * @param deques The deques that the pool should use. Should be all of the 
   * same class.
   */
  FJavaPool(int poolSize, TaskRunnerDeque [] deques) {
    this.setup(poolSize, deques);
  }

  /**
   * Runs the given FJavaTask (task). 
   * Users should call this method to initiate FJava.
   * @param task The task to run
   * @throws IllegalStateException if the pool is already running a task
   */
  public void run(FJavaTask task) {
    if (this.isRunning)
      throw new IllegalStateException("This pool is already running a task!");
    this.isRunning = true;
    this.rootTask = task;
    this.taskRunners[0].addTask(task);
    for (int i = 0; i < this.poolSize; ++i) {
      this.taskRunners[i].setRootTask(task);
      this.threads[i].start();
    }

    for(int i = 0; i < this.poolSize; ++i) {
      try{
        this.threads[i].join();
      }
      catch(InterruptedException e) { }
    }
    
    if (FJavaConf.shouldTrackStats()) {
      StatsTracker.getInstance().printStats();
    }
  }
  
  /**
   * Returns true iff the pool is currently shutting down
   * @return true iff the pool is currently shutting down
   */
  public boolean isShuttingDown() {
    return this.rootTask.isDone();
  }

  private void setup(int poolSize, TaskRunnerDeque [] deques) {
    if (poolSize <= 0)
      throw new IllegalArgumentException("Pool size should be > 0");

    if (FJavaConf.shouldTrackStats()) {
      StatsTracker.getInstance().setup(poolSize);
    }

    this.poolSize = poolSize;
    this.threads = new Thread[this.poolSize];
    this.taskRunners = new TaskRunner[this.poolSize];
    this.isRunning = false;

    for (int i = 0; i < this.poolSize; ++i) {
      deques[i].setupWithPool(this);
      this.taskRunners[i] = new TaskRunner(deques[i], i);
      this.threads[i] = new Thread(this.taskRunners[i], "Task runner " + i);
    }

  }

}
