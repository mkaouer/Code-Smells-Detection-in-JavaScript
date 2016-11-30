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

package org.mov.parser.expression;

import java.util.ArrayList;
import java.util.List;

import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.TypeMismatchException;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;

/**
 * A clause is an ordered list of sub-expressions. Each sub-expression is
 * executed serially. The value and type of the clause is the value and type of
 * the last sub-expression in the clause.
 */
public class ClauseExpression extends AbstractExpression {
    // Number of sub-expressions in the clause
    private int childCount;

    /**
     * Create a new clause expression from the given list of expressions.
     *
     * @param children
     *            list of expressions.
     */
    public ClauseExpression(List children) {
        super(children.size());

        childCount = children.size();
        assert childCount > 0;

        // Transfer the children in the list to the base class array, so we
        // can reuse its functionality.
        for (int child = 0; child < getChildCount(); child++)
            setChild((Expression) children.get(child), child);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle,
                           Symbol symbol, int day) throws EvaluationException {

        // Execute all the sub-expressions in this clause and return the value
        // of
        // the last sub-expression.
        double value = 0.0D;

        Variables tmpVar = null;
        try {
            tmpVar = (Variables) variables.clone();
        } catch (CloneNotSupportedException e) {
        }

        for (int child = 0; child < getChildCount(); child++)
            //	    value = getChild(child).evaluate(variables, quoteBundle, symbol,
            // day);
            value = getChild(child).evaluate(tmpVar, quoteBundle, symbol, day);

        return value;
    }

    /**
     * A helper method to use when printing expressions that might be either
     * sub-expressions or clause expressions. For example the "if" expression,
     * ignoring the else clause, could be printed in two ways:
     *
     * <pre>
     *
     *  if(x) {
     *     statement1
     *     statement2
     *     ...
     *  }
     * </pre>
     *
     * or
     *
     * <pre>
     *
     *  if(x)
     *     statement
     *
     * </pre>
     *
     * Which one will depend on whether the expression is a clause or a
     * sub-expression. This function analyses the given expression and makes
     * sure it is printed correctly.
     *
     * @param expression
     *            the expression to print.
     * @return string representation.
     */
    public static String toString(Expression expression) {
        // if (x) {
        //   clause
        // }
        if (expression instanceof ClauseExpression &&
            expression.getChildCount() != 1)
            return " " + expression + " ";

        // if (x)
        //    one line
        else
            return "\n   " + expression + "\n";
    }

    public String toString() {
        String string;

        // If there is only a single expression in the clause, then
        // don't bother with the "{" and "}".
        if(getChildCount() == 1)
            string = getChild(0).toString();

        else {
            string = "{\n";

            for (int child = 0; child < getChildCount(); child++) {
                string = string.concat("   ");
                string = string.concat(getChild(child).toString());
                string = string.concat("\n");
            }

            string = string.concat("}\n");
        }

        return string;
    }

    public int checkType() throws TypeMismatchException {
        // Type of the clause expression is the type of the last contained
        // sub-expression
        int type = Expression.INTEGER_TYPE;

        for (int child = 0; child < getChildCount(); child++)
            type = getChild(child).checkType();

        return type;
    }

    public int getType() {
        // Type of the clause expression is the type of the last contained
        // sub-expression
        return getChild(getChildCount() - 1).getType();
    }

    /**
     * Return the number of children in the clause. This is the number of
     * sub-expressions.
     *
     * @return the number of sub-expressions.
     */
    public int getChildCount() {
        return childCount;
    }

    public Object clone() {
        List expressions = new ArrayList();

        for (int child = 0; child < getChildCount(); child++)
            expressions.add(getChild(child));

        return new ClauseExpression(expressions);
    }
}