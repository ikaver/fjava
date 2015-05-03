import java.util.Random;
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
    for (int i = 0; i < size; ++i) {
      expected[i] = mapFunction.map(original[i]);
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
  public void testForkJoinPoolComputationalExpensiveMap() {
    ForkJoinPool pool = new ForkJoinPool();
    new MapJavaForkJoin<Integer, Integer>(pool).map(testArray, result, mapFunction);
    if (debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }

  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testComputationalExpensiveMap() {
    new SeqMap<Integer, Integer>().map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }

}
