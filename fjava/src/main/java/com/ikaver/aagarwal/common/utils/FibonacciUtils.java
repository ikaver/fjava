package com.ikaver.aagarwal.common.utils;


public class FibonacciUtils {

	/**
	 * Calculates the nth fibonacci number
	 * @param n
	 * @return nth fibonacci number
	 */
	public static long fibnth(int n) {
    if (n == 0) {
      return 0L;
    } else if (n <= 2) {
      return 1L;
    }
    else return fibnth(n-1) + fibnth(n-2);
	}
}
