package com.ikaver.aagarwal.fjavaexamples;

import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.FastStopwatch;
import com.ikaver.aagarwal.fjava.FJavaPool;
import com.ikaver.aagarwal.fjava.FJavaTask;

/**
 * LU matrix decomposition.
 * 
 * Code of recursive decomposition taken from  
 * @see <a href="http://gee.cs.oswego.edu/dl/papers/fj.pdf">this paper</a>
 * 
 * See  
 * @see <a href="http://www.cs.cornell.edu/~bindel/class/cs6210-f09/lec10.pdf">this explanation</a> 
 * to understand the math</a>
 **/
public class FJavaLU {

  // granularity is hard-wired as compile-time constant here
  static final int BLOCK_SIZE = 16; 

  public void calculateLU(FJavaPool pool, double [][] m, int n) {
    Block M = new Block(m, 0, 0);
    pool.run(new LowerUpper(n, M));
  }

  // Blocks record underlying matrix, and offsets into current block
  static class Block {
    final double[][] m;
    final int loRow;
    final int loCol;

    Block(double[][] mat, int lr, int lc) {
      m = mat; loRow = lr; loCol = lc;
    }
  }

  static class LUBase extends FJavaTask {

    private final FastStopwatch watch;

    public LUBase() {
      if (FJavaConf.shouldTrackStats()) {
        watch = new FastStopwatch();
      } else {
        watch = null;
      }
    }

    @Override
    public void compute() {
      if (FJavaConf.shouldTrackStats()) {
        watch.start();
      }
    }

    /**
     * Signified end of computation.
     */
    public void endCompute() {
      if (FJavaConf.shouldTrackStats()) {
        addComputeTime(watch.end());
      }
    }

  }

  static class Schur extends LUBase {
    final int size;
    final Block V;
    final Block W;
    final Block M;

    Schur(int size, Block V, Block W, Block M) {
      super();
      this.size = size; this.V = V; this.W = W; this.M = M;
    }

    void schur() { // base case
      for (int j = 0; j < BLOCK_SIZE; ++j) {
        this.tryLoadBalance();
        for (int i = 0; i < BLOCK_SIZE; ++i) {
          double s = M.m[i+M.loRow][j+M.loCol];
          for (int k = 0; k < BLOCK_SIZE; ++k) {
            s -= V.m[i+V.loRow][k+V.loCol] * W.m[k+W.loRow][j+W.loCol];
          }
          M.m[i+M.loRow][j+M.loCol] = s;
        }
      }
    }

    @Override
    public void compute() {
      super.compute();
      if (size == BLOCK_SIZE) {
        schur();
        endCompute();
      }
      else {
        int h = size / 2;

        Block M00 = new Block(M.m, M.loRow,   M.loCol);
        Block M01 = new Block(M.m, M.loRow,   M.loCol+h);
        Block M10 = new Block(M.m, M.loRow+h, M.loCol);
        Block M11 = new Block(M.m, M.loRow+h, M.loCol+h);

        Block V00 = new Block(V.m, V.loRow,   V.loCol);
        Block V01 = new Block(V.m, V.loRow,   V.loCol+h);
        Block V10 = new Block(V.m, V.loRow+h, V.loCol);
        Block V11 = new Block(V.m, V.loRow+h, V.loCol+h);

        Block W00 = new Block(W.m, W.loRow,   W.loCol);
        Block W01 = new Block(W.m, W.loRow,   W.loCol+h);
        Block W10 = new Block(W.m, W.loRow+h, W.loCol);
        Block W11 = new Block(W.m, W.loRow+h, W.loCol+h);

        endCompute();
        new FJavaSeq(
            new Schur(h, V00, W00, M00),
            new Schur(h, V01, W10, M00)
            ).runAsync(this);


        new FJavaSeq(
            new Schur(h, V00, W01, M01),
            new Schur(h, V01, W11, M01)
            ).runAsync(this);

        new FJavaSeq(
            new Schur(h, V10, W00, M10),
            new Schur(h, V11, W10, M10)
            ).runAsync(this);

        new FJavaSeq(
            new Schur(h, V10, W01, M11),
            new Schur(h, V11, W11, M11)
            ).runSync(this);

        this.sync();

      }
    }
  }


  static class Lower extends LUBase {

    final int size;
    final Block L;
    final Block M;

    Lower(int size, Block L, Block M) {
      super();
      this.size = size; this.L = L; this.M = M;
    }


    void lower() {  // base case
      for (int i = 1; i < BLOCK_SIZE; ++i) {
        this.tryLoadBalance();
        for (int k = 0; k < i; ++k) {
          double a = L.m[i+L.loRow][k+L.loCol];
          double[] x = M.m[k+M.loRow];
          double[] y = M.m[i+M.loRow];
          int n = BLOCK_SIZE;
          for (int p = n-1; p >= 0; --p) {
            y[p+M.loCol] -= a * x[p+M.loCol];
          }
        }
      }
    }

