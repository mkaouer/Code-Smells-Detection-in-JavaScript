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

import org.mov.analyser.GPGondolaSelection;
import org.mov.parser.Expression;
import org.mov.parser.expression.*;
import org.mov.quote.Quote;

/**
 * The mutator can build random expressions and randomly mutate existing
 * expressions. This class is at the heart of the GP as it creates the
 * random buy and sell rules and combines the rules during "breeding".
 *
 * @author Andrew Leppard
 * @see Individual
 * @see GeneticProgramme
 */
public class Mutator {

    // The branch factor is a number which defines the likelihood of
    // us choosing a non-terminal expression over a terminal expression.
    // The likelihood will be diminished as the expression tree grows
    // in depth.
    private final static int BRANCH_FACTOR = 80;

    // When mutating a numeric value (e.g. 10), this is the percent
    // chance that we favour applying a percent change to the number
    // (e.g. +20%) rather than replacing it with an entirely random number
    private final static int FAVOUR_NUMBER_PERCENT = 85;

    // This is the chance that a mutation occurs
    private final static int MUTATION_PERCENT       = 10;

    // This is the chance that an additional mutation occurs. And an
    // additional mutation after that, and after that etc.
    private final static int EXTRA_MUTATION_PERCENT = 10;

    // Given a mutation, this is the chance of it being an insertion mutation
    // (i.e. we insert an expression tree at the mutation point).
    private final static int INSERTION_MUTATION_PERCENT    = 10;

    // Given a mutation, this is the chance of it being a deletion mutation
    // (i.e. we delete the expression tree at the mutation point).
    private final static int DELETION_MUTATION_PERCENT     = 20;

    // Given a mutation, this is the chance of it being a modification mutation
    // (i.e. we modify the expression tree at the mutation point).
    private final static int MODIFICATION_MUTATION_PERCENT = 70;

    /** SUBTYPE
     * The subtype concept is introduced in Mutator class to pilot the GP process,
     * so that less time is wasted to find the best fitting expressions.
     * The type concept is present in Expression class and it is widely used in Gondola,
     * the subtype concept is a weaker tool, that only suggests the GP what to find
     * for the best fit in an expression.
     * For example in lag(close,xxx), we would like xxx be a INTEGER_TYPE expression,
     * by the point of view of the type, but an integer is not enough,
     * we also need that this integer be small and negative,
     * because xxx is the number of days of delay.
     * So we pilot the GP to find a small negative integer using
     * the subtype NEGATIVE_SHORT_INTEGER_SUBTYPE.
     */
    private final static int NO_SUBTYPE = 0;
    private final static int POSITIVE_SHORT_INTEGER_SUBTYPE = 1;
    private final static int NEGATIVE_SHORT_INTEGER_SUBTYPE = 2;
    private final static int SMOOTHING_CONSTANT_SUBTYPE = 3;

    // Random number generator
    private Random random;

    // UI Panel containing user's selection of percent chance of generating
    // each expression type
    private GPGondolaSelection GPGondolaSelection;

    // Is this mutator allowed to generate the "held" variable? Typically
    // buy mutators cannot, and sell mutators can.
    private boolean allowHeld;

    // Is this mutator allowed to generate the "order" variable? Typically
    // only if the user has ordered the stocks will this variable bew
    // available.
    private boolean allowOrder;

    /**
     * Create a new mutator.
     *
     * @param random use this random number generator
     * @param GPGondolaSelection UI containing user's desired expression probabilities
     * @param allowHeld allow the creation of the <code>held</code> variable
     * @param allowOrder allow the creation of the <code>order</code> variable
     */
    public Mutator(Random random, GPGondolaSelection GPGondolaSelection,
                   boolean allowHeld, boolean allowOrder) {
        this.random = random;
        this.GPGondolaSelection = GPGondolaSelection;
        this.allowHeld = allowHeld;
        this.allowOrder = allowOrder;
    }

    /**
     * Create a new random expression of the given type.
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @return a randomly generated expression
     */
    public Expression createRandom(int type) {
        return createRandom(null, type, this.NO_SUBTYPE, 1);
    }

