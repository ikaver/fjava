package com.ikaver.aagarwal.javaforkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import com.ikaver.aagarwal.common.FibonacciBase;
import com.ikaver.aagarwal.common.utils.FibonacciUtils;

public class FibonacciJavaForkJoin extends FibonacciBase {

	private final ForkJoinPool pool;

	public FibonacciJavaForkJoin(ForkJoinPool pool) {
		this.pool = pool;
	}

	@Override
	public long fibonacci(int n) {
		return pool.invoke(new FibonacciTask(n));
	}

	private static class FibonacciTask extends RecursiveTask<Long> {

		private final int n;

		public FibonacciTask(int n) {
			this.n = n;
		}

		@Override
		protected Long compute() {
			if (n == 0) {
				return 0L;
			} else if (n <= 2) {
				return 1L;
			} else if (n <= THRESHOLD) {
                return FibonacciUtils.fibnth(n);
			}

			FibonacciTask f1 = new FibonacciTask(n - 1);
			FibonacciTask f2 = new FibonacciTask(n - 2);

			f1.fork();
			long n_2 = f2.compute();
			long n_1 = f1.join();

			return n_2 + n_1;
		}
	}
}
