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

import java.util.Random;

import org.mov.analyser.OrderCache;
import org.mov.analyser.PaperTrade;
import org.mov.parser.EvaluationException;
import org.mov.parser.Expression;
import org.mov.parser.TypeMismatchException;
import org.mov.parser.Variables;
import org.mov.portfolio.Portfolio;
import org.mov.quote.MissingQuoteException;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;

/**
 * A trading individual evovled by the GP. The Individual contains a buy rule and
 * a sell rule that will have been evolved by the GP.
 *
 * @author Andrew Leppard
 * @see GeneticProgramme
 * @see Mutator
 */
public class Individual implements Comparable {

    // The evolved buy indicator
    private Expression buyRule = null;

    // The evolved sell indicator
    private Expression sellRule = null;

    // The individuals portfolio after trades
    private Portfolio portfolio = null;

    // The value of the portfolio
    private Money value = null;

    // The following define the probabilities that two inviduals
    // will be breed by the following methods.
    private final static int CLONE_PERCENT                     = 10;
    private final static int SWAP_PERCENT                      = 5;
    private final static int RECOMBINE_PERCENT                 = 35;
    private final static int SWAP_AND_RECOMBINE_PERCENT        = 40;
    private final static int DOUBLE_RECOMBINE_PERCENT          = 10;

    // The following enumerate the different methods that indivudals can
    // "breed" or be combined.
    private final static int BREED_BY_CLONING                         = 0;
    private final static int BREED_BY_SWAPPING                        = 1;
    private final static int BREED_BY_RECOMBINING                     = 2;
    private final static int BREED_BY_SWAPPING_AND_RECOMBINING        = 3;
    private final static int BREED_BY_DOUBLE_RECOMBINING              = 4;

    // The generic name of all the Individuals' portfolios
    private final static String PORTFOLIO_NAME =
        Locale.getString("GENETIC_PROGRAMME_PORTFOLIO");

    /**
     * Create a new individual with the given buy and sell rules.
     *
     * @param buyRule the buy indicator
     * @param sellRule the sell indicator
     */
    public Individual(Expression buyRule, Expression sellRule) {
        this.buyRule = buyRule;
        this.sellRule = sellRule;

        checkType();
    }

    /**
     * Randomly generate an individual using the two mutators.
     *
     * @param buyRuleMutator mutator used for creating buy rule
     * @param sellRuleMutator mutator used for creating sell rule
     */
    public Individual(Mutator buyRuleMutator, Mutator sellRuleMutator) {
        // By setting the level low we create bushier trees
        buyRule = buyRuleMutator.createRandomNonTerminal(Expression.BOOLEAN_TYPE, 0);
        sellRule = sellRuleMutator.createRandomNonTerminal(Expression.BOOLEAN_TYPE, 0);

        buyRule = buyRule.simplify();
        sellRule = sellRule.simplify();

        checkType();
    }