    /**
     * Create a new random expression of the given type and subType.
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @param subType the subType of the expression, e.g. {@link Mutator#NO_SUBTYPE}
     * @return a randomly generated expression
     */
    public Expression createRandom(int type, int subType) {
        return createRandom(null, type, subType, 1);
    }

    /**
     * Create a new random expression of the given type,subType at the given level. The
     * level parameter is used to vary the probability of the expression
     * being a non-terminal or a terminal expression. As the level of the expression
     * tree gets larger, the probability of creating a non-terminal child
     * expression decreases.
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @param subType the subType of the expression, e.g. {@link Mutator#NO_SUBTYPE}
     * @param level the level in the tree
     * @return a randomly generated expression
     */
    public Expression createRandom(int type, int subType, int level) {
        return createRandom(null, type, subType, level);
    }

    /**
     * Create a new random expression based on mutating the given expression.
     * If <code>level < 1</code> then the top node of the created expression
     * will not be terminal.
     *
     * @param model initial expression to work with
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @param subType the subType of the expression, e.g. {@link Mutator#NO_SUBTYPE}
     * @param level the level in the tree
     * @return a randomly generated expression
     * @see #createRandom(int type, int subType, int level)
     */
    public Expression createRandom(Expression model, int type, int subType, int level) {
        boolean terminal = true;

        if(level < 1) {
            terminal = false;
        } else {
            // Work out percent chance of non-terminate symbol
            double branchPercent = (double)BRANCH_FACTOR / (double)level;
            double percent = random.nextDouble() * 100;

            if(branchPercent > percent) {
                terminal = false;
            }
        }

        // If the type is a boolean then there isn't much point generating
        // the boolean terminal expressions TRUE or FALSE because our
        // simplification code will just simplify it out of existence,
        // e.g. "and or a" would just become "a".
        if(type == Expression.BOOLEAN_TYPE || !terminal) {
            return createRandomNonTerminal(model, type, subType, level + 1);
        } else {
            return createRandomTerminal(type, subType);
        }
    }

    /**
     * Create a  new random non-terminal expression of the given type.
     * A terminal expression is one that has children, e.g. an operator
     * such as plus. (Thus plus operator would have two children, e.g.
     * 1 and 1).
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @return a randomly generated non-terminal expression
     */
    public Expression createRandomNonTerminal(int type) {
        return createRandomNonTerminal(null, type, this.NO_SUBTYPE, 1);
    }

    /**
     * Create a  new random non-terminal expression of the given type and subType.
     * A terminal expression is one that has children, e.g. an operator
     * such as plus. (Thus plus operator would have two children, e.g.
     * 1 and 1).
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @param subType the subType of the expression, e.g. {@link Mutator#NO_SUBTYPE}
     * @return a randomly generated non-terminal expression
     */
    public Expression createRandomNonTerminal(int type, int subType) {
        return createRandomNonTerminal(null, type, subType, 1);
    }

    /**
     * Create a new random non-terminal expression of the given type and subType
     * at the given level.
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @param subType the subType of the expression, e.g. {@link Mutator#NO_SUBTYPE}
     * @param level the level in the tree
     * @return a randomly generated non-terminal expression
     */
    public Expression createRandomNonTerminal(int type, int subType, int level) {
        return createRandomNonTerminal(null, type, subType, level);
    }

    /**
     * Create a new random non-terminal expression based on mutating the given expression.
     *
     * @param model initial expression to work with
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @param subType the subType of the expression, e.g. {@link Mutator#NO_SUBTYPE}
     * @param level the level in the tree
     * @return a randomly generated non-terminal expression
     * @see #createRandom(int type, int subType, int level)
     */
    public Expression createRandomNonTerminal(Expression model, int type, int subType, int level) {
        if(type == Expression.BOOLEAN_TYPE) {
            return createRandomNonTerminalBoolean(model, level);
        } else if(type == Expression.FLOAT_TYPE) {
            return createRandomNonTerminalFloat(model, level);
        } else if((type == Expression.INTEGER_TYPE) && (subType == this.POSITIVE_SHORT_INTEGER_SUBTYPE)) {
            return createRandomNonTerminalPositiveShortInteger(model, level);
        } else if((type == Expression.INTEGER_TYPE) && (subType == this.NEGATIVE_SHORT_INTEGER_SUBTYPE)) {
            return createRandomNonTerminalNegativeShortInteger(model, level);
        // At the end of all integer type, we put the integer type with no subType.
        } else if(type == Expression.INTEGER_TYPE) {
            return createRandomNonTerminalInteger(model, level);
        } else {
            // Quote types are all terminal!
            assert(type == Expression.FLOAT_QUOTE_TYPE ||
                   type == Expression.INTEGER_QUOTE_TYPE);
            return createRandomTerminal(type);
        }
    }

