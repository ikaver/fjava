import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.ikaver.aagarwal.common.ArrayHelper;
import com.ikaver.aagarwal.common.TimeHelper;
import com.ikaver.aagarwal.javaforkjoin.QuickSort;
import com.ikaver.aagarwal.seq.SeqQuickSort;


public class TestQuickSort extends AbstractBenchmark {

  double [] testArray;
  
  static int size;
  static double [] original;
  static double [] sorted;
  static boolean debug;
  
  @BeforeClass
  public static void setup() {
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);
    
    size = 10000000;
    int min = -10000000;
    int max =  10000000;
    original = ArrayHelper.createRandomArray(size, min, max);
    sorted = Arrays.copyOf(original, size);
    Arrays.sort(sorted);
  }
  
  @Before
  public void setupTest() {
    testArray = Arrays.copyOf(original, size);
  }

  @Test
  public void testForkJoinPoolQuickSort() {   
    new ForkJoinPool().invoke(new QuickSort(testArray, 0, size-1));    
    if(debug) Assert.assertArrayEquals(sorted, original, 0.0);
  }
  
  
  
  @Test
  public void testQuickSort() {
    new SeqQuickSort(testArray, 0, size-1).compute();
    if(debug) Assert.assertArrayEquals(sorted, original, 0.0);
  }
  
  
}
