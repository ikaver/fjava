package com.ikaver.aagarwal.fjava;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


public class ConcurrentArrayDeque implements TaskRunnerDeque {

  private volatile CircularArray elements; 
  private AtomicLong top;
  private AtomicLong bottom;
  private FJavaPool pool;
  
  private final ConcurrentArrayDeque [] deques;
  private final int dequeID;
  private final int numWorkers;
  private final Random random;

  public static final FJavaTask EMPTY = new EmptyFJavaTask();
  public static final FJavaTask FAILED = new EmptyFJavaTask();

  public ConcurrentArrayDeque(ConcurrentArrayDeque [] deques, int dequeID) {
    this.deques = deques;
    this.dequeID = dequeID;
    this.numWorkers = this.deques.length;
    this.random = new Random();
    this.elements = new CircularArray(4096);
    this.top = new AtomicLong(0);
    this.bottom = new AtomicLong(0);
  }

  //only owner thread can call add last
  public void addLast(FJavaTask task) {
    long b = this.bottom.get();
    long t = this.top.get();
    long size = b - t;
    if(size >= this.elements.size()-1) {
      this.elements = this.elements.grow(b, t);
    }
    this.elements.put(b, task);
    this.bottom.set(b+1);
  }

  //anyone can call remove first (steal)
  public FJavaTask removeFirst() {
    long b = this.bottom.get();
    long t = this.top.get();
    long size = b - t;
    if(size <= 0) return EMPTY;
    FJavaTask topTask = this.elements.get(t);
    if(top.get() != t || !top.compareAndSet(t, t+1)) {
      return FAILED;
    }
    else {
      this.elements.put(t, null);
    }
    return topTask;
  }

  public FJavaTask removeLast() {
    long b = this.bottom.get() - 1;
    this.bottom.set(b);
    long t = this.top.get();
    long size = b - t;
    if(size < 0) {
      this.bottom.set(t);
      return EMPTY;
    }
    FJavaTask bottomTask = this.elements.get(b);
    if(size > 0) {
      this.elements.put(b, null);
      return bottomTask;
    }
    if(top.get() != t || !top.compareAndSet(t, t+1)) {
      bottomTask = EMPTY;
    } 
    else {
      this.elements.put(b, null);
    }
    this.bottom.set(t+1);
    return bottomTask;
  }

  public boolean isEmpty() {
    return top == bottom;
  }

  @Override
  public void addTask(FJavaTask task) {
    this.addLast(task);
  }

  @Override
  public FJavaTask getTask(FJavaTask task) {
    FJavaTask newTask = this.removeLast();
    while(newTask == EMPTY || newTask == FAILED) {      
      if(task != null && task.areAllChildsDone()) return null;
      if(pool.isShuttingDown()) return null;
      int randIdx = this.random.nextInt(this.numWorkers);
      if(randIdx == this.dequeID) continue;
      newTask = this.deques[randIdx].removeFirst();
    }
    if(newTask == EMPTY || newTask == FAILED) newTask = null;
    return newTask;
  }

  @Override
  public void tryLoadBalance() {
    //NOOP
  }

  @Override
  public void setupWithPool(FJavaPool pool) {
    this.pool = pool;
  }

}
