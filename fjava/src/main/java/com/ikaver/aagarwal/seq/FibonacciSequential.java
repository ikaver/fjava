package com.ikaver.aagarwal.seq;

import com.ikaver.aagarwal.common.FibonacciBase;
import com.ikaver.aagarwal.common.utils.FibonacciUtils;

/**
 * Recursive implementation for fibonacci.
 * 
 * @author ankit
 */
public class FibonacciSequential extends FibonacciBase {

	public long fibonacci(int n) {
		if (n == 0) {
			return 0L;
		} else if (n <= 2L) {
			return 1L;
		} else if (n <= THRESHOLD) {
			return FibonacciUtils.fibnth(n);
		}
		return fibonacci(n - 1) + fibonacci(n - 2);
	}
}
