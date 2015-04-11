import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.ikaver.aagarwal.javaforkjoin.FibonacciJavaForkJoin;

public class TestFibonacciJavaForkJoin extends AbstractBenchmark {

	private static final int N = 40;

	static long expected;
	static boolean debug;

	private static long fibnth(int n) {
		long arr[] = new long[3];
		arr[0] = 0;
		arr[1] = 1;
		for (int i = 2; i <= n; i++) {
			arr[i % 3] = arr[(i + 2) % 3] + arr[(i + 1) % 3];
		}

		return arr[n % 3];
	}

	@BeforeClass
	public static void setUp() {
		debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
		System.out.println("Debug " + debug);

		// sequentially compute the nth fibonacci number.
		expected = fibnth(N);
	}
	
	@Test
	public void testFibonacciJavaForkJoin() {
		ForkJoinPool pool = new ForkJoinPool();
		FibonacciJavaForkJoin fibonacciJavaForkJoin =
				new FibonacciJavaForkJoin(pool);
		long result = fibonacciJavaForkJoin.fibonacci(N);
		Assert.assertEquals(result, expected);
	}
}