    /**
     * Creates a random terminal expression of the given type. A terminal
     * expression is one that does not have any children, e.g. a number
     * or a variable expression.
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @return a randomly generated terminal expression
     */
    public Expression createRandomTerminal(int type) {
        return createRandomTerminal(type, this.NO_SUBTYPE);
    }
    
    /**
     * Creates a random terminal expression of the given type and subType. A terminal
     * expression is one that does not have any children, e.g. a number
     * or a variable expression.
     *
     * @param type the type of the expression, e.g. {@link Expression#BOOLEAN_TYPE}
     * @param subType the subType of the expression, e.g. {@link Mutator#NO_SUBTYPE}
     * @return a randomly generated terminal expression
     */
    public Expression createRandomTerminal(int type, int subType) {
        
        int randomNumber = 0;

        switch(type) {
            
            case Expression.BOOLEAN_TYPE:
                randomNumber = random.nextInt(2);

                if(randomNumber == 0) {
                    return new NumberExpression(true);
                } else {
                    assert randomNumber == 1;
                    return new NumberExpression(false);
                }

            case Expression.FLOAT_TYPE:
                if(subType == this.SMOOTHING_CONSTANT_SUBTYPE) {
                    // Generate an ordinary number that fit for the smoothing constant.
                    return new NumberExpression(0.01D + random.nextDouble() * (1.0D - 0.01D));
                }

                randomNumber = GPGondolaSelection.getRandomToGenerateTerminalFloat(allowHeld);

                if(randomNumber == 0) {
                    // Generate an ordinary number
                    return new NumberExpression(50 - random.nextDouble() * 100);
                } else if(randomNumber == 1) {
                    return new GetVariableExpression("capital", Expression.FLOAT_TYPE);
                } else if(randomNumber == 2) {
                    assert allowHeld;
                    return new GetVariableExpression("stockcapital", Expression.FLOAT_TYPE);
                } 

            case Expression.INTEGER_TYPE:

                // Generate an ordinary number small and negative.
                if(subType == this.NEGATIVE_SHORT_INTEGER_SUBTYPE) {
                    return new NumberExpression(0 - random.nextInt(50));
                }

                randomNumber = GPGondolaSelection.getRandomToGenerateTerminalInteger(allowHeld, allowOrder);

                if(randomNumber == 0) {

                    // Generate an ordinary number small and positive.
                    if(subType == this.POSITIVE_SHORT_INTEGER_SUBTYPE) {
                        return new NumberExpression(50 - random.nextInt(50));
                    }

                    // Generate an ordinary number
                    return new NumberExpression(50 - random.nextInt(100));

                } else if(randomNumber == 1) {
                    return new DayOfYearExpression();
                } else if(randomNumber == 2) {
                    return new MonthExpression();
                } else if(randomNumber == 3) {
                    return new DayExpression();
                } else if(randomNumber == 4) {
                    return new DayOfWeekExpression();
                } else if(randomNumber == 5) {
                    return new GetVariableExpression("daysfromstart", Expression.INTEGER_TYPE);
                } else if(randomNumber == 6) {
                    return new GetVariableExpression("transactions", Expression.INTEGER_TYPE);
                } else {
                    if(allowOrder && allowHeld) {
                        if(randomNumber == 7) {
                            return new GetVariableExpression("held", Expression.INTEGER_TYPE);
                        } else {
                            return new GetVariableExpression("order", Expression.INTEGER_TYPE);
                        }
                    } else if(allowHeld) {
                        return new GetVariableExpression("held", Expression.INTEGER_TYPE);
                    } else {
                        assert allowOrder;
                        return new GetVariableExpression("order", Expression.INTEGER_TYPE);
                    }
                }

            case Expression.FLOAT_QUOTE_TYPE:
                
                randomNumber = GPGondolaSelection.getRandomToGenerateFloatQuote();

                if(randomNumber == 0) {
                    return new QuoteExpression(Quote.DAY_OPEN);
                } else if(randomNumber == 1) {
                    return new QuoteExpression(Quote.DAY_HIGH);
                } else if(randomNumber == 2) {
                    return new QuoteExpression(Quote.DAY_LOW);
                } else {
                    assert randomNumber == 3;
                    return new QuoteExpression(Quote.DAY_CLOSE);
                }

            case Expression.INTEGER_QUOTE_TYPE:
                return new QuoteExpression(Quote.DAY_VOLUME);

            default:
                assert false;
                return null;
        }
    }

