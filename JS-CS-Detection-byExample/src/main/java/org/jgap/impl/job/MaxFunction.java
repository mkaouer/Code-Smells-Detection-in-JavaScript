/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licencing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package org.jgap.impl.job;

import org.jgap.*;
import org.jgap.impl.*;

/**
 * Fitness function for SimpleJobConsumer.
 *
 * @author Klaus Meffert
 * @since 3.2
 */
public class MaxFunction
    extends FitnessFunction {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.1 $";

  /*
   * @param a_subject the Chromosome to be evaluated
   * @return defect rate of our problem
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public double evaluate(IChromosome a_subject) {
    int total = 0;

    for (int i = 0; i < a_subject.size(); i++) {
      BooleanGene value = (BooleanGene) a_subject.getGene(a_subject.size() -
          (i + 1));
      if (value.booleanValue()) {
        total += Math.pow(2.0, (double) i);
      }
    }

    return total;
  }
}
