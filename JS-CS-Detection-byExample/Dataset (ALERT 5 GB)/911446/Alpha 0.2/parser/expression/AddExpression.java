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

import org.mov.util.*;
import org.mov.parser.*;
import org.mov.quote.*;

/**
 * An expression which adds two sub-expressions.
 */
public class AddExpression extends ArithmeticExpression {

    public AddExpression(Expression left, Expression right) {
	super(left, right);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	return getLeft().evaluate(variables, quoteBundle, symbol, day) +
	    getRight().evaluate(variables, quoteBundle, symbol, day);
    }

    public Expression simplify() {
        // First perform arithmetic simplifications
        Expression simplified = super.simplify();

        if(simplified == this) {
            NumberExpression left = (getLeft() instanceof NumberExpression? 
                                     (NumberExpression)getLeft() : null);
            NumberExpression right = (getRight() instanceof NumberExpression? 
                                      (NumberExpression)getRight() : null);

            // 0+a -> a.
            if(left != null && left.equals(0.0F))
                return getRight();

            // a+0 -> a.
            else if(right != null && right.equals(0.0F))
                return getLeft();

            // a+a -> 2*a. This doesn't seem like a simplification but
            // remember a could be a complicated expression like:
            // (lag(day_close, 0) * lag(day_open, 0))
            else if(getLeft().equals(getRight()))
                return new MultiplyExpression(new NumberExpression(2.0F, getLeft().getType()),
                                              getLeft());
        }
        return simplified;
    }

    public boolean equals(Object object) {

        // Are they both add expressions?
        if(object instanceof AddExpression) {
            AddExpression expression = (AddExpression)object;

            // (x+y) == (x+y)
            if(getLeft().equals(expression.getLeft()) &&
               getRight().equals(expression.getRight()))
                return true;

            // (x+y) == (y+x)
            if(getLeft().equals(expression.getRight()) &&
               getRight().equals(expression.getLeft()))
                return true;
        }
    
        return false;
    }

    public String toString() {
	return super.toString("+");
    }

    public Object clone() {
        return new AddExpression((Expression)getLeft().clone(), 
                                 (Expression)getRight().clone());
    }
}