    /**
     * Create a random non-terminal {@link Expression#BOOLEAN_TYPE} expression.
     *
     * @param model model expression
     * @param level tree level
     * @return randomly generated non-terminal boolean expression
     */
    private Expression createRandomNonTerminalBoolean(Expression model, int level) {
        int randomNumber = GPGondolaSelection.getRandomToGenerateBoolean();

        if(randomNumber == 0) {
            return new NotExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE));
            
        } else if(randomNumber == 1) {
            Expression first = getChild(model, level, 0);
            return new EqualThanExpression(first,
                                           getChild(model, level, 1, first.getType()));
            
        } else if(randomNumber == 2) {
            Expression first = getChild(model, level, 0);
            return new GreaterThanEqualExpression(first,
                                                  getChild(model, level, 1, first.getType()));
            
        } else if(randomNumber == 3) {
            Expression first = getChild(model, level, 0);
            return new GreaterThanExpression(first,
                                             getChild(model, level, 1, first.getType()));
            
        } else if(randomNumber == 4) {
            Expression first = getChild(model, level, 0);
            return new LessThanEqualExpression(first,
                                               getChild(model, level, 1, first.getType()));
            
        } else if(randomNumber == 5) {
            Expression first = getChild(model, level, 0);
            return new LessThanExpression(first,
                                          getChild(model, level, 1, first.getType()));
            
        } else if(randomNumber == 6) {
            Expression first = getChild(model, level, 0);
            return new NotEqualExpression(first,
                                          getChild(model, level, 1, first.getType()));
            
        } else if(randomNumber == 7) {
            return new AndExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                     getChild(model, level, 1, Expression.BOOLEAN_TYPE));
            
        } else {
            assert randomNumber == 8;
            return new OrExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.BOOLEAN_TYPE));
        }
    }

    /**
     * Create a random non-terminal {@link Expression#FLOAT_TYPE} expression.
     *
     * @param model model expression
     * @param level tree level
     * @return randomly generated non-terminal float expression
     */
    private Expression createRandomNonTerminalFloat(Expression model, int level) {

        // If we are mutating an existing number expression then favour
        // just modifying the number's value rather than replacing it
        // with a random expressions. This helps keep the equation size down and
        // favours trying different values.
        if(model != null &&
           model instanceof NumberExpression &&
           FAVOUR_NUMBER_PERCENT > random.nextInt(100)) {

            NumberExpression numberExpression = (NumberExpression)model;
            double step = random.nextDouble() * 6.0D;
            double value = Math.pow(10.0D, step);

            if(random.nextBoolean())
                value = -value;

            numberExpression.setValue(numberExpression.getValue() + value);
            return numberExpression;
        }

        int randomNumber = GPGondolaSelection.getRandomToGenerateFloat();
        
        if(randomNumber == 0) {
            return createRandomTerminal(Expression.FLOAT_TYPE);
            
        } else if(randomNumber == 1) {
            return new AddExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                     getChild(model, level, 1));
            
        } else if(randomNumber == 2) {
            return new SubtractExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                          getChild(model, level, 1));
            
        } else if(randomNumber == 3) {
            return new MultiplyExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                          getChild(model, level, 1));
            
        } else if(randomNumber == 4) {
            return new DivideExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                        getChild(model, level, 1));
            
        } else if(randomNumber == 5) {
            return new PercentExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                         getChild(model, level, 1));
            
        } else if(randomNumber == 6) {
            return new IfExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.FLOAT_TYPE),
                                    getChild(model, level, 2, Expression.FLOAT_TYPE));
            
        } else if(randomNumber == 7) {
            return new LagExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 8) {
            return new MinExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 9) {
            return new MaxExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 10) {
            return new SumExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 11) {
            return new SqrtExpression(getChild(model, level, 0, Expression.FLOAT_TYPE));

        } else if(randomNumber == 12) {
            return new AbsExpression(getChild(model, level, 0, Expression.FLOAT_TYPE));
            
        } else if(randomNumber == 13) {
            return new CosineExpression(getChild(model, level, 0));
            
        } else if(randomNumber == 14) {
            return new SineExpression(getChild(model, level, 0));
            
        } else if(randomNumber == 15) {
            return new LogarithmExpression(getChild(model, level, 0));
            
        } else if(randomNumber == 16) {
            return new ExponentialExpression(getChild(model, level, 0));
            
        } else if(randomNumber == 17) {
            return new AvgExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 18) {
            return new EMAExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                     createRandomTerminal(Expression.FLOAT_TYPE, this.SMOOTHING_CONSTANT_SUBTYPE));

        } else if(randomNumber == 19) {
            return new MACDExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 20) {
            return new MomentumExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 21) {
            return new RSIExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 22) {
            return new StandardDeviationExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 23) {
            return new BBLExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else {
            assert randomNumber == 24;
            return new BBUExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
        }
    }

    /**
     * Create a random non-terminal {@link Expression#INTEGER_TYPE} expression.
     *
     * @param model model expression
     * @param level tree level
     * @return randomly generated non-terminal integer expression
     */
    private Expression createRandomNonTerminalInteger(Expression model, int level) {

        // If we are mutating an existing number expression then favour
        // just modifying the number's value rather than replacing it
        // with a random expressions. This helps keep the equation size down and
        // favours trying different values.
        if(model != null &&
           model instanceof NumberExpression &&
           FAVOUR_NUMBER_PERCENT > random.nextInt(100)) {

            NumberExpression numberExpression = (NumberExpression)model;
            double step = random.nextDouble() * 6.0D;
            double value = Math.pow(10.0D, step);

            if(random.nextBoolean())
                value = -value;

            numberExpression.setValue(numberExpression.getValue() + value);
            return numberExpression;
        }

        int randomNumber = GPGondolaSelection.getRandomToGenerateInteger();

        if(randomNumber == 0) {
            return createRandomTerminal(Expression.INTEGER_TYPE);
            
        } else if(randomNumber == 1) {
            return new AddExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                     getChild(model, level, 1));
            
        } else if(randomNumber == 2) {
            return new SubtractExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                          getChild(model, level, 1));
            
        } else if(randomNumber == 3) {
            return new MultiplyExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                          getChild(model, level, 1));
            
        } else if(randomNumber == 4) {
            return new DivideExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                        getChild(model, level, 1));
            
        } else if(randomNumber == 5) {
            return new PercentExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                         getChild(model, level, 1));
            
        } else if(randomNumber == 6) {
            return new IfExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.INTEGER_TYPE),
                                    getChild(model, level, 2, Expression.INTEGER_TYPE));
            
        } else if(randomNumber == 7) {
            return new LagExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                   getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 8) {
            return new MinExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 9) {
            return new MaxExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 10) {
            return new SumExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 11) {
            return new SqrtExpression(getChild(model, level, 0, Expression.INTEGER_TYPE));

        } else if(randomNumber == 12) {
            return new AbsExpression(getChild(model, level, 0, Expression.INTEGER_TYPE));
            
        } else if(randomNumber == 13) {
            return new AvgExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 14) {
            return new EMAExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                     createRandomTerminal(Expression.FLOAT_TYPE, this.SMOOTHING_CONSTANT_SUBTYPE));

        } else if(randomNumber == 15) {
            return new MACDExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 16) {
            return new MomentumExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 17) {
            return new StandardDeviationExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 18) {
            return new BBLExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 19) {
            return new BBUExpression(new QuoteExpression(Quote.DAY_VOLUME),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));

        } else {
            assert randomNumber == 20;
            return new OBVExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        }
    }


    /**
     * Create a random non-terminal {@link Expression#INTEGER_TYPE} expression.
     * The number should be a positive short integer
     * {@link Mutator#POSITIVE_SHORT_INTEGER_SUBTYPE}.
     *
     * @param model model expression
     * @param level tree level
     * @return randomly generated non-terminal integer expression
     */
    private Expression createRandomNonTerminalPositiveShortInteger(Expression model, int level) {

        // If we are mutating an existing number expression then favour
        // just modifying the number's value rather than replacing it
        // with a random expressions. This helps keep the equation size down and
        // favours trying different values.
        if(model != null &&
           model instanceof NumberExpression &&
           FAVOUR_NUMBER_PERCENT > random.nextInt(100)) {

            NumberExpression numberExpression = (NumberExpression)model;
            double step = random.nextDouble() * 4.0D;
            double value = Math.pow(2.0D, step);

            numberExpression.setValue(numberExpression.getValue() + value);
            return numberExpression;
        }

        int randomNumber = GPGondolaSelection.getRandomToGeneratePositiveShortInteger();

        if(randomNumber == 0) {
            return createRandomTerminal(Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE);
            
        } else if(randomNumber == 1) {
            return new AddExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 2) {
            return new SubtractExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                          getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 3) {
            return new MultiplyExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                          getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 4) {
            return new DivideExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                        getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 5) {
            return new PercentExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                         getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 6) {
            return new IfExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE),
                                    getChild(model, level, 2, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 7) {
            return new SqrtExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));

        } else if(randomNumber == 8) {
            return new AbsExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 9) {
            assert randomNumber == 10;
            /* We put also the following model: (+1)*(generic float expression).
             * The generic float can be got from any econometric function.
             * This permits to use all the functions in a positive small integer number,
             * the conversion to integer is obtained with a multiply expression,
             * that is the simplest method to got the conversion in Gondola.
            */ 
            return new MultiplyExpression(new NumberExpression(1),
                                          getChild(model, level, 1, Expression.FLOAT_TYPE));
                        
        } else {
            assert randomNumber == 10;
            /* We put also the following model: (generic integer expression).
             * The generic integer can be got from any econometric function.
             * This permits to use all the functions in a positive small integer number.
            */ 
            return createRandomNonTerminal(model, Expression.INTEGER_TYPE, this.NO_SUBTYPE, level);
            
        }
    }


    /**
     * Create a random non-terminal {@link Expression#INTEGER_TYPE} expression.
     * The number should be a negative small integer
     * {@link Mutator#NEGATIVE_SHORT_INTEGER_SUBTYPE}.
     *
     * @param model model expression
     * @param level tree level
     * @return randomly generated non-terminal integer expression
     */
    private Expression createRandomNonTerminalNegativeShortInteger(Expression model, int level) {

        // If we are mutating an existing number expression then favour
        // just modifying the number's value rather than replacing it
        // with a random expressions. This helps keep the equation size down and
        // favours trying different values.
        if(model != null &&
           model instanceof NumberExpression &&
           FAVOUR_NUMBER_PERCENT > random.nextInt(100)) {

            NumberExpression numberExpression = (NumberExpression)model;
            double step = random.nextDouble() * 4.0D;
            double value = Math.pow(2.0D, step);

            numberExpression.setValue(numberExpression.getValue() - value);
            return numberExpression;
        }

        int randomNumber = GPGondolaSelection.getRandomToGenerateNegativeShortInteger();

        if(randomNumber == 0) {
            return createRandomTerminal(Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE);
            
        } else if(randomNumber == 1) {
            return new AddExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 2) {
            return new SubtractExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                          getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 3) {
            return new MultiplyExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                          getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 4) {
            return new DivideExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                        getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 5) {
            return new PercentExpression(getChild(model, level, 0, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                         getChild(model, level, 1, Expression.INTEGER_TYPE, this.POSITIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 6) {
            return new IfExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE),
                                    getChild(model, level, 2, Expression.INTEGER_TYPE, this.NEGATIVE_SHORT_INTEGER_SUBTYPE));
            
        } else if(randomNumber == 7) {
            /* We put also the following model: (-1)*(generic float expression).
             * The generic float can be got from any econometric function.
             * This permits to use all the functions in a negative small integer number,
             * the conversion to integer is obtained with a multiply expression,
             * that is the simplest method to got the conversion in Gondola.
            */ 
            return new MultiplyExpression(new NumberExpression(-1),
                                          getChild(model, level, 1, Expression.FLOAT_TYPE));
            
        } else {
            assert randomNumber == 8;
            /* We put also the following model: (-1)*(generic integer expression).
             * The generic integer can be got from any econometric function.
             * This permits to use all the functions in a negative small integer number.
            */ 
            return new MultiplyExpression(new NumberExpression(-1),
                                          getChild(model, level, 1, Expression.INTEGER_TYPE));
            
        }
    }


    // creates a float or integer type
    private Expression getChild(Expression model, int level, int arg) {

        // Case 1: The expression doesn't have this many children or
        // it has a child here but it is a different type. So create
        // a new argument.
        if(model == null || arg >= model.getChildCount() ||
           (model.getChild(arg).getType() != Expression.FLOAT_TYPE &&
            model.getChild(arg).getType() != Expression.INTEGER_TYPE)) {

            int randomNumber = GPGondolaSelection.getRandomToGenerateFloatInteger();

            if(randomNumber == 0) {
                return createRandom(null, Expression.FLOAT_TYPE, this.NO_SUBTYPE, level);
            } else {
                assert randomNumber == 1;
                return createRandom(null, Expression.INTEGER_TYPE, this.NO_SUBTYPE, level);
            }
        }

        // Case 2: It has an argument of the right type
        else {
            return model.getChild(arg);
        }
    }

    // create a child with no subType defined
    private Expression getChild(Expression model, int level, int arg, int type) {
        return getChild(model, level, arg, type, this.NO_SUBTYPE);
    }

    private Expression getChild(Expression model, int level, int arg, int type, int subType) {

        // Case 1: The expression doesn't have this many children or
        // it has a child here but it is a different type. So create
        // a new argument.
        if(model == null || arg >= model.getChildCount() ||
           model.getChild(arg).getType() != type) {
            return createRandom(null, type, subType, level);
        }

        // Case 2: It has an argument of the right type
        else {
            return model.getChild(arg);
        }
    }

    /**
     * Randomly pick a node in the given expression.
     *
     * @param expression the expression to search
     * @return expression node
     */
    public Expression findRandomSite(Expression expression) {
        int randomNumber = random.nextInt(expression.size());
        Expression randomSite = null;

        for(Iterator iterator = expression.iterator();
            iterator.hasNext();) {

            randomSite = (Expression)iterator.next();

            // Return if this is the xth random element
            if(randomNumber-- <= 0)
                break;
        }

        assert randomSite != null;

        return randomSite;
    }

    /**
     * Randomly pick a node of the given type in the given expression.
     *
     * @param expression the expression node
     * @param type the type of the node, e.g. {@link Expression#BOOLEAN_TYPE}
     * @return expression node or <code>null</code> if one could not be found
     */
    public Expression findRandomSite(Expression expression, int type) {
        Expression randomSite = null;
        int possibleSites = expression.size(type);

        if(possibleSites > 0) {
            int randomNumber = random.nextInt(possibleSites);

            for(Iterator iterator = expression.iterator();
                iterator.hasNext();) {

                randomSite = (Expression)iterator.next();

                // Return if this is the xth random element of the
                // given type
                if(randomSite.getType() == type)
                    if(randomNumber-- <= 0)
                        break;
            }
            assert randomSite != null;
        }

        return randomSite;
    }

    /**
     * Perform a deletion mutation on the given expression. Since the mutation
     * might chance the root of the expression, the updated root is
     * returned. The returned root may be the same as the one passed in.
     *
     * @param root the root of the expression being mutated
     * @param destination the destination site for the deletion
     * @return the new root of the expression
     */
    public Expression delete(Expression root, Expression destination) {
        return insert(root, destination,
                      createRandomTerminal(destination.getType()));
    }

    /**
     * Perform an insertion mutation on the given expression.
     *
     * @param root the root of the expression being mutated
     * @param destination the destination site for the insertion
     * @param source the expression to insert
     * @return the new root of the expression
     * @see #delete(Expression root, Expression destination)
     */
    public Expression insert(Expression root, Expression destination,
                             Expression source) {

        Expression parent = (Expression)destination.getParent();

        if(parent == null) {
            // If the destination has no parent it must be the root of the tree.
            assert root == destination;
            return source;
        }
        else {
            int childNumber = parent.getIndex(destination);
            parent.setChild(source, childNumber);
            return root;
        }
    }

    /**
     * Perform a modification mutation on the given expression.
     *
     * @param root the root of the expression being mutated
     * @param destination the destination site for the modification
     * @return the new root of the expression
     * @see #delete(Expression root, Expression destination)
     */
    public Expression modify(Expression root, Expression destination) {
        if(destination == root) {
            Expression newExpression = createRandom(destination.getType(), this.NO_SUBTYPE, 1);
            newExpression = newExpression.simplify();
            return newExpression;
        } else {
            Expression newExpression = createRandom(destination, destination.getType(), this.NO_SUBTYPE, 1);
            newExpression = newExpression.simplify();
            return insert(root, destination, newExpression);
        }
    }

    /**
     * Possibly mutate the given expression.
     *
     * @param expression the root of the expression to modify
     * @return the new root of the expression
     */
    public Expression mutate(Expression expression) {
        return mutate(expression, MUTATION_PERCENT);
    }

    /**
     * Possibly mutate the given expression
     *
     * @param expression the root of the expression to modify
     * @param percent percent change of mutation
     * @return the new root of the expression
     */
    public Expression mutate(Expression expression, int percent) {
        // Mutations do not always occur. Use the given percent to work
        // out whether one should occur.
        int randomPercent = random.nextInt(100);
        if(percent < randomPercent) {
            return expression;
        }

        // Mutate
        int randomTypePercent = random.nextInt(100);
        if(INSERTION_MUTATION_PERCENT > randomTypePercent) {
            expression = mutateByInsertion(expression);
        } else {
            randomTypePercent -= INSERTION_MUTATION_PERCENT;

            if(DELETION_MUTATION_PERCENT > randomTypePercent) {
                expression = mutateByDeletion(expression);
            } else {
                randomTypePercent -= DELETION_MUTATION_PERCENT;

                expression = mutateByModification(expression);
            }
        }

        // There's always the possibility of a 2nd, 3rd, etc mutation. This
        // can be useful if the gene pool is stagnant.
        return mutate(expression, EXTRA_MUTATION_PERCENT);
    }

    /**
     * Mutate the given expression by modification.
     *
     * @param expression the root of the expression to modify
     * @return the new root of the expression
     */
    private Expression mutateByModification(Expression expression) {
        Expression destination = findRandomSite(expression);

        return modify(expression, destination);
    }

    /**
     * Mutate the given expression by insertion.
     *
     * @param expression the root of the expression to modify
     * @return the new root of the expression
     */
    private Expression mutateByInsertion(Expression expression) {
        Expression destination = findRandomSite(expression);
        Expression insertSubTree = createRandom(destination.getType());

        return insert(expression, destination, insertSubTree);
    }

    /**
     * Mutate the given expression by deletion.
     *
     * @param expression the root of the expression to modify
     * @return the new root of the expression
     */
    private Expression mutateByDeletion(Expression expression) {
        Expression destination = findRandomSite(expression);

        // There's no point in replacing the root node with a terminal
        // expression, and replacing a terminal expression with
        // a random expression is closer to an insertion mutation than
        // deletion. So just skip the whole deletion idea and try a random
        // mutation somewhere.
        if(destination.isRoot() || destination.getChildCount() == 0) {
            return mutateByModification(expression);
        } else {
            return delete(expression, destination);
        }
    }
}
