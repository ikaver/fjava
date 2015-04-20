package com.ikaver.aagarwal.fjava;

public class EmptyFJavaTask extends FJavaTask {

  public EmptyFJavaTask() {
    super();
  }

  @Override
  public void compute() {
    
  }
  
  @Override
  public String toString() {
  	return "empty task()";
  }

}
