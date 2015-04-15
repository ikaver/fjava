package com.ikaver.aagarwal.fjava;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SenderInitiatedDeque<T> {
  
  public static final int VALID_STATUS = 1;
  public static final int INVALID_STATUS = 0;
  public static final int EMPTY_REQUEST = -1;
  
  //TODO: make cache efficient? (False sharing) We don't need atomic here. 
  //Only need to make sure that writes are propagates to all threads.
  private AtomicInteger [] status; 
  //TODO: make cache efficient? (False sharing)
  private AtomicInteger [] requestCells;
  //TODO: make cache efficient? (False sharing)
  private T [] responseCells;
  private T emptyTask;
  
  private Deque<T> tasks;
  
  private Random random;

  private int myIdx;
  private int numWorkers;
  
  public SenderInitiatedDeque(AtomicInteger [] status, 
      AtomicInteger [] requestCells, T [] responseCells, int myIdx, 
      T emptyTask) {
    for(int i = 0; i < requestCells.length; ++i) {
      if(requestCells[i].get() != EMPTY_REQUEST) 
        throw new IllegalArgumentException("All request cells should be EMPTY_REQUEST initially");
      if(responseCells[i] != null)
        throw new IllegalArgumentException("All response cells should be null");
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
    this.tasks = new ArrayDeque<T>();
  } 
  
  public T acquire() {
    while(true) {
      responseCells[myIdx] = null;
      int stealIdx = this.random.nextInt(this.numWorkers);
      if(status[stealIdx].get() == VALID_STATUS 
          && requestCells[stealIdx].compareAndSet(EMPTY_REQUEST, this.myIdx)) {
          while(this.responseCells[this.myIdx] == emptyTask) {
            try {
              this.wait();
            } catch (InterruptedException e) { }
            this.communicate(); //TODO: why not sleep until waken up?
          }
          if(this.responseCells[this.myIdx] != null) {
            T newTask = this.responseCells[this.myIdx];
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
  public void communicate() {
    int requestIdx = this.requestCells[this.myIdx].get();
    if(requestIdx == EMPTY_REQUEST) return;
    if(this.tasks.isEmpty()) {
      this.responseCells[this.myIdx] = null;
    }
    else {
      this.responseCells[this.myIdx] = this.tasks.removeFirst();
    }
    notifyAll();
    this.requestCells[this.myIdx].set(EMPTY_REQUEST);
  }
  
  public void addTask(T task) {
    if(task == null) 
        throw new IllegalArgumentException("Task cannot be null");
    this.tasks.push(task);
    notifyAll();
  }
  
  

}
