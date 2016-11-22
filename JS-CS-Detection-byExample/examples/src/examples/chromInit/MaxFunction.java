/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licencing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package examples.chromInit;

import org.jgap.*;
import org.jgap.impl.*;

/**
 * Fitness function for our example. See evolve() method for details.
 *
 * @author Neil Rotstan
 * @author Klaus Meffert
 * @since 2.4
 */
public class MaxFunction
    extends FitnessFunction {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.3 $";

  /**
   * See examples.simpleBoolean.MaxFunction for description.
   * Please notice that in this example here, we have chromosomes with one and
   * Chromosomes with two Genes, so that the description from the original
   * MaxFunction does not apply directly here.
   * @param a_subject the Chromosome to be evaluated
   * @return defect rate of our problem
   *
   * @author Klaus Meffert
   * @since 2.4
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
