package com.ikaver.aagarwal.seq;

import java.util.ArrayList;
import java.util.Arrays;

import com.ikaver.aagarwal.common.Point2D;

public class SeqConvexHull {
  
  private Point2D [] points;
  private int left;
  private int right;

  public SeqConvexHull(Point2D [] points, int left, int right) {
    this.points = points;
    this.left = left;
    this.right = right;
  }

  public ArrayList<Point2D> compute() {
    Arrays.sort(points, Point2D.X_AXIS_COMPARATOR);
    return convexHull(this.left, this.right);
  }
  
  private ArrayList<Point2D> convexHull(int left, int right) {
    int mid = (left+right) / 2;
    ArrayList<Point2D> leftCH = convexHull(left, mid);
    ArrayList<Point2D> rightCH = convexHull(mid+1, right);
    return stich(leftCH, rightCH);
  }
  
  private ArrayList<Point2D> stich(ArrayList<Point2D> leftCH, ArrayList<Point2D> rightCH) {
    ArrayList<Point2D> stichedCH = new ArrayList<Point2D>();
    int left = leftCH.size()-1;
    int right = rightCH.size()-1;
    //https://code.google.com/p/dyn4j/source/browse/trunk/src/org/dyn4j/game2d/geometry/hull/DivideAndConquer.java?r=50
    return stichedCH;
  }

}
