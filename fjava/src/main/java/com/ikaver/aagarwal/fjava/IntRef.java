package com.ikaver.aagarwal.fjava;

import sun.misc.Contended;

@Contended
public class IntRef {
  
  public volatile int value;
  
  public IntRef() {
    this.value = 0;
  }
  
  public IntRef(int value) {
    this.value = value;
  }

}
