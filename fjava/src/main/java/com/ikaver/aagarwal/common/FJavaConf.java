package com.ikaver.aagarwal.common;

/**
 * Configuration file for FJava framework.
 */
public class FJavaConf {

	private static final String COLLECT_STATS = "COLLECT_STATS";
	private static final String ALGORITHM = "ALGORITHM";
	private static final String POOL_SIZE = "POOL_SIZE";
	private static final String DELTA = "DELTA";
	private static final String MATRIX_MULT_SEQ_THRESHOLD = "TestMatrixMultiplication_THRESHOLD";
	private static final String QUICKSORT_SEQ_THRESHOLD = "TestQuickSort_THRESHOLD";
	private static final String FILTER_SEQ_THRESHOLD = "TestPrimes_THRESHOLD";
	private static final String KARATSUBA_SEQ_THRESHOLD = "TestKaratsuba_THRESHOLD";
	private static final String FIBONACCI_SEQ_THRESHOLD = "TestFibonacci_THRESHOLD";

	private static final double DEFAULT_DELTA = 0.001;
	public static final int DEFAULT_QUICKSORT_SEQ_THRESHOLD = 4000;
	public static final int DEFAULT_FILTER_SEQ_THRESHOLD = 64;
	public static final int DEFAULT_MATRIX_MULT_SEQ_THRESHOLD = 64;
	public static final int DEFAULT_KARATSUBA_SEQ_THRESHOLD = 100;
	public static final int DEFAULT_FIBONACCI_SEQ_THRESHOLD = 13;
	
	private static boolean trackStats;
	private static StealingAlgorithm algorithm;
	private static int poolSize;
	private static double delta;
	private static int quicksortSequentialThreshold;
	private static int mapSequentialThreshold;
	private static int matrixMultSequentialThreshold;
	private static int karatsubaSequentialThreshold;
	private static int fibonacciSequentialThreshold;

	public static void initialize() {
		String statsString = System.getenv(COLLECT_STATS);
		if (statsString != null) {
			trackStats = Boolean.valueOf(statsString);
		} else {
			trackStats = false;
		}

		String algorithmString = System.getenv(ALGORITHM);
		if ("SID".equals(algorithmString)) {
			algorithm = StealingAlgorithm.SENDER_INITIATED;
		} else if ("CONCURRENT_LIST".equals(algorithmString)) {
			algorithm = StealingAlgorithm.CONCURRENT_LIST;
		} else if ("SHARED_CONCURRENT".equals(algorithmString)) {
			algorithm = StealingAlgorithm.SHARED_CONCURRENT_QUEUE;
		} else if ("CONCURRENT_ARRAY".equals(algorithmString)) {
      algorithm = StealingAlgorithm.CONCURRENT_ARRAY;
    }
		else {
			algorithm = StealingAlgorithm.RECEIVER_INITIATED;
		}

		String poolSizeString = System.getenv(POOL_SIZE);
		if (poolSizeString != null) {
			poolSize = Integer.valueOf(poolSizeString);
		} else {
			poolSize = Runtime.getRuntime().availableProcessors();
		}

		String deltaString = System.getenv(DELTA);
		if (deltaString != null) {
			delta = Double.valueOf(deltaString);
		} else {
			delta = DEFAULT_DELTA;
		}

		parseThresholds();

		System.out.printf("Using track stats = %s algorithm = %s pool size = %d\n",
				trackStats == true ? "true" : "false", algorithm, poolSize);
		System.out.printf("Threshold values:\n" + "MATRIX_MULT_SEQ_THRESHOLD %d\n"
				+ "QUICKSORT_SEQ_THRESHOLD %d\n" + "FILTER_SEQ_THRESHOLD %d	\n"
				+ "KARATSUBA_SEQ_THRESHOLD %d\n"
				+ "FIBONACCI_SEQ_THRESHOLD %d\n",
				matrixMultSequentialThreshold,
				quicksortSequentialThreshold,
				mapSequentialThreshold,
				karatsubaSequentialThreshold,
				fibonacciSequentialThreshold);
	}

	private static void parseThresholds() {
		matrixMultSequentialThreshold = parseValue(MATRIX_MULT_SEQ_THRESHOLD,
				DEFAULT_MATRIX_MULT_SEQ_THRESHOLD);
		quicksortSequentialThreshold = parseValue(QUICKSORT_SEQ_THRESHOLD,
				DEFAULT_QUICKSORT_SEQ_THRESHOLD);
		mapSequentialThreshold = parseValue(FILTER_SEQ_THRESHOLD,
				DEFAULT_FILTER_SEQ_THRESHOLD);
		karatsubaSequentialThreshold = parseValue(KARATSUBA_SEQ_THRESHOLD,
				DEFAULT_KARATSUBA_SEQ_THRESHOLD);
		fibonacciSequentialThreshold = parseValue(FIBONACCI_SEQ_THRESHOLD,
				DEFAULT_FIBONACCI_SEQ_THRESHOLD);
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static int parseValue(String key, int defaultValue) {
		String value = System.getenv(key);
		if (value != null) {
			return Integer.parseInt(value);
		} else {
			return defaultValue;
		}
	}

	public static boolean shouldTrackStats() {
		return trackStats;
	}

	public static StealingAlgorithm getStealingAlgorithm() {
		return algorithm;
	}

	public static int getPoolSize() {
		return poolSize;
	}

	public static double getDelta() {
		return delta;
	}

	public static int getMatrixMultiplicationSequentialThreshold() {
		return matrixMultSequentialThreshold;
	}

	public static int getQuicksortSequentialThreshold() {
		return quicksortSequentialThreshold;
	}

	public static int getMapSequentialThreshold() {
		return mapSequentialThreshold;
	}

	public static int getKaratsubaSequentialThreshold() {
		return karatsubaSequentialThreshold;
	}
	
	public static int getFibonacciSequentialThreshold() {
		return fibonacciSequentialThreshold;
	}
}
