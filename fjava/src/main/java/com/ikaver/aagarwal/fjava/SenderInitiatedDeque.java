package com.ikaver.aagarwal.fjava;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.MathHelper;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * Implementation of sender initiated deque. Taken from {@link http
 * ://dl.acm.org/citation.cfm?id=2442538}
 * 
 * @author ankit
 */
public class SenderInitiatedDeque implements TaskRunnerDeque {

	private static final double DELTA = 10;
	
	private static final FJavaTask DUMMY_TASK = new EmptyFJavaTask(null);
	private static final FJavaTask INIT_TASK = new EmptyFJavaTask(null);

	private final AtomicReference<FJavaTask> communicationCells[];
	private final double nextDealTime[];
	private final Deque<FJavaTask> deque;
	private FJavaPool pool;

	private final int myIdx;
	private final int numWorkers;
	private final Random random;

	/**
	 * Constructor for {@code SenderInitiatedDeque}
	 * 
	 * @param communicationCells
	 *          is the set of cells used for communication
	 * @param nextDealTime
	 *          is the next deal time, i.e. the time at which
	 *          {@code SenderInitiatedDeque#dealAttempt} should be called.
	 * @param deque
	 *          is an instance of an empty deque
	 * @param myIdx
	 *          is the identifier for the {@code FJava

	 */
	public SenderInitiatedDeque(AtomicReference<FJavaTask> communicationCells[],
			double[] nextDealTime, int myIdx, int numWorkers) {
		this.communicationCells = communicationCells;
		this.nextDealTime = nextDealTime;
		this.deque = new ArrayDeque<FJavaTask>();
		this.myIdx = myIdx;
		this.numWorkers = numWorkers;
		this.random = new Random();

		communicationCells[myIdx].set(INIT_TASK);
	}
	
	public void setupWithPool(FJavaPool pool) {
	  this.pool = pool;
	}

	@Override
	public void addTask(FJavaTask task) {
		deque.addLast(task);
		if (deque.size() > 1) {			
			// I have some spare tasks. Let's see if I can send some of my work around.
	  	communicate();
		}
	}

	@Override
	public FJavaTask getTask(FJavaTask parentTask) {
		if (deque.size() == 0) {
			acquire(parentTask);
		}

		if (deque.size() == 0) {
			return null;
		} else {
			return deque.removeLast();
		}
	}

	protected void acquire(FJavaTask parentTask) {
		// I am looking to receive additional tasks.
		communicationCells[myIdx].set(DUMMY_TASK);

		// While I have not received a task.
		while (communicationCells[myIdx].get() == DUMMY_TASK) {
			if (parentTask != null && parentTask.areAllChildsDone()) {

				boolean success = communicationCells[myIdx].compareAndSet(DUMMY_TASK,
						INIT_TASK);

				if (!success) {
					break;
				}

				return;
			}
			else if(pool.isShuttingDown()) {
			  return;
			}
		}

		deque.addLast(communicationCells[myIdx].get());
		communicationCells[myIdx].set(INIT_TASK);
	}

	protected void dealAttempt() {
		if (deque.size() == 0) {
			return;
		}

		int victim = random.nextInt(numWorkers);
		
		if (victim == myIdx) {
			return;
		}
    
		// See if my victim is looking for some additional work.
		if (communicationCells[victim].get() != DUMMY_TASK) {
			return;
		}

		FJavaTask task = deque.peekFirst();
						
		// Try to set work on the victim.
		boolean success = communicationCells[victim].compareAndSet(DUMMY_TASK, task);

		if (success) {
			if (Definitions.TRACK_STATS) {
				StatsTracker.getInstance().onSuccessfulTaskDelegation(myIdx);
			}
			
			// I successfully managed to send the task to the victim. Now, its time
			// that I delete the task from my work queue.
			deque.removeFirst();
		}
	}

	protected void communicate() {
		long now = System.currentTimeMillis();
		if (now > nextDealTime[myIdx]) {
			dealAttempt();
			nextDealTime[myIdx] = now - DELTA * Math.log(
					MathHelper.randomBetween(0.2, 0.9));
		}
	}
}
