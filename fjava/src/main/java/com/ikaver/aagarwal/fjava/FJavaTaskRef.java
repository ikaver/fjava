package com.ikaver.aagarwal.fjava;

public class FJavaTaskRef {
  
  //object 16 bytes
  public FJavaTask task; // 8 bytes
  int a1,a2,a3,a4,a5,a6,a7,a8,a9,a10; //padding (64 bytes)
  
  public FJavaTaskRef(FJavaTask task) {
    this.task = task;
  }

}
