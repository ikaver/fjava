package com.ikaver.aagarwal.javaforkjoin;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import com.ikaver.aagarwal.common.problems.Multiply;

public class KaratsubaMultiplyJavaForkJoin implements Multiply {

	private ForkJoinPool pool;

	public KaratsubaMultiplyJavaForkJoin(ForkJoinPool pool) {
		this.pool = pool;
	}

	@Override
	public BigInteger multiply(BigInteger x, BigInteger y) {
		return pool.invoke(new KaratsubaTask(x, y));
	}

	private static BigInteger[] split(BigInteger x, int m) {
		BigInteger left = x.shiftRight(m);
		BigInteger right = x.subtract(left.shiftLeft(m));
		return new BigInteger[] { left, right };
	}

	private static class KaratsubaTask extends RecursiveTask<BigInteger> {

		private final BigInteger x, y;
		private static final int THRESHOLD = 1000;

		public KaratsubaTask(BigInteger x, BigInteger y) {
			this.x = x;
			this.y = y;
		}

		protected BigInteger compute() {
			int m = (Math.min(x.bitLength(), y.bitLength()) / 2);
			if (m <= THRESHOLD) {
				return x.multiply(y);
			}

			BigInteger[] xs = split(x, m);
			BigInteger[] ys = split(y, m);

			KaratsubaTask z2task = new KaratsubaTask(xs[0], ys[0]);
			KaratsubaTask z0task = new KaratsubaTask(xs[1], ys[1]);
			KaratsubaTask z1task = new KaratsubaTask(add(xs), add(ys));

			z0task.fork();
			z2task.fork();
			BigInteger z0, z2;
			BigInteger z1 = z1task.invoke().subtract(z2 = z2task.join())
					.subtract(z0 = z0task.join());

			return z2.shiftLeft(2 * m).add(z1.shiftLeft(m)).add(z0);
		}

		private static BigInteger add(BigInteger integers[]) {
			BigInteger ans = BigInteger.valueOf(0);
			for (BigInteger bigInteger : integers) {
				ans = ans.add(bigInteger);
			}
			return ans;
		}

	}

}
