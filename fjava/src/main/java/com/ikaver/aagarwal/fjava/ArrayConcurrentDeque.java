package com.ikaver.aagarwal.fjava;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ArrayConcurrentDeque implements TaskRunnerDeque {

  private final int dequeID;
  private ArrayConcurrentDeque [] deques;
  private FJavaTask [] myDeque;
  private Random random;
  

  public ArrayConcurrentDeque(int dequeID) {
    this.dequeID = dequeID;
    this.myDeque = new FJavaTask[4096];
    this.random = new Random();
  }
  
  public void setupWithDeques(ArrayConcurrentDeque [] deques) {
    this.deques = deques;
  }
  
  @Override
  public void addTask(FJavaTask task) {
    
  }
  

  @Override
  public FJavaTask getTask(FJavaTask parentTask) {

    return null;
  }

  @Override
  public void setupWithPool(FJavaPool pool) {
    
  }

}
