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

package nz.org.venice.analyser.gp;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import nz.org.venice.analyser.GPGondolaSelection;
import nz.org.venice.analyser.GPModuleConstants;
import nz.org.venice.analyser.OrderCache;

import nz.org.venice.parser.Expression;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.VeniceLog;

/**
 * The Genetic Programme creates and breeds random paper trading individuals. This
 * class runs the GP.
 *
 * @author Andrew Leppard
 * @see Individual
 * @see Mutator
 */
public class GeneticProgramme {
    
    // An individual with less nodes than this will be dropped
    private final int MIN_SIZE = 12;
    
    // An individual with more nodes than this will be dropped
    private final int MAX_SIZE = 48;
    
    // Number of rules (buy rule and sell rule)
    private final int BUY_RULE = GPModuleConstants.BUY_RULE;
    private final int SELL_RULE = GPModuleConstants.SELL_RULE;
    private final int NUMBER_RULES = GPModuleConstants.NUMBER_RULES;
    
    // Mutation rate to generate a population from a given initial population
    private final static int MUTATION_PERCENT = 95;
    
    // Size of breeding population. This is the number of individuals
    // each generation that can have their "genes" pass on to the
    // next generation.
    private int breedingPopulationSize;
    
    // The sum of the value of all the breeding population indviduals.
    // This is used when calculating which individual to breed from,
    // it allows us to proportionally breed from the most successful
    // individuals.
    private double breedingPopulationSum;
    
    // An ordered map of the breeding individauls for the current generation.
    // This will be empty for the first generation.
    private TreeMap breedingPopulation;
    
    // An ordered map of the breeding individuals for the next generation
    private TreeMap nextBreedingPopulation;
    
    // The mutator used to create/mutate buy rules
    private Mutator buyRuleMutator;
    
    // The mutator used to create/mutate sell rules
    private Mutator sellRuleMutator;
    
    // Our random number generator
    private Random random;
    
    // Historical quote data
    private GPQuoteBundle quoteBundle;
    
    // UI containing user's probability of using certain expression nodes
    private GPGondolaSelection GPGondolaSelection;
    
    // Cache of stock quote order
    private OrderCache orderCache;
    
    // Start date of paper trading
    private TradingDate startDate;
    
    // End date of paper trading
    private TradingDate endDate;
    
    // Initial value of each portfolio
    private Money initialCapital;
    
    // Initial value of portfolio's stocks
    private Money stockValue;
    
    // Number of stocks in portfolio (used iff stockValue == null).
    private int numberStocks;
    
    // Cost of trade
    private Money tradeCost;
    
    // Current generation number, starting from 1.
    private int generation;
    
    // The rule getting the buy price
    private String tradeValueBuy;

    // The rule getting the sell price
    private String tradeValueSell;
    
    /**
     * Get ready to run the GP.
     *
     * @param quoteBundle the historical quote data
     * @param GPGondolaSelection UI containing user's desired expression probabilities
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param initialCapital initial capital in the portfolio
     * @param stockValue the rough value of each stock holding
     * @param numberStocks number of stocks in the portfolio
     * @param tradeCost the cost of a trade
     * @param breedingPopulationSize number of individuals that can breed
     * @param tradeValueBuy value for buying a stock
     * @param tradeValueSell value for selling a stock
     */
    public GeneticProgramme(GPQuoteBundle quoteBundle,
                            GPGondolaSelection GPGondolaSelection,
                            OrderCache orderCache,
                            TradingDate startDate,
                            TradingDate endDate,
                            Money initialCapital,
                            Money stockValue,
                            int numberStocks,
                            Money tradeCost,
                            int breedingPopulationSize,
                            String tradeValueBuy,
                            String tradeValueSell) {
        
        this.quoteBundle = quoteBundle;
        this.GPGondolaSelection = GPGondolaSelection;
        this.orderCache = orderCache;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
        this.stockValue = stockValue;
        this.numberStocks = numberStocks;
        this.tradeCost = tradeCost;
        this.breedingPopulationSize = breedingPopulationSize;
        this.tradeValueBuy = tradeValueBuy;
        this.tradeValueSell = tradeValueSell;
        
        nextBreedingPopulation = new TreeMap();
        breedingPopulation = new TreeMap();
	long seed = System.currentTimeMillis(); 

	VeniceLog.getInstance().log("GeneticProgramme seed = " + seed);
        random = new Random(seed);
        
        // Create a mutator for the buy and sell rules. Buy rules shouldn't
        // use the "held" variable (buy rules won't be evaluated if held > 0).
        buyRuleMutator = new Mutator(random, GPGondolaSelection, false, orderCache.isOrdered());
        sellRuleMutator = new Mutator(random, GPGondolaSelection, true, orderCache.isOrdered());
        
        generation = 1;
    }
    
