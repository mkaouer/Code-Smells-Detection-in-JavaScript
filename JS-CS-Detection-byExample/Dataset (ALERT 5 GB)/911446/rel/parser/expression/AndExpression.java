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

package nz.org.venice.parser.expression;

import nz.org.venice.parser.*;
import nz.org.venice.quote.*;

/**
 * An expression which performs boolean <code>and</code> on two 
 * sub-expressions.
 */
public class AndExpression extends LogicExpression {

    public AndExpression(Expression left, Expression right) {
	super(left, right);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	if(getChild(0).evaluate(variables, quoteBundle, symbol, day) >= TRUE_LEVEL &&
	   getChild(1).evaluate(variables, quoteBundle, symbol, day) >= TRUE_LEVEL)
	    return TRUE;
	else
	    return FALSE;
    }

    public Expression simplify() {
        // First simplify all the child arguments
        Expression simplified = super.simplify();

        NumberExpression left = (simplified.getChild(0) instanceof NumberExpression? 
                                 (NumberExpression)simplified.getChild(0) : null);
        NumberExpression right = (simplified.getChild(1) instanceof NumberExpression? 
                                  (NumberExpression)simplified.getChild(1) : null);

        // If either child argument is the constant TRUE we can simplify to the 
        // other child argument
        if(left != null && left.getValue() >= TRUE_LEVEL)
            return simplified.getChild(1);
        else if(right != null && right.getValue() >= TRUE_LEVEL)
            return simplified.getChild(0);

        // If either child argument is the constant FALSE we can simplify to the
        // constant FALSE
        else if((left != null && left.getValue() < TRUE_LEVEL) ||
                (right != null && right.getValue() < TRUE_LEVEL))
            return new NumberExpression(false);

        // If both child arguments are the same then we can simplify to the constant
        // TRUE.
        else if(simplified.getChild(0).equals(simplified.getChild(1)))
            return new NumberExpression(true);

        else
            return simplified;
    }

    public boolean equals(Object object) {

        // Are they both and expressions?
        if(object instanceof AndExpression) {
            AndExpression expression = (AndExpression)object;

            // (x and y) == (x and y)
            if(getChild(0).equals(expression.getChild(0)) &&
               getChild(1).equals(expression.getChild(1)))
                return true;

            // (x and y) == (y and x)
            if(getChild(0).equals(expression.getChild(1)) &&
               getChild(1).equals(expression.getChild(0)))
                return true;
        }
    
        return false;
    }

    public int hashCode() {
	Expression child1 = getChild(0);
	Expression child2 = getChild(1);

	assert child1 != null;
	assert child2 != null;

	return child1.hashCode() ^ child2.hashCode();	
    }

    public String toString() {
	return super.toString("and");
    }

    public Object clone() {
        return new AndExpression((Expression)getChild(0).clone(), 
                                 (Expression)getChild(1).clone());
    }
}