    /**
     * Create a new individual by "breeding" or combining the two parent individuals.
     *
     * @param random the random number generator
     * @param buyRuleMutator mutator used for mutating buy rule
     * @param sellRuleMutator mutator used for mutating sell rule
     * @param father one parent individual
     * @param mother another parent individual
     */
    public Individual(Random random, Mutator buyRuleMutator, Mutator sellRuleMutator,
                      Individual father, Individual mother) {
        int breedType = getRandomBreedType(random);

        buyRule = (Expression)father.getBuyRule().clone();

        // SWAP
        {
            if(breedType == BREED_BY_SWAPPING ||
               breedType == BREED_BY_SWAPPING_AND_RECOMBINING)
                sellRule = (Expression)mother.getSellRule().clone();
            else
                sellRule = (Expression)father.getSellRule().clone();
        }

        // RECOMBINE
        {
            // Single
            if(breedType == BREED_BY_RECOMBINING ||
               breedType == BREED_BY_DOUBLE_RECOMBINING ||
               breedType == BREED_BY_SWAPPING_AND_RECOMBINING) {
                buyRule = recombine(buyRuleMutator, buyRule, mother.getBuyRule());

                // Double
                if(breedType == BREED_BY_DOUBLE_RECOMBINING)
                    sellRule = recombine(sellRuleMutator, sellRule, mother.getSellRule());
            }
        }

        // MUTATE
        {
            if(breedType == BREED_BY_CLONING) {
                // If it is a clone, at least one of the rules must mutate otherwise
                // we've created a duplicate individual which is a waste of processing
                // power.
                int randomNumber = random.nextInt(3);

                if(randomNumber == 0 || randomNumber == 2)
                    buyRule = buyRuleMutator.mutate(buyRule, 100);
                if(randomNumber == 1 || randomNumber == 2)
                    sellRule = sellRuleMutator.mutate(sellRule, 100);
            }
            else {
                buyRule = buyRuleMutator.mutate(buyRule);
                sellRule = sellRuleMutator.mutate(sellRule);
            }
        }

        sellRule = sellRule.simplify();
        buyRule = buyRule.simplify();
        checkType();
    }

    /**
     * Check that the given buy and sell rules are valid. This function will check
     * that both the buy and sell rules are within the given size limits. These
     * limits refer to the number of nodes in each rule. Rules that do not access
     * quote data will also be rejected, e.g. rules that are only based on the day of
     * the week, order, etc.
     *
     * <p>The minimum size check is to prevent us wasting time on too simplistic
     * rules. The maximum size check is to prevent rules exponentially increasing
     * and so reduce CPU time. We reject rules not based on quote data as also
     * being too simplistic to be worthwhile.
     *
     * @param min the minimum number of nodes in each rule
     * @param max the maximum number of nodes in each rule
     * @return <code>true</code> if both rules are valid
     */
    public boolean isValid(int min, int max) {
        int sellRuleSize = sellRule.size();
        int buyRuleSize = buyRule.size();
        
        return (sellRuleSize >= min && sellRuleSize <= max &&
                buyRuleSize >= min && buyRuleSize <= max &&
                (buyRule.size(Expression.FLOAT_QUOTE_TYPE) > 0 ||
                 buyRule.size(Expression.INTEGER_QUOTE_TYPE) > 0));
    }

    /**
     * Return the combined number of nodes of both the buy and sell rules.
     *
     * @return number of nodes
     */
    public int getTotalEquationSize() {
        return buyRule.size() + sellRule.size();
    }

