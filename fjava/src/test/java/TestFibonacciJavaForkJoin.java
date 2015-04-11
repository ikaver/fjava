import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.ikaver.aagarwal.common.utils.FibonacciUtils;
import com.ikaver.aagarwal.javaforkjoin.FibonacciJavaForkJoin;
import com.ikaver.aagarwal.seq.FibonacciSequential;

public class TestFibonacciJavaForkJoin extends AbstractBenchmark {

	private static final int N = 49;

	static long expected;
	static boolean debug;

	@BeforeClass
	public static void setUp() {
		debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
		System.out.println("Debug " + debug);

		// sequentially compute the nth fibonacci number.
		expected = FibonacciUtils.fibnth(N);
	}
	
	@Test
	public void testFibonacciJavaForkJoin() {
		ForkJoinPool pool = new ForkJoinPool();
		FibonacciJavaForkJoin fibonacciJavaForkJoin =
				new FibonacciJavaForkJoin(pool);
		long result = fibonacciJavaForkJoin.fibonacci(N);

		Assert.assertEquals(result, expected);
	}
	
	@Test
	public void testFibonacciSequential() {
		FibonacciSequential fibonacciSequential = new FibonacciSequential();
		long result = fibonacciSequential.fibonacci(N);

		Assert.assertEquals(result, expected);
	}
}