    /**
     * Run one iteration of the GP. This will create a single valid individual.
     */
    public void nextIndividual(Expression buyRule, Expression sellRule, int mutations) {
        boolean validIndividual = false;
        // consider if you try to create an individual twice,
        // the second time we create it with random mutations, because
        // the rules passed as parameters from user do not fit the breeding process.
        boolean twice = false;
        
        // Loop until we create a valid individual that paper trades OK
        while(!validIndividual) {
            Individual individual = createIndividual(buyRule, sellRule, mutations, twice);
            twice = true;
            if(individual.isValid(MIN_SIZE, MAX_SIZE)) {
                try {
                    Money value =
                        individual.paperTrade(quoteBundle,
                                                orderCache,
                                                startDate,
                                                endDate,
                                                initialCapital,
                                                stockValue,
                                                numberStocks,
                                                tradeCost,
                                                tradeValueBuy,
                                                tradeValueSell);
                    
                    // If we got here the paper trade was successful. Now let the
                    // individual 'compete' to see if it gets to breed next round.
                    // If the individual is fit enough, it'll get a chance to breed.
                    competeForBreeding(individual, value);
                    if (individual!=null && value!=null) validIndividual = true;
                }
                catch(EvaluationException e) {
                    // If there is a problem running the equation then
                    // it dies off naturally!
                }
            }
        }
    }
    
    /**
     * Enter the next generation.
     */
    public int nextGeneration() {
        // The new breeding population is made from the strongest
        // individuals from last generation. We also leave "nextBreedingPopulation"
        // the same - to ensure that the next population's strongest individuals
        // will be at least as good as the previous ones.
        breedingPopulation = new TreeMap(nextBreedingPopulation);
        
        // Calculate sum of portfolio values of each individual. We use this
        // when choosing who gets to breed next. The bigger the value compared
        // to other individuals, the greater chance of breeding.
        breedingPopulationSum = 0.0D;
        
        for(Iterator iterator = breedingPopulation.keySet().iterator(); iterator.hasNext();) {
            Money value = (Money)iterator.next();
            breedingPopulationSum += value.doubleValue();
        }
        
        return ++generation;
    }
    
    /**
     * Get one of the current generation's breeding individual.
     *
     * @param index of the breeding individual
     * @return the breeding individual
     */
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
    
    /**
     * This function is used to return a breeding individuals. We keep a sum of
     * the values of all the breeding individuals. To choose an individual to
     * breed we pick a random number between 0 and the sum of the values.
     * That random number is passed to this function. The individuals with
     * the largest values will be more likely to breed.
     *
     * @param value random number
     * @return breeding individual
     */
    private Individual getBreedingIndividual(double value) {
        assert value <= breedingPopulationSum;
        
        for(Iterator iterator = breedingPopulation.values().iterator(); iterator.hasNext();) {
            Individual individual = (Individual)iterator.next();
            
            value -= individual.getValue().doubleValue();
            
            if(value <= 0.0D)
                return individual;
        }
        
        // It's possible but unlikely we get here. If so return the best performing
        // individual to give it a little more chance.
        return (Individual)breedingPopulation.get(breedingPopulation.lastKey());
        
    }
    
