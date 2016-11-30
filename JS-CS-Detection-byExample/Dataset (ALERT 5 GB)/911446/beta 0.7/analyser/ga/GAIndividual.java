/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/**
 *
 * @author  Alberto Nacher
 */
package org.mov.analyser.ga;

import java.util.Random;

import org.mov.parser.Expression;
import org.mov.portfolio.Portfolio;
import org.mov.util.Money;

/**
 * A trading individual evolved by the GA.
 * The Individual contains a set of parameters for the buy rule and
 * the sell rule.
 */
public class GAIndividual {
    
    // The parameters and their values and types
    private String[] parameters = null;
    private double[] values = null;
    private int[] types = null;
    
    // The portfolio value
    private Portfolio portfolio = null;
    private Money value = null;
    
    // The following define the probabilities that two inviduals
    // will be breed by the following methods.
    // These numbers will be overwritten by what is got from GA user interface.
    private static int MOTHER    = 40;
    private static int FATHER    = 40;
    private static int MUTATION  = 20;

    // The following enumerate the different methods that indivudals can
    // "breed" or be combined.
    private final static int BREED_BY_MOTHER    = 0;
    private final static int BREED_BY_FATHER    = 1;
    private final static int BREED_BY_MUTATION  = 2;
    
    /**
     * Create a new individual with the given parameters.
     *
     * @param parameters the name of parameters
     * @param values     the values of parameters
     * @param types      the types of parameters (integer or float)
     */
    public GAIndividual(String[] parameters, double[] values, int[] types) {
        
        this.parameters = new String[parameters.length];
        for (int ii=0; ii<this.parameters.length; ii++)
            this.parameters[ii] = new String(parameters[ii]);
        this.values = new double[values.length];
        for (int ii=0; ii<this.values.length; ii++)
            this.values[ii] = values[ii];
        this.types = new int[types.length];
        for (int ii=0; ii<this.types.length; ii++)
            this.types[ii] = types[ii];
    }

    /**
     * Randomly generate an individual using two individuals
     * as highest and lowest limits for the parameters.
     *
     * @param lowest lowest individual
     * @param highest highest individual
     */
    public GAIndividual(Random random, GAIndividual lowest, GAIndividual highest) {
        this.parameters = new String[lowest.size()];
        this.values = new double[lowest.size()];
        this.types = new int[lowest.size()];
        for (int ii=0; ii<lowest.size(); ii++) {
            this.parameters[ii] = lowest.parameter(ii);
            this.values[ii] = getRandom(random, lowest.value(ii), highest.value(ii), lowest.type(ii));
            this.types[ii] = lowest.type(ii);
        }
    }

    /**
     * Create a new individual by "breeding" or combining the two parent individuals.
     *
     * @param random the random number generator
     * @param father one parent individual
     * @param mother another parent individual
     */
    public GAIndividual(Random random, GAIndividual mother, GAIndividual father,
            GAIndividual lowest, GAIndividual highest) {
        this.parameters = new String[lowest.size()];
        this.values = new double[lowest.size()];
        this.types = new int[lowest.size()];
        for (int ii=0; ii<father.size(); ii++) {
            this.parameters[ii] = father.parameter(ii);
           // recombine mother and father values so that we have a mix as result.
            int breedType = getRandomBreedType(random);
            if (breedType == FATHER) // FATHER
                this.values[ii] = father.value(ii);
            else if (breedType == MOTHER) // MOTHER
                this.values[ii] = mother.value(ii);
            else // MUTATION
                this.values[ii] = getRandom(random, lowest.value(ii), highest.value(ii), lowest.type(ii));

            this.types[ii] = father.type(ii);
        }
    }
    
    // Throw the dice to get a random breeding type
    private int getRandomBreedType(Random random) {
        int percent = random.nextInt(100);

        if(MOTHER > percent)
            return BREED_BY_MOTHER;
        percent -= MOTHER;

        if(FATHER > percent)
            return BREED_BY_FATHER;
        percent -= FATHER;

        return BREED_BY_MUTATION;
    }
    
    // Throw the dice to get a random new value
    private double getRandom(Random random, double low, double up, int type) {
        
        double retValue = 0.0D;
        // if up<low then swap
        if(up<low) {
            double tmp = up;
            up = low;
            low = tmp;
        }
        if (type == Expression.INTEGER_TYPE) {
            int retValueInt = (new Double(low)).intValue() + random.nextInt((new Double(up)).intValue()+1-(new Double(low)).intValue());
            retValue = (double)retValueInt;
        } else {
            retValue = low + (up-low) * random.nextDouble();
        }
        return retValue;
    }
    
    /**
     * Get the final value of the individual after paper trading.
     *
     * @return the final value
     */
    public Money getValue() {
        return value;
    }

    /**
     * Set the final value of the individual after paper trading.
     *
     * @param value the final value
     */
    public void setValue(Money value) {
        this.value = value;
    }
    
    /**
     * Get the portfolio according to individual.
     *
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Set the portfolio according to individual.
     *
     * @param portfolio the portfolio
     */
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    /**
     * Get the number of parameters for this individual.
     *
     * @return the number of parameter
     */
    public int size() {
        return parameters.length;
    }

    /**
     * Get the index parameter.
     *
     * @return the string representing the parameter in the rule.
     */
    public String parameter(int index) {
        return parameters[index];
    }

    /**
     * Get the index value.
     *
     * @return the double representing the value of the parameter in the rule.
     */
    public double value(int index) {
        return values[index];
    }

    /**
     * Get the index type.
     *
     * @return the type of the parameter in the rule.
     */
    public int type(int index) {
        return types[index];
    }
    
    /**
     * Set the random mutation constants.
     *
     * @param randomPercentage the randomness percentage used in GA algorithm
     */
    public static void setRandomPercentage(int randomPercentage) {
        int rest = (100 - randomPercentage) % 2;
        MOTHER    = (int)((100 - randomPercentage + rest) / 2);
        FATHER    = (int)((100 - randomPercentage - rest) / 2);
        MUTATION  = randomPercentage;
    }
}
