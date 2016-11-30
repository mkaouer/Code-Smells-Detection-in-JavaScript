/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

public class Policy {
  public static final boolean Debug = true;
  public class Gpu {
    public static final int MinIterations = 10;
    public static final int MaxDivergentScore = 10;
    public static final int MaxRRegCount = 8;
    public static final boolean PossiblyUseNestedFor = false;
  }
}
