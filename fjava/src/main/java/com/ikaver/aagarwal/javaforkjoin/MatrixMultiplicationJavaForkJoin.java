package com.ikaver.aagarwal.javaforkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.ikaver.aagarwal.common.Definitions;
import com.ikaver.aagarwal.common.FJavaConf;
import com.ikaver.aagarwal.common.SeqJavaForkJoin;
import com.ikaver.aagarwal.common.problems.MatrixMultiplication;

public class MatrixMultiplicationJavaForkJoin extends RecursiveAction implements MatrixMultiplication {


  private static final long serialVersionUID = -3456393489891973164L;
  private float [][] A;
  private float [][] B;
  private float [][] C;
  private int aRow, aCol, bRow, bCol, cRow, cCol;
  private int size;
  
  private ForkJoinPool pool;
  
  public MatrixMultiplicationJavaForkJoin(ForkJoinPool pool) {
    this.pool = pool;
  }
  
  public MatrixMultiplicationJavaForkJoin(float [][] A, float [][] B, float [][] C, int size,
      int rowA, int colA, int rowB, int colB, int rowC, int colC) {
    this.A = A;
    this.B = B;
    this.C = C;
    this.size = size;
    this.aRow = rowA;
    this.aCol = colA;
    this.bRow = rowB;
    this.bCol = colB;
    this.cRow = rowC;
    this.cCol = colC;
  } 
  
  public void multiply(float[][] a, float[][] b, float[][] result) {
    this.pool.invoke(new MatrixMultiplicationJavaForkJoin(a, b, result, a.length, 0,0,0,0,0,0));
  }
  
  public void compute() {
    if(size <= FJavaConf.getMatrixMultiplicationSequentialThreshold()) {
      multiplySeq();
      return;
    }
    int mid = size/2;
    invokeAll(
        new SeqJavaForkJoin(new RecursiveAction[] {
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow, aCol,     bRow,     bCol, cRow, cCol),
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow, aCol+mid, bRow+mid, bCol, cRow, cCol)
        }),
        new SeqJavaForkJoin(new RecursiveAction[] {
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow, aCol,     bRow,     bCol+mid, cRow, cCol+mid),
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow, aCol+mid, bRow+mid, bCol+mid, cRow, cCol+mid)
        }),
        new SeqJavaForkJoin(new RecursiveAction[] {
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow+mid, aCol,     bRow,     bCol,     cRow+mid, cCol),
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow+mid, aCol+mid, bRow+mid, bCol, cRow+mid, cCol)
        }), 
        new SeqJavaForkJoin(new RecursiveAction[] {
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow+mid, aCol,     bRow,     bCol+mid, cRow+mid, cCol+mid),
            new MatrixMultiplicationJavaForkJoin(A, B, C, mid, aRow+mid, aCol+mid, bRow+mid, bCol+mid, cRow+mid, cCol+mid)
        })
              
    );
    
  }
  
  
  private void multiplySeq() {
    for (int j = 0; j < size; j+=2) {
      for (int i = 0; i < size; i +=2) {

        float[] a0 = A[aRow+i];
        float[] a1 = A[aRow+i+1];

        float s00 = 0.0F; 
        float s01 = 0.0F; 
        float s10 = 0.0F; 
        float s11 = 0.0F; 

        for (int k = 0; k < size; k+=2) {

          float[] b0 = B[bRow+k];

          s00 += a0[aCol+k]   * b0[bCol+j];
          s10 += a1[aCol+k]   * b0[bCol+j];
          s01 += a0[aCol+k]   * b0[bCol+j+1];
          s11 += a1[aCol+k]   * b0[bCol+j+1];

          float[] b1 = B[bRow+k+1];

          s00 += a0[aCol+k+1] * b1[bCol+j];
          s10 += a1[aCol+k+1] * b1[bCol+j];
          s01 += a0[aCol+k+1] * b1[bCol+j+1];
          s11 += a1[aCol+k+1] * b1[bCol+j+1];
        }

        C[cRow+i]  [cCol+j]   += s00;
        C[cRow+i]  [cCol+j+1] += s01;
        C[cRow+i+1][cCol+j]   += s10;
        C[cRow+i+1][cCol+j+1] += s11;
      }
    }
  }



}
