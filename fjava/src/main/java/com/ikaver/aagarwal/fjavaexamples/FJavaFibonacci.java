package com.ikaver.aagarwal.fjavaexamples;

import com.ikaver.aagarwal.common.FibonacciBase;
import com.ikaver.aagarwal.common.utils.FibonacciUtils;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

public class FJavaFibonacci extends FibonacciBase {

  private final FJavaPool pool;

  public FJavaFibonacci(FJavaPool pool) {
    this.pool = pool;
  }

  public long fibonacci(int n) {
    FibonacciTask task = new FibonacciTask(n);
    pool.run(task);
    return task.getFibonacci();
  }
  
  private static class FibonacciTask extends FJavaTask {
    
    private int n;
    private long answer;
    
    public FibonacciTask(int n, FibonacciTask parent) {
      super(parent);
      this.n = n;
    }
    
    public FibonacciTask(int n) {
      super();
      this.n = n;
    }

    @Override
    public void compute() {
      if (n == 0) {
        answer = 0;
        return;
      } else if (n <= 2) {
        answer = 1;
        return;
      } else if (n <= THRESHOLD) {
        answer = FibonacciUtils.fibnth(n);
        return;
      }
      FibonacciTask childTask1 = new FibonacciTask(n-1, this);
      FibonacciTask childTask2 = new FibonacciTask(n-2, this);
      childTask1.fork();
      childTask2.fork();
      sync();
      answer = childTask1.answer + childTask2.answer;
    }
    
    @Override
    public String toString() {
      return String.format("Fibonacci %d", n);
    }
    
    public long getFibonacci() {
      return this.answer;
    }
    
  }

}
