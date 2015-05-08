package com.ikaver.aagarwal.javaforkjoin;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import com.ikaver.aagarwal.common.FJavaConf;

public class KaratsubaMultiplyJavaForkJoin extends RecursiveTask<BigInteger> {


  private static final long serialVersionUID = 569249375244976958L;
  private ForkJoinPool pool;
  private BigInteger x, y;

  public KaratsubaMultiplyJavaForkJoin(ForkJoinPool pool) {
    this.pool = pool;
  }

  public KaratsubaMultiplyJavaForkJoin(BigInteger x, BigInteger y) {
    this.x = x;
    this.y = y;
  }

  public BigInteger multiply(BigInteger x, BigInteger y) {
    KaratsubaMultiplyJavaForkJoin task = new KaratsubaMultiplyJavaForkJoin(x, y);
    pool.invoke(task);
    return task.join();
  }

  @Override
  protected BigInteger compute() {
    int m = (Math.min(x.bitLength(), y.bitLength()) / 2);
    if (m <= FJavaConf.getKaratsubaSequentialThreshold()) {
      return x.multiply(y);
    }

    BigInteger[] xs = split(x, m);
    BigInteger[] ys = split(y, m);

    KaratsubaMultiplyJavaForkJoin z2task = new KaratsubaMultiplyJavaForkJoin(
        xs[0], ys[0]);
    KaratsubaMultiplyJavaForkJoin z0task = new KaratsubaMultiplyJavaForkJoin(
        xs[1], ys[1]);
    KaratsubaMultiplyJavaForkJoin z1task = new KaratsubaMultiplyJavaForkJoin(
        add(xs), add(ys));

    invokeAll(z0task, z1task, z2task);

    BigInteger z0, z2;
    BigInteger z1 = z1task.join().subtract(z2 = z2task.join())
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

  private static BigInteger[] split(BigInteger x, int m) {
    BigInteger left = x.shiftRight(m);
    BigInteger right = x.subtract(left.shiftLeft(m));
    return new BigInteger[] { left, right };
  }
}
