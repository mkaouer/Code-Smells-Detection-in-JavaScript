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
 * An expression which divides two sub-expressions.
 */
public class DivideExpression extends ArithmeticExpression {

    public DivideExpression(Expression left, Expression right) {
	super(left, right);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	float right = getRight().evaluate(variables, quoteBundle, symbol, day);

	if(right != 0.0F)
	    return getLeft().evaluate(variables, quoteBundle, symbol, day) / right;
	else
            throw new EvaluationException("Divide by zero error");
    }

    public Expression simplify() {
        // First perform arithmetic simplifications
        Expression simplified = super.simplify();

        if(simplified == this) {
            NumberExpression left = (getLeft() instanceof NumberExpression? 
                                     (NumberExpression)getLeft() : null);
            NumberExpression right = (getRight() instanceof NumberExpression? 
                                      (NumberExpression)getRight() : null);

            // 0/a -> 0.
            if(left != null && left.equals(0.0F))
                return new NumberExpression(0.0F, getType());

            // a/1 -> a.
            else if(right != null && right.equals(1.0F))
                return getLeft();
            
            // a/a -> 1 (pragmatism over idealism)
            else if(getLeft().equals(getRight()))
                return new NumberExpression(1.0F, getType());
        }
        return simplified;
    }

    public String toString() {
	return super.toString("/");
    }

    public Object clone() {
        return new DivideExpression((Expression)getLeft().clone(), 
                                    (Expression)getRight().clone());
    }
}
