/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse;

public class PointDistance {
  
  public static double distance(Point lhs, Point rhs){
    double xdiff = lhs.X - rhs.X;
    double ydiff = lhs.Y - rhs.Y;
    
    double sum = (xdiff * xdiff) + (ydiff * ydiff);
    
    return StrictMath.sqrt(sum);
  }
}
