package com.ikaver.aagarwal.fjavaexamples;

import java.math.BigInteger;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

public class FJavaKaratasubaMultiply {

	private final FJavaPool pool;

	public FJavaKaratasubaMultiply(FJavaPool pool) {
		this.pool = pool;
	}

	public BigInteger multiply(BigInteger x, BigInteger y) {
		FJavaKaratasubaTask task = new FJavaKaratasubaTask(x, y);
		pool.run(task);
		return task.getAnswer();
	}

	private static final class FJavaKaratasubaTask extends FJavaTask {

		private final BigInteger x, y;
		private BigInteger result;

		public FJavaKaratasubaTask(BigInteger x, BigInteger y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void compute() {
			int m = (Math.min(x.bitLength(), y.bitLength()) / 2);
			if (m <= Definitions.KARATSUBA_SEQ_THRESHOLD) {
				this.result = x.multiply(y);
				return;
			}

			BigInteger[] xs = split(x, m);
			BigInteger[] ys = split(y, m);

			FJavaKaratasubaTask z2task = new FJavaKaratasubaTask(xs[0], ys[0]);
			FJavaKaratasubaTask z0task = new FJavaKaratasubaTask(xs[1], ys[1]);
			FJavaKaratasubaTask z1task = new FJavaKaratasubaTask(add(xs), add(ys));

			
			z0task.runAsync(this);
			z2task.runAsync(this);
			z1task.runSync(this);

			sync();
			BigInteger z0, z2;
			BigInteger z1 = z1task.getAnswer().subtract(z2 = z2task.getAnswer())
					.subtract(z0 = z0task.getAnswer());
			this.result = z2.shiftLeft(2 * m).add(z1.shiftLeft(m)).add(z0);
			return;
		}

		private static BigInteger add(BigInteger integers[]) {
			BigInteger ans = BigInteger.valueOf(0);
			for (BigInteger bigInteger : integers) {
				ans = ans.add(bigInteger);
			}
			return ans;
		}

		private static BigInteger[] split(BigInteger x, int m) {
			BigInteger left = x.shiftRight(m);
			BigInteger right = x.subtract(left.shiftLeft(m));
			return new BigInteger[] { left, right };
		}

		public BigInteger getAnswer() {
			return result;
		}
	}
}
