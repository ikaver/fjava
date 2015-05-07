package com.ikaver.aagarwal.fjava;

public class CircularArray {
  
  private long size;
  private FJavaTask [] elements;
  
  public CircularArray(long size) {
    this.size = size;
    this.elements = new FJavaTask[(int)this.size];
  }
  
  public long size() {
    return this.size;
  }
  
  public FJavaTask get(long idx) {
    int posIdx = (int)(idx % this.size);
    if(posIdx < 0) posIdx += size;
    return this.elements[posIdx];
  }
  
  public void put(long idx, FJavaTask task) {
    int posIdx = (int)(idx % this.size);
    if(posIdx < 0) posIdx += size;
    this.elements[posIdx] = task;
  }
  
  public CircularArray grow(long bottom, long top) {
    CircularArray newArray = new CircularArray(this.size*2);
    for(long i = top; i < bottom; ++i) {
      newArray.put(i, this.get(i));
    }
    return newArray;
  }

}
