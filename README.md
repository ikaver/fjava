#FJava: Fork Join for Java

##What is FJava?
FJava is a high level fork join framework for the Java programming language. 
Our framework outperforms the native Java Fork Join framework under some workloads, and gets competitive 
results for others. With our implementation, we demonstrate that private deques 
are an effective work stealing algorithm for a Fork Join framework.

##Sample code

```java
public class FJavaQuickSort extends FJavaTask {

  private long [] array;
  private int left;
  private int right;

  public FJavaQuickSort(long [] array, int left, int right) {
    this.array = array;
    this.left = left;
    this.right = right;
  }

  @Override
  public void compute() {
    if(right <= left) return;

    if(right - left <= Definitions.QUICKSORT_SEQ_THRESHOLD) {
      Arrays.sort(array, left, right+1);
      return;
    }
    int mid = partition();
    new FJavaQuickSort(array, left, mid-1).runAsync(this);
    new FJavaQuickSort(array, mid+1, right).runSync(this);
    sync();
  }

  public static void sort(long[] array, int left, int right) {
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool();
    FJavaQuickSort task = new FJavaQuickSort(array, left, right);
    pool.run(task);
  }
}
```

##Preliminary results
The next figure shows the relative speedups achieved by FJava and Java Fork Join relative to 
the sequential version of the code for several problems.

- **Primes**: Call **isPrime** for an array of 5,000,000 primes
- **Matrix Multiplication**: Multiply two 2048x2048 matrices recursively
- **Fibonacci**: Solve **fibonacci(50)** recursively
- **QuickSort**: Sort 10,000,000 longs using QuickSort

![Preliminary results](http://www.andrew.cmu.edu/user/ikaveror/15618/images/speedup-github.png?1)

**For different values of the sequential threshold T, the results vary. 
FJava uses Private Deques, therefore, as expected, for larger values of T, 
Java's native Fork Join outperforms FJava (but not by a large margin)**.

To address this issue, we have added **tryLoadBalance** function to our API. This call allows FJava to perform competitively with Java 8 Fork Join even for tasks that have large sequential thresholds. On the downside, the user is responsible for making sure they call tryLoadBalance periodically during their long computations. For example:

```java
  @Override
  public void compute() { 
    if(right - left <= SEQUENTIAL_THRESHOLD) {
      for(int i = left; i <= right; ++i) {
        this.result[i] = this.mapFunc.map(this.array[i]);
        if(i % ITERATIONS_FOR_BALANCE == 0) this.tryLoadBalance();
      }
      return;
    }
    //create child tasks using runAsync here
  }
```
