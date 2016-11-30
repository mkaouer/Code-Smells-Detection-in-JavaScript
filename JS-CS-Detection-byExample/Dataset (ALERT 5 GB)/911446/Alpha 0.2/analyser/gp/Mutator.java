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

import java.util.Enumeration;
import java.util.Random;

import org.mov.parser.Expression;
import org.mov.parser.expression.*;
import org.mov.quote.Quote;

public class Mutator {

    private final static int BRANCH_FACTOR = 75;
    private final static int FAVOUR_NUMBER_PERCENT = 90;

    private final static int MUTATION_PERCENT       = 10;
    private final static int EXTRA_MUTATION_PERCENT = 10;

    private final static int INSERTION_MUTATION_PERCENT    = 10;
    private final static int DELETION_MUTATION_PERCENT     = 20;
    private final static int MODIFICATION_MUTATION_PERCENT = 70;

    private Random random;
    private boolean allowHeld;
    private boolean allowOrder;

    public Mutator(Random random, boolean allowHeld, boolean allowOrder) {
        this.random = random;
        this.allowHeld = allowHeld;
        this.allowOrder = allowOrder;
    }

    public Expression createRandom(int type) {
        return createRandom(null, type, 1);
    }

    // hmm i think the mutate is going to want better percent controls on the
    // terminal/non-terminal relationship?
    public Expression createRandom(Expression model, int type, int level) {
        assert level > 0;

        // Work out percent chance of non-terminate symbol
        float branchPercent = (float)BRANCH_FACTOR / (float)level;
        float percent = random.nextFloat() * 100;

        // If the type is a boolean then there isn't much point generating
        // the boolean terminal expressions TRUE or FALSE because our
        // simplification code will just simplify it out of existence,
        // e.g. "and or a" would just become "a".
        if(type == Expression.BOOLEAN_TYPE || branchPercent > percent)
            return createRandomNonTerminal(model, type, level + 1);
        else
            return createRandomTerminal(type);
    }

    public Expression createRandomNonTerminal(int type) {
        return createRandomNonTerminal(null, type, 1);
    }

    public Expression createRandomNonTerminal(Expression model, int type, int level) {
        assert level > 0;

        if(type == Expression.BOOLEAN_TYPE)
            return createRandomNonTerminalBoolean(model, level);
        else if(type == Expression.FLOAT_TYPE)
            return createRandomNonTerminalFloat(model, level);
        else if(type == Expression.INTEGER_TYPE)
            return createRandomNonTerminalInteger(model, level);
        else {
            // Quote types are all terminal!
            assert(type == Expression.FLOAT_QUOTE_TYPE ||
                   type == Expression.INTEGER_QUOTE_TYPE);
            return createRandomTerminal(type);
        }
    }

    public Expression createRandomTerminal(int type) {
        int randomNumber;

        switch(type) {
        case Expression.BOOLEAN_TYPE:
            randomNumber = random.nextInt(2);

            if(randomNumber == 0)
                return new NumberExpression(true);
            else {
                assert randomNumber == 1;
                return new NumberExpression(false);
            }

        case Expression.FLOAT_TYPE:
            return new NumberExpression(50 - random.nextFloat() * 100);

        case Expression.INTEGER_TYPE:

            // Give it a 50/50 that it will generate an ordinary number
            if(random.nextBoolean())
                return new NumberExpression(50 - random.nextInt(100));

            // Otherwise generate some special values
            else {
                // We don't generate DayOfYearExpression() or MonthExpression()
                // because it would make it easy for the GP to hook onto specific dates
                // where the market is low. By removing these it forces the GP
                // to use the stock data to generate buy/sell decisions.
                int numberRandomSymbols = 4;

                if(!allowHeld)
                    numberRandomSymbols--;
                if(!allowOrder)
                    numberRandomSymbols--;

                randomNumber = random.nextInt(numberRandomSymbols);

                if(randomNumber == 0)
                    return new DayExpression();
                else if(randomNumber == 1)
                    return new DayOfWeekExpression();
                else {
                    if(allowOrder && allowHeld) {
                        if(randomNumber == 2)
                            return new VariableExpression("held", Expression.INTEGER_TYPE);
                        else
                            return new VariableExpression("order", Expression.INTEGER_TYPE);
                    }
                    else if(allowHeld)
                        return new VariableExpression("held", Expression.INTEGER_TYPE);
                    else {
                        assert allowOrder;
                        return new VariableExpression("order", Expression.INTEGER_TYPE);
                    }
                }
            }

        case Expression.FLOAT_QUOTE_TYPE:
            randomNumber = random.nextInt(4);

            if(randomNumber == 0)
                return new DayOpenExpression();
            else if(randomNumber == 1)
                return new DayHighExpression();
            else if(randomNumber == 2)
                return new DayLowExpression();
            else {
                assert randomNumber == 3;
                return new DayCloseExpression();
            }

        case Expression.INTEGER_QUOTE_TYPE:
            return new DayVolumeExpression();

        default:
            assert false;
            return null;
        }
    }

