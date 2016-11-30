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

package org.mov.analyser.gp;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import org.mov.analyser.OrderComparator;

import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.quote.QuoteBundle;
import org.mov.util.TradingDate;

public class GeneticProgramme {
    // TODO: Chance of breeding should be related to size,value somehow?
    // i.e. the smaller, richer should breed more often?
    // need some sort of negative for being massive. 

    private final int MIN_SIZE = 12;
    private final int MAX_SIZE = 48;

    private int breedingPopulationSize;
    private TreeMap breedingPopulation;
    private TreeMap nextBreedingPopulation;
    private Mutator buyRuleMutator;
    private Mutator sellRuleMutator;
    private Random random;

    private GPQuoteBundle quoteBundle;
    private OrderComparator orderComparator;
    private TradingDate startDate;
    private TradingDate endDate;
    private float initialCapital;
    private float stockValue;
    private int numberStocks;
    private float tradeCost;

    private int generation;

    public GeneticProgramme(GPQuoteBundle quoteBundle, 
                            OrderComparator orderComparator,
                            TradingDate startDate,
                            TradingDate endDate,
                            float initialCapital,
                            float stockValue,
                            int numberStocks,
                            float tradeCost,
                            int breedingPopulationSize, 
                            int seed) {

        this.quoteBundle = quoteBundle;
        this.orderComparator = orderComparator;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
        this.stockValue = stockValue;
        this.numberStocks = numberStocks;
        this.tradeCost = tradeCost;
        this.breedingPopulationSize = breedingPopulationSize;

        nextBreedingPopulation = new TreeMap();
        breedingPopulation = new TreeMap();
        random = new Random(seed);

        // Create a mutator for the buy and sell rules. Buy rules shouldn't
        // use the "held" variable (buy rules won't be evaluated if held > 0).
        // Only use the "order" variable if the user has applied any ordering
        // to the data.
        buyRuleMutator = new Mutator(random, false, orderComparator != null);
        sellRuleMutator = new Mutator(random, true, orderComparator != null);

        generation = 1;
    }

    public void nextIndividual() {
        boolean validIndividual = false;

        // Loop until we create a valid individual that paper trades OK
        while(!validIndividual) {
            Individual individual = createIndividual();

            if(individual.isValid(MIN_SIZE, MAX_SIZE)) {
                try {
                    float value =
                        individual.paperTrade(quoteBundle,
                                              orderComparator,
                                              startDate,
                                              endDate,
                                              initialCapital,
                                              stockValue,
                                              numberStocks,
                                              tradeCost);
                    
                    // If we got here the paper trade was successful. Now let the
                    // individual 'compete' to see if it gets to breed next round.
                    // If the individual is fit enough, it'll get a chance to breed.
                    competeForBreeding(individual, value);
                    validIndividual = true;
                    
                }
                catch(EvaluationException e) {
                    // If there is a problem running the equation then
                    // it dies off naturally!
                }
            }
        }
    }

    public int nextGeneration() {
        // The new breeding population is made from the strongest
        // individuals from last generation. We also leave "nextBreedingPopulation"
        // the same - to ensure that the next population's strongest individuals
        // will be at least as good as the previous ones.
        breedingPopulation = new TreeMap(nextBreedingPopulation);
        return ++generation;
    }

    public Individual getBreedingIndividual(int index) {
        assert index < breedingPopulation.size();

        for(Iterator iterator = breedingPopulation.values().iterator(); iterator.hasNext();) {
            Individual individual = (Individual)iterator.next();

            if(index == 0)
                return individual;
            else
                index--;
        }

        assert false;
        return null;
    }

    public int getActualBreedingPopulation() {
        return breedingPopulation.size();
    }

    private Individual createIndividual() {
        if(generation == 1) 
            return new Individual(buyRuleMutator, sellRuleMutator);
        else {
            // Otherwise breed two parent individuals
            int motherIndex = random.nextInt(breedingPopulation.size());
            int fatherIndex = random.nextInt(breedingPopulation.size());

            Individual mother = getBreedingIndividual(motherIndex);
            Individual father = getBreedingIndividual(fatherIndex);

            return new Individual(random, buyRuleMutator, sellRuleMutator, mother, father);
        }
    }

    private void competeForBreeding(Individual individual, float value) {
        // If the individual made a loss or broke even, then we are just going to get 
        // rubbish if we breed from it, so even if it is the best we've seen so far, it
        // gets ignored.
        if(value > initialCapital) {
            // If there is another individual with exactly the same value -
            // we assume it made the same trades. Replace this individual
            // ONLY if the new individual is smaller. This puts a small
            // pressure for equations to be as tight as possible.
            Individual sameTradeIndividual = 
                (Individual)nextBreedingPopulation.get(new Float(value));

            if(sameTradeIndividual != null) {
                if(individual.getTotalEquationSize() < 
                   sameTradeIndividual.getTotalEquationSize())
                    nextBreedingPopulation.put(new Float(value), individual);
            }

            // Our individual is a unique butterfly. It'll get in only if the
            // breeding population isn't full yet, or it is better than an
            // existing individual.
            else {
                if(nextBreedingPopulation.size() < breedingPopulationSize) 
                    nextBreedingPopulation.put(new Float(value), individual);
            
                else {
                    Float weakestValue = (Float)nextBreedingPopulation.firstKey();

                    if(value > weakestValue.floatValue()) {
                        nextBreedingPopulation.remove(weakestValue);
                        nextBreedingPopulation.put(new Float(value), individual);
                    }
                }
            }
        }
    }
}