    @Override
    public void compute() {
      super.compute();
      if (size == BLOCK_SIZE) {
        lower();
        endCompute();
      }
      else {
        int h = size / 2;

        Block M00 = new Block(M.m, M.loRow,   M.loCol);
        Block M01 = new Block(M.m, M.loRow,   M.loCol+h);
        Block M10 = new Block(M.m, M.loRow+h, M.loCol);
        Block M11 = new Block(M.m, M.loRow+h, M.loCol+h);

        Block L00 = new Block(L.m, L.loRow,   L.loCol);
        Block L01 = new Block(L.m, L.loRow,   L.loCol+h);
        Block L10 = new Block(L.m, L.loRow+h, L.loCol);
        Block L11 = new Block(L.m, L.loRow+h, L.loCol+h);

        endCompute();
        new FJavaSeq(
            new Lower(h, L00, M00),
            new Schur(h, L10, M00, M10),
            new Lower(h, L11, M10)
            ).runAsync(this); 
        new FJavaSeq(
            new Lower(h, L00, M01),
            new Schur(h, L10, M01, M11),
            new Lower(h, L11, M11)
            ).runSync(this);

        this.sync();

      }
    }
  }


  static class Upper extends LUBase {

    final int size;
    final Block U;
    final Block M;

    Upper(int size, Block U, Block M) {
      super();
      this.size = size; this.U = U; this.M = M;
    }

    void upper() { // base case
      for (int i = 0; i < BLOCK_SIZE; ++i) {
        this.tryLoadBalance();
        for (int k = 0; k < BLOCK_SIZE; ++k) {
          double a = M.m[i+M.loRow][k+M.loCol] / U.m[k+U.loRow][k+U.loCol];
          M.m[i+M.loRow][k+M.loCol] = a;
          double[] x = U.m[k+U.loRow];
          double[] y = M.m[i+M.loRow];
          int n = BLOCK_SIZE - k - 1;
          for (int p = n - 1; p >= 0; --p) {
            y[p+k+1+M.loCol] -= a * x[p+k+1+U.loCol];
          }
        }
      }
    }

    @Override
    public void compute() {
      super.compute();
      if (size == BLOCK_SIZE) {
        upper();
        endCompute();
      }
      else {
        int h = size / 2;

        Block M00 = new Block(M.m, M.loRow,   M.loCol);
        Block M01 = new Block(M.m, M.loRow,   M.loCol+h);
        Block M10 = new Block(M.m, M.loRow+h, M.loCol);
        Block M11 = new Block(M.m, M.loRow+h, M.loCol+h);

        Block U00 = new Block(U.m, U.loRow,   U.loCol);
        Block U01 = new Block(U.m, U.loRow,   U.loCol+h);
        Block U10 = new Block(U.m, U.loRow+h, U.loCol);
        Block U11 = new Block(U.m, U.loRow+h, U.loCol+h);

        endCompute();
        new FJavaSeq(
            new Upper(h, U00, M00),
            new Schur(h, M00, U01, M01),
            new Upper(h, U11, M01)   
            ).runAsync(this); 

        new FJavaSeq(
            new Upper(h, U00, M10),
            new Schur(h, M10, U01, M11),
            new Upper(h, U11, M11) 
            ).runSync(this); 
        this.sync();
      }
    }
  }


  static class LowerUpper extends LUBase {

    final int size;
    final Block M;

    LowerUpper(int size, Block M) {
      super();
      this.size = size; this.M = M;
    }

    void lu() {  // base case
      for (int k = 0; k < BLOCK_SIZE; ++k) {
        //if(k % 4 == 0) this.tryLoadBalance();
        tryLoadBalance();
        for (int i = k+1; i < BLOCK_SIZE; ++i) {
          double b = M.m[k+M.loRow][k+M.loCol];
          double a = M.m[i+M.loRow][k+M.loCol] / b;
          M.m[i+M.loRow][k+M.loCol] = a;
          double[] x = M.m[k+M.loRow];
          double[] y = M.m[i+M.loRow];
          int n = BLOCK_SIZE-k-1;
          for (int p = n-1; p >= 0; --p) {
            y[k+1+p+M.loCol] -= a * x[k+1+p+M.loCol];
          }
        }
      }
    }

    @Override
    public void compute() {
      super.compute();
      if (size == BLOCK_SIZE) {
        lu();
        endCompute();
      }
      else {
        int h = size / 2;

        Block M00 = new Block(M.m, M.loRow,   M.loCol);
        Block M01 = new Block(M.m, M.loRow,   M.loCol+h);
        Block M10 = new Block(M.m, M.loRow+h, M.loCol);
        Block M11 = new Block(M.m, M.loRow+h, M.loCol+h);

        endCompute();
        new FJavaSeq(
            /* 
             * A is given a input.
             * 
             * Compute A00 = L00 * U00 (we get L00 and U00 from this step)
             * After this step, M00 will be LU decomposed.
             * (Top right half of M00 is U00, bottom left is L00)
             */
            new LowerUpper(h, M00), 
            /**
             * Now, compute in parallel:
             * L10 = A10 * U00^(-1) and U01 = L00^(-1) * A01
             * Store L10 in M01, and U01 in M10.
             * After this step, M01 and M10 will be LU decomposed.
             */
            new FJavaConcurrent(
                new Lower(h, M00, M01),
                new Upper(h, M00, M10)
                ),
                /**
                 * Now, calculate Schur's complement 
                 * S = A11 - L10 * U01, store in M11
                 */
                new Schur(h, M10, M01, M11),
                /**
                 * Now, LU decompose S = A11 - L10 * U01
                 * M11 = L11 * U11
                 * Store in M11.
                 * After this step, M11 will be LU decomposed.
                 */
                new LowerUpper(h, M11)
            ).runSync(this);
      }
    }
  }
}

