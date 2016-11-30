/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.ofcoarse;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class Droplet {

  /* curr data */
  private Point mCenter;
  private double mVolume;

  /* next data */
  private double mXSum;
  private double mYSum;
  private double mVolumeSum;
  private int mSumCount;

  public Droplet(Point center, double volume){
    mCenter = new Point(center.X, center.Y);
    mVolume = volume;

    resetNextData();
  }

  public Droplet(Droplet drop1) {
    mCenter = new Point(drop1.mCenter.X, drop1.mCenter.Y);
    mVolume = drop1.mVolume;

    resetNextData();
  }

  public double getVolume() {
    return mVolume;
  }

  public void resetNextData() {
    mXSum = 0;
    mYSum = 0;
    mVolumeSum = 0;
    mSumCount = 0;
  }

  public void addNextData(Point center, double volume) {
    mXSum += center.X;
    mYSum += center.Y;
    mVolumeSum += volume;
    mSumCount++;
  }
  
  public boolean compareSums(Droplet rhs){
    System.out.println("mXSum: "+mXSum+" rhs.XSum:"+rhs.mXSum);
    System.out.println("mYSum: "+mYSum+" rhs.YSum:"+rhs.mYSum);
    System.out.println("mSumCount: "+mSumCount+" rhs.SumCount: "+rhs.mSumCount);
    System.out.println("mVolumeSum: "+mVolumeSum+" rhs.mVolumeSum: "+rhs.mVolumeSum);
    if(mXSum != rhs.mXSum){
      System.out.println("mXSum: ");
      return false;
    } 
    if(mYSum != rhs.mYSum){
      System.out.println("mYSum: ");
      return false;
    }
    if(mVolumeSum != rhs.mVolumeSum){
      System.out.println("mVolumeSum: ");
      return false;
    }
    if(mSumCount != rhs.mSumCount){
      System.out.println("mSumCount: ");
      return false;
    }
    return true;
  }

  public void finalizePrediction() {
    mXSum /= mSumCount;
    mYSum /= mSumCount;
    mVolumeSum /= mSumCount;

    mVolume = mVolumeSum;
    mCenter.X = (int) mXSum;
    mCenter.Y = (int) mYSum;

    if(mSumCount == 0){
      mVolume = 0;
      mCenter.X = 0;
      mCenter.Y = 0;

      mXSum = 0;
      mYSum = 0;
      mVolumeSum = 0;
    }

  }

  public double getRadius() {
    /* Volume = area of a circle */
    /* area = Pi * r *r */
    /* r = sqrt(area / Pi) */

    double r = StrictMath.sqrt(mVolume / java.lang.Math.PI);
    return r;
  }

  public Point getCenter() {
    return mCenter;
  }

}
