package com.ikaver.aagarwal.fjava;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.fjava.stats.StatsTracker;

/**
 * Represents a Sender Initiated Deque. This means that task runners that need
 * tasks to run (receivers) are the ones who ask others for tasks.
 */
public class ReceiverInitiatedDeque implements TaskRunnerDeque {

  public static final int VALID_STATUS = 1;
  public static final int INVALID_STATUS = 0;
  public static final int EMPTY_REQUEST = -1;
  private static final int TRIES_BEFORE_QUIT = 16;

  /**
   * Our deque of tasks.
   */
  private Deque<FJavaTask> tasks;
  
  /**
   * Indicates the status of the current deque.
   * status[i] == VALID_STATUS iff deque i has some work to offer to idle threads
   * else, status[i] = INVALID_STATUS
   */
  private IntRef [] status; //TODO: make cache efficient? (False sharing)   

  /**
   * requestCells[i] = j iff task runner j is waiting for task runner i to 
   * give him work
   */
  private AtomicInteger [] requestCells;   //TODO: make cache efficient? (False sharing)
  
  //TODO: make cache efficient? (False sharing)
  //responseCells[j] holds the task that task runner j stole from other task runner.
  
  /**
   * responseCells[j] holds the task that task runner j stole from other
   * task runner (specifically, where j put his id in requestCells array).
   */
  private FJavaTaskRef [] responseCells;
  
  /**
   * Index that we have reserved in the requestCells array. We are
   * still waiting for a response from the other task runner, but we had
   * to quit temporarily to handle a join request.
   */
  private int reservedRequestCell = EMPTY_REQUEST;
  
  /**
   * An empty task, used to differentiate "not responded yet" from "sorry, 
   * I have no tasks for you" responses in the responseCells array.
   */
  private FJavaTask emptyTask;
  
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
  
  private FJavaPool pool;
  
  private FastStopwatch acquireStopwatch;
  
  public ReceiverInitiatedDeque(IntRef [] status, 
      AtomicInteger [] requestCells, FJavaTaskRef [] responseCells, int dequeID, 
      FJavaTask emptyTask) {
    for(int i = 0; i < requestCells.length; ++i) {
      if(requestCells[i].get() != EMPTY_REQUEST) 
        throw new IllegalArgumentException("All request cells should be EMPTY_REQUEST initially");
      if(responseCells[i].task != emptyTask)
        throw new IllegalArgumentException("All response cells should be empty");
      if(status[i].value != INVALID_STATUS)
        throw new IllegalArgumentException("All status should be INVALID_STATUS initially");
    }
    if(emptyTask == null) {
      throw new IllegalArgumentException("Empty task shouldn't be null");
    }
    
    this.status = status;
    this.requestCells = requestCells;
    this.responseCells = responseCells;
    this.random = new Random();    
    this.dequeID = dequeID;
    this.numWorkers = this.status.length;
    this.tasks = new ArrayDeque<FJavaTask>();
    this.emptyTask = emptyTask;
    
    this.acquireStopwatch = new FastStopwatch();
  } 
  
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
  }
  
  /**
   * Returns a task from this deque, or a stolen task, possibly.
   * May return null if it failed to steal any tasks.
   * Caller should try again if necessary.
   */
  public FJavaTask getTask(FJavaTask parentTask) {
    if(FJavaConf.getInstance().shouldTrackStats()) {
      StatsTracker.getInstance().onDequeGetTask(this.dequeID);
    }

    if(this.tasks.isEmpty()) {
      if(FJavaConf.getInstance().shouldTrackStats()) {
        StatsTracker.getInstance().onDequeEmpty(this.dequeID);
      }
      acquireStopwatch.start();
      acquire(parentTask);
      if(FJavaConf.getInstance().shouldTrackStats()) {
        StatsTracker.getInstance().onAcquireTime(
        		this.dequeID, acquireStopwatch.end());
      }
      return null;
    }
    else {
      if(FJavaConf.getInstance().shouldTrackStats()) {
        StatsTracker.getInstance().onDequeNotEmpty(this.dequeID);
      }
      FJavaTask task = this.tasks.removeLast();
      updateStatus();
      communicate();
      return task;
    }
  }
    
  /**
   * Called whenever there are no tasks in this deque
   * @return
   */
  private void acquire(FJavaTask parentTask) {
    //TODO: measure time acquiring a task
    while(parentTask == null || !parentTask.areAllChildsDone()) {
      int stealIdx = this.random.nextInt(this.numWorkers);
      if(reservedRequestCell != EMPTY_REQUEST || (status[stealIdx].value == VALID_STATUS 
          && requestCells[stealIdx].compareAndSet(EMPTY_REQUEST, this.dequeID))) {
          
          if(reservedRequestCell != EMPTY_REQUEST) stealIdx = reservedRequestCell;
          
          //TODO: measure time waiting?
          while(this.responseCells[this.dequeID].task == emptyTask) {
            this.communicate(); //TODO: remove busy waiting
            if(parentTask != null && parentTask.areAllChildsDone() || pool.isShuttingDown()) {
              reservedRequestCell = stealIdx;
              return;
            }
          }
          reservedRequestCell = EMPTY_REQUEST;
          if(this.responseCells[this.dequeID].task != null 
              && this.responseCells[this.dequeID].task != emptyTask) {
            FJavaTask newTask = this.responseCells[this.dequeID].task;
            this.addTask(newTask);
            if(FJavaConf.getInstance().shouldTrackStats()) { 
              StatsTracker.getInstance().onDequeSteal(this.dequeID);
            }
          }
          this.responseCells[this.dequeID].task = emptyTask;
          return;
      }
      this.communicate();
      if(pool.isShuttingDown()) break;
    }
  }
  
  /*
   * Respond to requests, if any.
   */
  private void communicate() {
    int requestIdx = this.requestCells[this.dequeID].get();
    if(requestIdx == EMPTY_REQUEST) return;
    
    if(this.tasks.isEmpty()) {
      this.responseCells[requestIdx].task = null;
    }
    else {
      FJavaTask task =  this.tasks.removeFirst();
      this.responseCells[requestIdx].task = task;
    }
    this.requestCells[this.dequeID].set(EMPTY_REQUEST);
  }
  
  private void updateStatus() {
    int available = this.tasks.size() > 0 ? VALID_STATUS : INVALID_STATUS;
    if(this.status[this.dequeID].value != available) 
      this.status[this.dequeID].value = available;
  }
}
