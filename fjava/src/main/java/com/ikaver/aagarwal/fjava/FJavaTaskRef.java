package com.ikaver.aagarwal.fjava;

import sun.misc.Contended;

@Contended
public class FJavaTaskRef {
  
  //object 16 bytes
  public FJavaTask task; // 8 bytes  
  
  public FJavaTaskRef(FJavaTask task) {
    this.task = task;
  }

}
