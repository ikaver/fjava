package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * A simple class that encapsulates computation, some piece of work that 
 * needs to be done. A FJavaTask can potentially generate child tasks, 
 * that are added to the Task Runners deque to be computed later.
 */
public abstract class FJavaTask {

  private TaskRunner runner;
  private FJavaTask parent;
  private AtomicInteger childCompleteCount;
  private volatile boolean isDone; 

  public FJavaTask() {
    this.childCompleteCount = new AtomicInteger(0);
    this.isDone = false;
  }

  /**
   * The main computation that needs to be done. Defined by the user.
   */
  public abstract void compute();

  /**
   * This operation is called on child tasks created by an FJavaTask. 
   * It means that this child task should be run asynchronously, 
   * without interrupting execution of the parent task or any other child task. 
   * The call is simply forwarded to the task runner of the parent task, 
   * and the child task is added to the back of the deque of the parent task.
   * @param parent The parent task of this FJavaTask.
   */
  public void runAsync(FJavaTask parent) {
    parent.addChild(this);
    this.runner = parent.runner;
    this.runner.addTask(this);
  }

  /**
   * This operation is called on child tasks created by an FJavaTask. 
   * It means that the child task should be run synchronously, i.e the parent 
   * task blocks until the child task completes. 
   * Simply set the task runner of the child task and run the task synchronously.
   * @param parent
   */
  public void runSync(FJavaTask parent) {
    this.runner = parent.runner;
    this.compute();
    this.setIsDone(true);
  }

  /**
   * This operation blocks until all of the child tasks created by the 
   * FJavaTask complete. Usually, it is called by the client at the end of
   * every compute method.
   */
  public void sync() {
    this.runner.syncTask(this);
  }
  
  /**
   * When called, the deque associated to the task runner running the task 
   * tries to service any steal requests that it has pending.
   * This method should be called periodically in compute methods where the 
   * sequential threshold is large.
   * The user can call tryLoadBalance anywhere inside the compute method. 
   */
  public void tryLoadBalance() {
    this.runner.tryLoadBalance();
  }
  
  /**
   * True iff all of the child tasks are done.
   * @return True iff all of the child tasks are done.
   */
  public boolean areAllChildsDone() {
    return this.childCompleteCount.get() == 0;
  }
  
  /**
   * True iff this FJavaTask is done computing.
   * @return True iff this FJavaTask is done computing.
   */
  public boolean isDone() {
    return this.isDone;
  }
  
  /**
   * Indicates the amount of time we spent on "actually" doing something
   * suseful for the task.
   */
  public void addComputeTime(long amount) {
    if (FJavaConf.shouldTrackStats()) {
      int taskRunnerIdx = this.runner.getTaskRunnerID();
      // Add this amount of time to the computer counter.
      StatsTracker.getInstance().onComputeTime(taskRunnerIdx, amount);
    }
  }
  

  void execute(TaskRunner runner) {
    this.runner = runner;
    this.compute();
    this.setIsDone(true);
    this.parent = null;
  }

  void setIsDone(boolean done) {
    if (this.isDone && done == false)
      throw new IllegalStateException("Cannot 'undo' a done task");
    this.isDone = done;
    if(this.isDone) {
      if(this.parent != null) this.parent.onChildDone();
    }
  }
  
  void onChildDone() {
    this.childCompleteCount.decrementAndGet();
  }

  private void addChild(FJavaTask task) {
    task.parent = this;
    this.childCompleteCount.incrementAndGet();
  }
}
