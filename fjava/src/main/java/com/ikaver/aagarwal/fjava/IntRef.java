package com.ikaver.aagarwal.fjava;

public class IntRef {
  
  //16 bytes (object)
  public volatile int value; //value (4 btyes)
  int a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11; //padding (44 bytes)
  
  public IntRef() {
    this.value = 0;
  }
  
  public IntRef(int value) {
    this.value = value;
  }

}
