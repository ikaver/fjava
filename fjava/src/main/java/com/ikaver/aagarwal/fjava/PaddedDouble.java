package com.ikaver.aagarwal.fjava;

public class PaddedDouble {
  
  //16 bytes (object)
  public double value; //value (8 bytes)
  int a1,a2,a3,a4,a5,a6,a7,a8,a9,a10; //padding (40 bytes)
  
  public PaddedDouble() {
    this.value = 0;
  }
  
  public PaddedDouble(double value) {
    this.value = value;
  }
  
}
