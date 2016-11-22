/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package examples.supergene;

import org.jgap.*;
import org.jgap.impl.*;

/**
 * To test the Supergene, we created the "make change" version with
 * additional condition: the number of nickels and pennies must be
 * both even or both odd. The supergene encloses two genes
 * (nickels and pennies) and is valid if the condition above is
 * satisfied.
 *
 * @author Neil Rotstan
 * @author Klaus Meffert
 * @author Audrius Meskauskas
 * @since 2.0
 */
public class SupergeneSample
    extends AbstractSupergeneTest {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.3 $";

  /**
   * Executes the genetic algorithm to determine the minimum number of
   * coins necessary to make up the given target amount of change. The
   * solution will then be written to System.out.
   *
   * @param a_targetChangeAmount the target amount of change for which this
   * method is attempting to produce the minimum number of coins
   *
   * @return absolute difference between the required and computed change
   * amount
   * @throws Exception
   */
  public int makeChangeForAmount(int a_targetChangeAmount)
      throws Exception {
    // Start with a DefaultConfiguration, which comes setup with the
    // most common settings.
    // -------------------------------------------------------------
    Configuration conf = new DefaultConfiguration();
    // Set the fitness function we want to use, which is our
    // MinimizingMakeChangeFitnessFunction. We construct it with
    // the target amount of change passed in to this method.
    // ---------------------------------------------------------
    SupergeneChangeFitnessFunction fitnessFunction =
        new SupergeneChangeFitnessFunction(a_targetChangeAmount);
    conf.setFitnessFunction(fitnessFunction);
    conf.setKeepPopulationSizeConstant(false);
    // Now we need to tell the Configuration object how we want our
    // Chromosomes to be setup. We do that by actually creating a
    // sample Chromosome and then setting it on the Configuration
    // object. As mentioned earlier, we want our Chromosomes to each
    // have four genes, one for each of the coin types. We want the
    // values (alleles) of those genes to be integers, which represent
    // how many coins of that type we have. We therefore use the
    // IntegerGene class to represent each of the genes. That class
    // also lets us specify a lower and upper bound, which we set
    // to sensible values for each coin type.
    // --------------------------------------------------------------
    Gene[] sampleGenes = new Gene[3];
    sampleGenes[DIMES] = getDimesGene(conf);
    sampleGenes[QUARTERS] = getQuartersGene(conf);
    sampleGenes[2] = new NickelsPenniesSupergene(conf, new Gene[] {
        getNickelsGene(conf),
        getPenniesGene(conf),
    });
    int s = solve(conf, a_targetChangeAmount, fitnessFunction, sampleGenes);
    return s;
  }

  public static void main(String[] args) {
    SupergeneSample test = new SupergeneSample();
    test.test();
  }
}
