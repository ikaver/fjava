import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.ikaver.aagarwal.common.ArrayHelper;
import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaPoolFactory;
import com.ikaver.aagarwal.fjavaexamples.FJavaQuickSort;
import com.ikaver.aagarwal.javaforkjoin.QuickSortJavaForkJoin;
import com.ikaver.aagarwal.seq.SeqQuickSort;


public class TestQuickSort extends AbstractBenchmark {

  long [] testArray;
  
  static int size;
  static long [] original;
  static long [] sorted;
  static boolean debug;
  
  @BeforeClass
  public static void setup() {
    FJavaConf.initialize();
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);
    
    size = 10000000;
    long min = - (1 << 60);
    long max =   (1 << 60);
    original = ArrayHelper.createRandomArray(size, min, max);
    if(debug) {
      sorted = Arrays.copyOf(original, size);
      Arrays.sort(sorted);    
    }
  }
  
  @Before
  public void setupTest() {
    testArray = Arrays.copyOf(original, size);
  }

  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testJavaForkJoin() {   
    ForkJoinPool pool = new ForkJoinPool(FJavaConf.getPoolSize());
    new QuickSortJavaForkJoin(pool).sort(testArray, 0, size-1);
    if(debug) Assert.assertArrayEquals(sorted, original);
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testFJava() {
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool();
    FJavaQuickSort sort =
        new FJavaQuickSort(pool);
    sort.sort(testArray, 0, size-1);
    if(debug) Assert.assertArrayEquals(sorted, original);
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testSequential() {
    new SeqQuickSort().sort(testArray, 0, size-1);
    if(debug) Assert.assertArrayEquals(sorted, original);
  }
  
  
}
