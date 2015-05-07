package com.ikaver.aagarwal.fjava;

import java.util.ArrayDeque;
import java.util.Random;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * Represents a Sender Initiated Deque. This means that task runners that need
 * tasks to run (receivers) are the ones who ask others for tasks.
 */
public class ReceiverInitiatedDeque implements TaskRunnerDeque {

  /**
   * VALID_STATUS indicates that this Deque (possibly) has work to offer.
   * This means that it is ok for other Deques to request work to me.
   */
  public static final int VALID_STATUS = 1;
  /**
   * INVALID_STATUS indicates that this Deque does not have work to offer.
   * This means that it is not of for other Deques to request work to me.
   */
  public static final int INVALID_STATUS = 0;
  /**
   * Whenever requestCells[i] == EMPTY_REQUEST, it means that at this moment 
   * nobody is waiting for me to give them work.
   */
  public static final int EMPTY_REQUEST = -1;

  /**
   * Our deque of tasks.
   */
  private ArrayDeque<FJavaTask> tasks;
  
  /**
   * Indicates the status of the current deque.
   * status[i] == VALID_STATUS iff deque i has some work to offer to idle threads
   * else, status[i] = INVALID_STATUS
   */
  private IntRef [] status; 

  /**
   * requestCells[i] = j iff task runner j is waiting for task runner i to 
   * give him work
   */
  private PaddedAtomicInteger [] requestCells;
  
  /**
   * responseCells[j] holds the task that task runner j stole from other
   * task runner (specifically, where j put his id in requestCells array).
   */
  private FJavaTaskRef [] responseCells;
  
  /**
   * Index that we have reserved in the requestCells array. We are
   * still waiting for a response from the other task runner, but we had
   * to quit temporarily to handle a sync request.
   */
  private int reservedRequestCell = EMPTY_REQUEST;
  
  /**
   * An empty task, used to differentiate "not responded yet" from "sorry, 
   * I have no tasks for you" responses in the responseCells array.
   */
  private static final FJavaTask emptyTask = new EmptyFJavaTask();
  
  /**
   * A random number generator to find victim task runners.
   */
  private Random random;

  /**
   * The ID of this deque.
   */
  private int dequeID;
  
  /**
   * The total number of other workers in the fork join system.
   */
  private int numWorkers;
  
  /**
   * The pool that is responsible for this deque. We should ask the pool
   * periodically if all of the tasks are done.
   */
  private FJavaPool pool;
  
  /*
   * Stopwatch used for measuring time spent in acquire.
   */
  private FastStopwatch acquireStopwatch;
  
  public ReceiverInitiatedDeque(IntRef [] status, 
      PaddedAtomicInteger [] requestCells, FJavaTaskRef [] responseCells, int dequeID) {
    for(int i = 0; i < requestCells.length; ++i) {
      if(requestCells[i].get() != EMPTY_REQUEST) 
        throw new IllegalArgumentException("All request cells should be EMPTY_REQUEST initially");
      if(status[i].value != INVALID_STATUS)
        throw new IllegalArgumentException("All status should be INVALID_STATUS initially");
      responseCells[i] = new FJavaTaskRef(emptyTask);
    }
    this.status = status;
    this.requestCells = requestCells;
    this.responseCells = responseCells;
    this.random = new Random();    
    this.dequeID = dequeID;
    this.numWorkers = this.status.length;
    this.tasks = new ArrayDeque<FJavaTask>(8192);
    
    this.acquireStopwatch = new FastStopwatch();
  } 
  
  /**
   * Any additional initialization required can be done here.
   */
  public void setupWithPool(FJavaPool pool) { 
    this.pool = pool;
  }
  
  /**
   * Adds a task to this deque. Should be only called by the associated
   * task runners thread, once the thread has started running.
   */
  public void addTask(FJavaTask task) {
    if(task == null) 
        throw new IllegalArgumentException("Task cannot be null");

    this.tasks.addLast(task);
    this.updateStatus();
    this.communicate();
    this.updateStatus();
  }
  
  public void tryLoadBalance() {
    this.communicate();
    this.updateStatus();
  }
  
