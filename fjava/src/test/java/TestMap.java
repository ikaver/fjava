import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.problems.MapFunction;
import com.ikaver.aagarwal.common.utils.TestArrayHelper;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaPoolFactory;
import com.ikaver.aagarwal.fjavaexamples.FJavaMap;
import com.ikaver.aagarwal.javaforkjoin.MapJavaForkJoin;
import com.ikaver.aagarwal.seq.SeqMap;


public class TestMap extends AbstractBenchmark {

  Double [] testArray;
  Double [] result;

  static MapFunction<Double, Double> mapFunction;
  static int size;
  static double [] original;
  static Double [] expected;
  static boolean debug;
  
  private static double testMapFunc(double in) {
    return in*in*in - 2*in*in + Math.pow(in, -3) * 30;
  }
  
  @BeforeClass
  public static void setup() {
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);
    size = 1000000;
    double min = 0;
    double max = 2;
    original = TestArrayHelper.createRandomArray(size, min, max);
    expected = new Double[size];
    mapFunction = new MapFunction<Double, Double>() {
      public Double map(Double obj) {
        double num = obj.doubleValue();
        return testMapFunc(num);
      }
    };
    for(int i = 0; i < size; ++i) {
      expected[i] = testMapFunc(original[i]);
    }
  }
  
  @Before
  public void setupTest() {
    testArray = new Double[size];
    result = new Double[size];
    for(int i = 0; i < size; ++i) {
      testArray[i] = original[i];
    }
  }

  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testJavaForkJoin() {  
    ForkJoinPool pool = new ForkJoinPool(FJavaConf.getPoolSize());
    new MapJavaForkJoin<Double, Double>(pool).map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testFJava() {  
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool();
    new FJavaMap<Double, Double>(pool).map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
    
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testSequential() {
    new SeqMap<Double, Double>().map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
  
}
