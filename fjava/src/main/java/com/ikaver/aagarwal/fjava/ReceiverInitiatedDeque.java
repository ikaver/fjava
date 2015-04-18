package com.ikaver.aagarwal.fjava;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a Sender Initiated Deque. This means that task runners that need
 * tasks to run (receivers) are the ones who ask others for tasks.
 */
public class ReceiverInitiatedDeque implements TaskRunnerDeque {
  //TODO: what is the run loop of this? Simple approach: Same thread as runner.

  public static final int VALID_STATUS = 1;
  public static final int INVALID_STATUS = 0;
  public static final int EMPTY_REQUEST = -1;
  
  //TODO: make cache efficient? (False sharing) 
  //BTW, We don't need atomic here. 
  //Only need to make sure that writes are propagates to all threads.
  private AtomicInteger [] status; 
  
  //TODO: make cache efficient? (False sharing)
  //requestCells[i] = j if task runner j is waiting for task runner i to give
  //him work.
  private AtomicInteger [] requestCells;
  
  //TODO: make cache efficient? (False sharing)
  //responseCells[j] holds the task that task runner j stole from other task runner.
  private FJavaTask [] responseCells;
  
  private FJavaTask emptyTask;
  
  private Deque<FJavaTask> tasks;
  
  private Random random;

  private int myIdx;
  private int numWorkers;
  
  public ReceiverInitiatedDeque(AtomicInteger [] status, 
      AtomicInteger [] requestCells, FJavaTask [] responseCells, int myIdx, 
      FJavaTask emptyTask) {
    for(int i = 0; i < requestCells.length; ++i) {
      if(requestCells[i].get() != EMPTY_REQUEST) 
        throw new IllegalArgumentException("All request cells should be EMPTY_REQUEST initially");
      if(responseCells[i] != emptyTask)
        throw new IllegalArgumentException("All response cells should be empty");
      if(status[i].get() != INVALID_STATUS)
        throw new IllegalArgumentException("All status should be INVALID_STATUS initially");
    }
    if(emptyTask == null) {
      throw new IllegalArgumentException("Empty task shouldn't be null");
    }
    
    this.status = status;
    this.requestCells = requestCells;
    this.responseCells = responseCells;
    this.random = new Random();    
    this.myIdx = myIdx;
    this.numWorkers = this.status.length;
    this.tasks = new ArrayDeque<FJavaTask>();
  } 
  
  
  public void addTask(FJavaTask task) {
    //TODO: how to ensure that this is called on the correct thread?
    //Idea: Can only be called by task runner
    if(task == null) 
        throw new IllegalArgumentException("Task cannot be null");
    this.tasks.push(task);
    this.updateStatus();
  }
  
  public FJavaTask getTask() {
    if(this.tasks.isEmpty()) {
      //TODO: add counter (worker found queue empty)
      return acquire();
    }
    else {
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
  private FJavaTask acquire() {
    //TODO: measure time acquiring a task
    while(true) {
      responseCells[myIdx] = null;
      int stealIdx = this.random.nextInt(this.numWorkers);
      if(status[stealIdx].get() == VALID_STATUS 
          && requestCells[stealIdx].compareAndSet(EMPTY_REQUEST, this.myIdx)) {
          
          //TODO: measure time waiting?
          while(this.responseCells[this.myIdx] == emptyTask) {
            this.communicate(); //TODO: remove busy waiting
          }
          
          if(this.responseCells[this.myIdx] != null) {
            FJavaTask newTask = this.responseCells[this.myIdx];
            this.responseCells[this.myIdx] = emptyTask;
            return newTask;
          }
          this.communicate(); //TODO: why is this here?
      }
    }
  }
  
  /*
   * Respond to requests, if any.
   */
  private void communicate() {
    int requestIdx = this.requestCells[this.myIdx].get();
    if(requestIdx == EMPTY_REQUEST) return;
    
    if(this.tasks.isEmpty()) {
      this.responseCells[requestIdx] = null;
    }
    else {
      this.responseCells[requestIdx] = this.tasks.removeFirst();
    }
    this.requestCells[this.myIdx].set(EMPTY_REQUEST);
  }
  
  private void updateStatus() {
    int available = this.tasks.size() > 0 ? VALID_STATUS : INVALID_STATUS;
    if(this.status[this.myIdx].get() != available) 
      this.status[this.myIdx].set(available);
  }
}
