package com.ikaver.aagarwal.fjava.deques;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.common.utils.MathHelper;
import com.ikaver.aagarwal.fjava.EmptyFJavaTask;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * Implementation of sender initiated deque. 
 * 
 * Code based on paper: {@link http://dl.acm.org/citation.cfm?id=2442538}
 * @author ankit
 */
public class SenderInitiatedDeque implements TaskRunnerDeque {

  /**
   * {@code FJavaTask} object which indicates if a task runner is waiting for receive
   * additional tasks.
   */
  private static final FJavaTask WAITING_TO_RECEIVE_TASK = new EmptyFJavaTask();

  /**
   * {@code FJavaTask} object which is used to indicate "NULL" or "EMPTY" task value.
   * This is useful for initializing cells and for preventing spurious references to
   * a previously assigned task value.
   */
  private static final FJavaTask SENTINEL_TASK = new EmptyFJavaTask();

  private final AtomicReference<FJavaTask> communicationCells[];
  private final Deque<FJavaTask> deque;
  private final FastStopwatch acquireStopwatch;
  private FJavaPool pool;

  private final int dequeID;
  private final int numWorkers;
  private final Random random;
  /**
   *  Deal time is the time at which the next {@link SenderInitiatedDeque#attemptDeal}
   *  has to be executed
   */
  private double nextDealTime;

  /**
   * Constructor for {@code SenderInitiatedDeque}
   * 
   * @param communicationCells
   *          is the set of cells used for communication
   * @param deque
   *          is an instance of an empty deque
   * @param dequeID
   *          is the identifier for the {@code FJava

   */
  public SenderInitiatedDeque(AtomicReference<FJavaTask> communicationCells[],
      int dequeID,
      int numWorkers) {
    this.communicationCells = communicationCells;
    this.nextDealTime = -1.0;
    this.deque = new ArrayDeque<FJavaTask>(8192);
    this.dequeID = dequeID;
    this.numWorkers = numWorkers;
    this.random = new Random();

    communicationCells[dequeID].set(SENTINEL_TASK);

    this.acquireStopwatch = new FastStopwatch();
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
  public void tryLoadBalance() {
    if(deque.size() > 1) {
      communicate();
    }
  }

  @Override
  public FJavaTask getTask(FJavaTask parentTask) {
    if(FJavaConf.shouldTrackStats()) {
      StatsTracker.getInstance().onDequeGetTask(this.dequeID);
    }

    if (deque.size() == 0) {
      if(FJavaConf.shouldTrackStats()) {
        StatsTracker.getInstance().onDequeEmpty(this.dequeID);
      }

      acquireStopwatch.start();
      acquire(parentTask);
      if(FJavaConf.shouldTrackStats()) {
        StatsTracker.getInstance().onAcquireTime(
            this.dequeID, acquireStopwatch.end());
      }
      return null;
    }
    else {
      if(FJavaConf.shouldTrackStats()) {
        StatsTracker.getInstance().onDequeNotEmpty(this.dequeID);
      }
      if(deque.size() > 1) {
        this.communicate();
      }
      return deque.removeLast();
    }
  }

  protected void acquire(FJavaTask parentTask) {
    // I am looking to receive additional tasks.
    communicationCells[dequeID].set(WAITING_TO_RECEIVE_TASK);

    // While I have not received a task.
    while (communicationCells[dequeID].get() == WAITING_TO_RECEIVE_TASK) {
      if (parentTask != null && parentTask.areAllChildsDone()) {

        // Someone may have assigned me work even if my parentTask was done. If the check fails,
        // is means that I got assigned some piece of work which may not be in the computation
        // tree of my {@param parentTask} but is a node in some other part of the 
        // computation tree.
        boolean success = communicationCells[dequeID].compareAndSet(WAITING_TO_RECEIVE_TASK,
            SENTINEL_TASK);

        // Process the piece of work assigned. Not processing this work will lead to a lost
        // node in the computation tree and hence the computation will never finish.
        if (!success) {
          break;
        }

        return;
      }
      // There is no more work left. This is required so that the thread does not end up
      // busy waiting while there is no more work.
      else if(pool.isShuttingDown()) {
        return;
      }
    }

    deque.addLast(communicationCells[dequeID].get());
    communicationCells[dequeID].set(SENTINEL_TASK);
  }

  /**
   * Attempt to deal a task to other nodes which are looking for work.
   */
  private void attemptDeal() {
    if (deque.size() == 0) {
      return;
    }

    int victim = random.nextInt(numWorkers);

    if (victim == dequeID) {
      return;
    }

    // See if my victim is looking for some additional work.
    if (communicationCells[victim].get() != WAITING_TO_RECEIVE_TASK) {
      return;
    }

    FJavaTask task = deque.peekFirst();

    // Try to set work on the victim.
    boolean success = communicationCells[victim].compareAndSet(WAITING_TO_RECEIVE_TASK, task);

    if (success) {
      if (FJavaConf.shouldTrackStats()) {
        StatsTracker.getInstance().onSuccessfulTaskDelegation(dequeID);
      }

      // I successfully managed to send the task to the victim. Now, its time
      // that I delete the task from my work queue.
      deque.removeFirst();
    }
  }

  protected void communicate() {
    long now = System.currentTimeMillis();
    if (now > nextDealTime) {
      attemptDeal();
      nextDealTime = now - FJavaConf.getDelta() * Math.log(
          MathHelper.randomBetween(0.2, 0.9));
    }
  }

}
