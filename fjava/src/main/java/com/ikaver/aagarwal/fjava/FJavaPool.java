package com.ikaver.aagarwal.fjava;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.fjava.deques.TaskRunnerDeque;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

public class FJavaPool {

  private Thread [] threads;
	private TaskRunner[] taskRunners;
	private int poolSize;
	private boolean isRunning;
	
	private FJavaTask rootTask;


	FJavaPool(int poolSize, TaskRunnerDeque [] deques) {
		this.setup(poolSize, deques);
	}

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
		
		try {
		  for(int i = 0; i < this.poolSize; ++i) {
		    this.threads[i].join();
		  }
    } 
		catch (InterruptedException e) {
      e.printStackTrace();
    }

		if (FJavaConf.shouldTrackStats()) {
			StatsTracker.getInstance().printStats();
		}
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

	public boolean isShuttingDown() {
	  return this.rootTask.isDone();
	}


}
