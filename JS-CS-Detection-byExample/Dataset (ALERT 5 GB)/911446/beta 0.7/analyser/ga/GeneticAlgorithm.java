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

import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import org.mov.analyser.OrderCache;
import org.mov.analyser.PaperTrade;

import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.Variables;
import org.mov.portfolio.Portfolio;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.EODQuoteBundle;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;

/**
 * The Genetic Algorithm creates and breeds random paper trading individuals. This
 * class runs the GA.
 */
public class GeneticAlgorithm {
    
    // The generic name of all the Individuals' portfolios
    private final static String PORTFOLIO_NAME =
        Locale.getString("GENETIC_ALGORITHM_PORTFOLIO");

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
    
    // Our random number generator
    private Random random;
    
    // Historical quote data
    private EODQuoteBundle quoteBundle;
    
    // Cache of stock quote order
    private OrderCache orderCache;
    
    // Expression rule (fixed in the GA process, becasue only prameters can change)
    private Expression buyRule;
    private Expression sellRule;
    
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
    
    // lowest and highest individual that show the limits where GA have to run
    private GAIndividual lowest;
    private GAIndividual highest;
    
    // Variables containing parameters
    private Variables variables;
    
    
    /**
     * Get ready to run the GA.
     *
     * @param quoteBundle the historical quote data
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
     * @param lowest lowest GA individual to know the lowest bound for generating new individuals
     * @param highest highest GA individual to know the highest bound for generating new individuals
     * @param variables variables containing the parameters of GA
     */
    public GeneticAlgorithm(EODQuoteBundle quoteBundle,
                            OrderCache orderCache,
                            Expression buyRule,
                            Expression sellRule,
                            TradingDate startDate,
                            TradingDate endDate,
                            Money initialCapital,
                            Money stockValue,
                            int numberStocks,
                            Money tradeCost,
                            int breedingPopulationSize,
                            String tradeValueBuy,
                            String tradeValueSell,
                            GAIndividual lowest,
                            GAIndividual highest,
                            Variables variables) {
        
        this.quoteBundle = quoteBundle;
        this.orderCache = orderCache;
        this.buyRule = buyRule;
        this.sellRule = sellRule;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
        this.stockValue = stockValue;
        this.numberStocks = numberStocks;
        this.tradeCost = tradeCost;
        this.breedingPopulationSize = breedingPopulationSize;
        this.tradeValueBuy = tradeValueBuy;
        this.tradeValueSell = tradeValueSell;
        this.lowest = lowest;
        this.highest = highest;
        this.variables = variables;
        
        nextBreedingPopulation = new TreeMap();
        breedingPopulation = new TreeMap();
        random = new Random(System.currentTimeMillis());
        
        generation = 1;
    }
    
