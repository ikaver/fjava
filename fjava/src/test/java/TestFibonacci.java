import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.StealingAlgorithm;
import com.ikaver.aagarwal.common.utils.FibonacciUtils;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaPoolFactory;
import com.ikaver.aagarwal.fjavaexamples.FJavaFibonacci;
import com.ikaver.aagarwal.javaforkjoin.FibonacciJavaForkJoin;
import com.ikaver.aagarwal.seq.SeqFibonacci;

public class TestFibonacci extends AbstractBenchmark {

	private static final int N = 40;

	static long expected;
	static boolean debug;

	@BeforeClass
	public static void setUp() {
		debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
		System.out.println("Debug " + debug);

		// sequentially compute the nth fibonacci number.
		expected = FibonacciUtils.fibnth(N);
	}
	
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
	@Test
	public void testFibonacciJavaForkJoin() {
		ForkJoinPool pool = new ForkJoinPool();
		FibonacciJavaForkJoin fibonacciJavaForkJoin =
				new FibonacciJavaForkJoin(pool);
		long result = fibonacciJavaForkJoin.fibonacci(N);

		Assert.assertEquals(result, expected);
	}
  
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
  @Test
  public void testFibonacciFJava() {
    FJavaPool pool = FJavaPoolFactory.getInstance().createPool(StealingAlgorithm.RECEIVER_INITIATED);
    FJavaFibonacci fibonacci =
        new FJavaFibonacci(pool);
    long result = fibonacci.fibonacci(N);
    Assert.assertEquals(result, expected);
  }
	
  @BenchmarkOptions(benchmarkRounds = Definitions.BENCHMARK_ROUNDS, warmupRounds = Definitions.WARMUP_ROUNDS)
	@Test
	public void testFibonacciSequential() {
		SeqFibonacci fibonacciSequential = new SeqFibonacci();
		long result = fibonacciSequential.fibonacci(N);

		Assert.assertEquals(result, expected);
	}
}