    /**
     * Paper trade with the individual's buy and sell rules. Set the <code>stockValue</code>
     * to <code>null</code> to trade by number of stocks in the portfolio.
     *
     * @param quoteBundle the historical quote data
     * @param orderCache cache of ordered symbols
     * @param startDate start date of trading
     * @param endDate last date of trading
     * @param initialCapital initial capital in the portfolio
     * @param stockValue the rough value of each stock holding
     * @param numberStocks number of stocks in the portfolio
     * @param tradeCost the cost of a trade
     * @return value of individual after paper trading
     */
    public Money paperTrade(GPQuoteBundle quoteBundle,
                            OrderCache orderCache,
                            TradingDate startDate,
                            TradingDate endDate,
                            Money initialCapital,
                            Money stockValue,
                            int numberStocks,
                            Money tradeCost,
                            String tradeValueBuy,
                            String tradeValueSell)
        throws EvaluationException {

        // Is there a fixed number of stocks?
        if(stockValue == null)
            portfolio = PaperTrade.paperTrade(PORTFOLIO_NAME,
                                              quoteBundle,
                                              new Variables(),
                                              orderCache,
                                              startDate,
                                              endDate,
                                              getBuyRule(),
                                              getSellRule(),
                                              initialCapital,
                                              numberStocks,
                                              tradeCost,
                                              tradeValueBuy,
                                              tradeValueSell);
        // Or a fixed value?
        else {
            portfolio = PaperTrade.paperTrade(PORTFOLIO_NAME,
                                              quoteBundle,
                                              new Variables(),
                                              orderCache,
                                              startDate,
                                              endDate,
                                              getBuyRule(),
                                              getSellRule(),
                                              initialCapital,
                                              stockValue,
                                              tradeCost,
                                              tradeValueBuy,
                                              tradeValueSell);
        }

        // Get final value of portfolio
        try {
            value = portfolio.getValue(quoteBundle, endDate);
        }
        catch(MissingQuoteException e) {
            // Already checked...
            assert false;
        }

        return value;
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
     * Get the buy rule.
     *
     * @return buy rule
     */
    public Expression getBuyRule() {
        return buyRule;
    }

    /**
     * Get the sell rule.
     *
     * @return sell rule
     */
    public Expression getSellRule() {
        return sellRule;
    }

    /**
     * Get the final portfolio.
     *
     * @return final portfolio
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Compare the individuals' final values.
     *
     * @param object the other individual
     * @return <code>-1</code> if this individual is worth less,
     *         <code>0</code> if the individuals are worth the same or
     *         <code>1</code> if this individual is worth more.
     */
    public int compareTo(Object object) {
        Individual other = (Individual)object;

        return getValue().compareTo(other.getValue());
    }

    /**
     * Test the invidiuals' final values for equality.
     *
     * @return <code>true</code> if the two individuals are worth the same
     */
    public boolean equals(Object object) {
        // TODO: equals shouldn't say the equations are equal unless they
        // ARE the same equations. Otherwise it should rate them slightly
        // differently?? I'm not sure.
        // TODO: Also the 'order' variable isn't defined if the order is
        // not set. So this variable shouldnt always be generated by
        // mutator.

        Individual other = (Individual)object;

        return getValue().equals(other.getValue());
    }

    /**
     * Calculate a hash code for the individual.
     *
     * @return the hash code
     */
    public int hashCode() {
        // If you implement equals you should implement hashCode().
        // Since I don't need it I haven't bothered to implement a very
        // good hash.
        return getBuyRule().hashCode() + getSellRule().hashCode();
    }

    // Throw the dice to get a random breeding type
    private int getRandomBreedType(Random random) {
        int percent = random.nextInt(100);

        if(CLONE_PERCENT > percent)
            return BREED_BY_CLONING;
        percent -= CLONE_PERCENT;

        if(SWAP_PERCENT > percent)
            return BREED_BY_SWAPPING;
        percent -= SWAP_PERCENT;

        if(RECOMBINE_PERCENT > percent)
            return BREED_BY_RECOMBINING;
        percent -= RECOMBINE_PERCENT;

        if(SWAP_AND_RECOMBINE_PERCENT > percent)
            return BREED_BY_SWAPPING_AND_RECOMBINING;
        percent -= SWAP_AND_RECOMBINE_PERCENT;

        return BREED_BY_DOUBLE_RECOMBINING;
    }

    // Recombine the two expressions using the given mutator to generate mutations
    private Expression recombine(Mutator mutator, Expression destination, Expression source) {
        Expression destinationSubTree = mutator.findRandomSite(destination);
        Expression sourceSubTree = mutator.findRandomSite(source,
                                                          destinationSubTree.getType());

        // It's possible that there is no match in the source for the given type.
        if(sourceSubTree != null) {
            assert sourceSubTree.getType() == destinationSubTree.getType();
            destination = mutator.insert(destination, destinationSubTree,
                                         (Expression)sourceSubTree.clone());
        }

        return destination;
    }

    // All created individuals should have the correct types used throughout.
    // Assert if it turns out we have made a mistake
    private void checkType() {
        try {
            // Check that both the buy and sell rules are both booleans and
            // that their subtrees have proper types.
            assert buyRule.checkType() == Expression.BOOLEAN_TYPE;
            assert sellRule.checkType() == Expression.BOOLEAN_TYPE;

        } catch(TypeMismatchException e) {
            assert false;
        }
    }
}
