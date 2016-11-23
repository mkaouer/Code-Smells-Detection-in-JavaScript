/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime.util;

public class Stopwatch {
  private long start;
  private long stop;

  public void start() {
    start = System.currentTimeMillis(); // start timing
  }

  public void stop() {
    stop = System.currentTimeMillis(); // stop timing
  }

  public long elapsedTimeMillis() {
    return stop - start;
  }

  public @Override String toString() {
    return "elapsedTimeMillis: " + Long.toString(elapsedTimeMillis()); // print execution time
  }

  public void stopAndPrint(String str){
    stop();
    System.out.println(str + toString());
  }
}
