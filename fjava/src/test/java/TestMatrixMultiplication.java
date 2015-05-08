import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.utils.ArrayHelper;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaPoolFactory;
import com.ikaver.aagarwal.fjavaexamples.FJavaMatrixMultiplication;
import com.ikaver.aagarwal.javaforkjoin.MatrixMultiplicationJavaForkJoin;
import com.ikaver.aagarwal.seq.SeqMatrixMultiplication;


public class TestMatrixMultiplication extends AbstractBenchmark {

  float [][] result;

  static int size;
  static float [][] testA;
  static float [][] testB;
  static float [][] expected;
  static boolean debug;
  
  @BeforeClass
  public static void setup() {
    FJavaConf.initialize();
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);
    //debug = true;
    size = 2048;
    float min = -160;
    float max = 160;
    testA = ArrayHelper.createRandomMatrix(size, size, min, max);
    testB = ArrayHelper.createRandomMatrix(size, size, min, max);
    if(debug) {
      expected = new float[size][size];
      new SeqMatrixMultiplication().multiply(testA, testB, expected);
    }
  }
  
  @Before
  public void setupTest() {
    result = new float[size][size];
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testJavaForkJoin() {
    new MatrixMultiplicationJavaForkJoin(
        new ForkJoinPool(FJavaConf.getPoolSize()))
        .multiply(testA, testB, result);
    if(debug) {
      for(int i = 0; i < size; ++i) {
        Assert.assertArrayEquals(expected[i], result[i], 2.0f);
      }
    }
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testFJava() {
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool();
    new FJavaMatrixMultiplication(pool).multiply(testA, testB, result);
    if(debug) {
      for(int i = 0; i < size; ++i) {
        Assert.assertArrayEquals(expected[i], result[i], 2.0f);
      }
    }
  }
    
    
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testSequential() {
    new SeqMatrixMultiplication().multiply(testA, testB, result);
    if(debug) {
      for(int i = 0; i < size; ++i) {
        Assert.assertArrayEquals(result[i], expected[i], 2.0f);
      }
    }
  }
  
  
}
