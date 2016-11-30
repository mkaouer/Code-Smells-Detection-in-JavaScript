/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.ofcoarse;

public class Point {

  public double X;
  public double Y;

  public Point(int x, int y){
    this.X = x;
    this.Y = y;
  }

  public Point(double x, double y){
    this.X = x;
    this.Y = y;
  }

  public Point(Point other){
    this.X = other.X;
    this.Y = other.Y;
  }
  
  public Point(){
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Point other = (Point) obj;
    if (this.X != other.X) {
      return false;
    }
    if (this.Y != other.Y) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    long hash = 5;
    hash = 37 * hash + (long) this.X ^ (long) this.X >>> 32;
    hash = 37 * hash + (long) this.Y ^ (long) this.Y >>> 32;
    return (int) hash;
  }

}
