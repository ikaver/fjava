import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.ikaver.aagarwal.common.ArrayHelper;
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
    debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
    System.out.println("Debug " + debug);
    
    size = 10000000;
    long min = - (1 << 60);
    long max =   (1 << 60);
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
    ForkJoinPool pool = new ForkJoinPool();
    new QuickSortJavaForkJoin(pool).sort(testArray, 0, size-1);
    if(debug) Assert.assertArrayEquals(sorted, original);
  }
  
  
  
  @Test
  public void testQuickSort() {
    new SeqQuickSort().sort(testArray, 0, size-1);
    if(debug) Assert.assertArrayEquals(sorted, original);
  }
  
  
}