  /**
   * Returns a task from this deque, or a stolen task, possibly.
   * May return null if it failed to steal any tasks.
   * Caller should try again if necessary.
   */
  public FJavaTask getTask(FJavaTask parentTask) {
    if(FJavaConf.shouldTrackStats()) {
      StatsTracker.getInstance().onDequeGetTask(this.dequeID);
    }

    if(this.tasks.isEmpty()) {
      //we have no tasks, must try to steal from somebody else!
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
      //I have tasks! Let everybody know by updating my status.
      if(FJavaConf.shouldTrackStats()) {
        StatsTracker.getInstance().onDequeNotEmpty(this.dequeID);
      }
      FJavaTask task = this.tasks.removeLast();
      updateStatus();
      communicate();
      updateStatus();
      return task;
    }
  }
    
  /**
   * Called whenever there are no tasks in this deque
   * @return
   */
  private void acquire(FJavaTask parentTask) {
    //Try for as much as possible to steal a task. If the parent task
    //is done syncing, or if the pool has no more tasks, I must quit.
    while(parentTask == null || !parentTask.areAllChildsDone()) {
      int stealIdx = this.random.nextInt(this.numWorkers);
      if(reservedRequestCell != EMPTY_REQUEST || (status[stealIdx].value == VALID_STATUS 
          && requestCells[stealIdx].compareAndSet(EMPTY_REQUEST, this.dequeID))) {
          //We must use the old reserved request cell if we had one
          //(There might be a task waiting for us to run in our responseCells array,
          //since the other deque might have responded while we did the sync).
          if(reservedRequestCell != EMPTY_REQUEST) stealIdx = reservedRequestCell;
          
          while(this.responseCells[this.dequeID].task == emptyTask) {
            //must communicate in case somebody was expecting to receive a task from me
            this.communicate(); 
            //my parent is done syncing! Must quit to let it proceed.
            if(parentTask != null && parentTask.areAllChildsDone() || pool.isShuttingDown()) {
              //however, we cannot cancel a request once we made one, so 
              //save the index of the deque we requested a task to 
              reservedRequestCell = stealIdx; 
              return;
            }
          }
          //Finally (possibly) got a task! Clear the reserved request cell and
          //check what we got!
          reservedRequestCell = EMPTY_REQUEST;
          if(this.responseCells[this.dequeID].task != null 
              && this.responseCells[this.dequeID].task != emptyTask) {
            //awesome, we got a task. add it to the deque and quit.
            FJavaTask newTask = this.responseCells[this.dequeID].task;
            this.addTask(newTask);
            if(FJavaConf.shouldTrackStats()) { 
              StatsTracker.getInstance().onDequeSteal(this.dequeID);
            }
          }
          //clear out entry of the response cells array, we got out response.
          this.responseCells[this.dequeID].task = emptyTask;
          return;
      }
      //must communicate in case somebody was expecting to receive a task from me
      this.communicate();
      if(pool.isShuttingDown()) break;
    }
  }
  
  /*
   * Respond to steal requests, if any.
   */
  private boolean communicate() {
    int requestIdx = this.requestCells[this.dequeID].get();
    if(requestIdx == EMPTY_REQUEST) return false; //no steal requests, quick quit.
    
    boolean didCommunicate = true;
    if(this.tasks.isEmpty()) {
      //we were available at the time the other deque requested work to us,
      //but not anymore! respond with null
      didCommunicate = false;
      this.responseCells[requestIdx].task = null;
    }
    else {
      //We have work for the other deque! Give work to them.
      FJavaTask task =  this.tasks.removeFirst();
      this.responseCells[requestIdx].task = task;
    }
    //Responded the request successfully, clear my entry of the request cells
    //array for others to be able to request work to me.
    this.requestCells[this.dequeID].set(EMPTY_REQUEST);
    return didCommunicate;
  }
  
  /**
   * Update status, let it be to indicate others that I'm available to give
   * work to them, or to indicate others that I do not have tasks currently.
   */
  private void updateStatus() {
    int available = this.tasks.size() > 0 ? VALID_STATUS : INVALID_STATUS;
    if(this.status[this.dequeID].value != available) 
      this.status[this.dequeID].value = available;
  }
}
