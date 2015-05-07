package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;

public class PaddedAtomicInteger {
  
  private AtomicInteger atomicInteger;
  int a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12;
  
  public PaddedAtomicInteger(int n) {
    this.atomicInteger = new AtomicInteger(n);
  }
  
  public int get() {
    return this.atomicInteger.get();
  }
  
  public void set(int n) {
    this.atomicInteger.set(n);
  }
  
  public boolean compareAndSet(int expect, int update) {
    return this.atomicInteger.compareAndSet(expect, update);
  }

}
