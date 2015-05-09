import java.util.concurrent.ForkJoinPool;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaPoolFactory;
import com.ikaver.aagarwal.fjavaexamples.FJavaLU;
import com.ikaver.aagarwal.javaforkjoin.LUJavaForkJoin;
import com.ikaver.aagarwal.seq.SeqLU;


public class TestLU extends AbstractBenchmark {

  static int size;
  static double [][] testMatrix;
  static double [][] copy;
  static boolean debug;
  
  @BeforeClass
  public static void setup() {
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);

    size = 1024;
    testMatrix = new double[size][size];
    randomInit(testMatrix, size);
    copy = new double[size][size];
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        copy[i][j] = testMatrix[i][j];
      }
    }
  }
  
  static void randomInit(double[][] M, int n) {

    java.util.Random rng = new java.util.Random(133331);

    for (int i = 0; i < n; ++i)
      for (int j = 0; j < n; ++j)
        M[i][j] = rng.nextDouble();

    // for compatibility with hood demo, force larger diagonals
    for (int k = 0; k < n; ++k)
      M[k][k] *= 10.0;
  }

  static void check(double[][] LU, double[][] M, int n) {

    double maxDiff = 0.0; // track max difference

    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        double v = 0.0;
        int k;
        for (k = 0; k < i && k <= j; k++ ) v += LU[i][k] * LU[k][j];
        if (k == i && k <= j ) v += LU[k][j];
        double diff = M[i][j] - v;
        if (diff < 0) diff = -diff;
        if (diff > 0.001) {
          System.out.println("large diff at[" + i + "," + j + "]: " + M[i][j] + " vs " + v);
        }
        if (diff > maxDiff) maxDiff = diff;
      }
    }

    System.out.println("Max difference = " + maxDiff);
  }
  
  @Before
  public void setupTest() {
    for(int i = 0; i < size; ++i) {
      for(int j = 0; j < size; ++j) {
        testMatrix[i][j] = copy[i][j];
      }
    }
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testJavaForkJoin() {
    new LUJavaForkJoin().calculateLU(
        new ForkJoinPool(FJavaConf.getPoolSize()),
        testMatrix, size);
    if(debug) {
      check(testMatrix, copy, size);
    }
  }
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testFJava() {
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool();
    new FJavaLU().calculateLU(pool, testMatrix, size);
    if(debug) {
      check(testMatrix, copy, size);
    }
  }
    
    
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testSequential() {
    new SeqLU().calculateLU(testMatrix, size);
    if(debug) {
      check(testMatrix, copy, size);
    }
  }
}
