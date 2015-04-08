package com.ikaver.aagarwal.common;

import java.util.Comparator;

public class Point2D {
  
  public static final Comparator<Point2D> X_AXIS_COMPARATOR = new Comparator<Point2D>() {

    public int compare(Point2D o1, Point2D o2) {
      if(o1.x > o2.x) return 1;
      else if(o1.x < o2.x) return -1;
      else {
        if(o1.y > o2.y) return 1;
        else if(o1.y < o2.y) return -1;
        else return 0;
      }
    }
  };
  
  public final double x;
  public final double y;
  
  public Point2D(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public Point2D subtract(Point2D other) {
    return new Point2D(this.x-other.x, this.y-other.y);
  }
  
  public static boolean isRightTurn(Point2D first, Point2D second, Point2D third) {
    //sign( (Bx-Ax)*(Y-Ay) - (By-Ay)*(X-Ax) )
    Point2D line12 = second.subtract(first);
    Point2D line13 = third.subtract(first);
    double cross = cross(line12, line13);
    return cross < 0;
  }
  
  public static double cross(Point2D a, Point2D b) {
    return a.x * b.y - a.y * b.x;
  }
}
