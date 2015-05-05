package com.ikaver.aagarwal.seq;

import java.math.BigInteger;

import com.ikaver.aagarwal.common.FJavaConf;

/***
 * Sequential implementation of Karatsuba algorithm
 */
public class SeqKaratsuba {
	
	/**
	 * Method to multiply 2 BigIntegers via Karatsuba.
	 */
	public static BigInteger multiply(BigInteger x, BigInteger y) {
		int m = (Math.min(x.bitLength(), y.bitLength()) / 2);
		if (m <= FJavaConf.getKaratsubaSequentialThreshold()) {
			return x.multiply(y);
		}

		BigInteger[] xs = split(x, m);
		BigInteger[] ys = split(y, m);
		
		BigInteger z2 = multiply(xs[0], ys[0]);
		BigInteger z0 = multiply(xs[1], ys[1]);
		BigInteger z1 = multiply(add(xs), add(ys));
		
		z1 = z1.subtract(z2).subtract(z0);
		return z2.shiftLeft(2 * m).add(z1.shiftLeft(m)).add(z0);
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
}
