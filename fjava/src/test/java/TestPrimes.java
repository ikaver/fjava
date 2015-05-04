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
import com.ikaver.aagarwal.common.problems.MapFunction;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaPoolFactory;
import com.ikaver.aagarwal.fjavaexamples.FJavaMap;
import com.ikaver.aagarwal.javaforkjoin.MapJavaForkJoin;
import com.ikaver.aagarwal.seq.SeqMap;


public class TestPrimes extends AbstractBenchmark {

  Integer [] testArray;
  Boolean [] result;

  static MapFunction<Integer, Boolean> mapFunction;
  static int size;
  static int [] original;
  static Boolean [] expected;
  static boolean debug;
  
  private static boolean testMapFunc(long in) {
    if(in <= 3) return in == 2 || in == 3;
    for(int i = 5; i < in; i += 2) {
      if(in % i == 0) return false;
    }
    return true;
  }
  
  @BeforeClass
  public static void setup() {
    FJavaConf.initialize();
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);
    
    size = 100000;
    int min = 1000;
    int max = (1 << 17);
    //we beat ForkJoin with size = 1000000, min = 1000, max = (1 << 15).
    original = ArrayHelper.createRandomAray(size, min, max);
    expected = new Boolean[size];
    mapFunction = new MapFunction<Integer, Boolean>() {
      public Boolean map(Integer obj) {
        int num = obj.intValue();
        return testMapFunc(num);
      }
    };
    if(debug) {
      for(int i = 0; i < size; ++i) {
        expected[i] = testMapFunc(original[i]);
      }
    }
  }
  
  @Before
  public void setupTest() {
    testArray = new Integer[size];
    result = new Boolean[size];
    for(int i = 0; i < size; ++i) {
      testArray[i] = original[i];
    }
  }

  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testJavaForkJoin() {  
    ForkJoinPool pool = new ForkJoinPool(FJavaConf.getPoolSize());
    new MapJavaForkJoin<Integer, Boolean>(pool).map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testFJava() {  
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool();
    new FJavaMap<Integer, Boolean>(pool).map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
    
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testSequential() {
    new SeqMap<Integer, Boolean>().map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
  
}
