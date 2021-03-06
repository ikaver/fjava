package com.ikaver.aagarwal.fjavaexamples;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.common.utils.FibonacciUtils;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

public class FJavaFibonacci {

	private final FJavaPool pool;

	public FJavaFibonacci(FJavaPool pool) {
		this.pool = pool;
	}

	public long fibonacci(int n) {
		FibonacciTask task = new FibonacciTask(n);
		pool.run(task);
		return task.getFibonacci();
	}

	private static class FibonacciTask extends FJavaTask {

		private int n;
		private long answer;
		private FastStopwatch watch;

		public FibonacciTask(int n) {
			super();
			if (FJavaConf.shouldTrackStats()) {
				watch = new FastStopwatch();
			}
			this.n = n;
		}

		@Override
		public void compute() {
			if (FJavaConf.shouldTrackStats()) {
				watch.start();
			}

			if (n <= FJavaConf.getFibonacciSequentialThreshold()) {
				answer = FibonacciUtils.fibnth(n);
				if (FJavaConf.shouldTrackStats()) {
					addComputeTime(watch.end());
				}
				return;
			}
			FibonacciTask childTask1 = new FibonacciTask(n - 1);
			FibonacciTask childTask2 = new FibonacciTask(n - 2);
			if (FJavaConf.shouldTrackStats()) {
				addComputeTime(watch.end());
			}
			childTask1.runAsync(this);
			childTask2.runSync(this);
			sync();
			answer = childTask1.answer + childTask2.answer;
		}

		@Override
		public String toString() {
			return String.format("Fibonacci %d", n);
		}

		public long getFibonacci() {
			return this.answer;
		}
	}

}