    /**
     * Get the size of the current breeding population.
     *
     * @return current breeding population size
     */
    public int getBreedingPopulationSize() {
        return breedingPopulation.size();
    }
    
    /**
     * Get the size of the next generation's breeding population.
     *
     * @return future breeding population size
     */
    public int getNextBreedingPopulationSize() {
        return nextBreedingPopulation.size();
    }
    
    /**
     * Create a new individual. If it is the first generation then we will
     * create an entirely random individual, otherwise we will base it on
     * the combination of two existing individuals.
     *
     * @return the new individual
     */
    private Individual createIndividual(Expression buyRule, Expression sellRule, int mutations, boolean twice) {
        // The first generation we use the rules as defined
        // in Initial Population Section.
        if(generation == 1)
            // buyRule or sellRule are null
            // if we must create a random individual.
            // buyRule and sellRule are not null
            // if we must create an individual according to
            // the buy/sell rules passed as parameters
            // (got from Initial Population Section).
            if ((buyRule==null) || (sellRule==null)) {				
                return new Individual(buyRuleMutator, sellRuleMutator);
            } else {
                Expression newBuyExpression;
                Expression newSellExpression;
                if (twice) {

                    newBuyExpression = buyRuleMutator.mutate(buyRule, MUTATION_PERCENT);
                    newSellExpression = sellRuleMutator.mutate(sellRule, MUTATION_PERCENT);
                } else {
                    newBuyExpression = buyRule;
                    newSellExpression = sellRule;
                }
                // Loop mutations times so to get a new couple of buy/sell rules
                // the mutate rate is given by the constant value MUTATION_PERCENT
                for (int i=0; i<mutations; i++) {
                    newBuyExpression = buyRuleMutator.mutate(newBuyExpression, MUTATION_PERCENT);
                    newSellExpression = sellRuleMutator.mutate(newSellExpression, MUTATION_PERCENT);
                }

                return new Individual(newBuyExpression.simplify(), newSellExpression.simplify());
            }
        else {
            // Otherwise breed two parent individuals. We do these by calculating
            // a random value between 0 and the sum of all the individual values.
            // See getBreedingIndividual(double) for details.
            double motherValue = random.nextDouble() * breedingPopulationSum;
            double fatherValue = random.nextDouble() * breedingPopulationSum;
            
            Individual mother = getBreedingIndividual(motherValue);
            Individual father = getBreedingIndividual(fatherValue);
	    
            return new Individual(random, buyRuleMutator, sellRuleMutator, mother, father);
        }
    }
    
    /**
     * Given an individual and its net worth, make it fight for a place in the
     * next generation's breeding population.
     *
     * @param individual new individual
     * @param value the individual's value
     */
    private void competeForBreeding(Individual individual, Money value) {
        // If the individual made a loss or broke even, then we are just going to get
        // rubbish if we breed from it, so even if it is the best we've seen so far, it
        // gets ignored.
        if(individual!=null && value!=null && value.isGreaterThan(initialCapital)) {
            // If there is another individual with exactly the same value -
            // we assume it made the same trades. Replace this individual
            // ONLY if the new individual is smaller in size. This puts a small
            // pressure for equations to be as tight as possible.
            Individual sameTradeIndividual =
                (Individual)nextBreedingPopulation.get(value);
            
            if(sameTradeIndividual != null) {
                if(individual.getTotalEquationSize() <
                    sameTradeIndividual.getTotalEquationSize())
                     nextBreedingPopulation.put(value, individual);
            }
            
            // Our individual is a unique butterfly. It'll get in only if the
            // breeding population isn't full yet, or it is better than an
            // existing individual.
            else {
                if(nextBreedingPopulation.size() < breedingPopulationSize)
                    nextBreedingPopulation.put(value, individual);
                
                else {
                    Money weakestValue = (Money)nextBreedingPopulation.firstKey();
                    
                    if(value.isGreaterThan(weakestValue)) {
                        nextBreedingPopulation.remove(weakestValue);
                        nextBreedingPopulation.put(value, individual);
                    }
                }
            }
        }
    }
}
