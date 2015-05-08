package com.ikaver.aagarwal.fjava.deques;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadLocalRandom;

import sun.misc.Unsafe;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.common.UnsafeHelper;
import com.ikaver.aagarwal.fjava.EmptyFJavaTask;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * Represents a Sender Initiated Deque. This means that task runners that need
 * tasks to run (receivers) are the ones who ask others for tasks.
 * 
 * Code based on paper: {@link http://dl.acm.org/citation.cfm?id=2442538}
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
   * {@code FJavaTask} used to differentiate "not responded yet" from "sorry, 
   * I have no tasks for you" responses in the responseCells array.
   */
  private static final FJavaTask emptyTask = new EmptyFJavaTask();
  
  /**
   * Reference to UNSAFE. Nasty implementation needed to speed up our code.
   */
  private static final Unsafe UNSAFE = UnsafeHelper.getUnsafe();
 
  /**
   * Our deque of tasks.
   */
  private ArrayDeque<FJavaTask> tasks;
  
  /**
   * Indicates the status of the current deque.
   * status[i] == VALID_STATUS iff deque i has some work to offer to idle threads
   * else, status[i] = INVALID_STATUS
   */
  private int [] status; 

  /**
   * requestCells[i] = j iff task runner j is waiting for task runner i to 
   * give him work
   */
  private int [] requestCells;
    
  /**
   * responseCells[j] holds the task that task runner j stole from other
   * task runner (specifically, where j put his id in requestCells array).
   */
  private FJavaTask [] responseCells;
  
  /**
   * Index that we have reserved in the requestCells array. We are
   * still waiting for a response from the other task runner, but we had
   * to quit temporarily to handle a sync request.
   */
  private int reservedRequestCell = EMPTY_REQUEST;
  
  /**
   * Holds the state of the current status of this deque.
   * Indicates INVALID_STATUS if we currently have no tasks to offer to other
   * deques, else it indicates VALID_STATUS.
   * Optimization to avoid reading volatile variables a lot.
   */
  private int localStatus;

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
  
  public ReceiverInitiatedDeque(int [] status, 
      int [] requestCells, FJavaTask [] responseCells, int dequeID) {
    for(int i = 0; i < responseCells.length; ++i) {
      if(requestCells[16*i] != EMPTY_REQUEST) 
        throw new IllegalArgumentException("All request cells should be EMPTY_REQUEST initially");
      if(status[16*i] != INVALID_STATUS)
        throw new IllegalArgumentException("All status should be INVALID_STATUS initially");
      responseCells[i] = emptyTask;
    }
    this.status = status;
    this.requestCells = requestCells;
    this.responseCells = responseCells;
    this.dequeID = dequeID;
    this.numWorkers = this.responseCells.length;
    this.tasks = new ArrayDeque<FJavaTask>(8192);
    this.localStatus = INVALID_STATUS;
    
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
  
  /**
   * Respond to steal requests, if any.
   */
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
      //update my status, respond to steal requests, and update my status again.
      updateStatus();
      communicate();
      updateStatus();
      return task;
    }
  }
    
  /**
   * Called whenever there are no tasks in this deque.
   * This code will return whenever we manage to steal a task, or 
   * the parent task we are syncing on has finished syncing, or 
   * the pool has shutted down.
   */
  private void acquire(FJavaTask parentTask) {
    //Try for as much as possible to steal a task. If the parent task
    //is done syncing, or if the pool has no more tasks, I must quit.
    while(parentTask == null || !parentTask.areAllChildsDone()) {
      int stealIdx = ThreadLocalRandom.current().nextInt(this.numWorkers);
      int offset = INT_ARRAY_BASE + 16 * stealIdx * INT_ARRAY_SCALE;
      if(reservedRequestCell != EMPTY_REQUEST || (UNSAFE.getIntVolatile(status, offset) == VALID_STATUS 
          && UNSAFE.compareAndSwapInt(requestCells, offset, EMPTY_REQUEST, this.dequeID))) {
          //We must use the old reserved request cell if we had one
          //(There might be a task waiting for us to run in our responseCells array,
          //since the other deque might have responded while we did the sync).
          if(reservedRequestCell != EMPTY_REQUEST) stealIdx = reservedRequestCell;
          
          int fjoffset = RESPONSE_CELLS_BASE + this.dequeID * RESPONSE_CELLS_SCALE;
          while(UNSAFE.getObjectVolatile(this.responseCells, fjoffset) == emptyTask) {
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
          if(this.responseCells[this.dequeID] != null 
              && this.responseCells[this.dequeID] != emptyTask) {
            //awesome, we got a task. add it to the deque and quit.
            FJavaTask newTask = this.responseCells[this.dequeID];
            this.addTask(newTask);
            if(FJavaConf.shouldTrackStats()) { 
              StatsTracker.getInstance().onDequeSteal(this.dequeID);
            }
          }
          //clear out entry of the response cells array, we got out response.
          this.responseCells[this.dequeID] = emptyTask;
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
    int offset = INT_ARRAY_BASE + 16 * this.dequeID * INT_ARRAY_SCALE;
    int requestIdx = UNSAFE.getIntVolatile(this.requestCells, offset);
    if(requestIdx == EMPTY_REQUEST) return false; //no steal requests, quick quit.
    
    boolean didCommunicate = true;
    int fjoffset = RESPONSE_CELLS_BASE + requestIdx * RESPONSE_CELLS_SCALE;
    if(this.tasks.isEmpty()) {
      //we were available at the time the other deque requested work to us,
      //but not anymore! respond with null
      didCommunicate = false;
      UNSAFE.putObjectVolatile(this.responseCells, fjoffset, null);
    }
    else {
      //We have work for the other deque! Give work to them.
      FJavaTask task =  this.tasks.removeFirst();
      UNSAFE.putObjectVolatile(this.responseCells, fjoffset, task);
    }
    //Responded the request successfully, clear my entry of the request cells
    //array for others to be able to request work to me.
    UNSAFE.putIntVolatile(this.requestCells, offset, EMPTY_REQUEST);
    return didCommunicate;
  }
  
  /**
   * Update status, let it be to indicate others that I'm available to give
   * work to them, or to indicate others that I do not have tasks currently.
   */
  /**
   * Update status, let it be to indicate others that I'm available to give
   * work to them, or to indicate others that I do not have tasks currently.
   */
  private void updateStatus() {
    int newValue = this.tasks.size() > 0 ? VALID_STATUS : INVALID_STATUS;
    if(this.localStatus != newValue) { 
      int offset = INT_ARRAY_BASE + 16 * this.dequeID * INT_ARRAY_SCALE;
      UNSAFE.putIntVolatile(this.status, offset, newValue);
      this.localStatus = newValue;
    }
  }
  
  /** Code for setting up UNSAFE here **/
  
  private static final int INT_ARRAY_BASE;
  private static final int INT_ARRAY_SCALE;
  private static final int RESPONSE_CELLS_BASE;
  private static final int RESPONSE_CELLS_SCALE;

  static {
      try {
          Class<?> intarray = int[].class;
          Class<?> fjarray = FJavaTask[].class;
          INT_ARRAY_BASE = UNSAFE.arrayBaseOffset(intarray);
          INT_ARRAY_SCALE = UNSAFE.arrayIndexScale(intarray);
          RESPONSE_CELLS_BASE = UNSAFE.arrayBaseOffset(fjarray);
          RESPONSE_CELLS_SCALE = UNSAFE.arrayIndexScale(fjarray);
      } catch (Exception e) {
          throw new Error(e);
      }
  }
}