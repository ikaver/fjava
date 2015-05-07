package com.ikaver.aagarwal.fjava;

import java.util.concurrent.atomic.AtomicInteger;

import sun.misc.Contended;

@Contended
public class PaddedAtomicInteger {
  
  private AtomicInteger atomicInteger;
  
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
