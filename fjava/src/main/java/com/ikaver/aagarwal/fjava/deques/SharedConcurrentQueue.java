package com.ikaver.aagarwal.fjava.deques;

import java.util.concurrent.LinkedBlockingDeque;

import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

/**
 * Implementation of the FJava framework with a single shared concurrent queue.
 * In the single concurrent queue we push the work to the back of the queue and
 * remove "tasks" from the front.
 * 
 * We expect this strategy to perform slower than the existing strategies.
 * Note that this class was created for the sole purpose of benchmarking and
 * will NOT be shipped with the production API.
 * 
 */
public class SharedConcurrentQueue implements TaskRunnerDeque {

	private final LinkedBlockingDeque<FJavaTask> queue;

	public SharedConcurrentQueue() {
		queue = new LinkedBlockingDeque<>();
	}

	@Override
	public void addTask(FJavaTask task) {
		queue.add(task);
	}

	@Override
	public FJavaTask getTask(FJavaTask parentTask) {
		return queue.poll();
	}

	@Override
	public void setupWithPool(FJavaPool pool) {

	}

  @Override
  public void tryLoadBalance() {
    //NOOP
  }
}
