import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.ikaver.aagarwal.common.ArrayHelper;
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
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);
    debug = true;
    size = 1024;
    float min = -160;
    float max = 160;
    testA = ArrayHelper.createRandomMatrix(size, size, min, max);
    testB = ArrayHelper.createRandomMatrix(size, size, min, max);
    expected = new float[size][size];
    if(debug) {
      for(int i = 0; i < size; ++i) {
        for(int j = 0; j < size; ++j) {
          for(int k = 0; k < size; ++k) {
            expected[i][j] += testA[i][k] * testB[k][j];
          }
        }
      }
    }
  }
  
  @Before
  public void setupTest() {
    result = new float[size][size];
  }
  
  @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
  @Test
  public void testJavaForkJoinMatrixMultiplication() {
    new MatrixMultiplicationJavaForkJoin(new ForkJoinPool()).multiply(testA, testB, result);
    if(debug) {
      for(int i = 0; i < size; ++i) {
        Assert.assertArrayEquals(expected[i], result[i], 2.0f);
      }
    }
  }
    
  @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
  @Test
  public void testSeqMatrixMultiplication() {
    new SeqMatrixMultiplication().multiply(testA, testB, result);
    if(debug) {
      for(int i = 0; i < size; ++i) {
        Assert.assertArrayEquals(result[i], expected[i], 2.0f);
      }
    }
  }
  
  
}