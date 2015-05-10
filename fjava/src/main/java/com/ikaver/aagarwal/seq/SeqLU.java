package com.ikaver.aagarwal.seq;


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
public class SeqLU {

  // granularity is hard-wired as compile-time constant here
  static final int BLOCK_SIZE = 16; 


  public void calculateLU(double [][] m, int n) {
    Block M = new Block(m, 0, 0);
    new LowerUpper(n, M).run();
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

  static class Schur {
    final int size;
    final Block V;
    final Block W;
    final Block M;

    Schur(int size, Block V, Block W, Block M) {
      this.size = size; this.V = V; this.W = W; this.M = M;
    }

    void schur() { // base case
      for (int j = 0; j < BLOCK_SIZE; ++j) {
        for (int i = 0; i < BLOCK_SIZE; ++i) {
          double s = M.m[i+M.loRow][j+M.loCol];
          for (int k = 0; k < BLOCK_SIZE; ++k) {
            s -= V.m[i+V.loRow][k+V.loCol] * W.m[k+W.loRow][j+W.loCol];
          }
          M.m[i+M.loRow][j+M.loCol] = s;
        }
      }
    }

    public void run() {
      if (size == BLOCK_SIZE) {
        schur();
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

        new Schur(h, V00, W00, M00).run();
        new Schur(h, V01, W10, M00).run();
        new Schur(h, V00, W01, M01).run();
        new Schur(h, V01, W11, M01).run();
        new Schur(h, V10, W00, M10).run();
        new Schur(h, V11, W10, M10).run();
        new Schur(h, V10, W01, M11).run();
        new Schur(h, V11, W11, M11).run();

      }
    }
  }

  static class Lower {

    final int size;
    final Block L;
    final Block M;

    Lower(int size, Block L, Block M) {
      this.size = size; this.L = L; this.M = M;
    }


    void lower() {  // base case
      for (int i = 1; i < BLOCK_SIZE; ++i) {
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

    public void run() {
      if (size == BLOCK_SIZE) {
        lower();
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

        new Lower(h, L00, M00).run();
        new Schur(h, L10, M00, M10).run();
        new Lower(h, L11, M10).run();
        new Lower(h, L00, M01).run();
        new Schur(h, L10, M01, M11).run();
        new Lower(h, L11, M11).run();
      }
    }
  }


  static class Upper {

    final int size;
    final Block U;
    final Block M;

    Upper(int size, Block U, Block M) {
      this.size = size; this.U = U; this.M = M;
    }

    void upper() { // base case
      for (int i = 0; i < BLOCK_SIZE; ++i) {
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


    public void run() {
      if (size == BLOCK_SIZE) {
        upper();
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

        new Upper(h, U00, M00).run();
        new Schur(h, M00, U01, M01).run();
        new Upper(h, U11, M01).run();
        new Upper(h, U00, M10).run();
        new Schur(h, M10, U01, M11).run();
        new Upper(h, U11, M11).run();
      }
    }
  }


  static class LowerUpper {

    final int size;
    final Block M;

    LowerUpper(int size, Block M) {
      this.size = size; this.M = M;
    }

    void lu() {  // base case
      for (int k = 0; k < BLOCK_SIZE; ++k) {
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

    public void run() {
      if (size == BLOCK_SIZE) {
        lu();
      }
      else {
        int h = size / 2;

        Block M00 = new Block(M.m, M.loRow,   M.loCol);
        Block M01 = new Block(M.m, M.loRow,   M.loCol+h);
        Block M10 = new Block(M.m, M.loRow+h, M.loCol);
        Block M11 = new Block(M.m, M.loRow+h, M.loCol+h);


        new LowerUpper(h, M00).run();
        new Lower(h, M00, M01).run();
        new Upper(h, M00, M10).run();
        new Schur(h, M10, M01, M11).run();
        new LowerUpper(h, M11).run();

      }
    }
  }
}

