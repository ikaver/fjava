import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.ikaver.aagarwal.javaforkjoin.KaratsubaMultiplyJavaForkJoin;

public class TestKaratsubaMultiplyJavaForkJoin extends AbstractBenchmark {
	
	private static final int BASE = 2;
	private static final int NUM_DIGITS = 10000;

	static boolean debug;
	static BigInteger x;
	static BigInteger y;
	static BigInteger expected;

	@BeforeClass
	public static void setup() {
		debug = "1".equals(System.getenv("fjava-debug")) ? true : false;
		System.out.println("Debug " + debug);
		x = new BigInteger(generateRandomBinaryStringOfLength(NUM_DIGITS), BASE);
		y = new BigInteger(generateRandomBinaryStringOfLength(NUM_DIGITS), BASE);
		expected = x.multiply(y);
	}

	private static String generateRandomBinaryStringOfLength(int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char ch = (char) ((char)random.nextInt(2) + '0');
			builder.append(ch);
		}
		return builder.toString();
	}

	@Test
	public void testKaratsubaMultiplyJavaForkJoin() {
		ForkJoinPool pool = new ForkJoinPool();
		KaratsubaMultiplyJavaForkJoin karatsubaMultiplyJavaForkJoin = 
				new KaratsubaMultiplyJavaForkJoin(pool);
		
		BigInteger result = karatsubaMultiplyJavaForkJoin.multiply(x, y);
		Assert.assertTrue("Result value should match expected value",
				expected.equals(result));
	}
}
