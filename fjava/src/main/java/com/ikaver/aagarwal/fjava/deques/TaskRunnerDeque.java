package com.ikaver.aagarwal.fjava.deques;

import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

/**
 * Interface which has to be implemented by a deque for task runner.
 */
public interface TaskRunnerDeque {
  
	/**
	 * Adds a new task to the deque.
	 * 
	 * @param task is the new piece of work.
	 */
  public void addTask(FJavaTask task);

  /**
   * Gets a new task from the deque.
   * 
   * @param task is the parent task of the execution/computation tree. This is null
   * when we are executing a new piece of work and non-null when 
   * a {@code FJavaTask#sync()} is called.
   * 
   * @return a new piece of work or a null. A null return value means that either the
   *     there are no pieces of work left or the "execution tree" referred to by the task 
   *     has finished execution. 
   */
  public FJavaTask getTask(FJavaTask task);
  
  /**
   * Tries to respond to steal requests or to somehow, distribute work
   * of this deque to other deques
   */
  public void tryLoadBalance();

  /**
   * This is a one time setup method which is invoked when the pool creates new threads for
   * task runners.
   * 
   * @param pool is the pool to which the deque belongs.
   */
  public void setupWithPool(FJavaPool pool);
}
