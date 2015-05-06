package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

public abstract class FJavaTask {

  private TaskRunner  runner;
  private FJavaTask parent;
  private AtomicInteger childCompleteCount;
  private boolean     isDone;    // TODO: is it necessary to be volatile?

  public FJavaTask() {
    this.childCompleteCount = new AtomicInteger(0);
    this.isDone = false;
  }

  public abstract void compute();

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

  public void sync() {
    this.runner.syncTask(this);
  }

  void execute(TaskRunner runner) {
    this.runner = runner;
    this.compute();
    this.setIsDone(true);
    this.parent = null;
  }

  boolean areAllChildsDone() {
    return this.childCompleteCount.get() == 0;
  }

  boolean isDone() {
    return this.isDone;
  }

  void setIsDone(boolean done) {
    if (this.isDone && done == false)
      throw new IllegalStateException("Cannot 'undo' a done task");
    this.isDone = done;
    if(this.isDone) {
      if(this.parent != null) this.parent.onChildDone();
    }
  }
  
  /**
   * Indicates the amount of time we spent on "actually" doing something
   * suseful for the task.
   */
  public void addComputeTime(long amount) {
  	if (FJavaConf.shouldTrackStats()) {
  		int taskRunnerIdX = this.runner.getTaskRunnerID();
  		// Add this amount of time to the computer counter.
  		StatsTracker.getInstance().onComputeTime(taskRunnerIdX, amount);
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