    private Expression createRandomNonTerminalBoolean(Expression model, int level) {
        int randomNumber = random.nextInt(9);

        if(randomNumber == 0) {
            return new NotExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE));
        }
        else if(randomNumber == 1) {
            Expression first = getChild(model, level, 0);
            return new EqualThanExpression(first,
                                           getChild(model, level, 1, first.getType()));
        }
        else if(randomNumber == 2) {
            Expression first = getChild(model, level, 0);
            return new GreaterThanEqualExpression(first,
                                                  getChild(model, level, 1, first.getType()));
        }
        else if(randomNumber == 3) {
            Expression first = getChild(model, level, 0);
            return new GreaterThanExpression(first,
                                             getChild(model, level, 1, first.getType()));
        }
        else if(randomNumber == 4) {
            Expression first = getChild(model, level, 0);
            return new LessThanEqualExpression(first,
                                               getChild(model, level, 1, first.getType()));
        }
        else if(randomNumber == 5) {
            Expression first = getChild(model, level, 0);
            return new LessThanExpression(first,
                                          getChild(model, level, 1, first.getType()));
        }
        else if(randomNumber == 6) {
            Expression first = getChild(model, level, 0);
            return new NotEqualExpression(first,
                                          getChild(model, level, 1, first.getType()));
        }
        else if(randomNumber == 7) {
            return new AndExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                     getChild(model, level, 1, Expression.BOOLEAN_TYPE));
        }
        else {
            assert randomNumber == 8;
            return new OrExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.BOOLEAN_TYPE));
        }
    }

    private Expression createRandomNonTerminalFloat(Expression model, int level) {
        int randomNumber = random.nextInt(14);

        // If we are mutating an existing number expression then favour
        // just modifying the number's value rather than replacing it
        // with a random expressions. This helps keep the equation size down and
        // favours trying different values.
        if(model != null &&
           model instanceof NumberExpression &&
           FAVOUR_NUMBER_PERCENT > random.nextInt(100)) {

            NumberExpression numberExpression = (NumberExpression)model;
            int step = random.nextInt(6);
            float value = (float)Math.pow(10.0F, (double)step);

            if(random.nextBoolean())
                value = -value;

            numberExpression.setValue(numberExpression.getValue() + value);
            return numberExpression;
        }

        if(randomNumber == 0)
            return createRandomTerminal(Expression.FLOAT_TYPE);
        else if(randomNumber == 1)
            return new AddExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                     getChild(model, level, 1, Expression.FLOAT_TYPE));
        else if(randomNumber == 2)
            return new SubtractExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                          getChild(model, level, 1, Expression.FLOAT_TYPE));
        else if(randomNumber == 3)
            return new MultiplyExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                          getChild(model, level, 1, Expression.FLOAT_TYPE));
        else if(randomNumber == 4)
            return new DivideExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                        getChild(model, level, 1, Expression.FLOAT_TYPE));
        else if(randomNumber == 5)
            return new PercentExpression(getChild(model, level, 0, Expression.FLOAT_TYPE),
                                         getChild(model, level, 1));
        else if(randomNumber == 6)
            return new IfExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.FLOAT_TYPE),
                                    getChild(model, level, 2, Expression.FLOAT_TYPE));
        else if(randomNumber == 7)
            return new LagExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE));
        else if(randomNumber == 8)
            return new MinExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        else if(randomNumber == 9)
            return new MaxExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        else if(randomNumber == 10)
            return new SumExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        else if(randomNumber == 11)
            return new SqrtExpression(getChild(model, level, 0, Expression.FLOAT_TYPE));

        else if(randomNumber == 12)
            return new AbsExpression(getChild(model, level, 0, Expression.FLOAT_TYPE));
        else {
            assert randomNumber == 13;
            return new AvgExpression(createRandomTerminal(Expression.FLOAT_QUOTE_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        }
    }

    private Expression createRandomNonTerminalInteger(Expression model, int level) {
        int randomNumber = random.nextInt(14);

        // If we are mutating an existing number expression then favour
        // just modifying the number's value rather than replacing it
        // with a random expressions. This helps keep the equation size down and
        // favours trying different values.
        if(model != null &&
           model instanceof NumberExpression &&
           FAVOUR_NUMBER_PERCENT > random.nextInt(100)) {

            NumberExpression numberExpression = (NumberExpression)model;
            int step = random.nextInt(6);
            float value = (float)Math.pow(10.0, (double)step);

            if(random.nextBoolean())
                value = -value;

            numberExpression.setValue(numberExpression.getValue() + value);
            return numberExpression;
        }

        if(randomNumber == 0)
            return createRandomTerminal(Expression.INTEGER_TYPE);
        else if(randomNumber == 1)
            return new AddExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE));
        else if(randomNumber == 2)
            return new SubtractExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                          getChild(model, level, 1, Expression.INTEGER_TYPE));
        else if(randomNumber == 3)
            return new MultiplyExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                          getChild(model, level, 1, Expression.INTEGER_TYPE));
        else if(randomNumber == 4)
            return new DivideExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                        getChild(model, level, 1, Expression.INTEGER_TYPE));
        else if(randomNumber == 5)
            return new PercentExpression(getChild(model, level, 0, Expression.INTEGER_TYPE),
                                         getChild(model, level, 1));
        else if(randomNumber == 6)
            return new IfExpression(getChild(model, level, 0, Expression.BOOLEAN_TYPE),
                                    getChild(model, level, 1, Expression.INTEGER_TYPE),
                                    getChild(model, level, 2, Expression.INTEGER_TYPE));
        else if(randomNumber == 7)
            return new LagExpression(new DayVolumeExpression(),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE));
        else if(randomNumber == 8)
            return new MinExpression(new DayVolumeExpression(),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        else if(randomNumber == 9)
            return new MaxExpression(new DayVolumeExpression(),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        else if(randomNumber == 10)
            return new SumExpression(new DayVolumeExpression(),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        else if(randomNumber == 11)
            return new SqrtExpression(getChild(model, level, 0, Expression.INTEGER_TYPE));

        else if(randomNumber == 12)
            return new AbsExpression(getChild(model, level, 0, Expression.INTEGER_TYPE));
        else {
            assert randomNumber == 13;
            return new AvgExpression(new DayVolumeExpression(),
                                     getChild(model, level, 1, Expression.INTEGER_TYPE),
                                     getChild(model, level, 2, Expression.INTEGER_TYPE));
        }
    }

    private Expression getChild(Expression model, int level, int arg, int type) {

        // Case 1: The expression doesn't have this many children or
        // it has a child here but it is a different type. So create
        // a new argument.
        if(model == null ||
           arg >= model.getNeededChildren() ||
           model.get(arg).getType() != type) {
            return createRandom(null, type, level);
        }

        // Case 2: It has an argument of the right type
        else
            return model.get(arg);
    }

    // creates a float or integer type
    private Expression getChild(Expression model, int level, int arg) {

        // Case 1: The expression doesn't have this many children or
        // it has a child here but it is a different type. So create
        // a new argument.
        if(model == null ||
           arg >= model.getNeededChildren() ||
           (model.get(arg).getType() != Expression.FLOAT_TYPE &&
            model.get(arg).getType() != Expression.INTEGER_TYPE)) {

            int randomNumber = random.nextInt(2);

            if(randomNumber == 0)
                return createRandom(null, Expression.FLOAT_TYPE, level);
            else {
                assert randomNumber == 1;
                return createRandom(null, Expression.INTEGER_TYPE, level);
            }
        }

        // Case 2: It has an argument of the right type
        else
            return model.get(arg);
    }

    public Expression findRandomSite(Expression expression) {
        int randomNumber = random.nextInt(expression.size());
        Expression randomSite = null;

        for(Enumeration enumeration = expression.breadthFirstEnumeration();
            enumeration.hasMoreElements();) {

            randomSite = (Expression)enumeration.nextElement();

            // Return if this is the xth random element
            if(randomNumber-- <= 0)
                break;
        }

        assert randomSite != null;

        return randomSite;
    }

    // may return null
    public Expression findRandomSite(Expression expression, int type) {
        Expression randomSite = null;
        int possibleSites = expression.size(type);

        if(possibleSites > 0) {
            int randomNumber = random.nextInt(possibleSites);

            for(Enumeration enumeration = expression.breadthFirstEnumeration();
                enumeration.hasMoreElements();) {

                randomSite = (Expression)enumeration.nextElement();

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

    public Expression delete(Expression root, Expression destination) {
        return insert(root, destination,
                      createRandomTerminal(destination.getType()));
    }

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
            parent.remove(childNumber);
            parent.insert(source, childNumber);
            assert parent.getNeededChildren() == parent.getChildCount();
            return root;
        }
    }

    public Expression modify(Expression root, Expression destination) {
        Expression newExpression = createRandom(destination, destination.getType(), 1);

        if(destination == root)
            return newExpression;
        else
            return insert(root, destination, newExpression);
    }

    public Expression mutate(Expression expression) {
        return mutate(expression, MUTATION_PERCENT);
    }

    public Expression mutate(Expression expression, int percent) {
        // Mutations do not always occur. Use the given percent to work
        // out whether one should occur.
        if(percent < random.nextInt(100))
            return expression;

        // Mutate
        if(INSERTION_MUTATION_PERCENT > percent)
            expression = mutateByInsertion(expression);
        else {
            percent -= INSERTION_MUTATION_PERCENT;

            if(DELETION_MUTATION_PERCENT > percent)
                expression = mutateByDeletion(expression);
            else {
                percent -= DELETION_MUTATION_PERCENT;

                expression = mutateByModification(expression);
            }
        }

        // There's always the possibility of a 2nd, 3rd, etc mutation. This
        // can be useful if the gene pool is stagnant.
        return mutate(expression, EXTRA_MUTATION_PERCENT);
    }

    private Expression mutateByModification(Expression expression) {
        Expression destination = findRandomSite(expression);

        return modify(expression, destination);
    }

    private Expression mutateByInsertion(Expression expression) {
        Expression destination = findRandomSite(expression);
        Expression insertSubTree = createRandom(destination.getType());

        return insert(expression, destination, insertSubTree);
    }

    private Expression mutateByDeletion(Expression expression) {
        Expression destination = findRandomSite(expression);

        // There's no point in replacing the root node with a terminal
        // expression, and replacing a terminal expression with
        // a random expression is closer to an insertion mutation than
        // deletion. So just skip the whole deletion idea and try a random
        // mutation somewhere.
        if(destination.isRoot() || destination.getChildCount() == 0)
            return mutateByModification(expression);
        else
            return delete(expression, destination);
    }
}
