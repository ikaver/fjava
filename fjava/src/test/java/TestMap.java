import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.ikaver.aagarwal.common.ArrayHelper;
import com.ikaver.aagarwal.common.MapFunction;
import com.ikaver.aagarwal.common.ThreadSafeList;
import com.ikaver.aagarwal.javaforkjoin.Map;
import com.ikaver.aagarwal.seq.SeqMap;


public class TestMap extends AbstractBenchmark {

  Double [] testArray;
  Double [] result;

  static MapFunction<Double> mapFunction;
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
    int min = 0;
    int max = 2;
    original = ArrayHelper.createRandomArray(size, min, max);
    expected = new Double[size];
    mapFunction = new MapFunction<Double>() {
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

  @Test
  public void testForkJoinPoolFilter() {   
    new ForkJoinPool().invoke(new Map<Double>(testArray, result, mapFunction, 0, size-1)); 
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
  
  
  
  @Test
  public void testFilter() {
    new SeqMap<Double>().map(testArray, result, mapFunction);
    if(debug) {
      Assert.assertArrayEquals(expected, result);
    }
  }
  
  
}
