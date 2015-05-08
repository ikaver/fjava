package com.ikaver.aagarwal.fjava.deques;

import java.util.concurrent.LinkedBlockingDeque;

import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

/**
 * Implementation of the FJava framework with a single shared concurrent queue.
 * In the single concurrent queue we push the work to the back of the queue and
 * remove "tasks" from the front.
 * 
 * We expect this strategy to perform slower than the existing strategies. Also,
 * 
 */
public class SharedConcurrentQueue implements TaskRunnerDeque {

	private final LinkedBlockingDeque<FJavaTask> queue;
	private FJavaPool pool;

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
		this.pool = pool;
	}

  @Override
  public void tryLoadBalance() {
    //NOOP
  }
}
