package com.ikaver.aagarwal.common.utils;

public class FibonacciUtils {

	/**
	 * Calculates the nth fibonacci number
	 * @param n
	 * @return nth fibonacci number
	 */
	public static long fibnth(int n) {
		long arr[] = new long[3];
		arr[0] = 0;
		arr[1] = 1;
		for (int i = 2; i <= n; i++) {
			arr[i % 3] = arr[(i + 2) % 3] + arr[(i + 1) % 3];
		}

		return arr[n % 3];
	}
}