    /**
     * Run one iteration of the GA. This will create a single valid individual.
     */
    public void nextIndividual() {
        boolean validIndividual = false;
        GAIndividual individual;
        
        // Loop until we create a valid individual that paper trade is OK
        while(!validIndividual) {
            if (generation==1) {
                individual = new GAIndividual(random, lowest, highest);
            } else {
                // Otherwise breed two parent individuals. We do these by calculating
                // a random value between 0 and the sum of all the individual values.
                // See getBreedingIndividual(double) for details.
                double motherValue = random.nextDouble() * breedingPopulationSum;
                double fatherValue = random.nextDouble() * breedingPopulationSum;

                GAIndividual mother = getBreedingIndividual(motherValue);
                GAIndividual father = getBreedingIndividual(fatherValue);

                individual = new GAIndividual(random, mother, father, lowest, highest);
            }
            // Set the variables with parameters of individual just created
            for (int ii=0; ii<individual.size(); ii++) {
                variables.setValue(individual.parameter(ii), individual.value(ii));
            }
            // Calculate the portfolio over the trading perdiod for the individual just created
            try {
                Money value = null;
                Portfolio portfolio = paperTrade(quoteBundle,
                                            orderCache,
                                            startDate,
                                            endDate,
                                            this.buyRule,
                                            this.sellRule,
                                            initialCapital,
                                            stockValue,
                                            numberStocks,
                                            tradeCost,
                                            variables,
                                            tradeValueBuy,
                                            tradeValueSell);               
                
                individual.setPortfolio(portfolio);
                
                // Get final value of portfolio
                try {
                    value = portfolio.getValue(quoteBundle, endDate);
                    individual.setValue(value);
                }
                catch(MissingQuoteException e) {
                    // Already checked...
                }

                
                // If we got here the paper trade was successful. Now let the
                // individual 'compete' to see if it gets to breed next round.
                // If the individual is fit enough, it'll get a chance to breed.
                competeForBreeding(individual, value);
                if (value!=null && individual!=null) validIndividual = true;                    
            }
            catch(EvaluationException e) {
                // If there is a problem running the equation then
                // it dies off naturally!
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
    public GAIndividual getBreedingIndividual(int index) {
        assert index < breedingPopulation.size();
        
        for(Iterator iterator = breedingPopulation.values().iterator(); iterator.hasNext();) {
            GAIndividual individual = (GAIndividual)iterator.next();
            
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
    private GAIndividual getBreedingIndividual(double value) {
        assert value <= breedingPopulationSum;
        
        for(Iterator iterator = breedingPopulation.values().iterator(); iterator.hasNext();) {
            GAIndividual individual = (GAIndividual)iterator.next();
            
            value -= individual.getValue().doubleValue();
            
            if(value <= 0.0D)
                return individual;
        }
        
        // It's possible but unlikely we get here. If so return the best performing
        // individual to give it a little more chance.
        return (GAIndividual)breedingPopulation.get(breedingPopulation.lastKey());
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
     * Get the buy rule.
     *
     * @return buy rule expression
     */
    public Expression getBuyRule() {
        return buyRule;
    }
    
    /**
     * Get the sell rule.
     *
     * @return sell rule expression
     */
    public Expression getSellRule() {
        return sellRule;
    }
    
    /**
     * Given an individual and its net worth, make it fight for a place in the
     * next generation's breeding population.
     *
     * @param individual new individual
     * @param value the individual's value
     */
    private void competeForBreeding(GAIndividual individual, Money value) {
        // If the individual made a loss or broke even, then we are just going to get
        // rubbish if we breed from it, so even if it is the best we've seen so far, it
        // gets ignored.
        if(individual!=null && value!=null && value.isGreaterThan(initialCapital)) {
            if(nextBreedingPopulation.size() < breedingPopulationSize) {
                nextBreedingPopulation.put(value, individual);
            } else {
                Money weakestValue = (Money)nextBreedingPopulation.firstKey();

                if(value.isGreaterThan(weakestValue)) {
                    nextBreedingPopulation.remove(weakestValue);
                    nextBreedingPopulation.put(value, individual);
                }
            }
        }
    }
    
    /**
     * Paper trade with the individual's buy and sell rules. Set the <code>stockValue</code>
     * to <code>null</code> to trade by number of stocks in the portfolio.
     *
     * @param quoteBundle the historical quote data
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param buyRule expression got from buy rule defined by user
     * @param sellRule expression got from sell rule defined by user
     * @param initialCapital initial capital in the portfolio
     * @param stockValue the rough value of each stock holding
     * @param numberStocks number of stocks in the portfolio
     * @param tradeCost the cost of a trade
     * @param variables variables used by GA
     * @param tradeValueBuy the buy value of a stock
     * @param tradeValueSell the sell value of a stock
     * @return portfolio of individual after paper trading
     */
    public Portfolio paperTrade(EODQuoteBundle quoteBundle,
                                OrderCache orderCache,
                                TradingDate startDate,
                                TradingDate endDate,
                                Expression buyRule,
                                Expression sellRule,
                                Money initialCapital,
                                Money stockValue,
                                int numberStocks,
                                Money tradeCost,
                                Variables variables,
                                String tradeValueBuy,
                                String tradeValueSell)
        throws EvaluationException {

        Portfolio portfolio = null;
        
        // Is there a fixed number of stocks?
        if(stockValue == null) {
            portfolio = PaperTrade.paperTrade(PORTFOLIO_NAME,
                                              quoteBundle,
                                              variables,
                                              orderCache,
                                              startDate,
                                              endDate,
                                              buyRule,
                                              sellRule,
                                              initialCapital,
                                              numberStocks,
                                              tradeCost,
                                              tradeValueBuy,
                                              tradeValueSell);
        // Or a fixed value?
        } else {
            portfolio = PaperTrade.paperTrade(PORTFOLIO_NAME,
                                              quoteBundle,
                                              variables,
                                              orderCache,
                                              startDate,
                                              endDate,
                                              buyRule,
                                              sellRule,
                                              initialCapital,
                                              stockValue,
                                              tradeCost,
                                              tradeValueBuy,
                                              tradeValueSell);
        }

        return portfolio;
    }

}
