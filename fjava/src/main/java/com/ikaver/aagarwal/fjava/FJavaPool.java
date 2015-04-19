package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

public class FJavaPool {

	private TaskRunner[] taskRunners;
	private int poolSize;
	private boolean isRunning;


	FJavaPool(int poolSize, TaskRunnerDeque [] deques) {
		this.setup(poolSize, deques);
	}

	public synchronized void run(FJavaTask task) {
		// TODO: record total running time
		if (this.isRunning)
			throw new IllegalStateException("This pool is already running a task!");
		this.isRunning = true;
		this.taskRunners[0].addTask(task);
		for (int i = 0; i < this.poolSize; ++i) {
			this.taskRunners[i].startRunning();
		}
		while (!task.isDone()) {
			// TODO: remove busy waiting
		}
		for (int i = 0; i < this.poolSize; ++i) {
			this.taskRunners[i].setShouldShutdown(true);
		}
		if (Definitions.TRACK_STATS)
			StatsTracker.getInstance().printStats();
	}

	private void setup(int poolSize, TaskRunnerDeque [] deques) {
		if (poolSize <= 0)
			throw new IllegalArgumentException("Pool size should be > 0");

		if (Definitions.TRACK_STATS)
			StatsTracker.getInstance().setup(poolSize);

		this.poolSize = poolSize;
		this.taskRunners = new TaskRunner[this.poolSize];
		this.isRunning = false;

		for (int i = 0; i < this.poolSize; ++i) {
			this.taskRunners[i] = new TaskRunner(deques[i], i);
		}

	}



}
