import java.util.Random;
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
import com.ikaver.aagarwal.common.utils.ArrayHelper;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaPoolFactory;
import com.ikaver.aagarwal.fjavaexamples.FJavaMap;
import com.ikaver.aagarwal.javaforkjoin.MapJavaForkJoin;
import com.ikaver.aagarwal.seq.SeqMap;

/**
 * Tests a map function which is expensoive to compute.
 */
public class TestExpensiveMap extends AbstractBenchmark {

  private static final int NUM_ITERATIONS = 1000;

  Integer[] testArray;
  Integer[] result;

  static MapFunction<Integer, Integer> mapFunction;
  static int size;
  static int[] original;
  static Integer[] expected;
  static boolean debug;

  @BeforeClass
  public static void setup() {
    FJavaConf.initialize();
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);

    size = 100000;
    int min = 0;
    int max = 10001;
    original = ArrayHelper.createRandomAray(size, min, max);
    expected = new Integer[size];

    mapFunction = new MapFunction<Integer, Integer>() {
      public Integer map(Integer obj) {
        Random random = new Random();
        random.setSeed(obj);
        int res = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
          res ^= random.nextInt();
        }
        return res;
      }
    };
    if(debug) {
      for (int i = 0; i < size; ++i) {
        expected[i] = mapFunction.map(original[i]);
      }
    }
  }

  @Before
  public void setupTest() {
    testArray = new Integer[size];
    result = new Integer[size];
    for (int i = 0; i < size; ++i) {
      testArray[i] = original[i];
    }
  }

  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testJavaForkJoin() {
    ForkJoinPool pool = new ForkJoinPool();
    new MapJavaForkJoin<Integer, Integer>(pool).map(testArray, result, mapFunction);
    if (debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testFJava() {  
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool();
    new FJavaMap<Integer, Integer>(pool).map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }

  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testSequential() {
    new SeqMap<Integer, Integer>().map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }

}
