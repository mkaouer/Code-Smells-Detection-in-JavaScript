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

import org.mov.parser.*;
import org.mov.quote.*;

/**
 * An expression which compares the two sub-expressions for inequality.
 */
public class NotEqualExpression extends ComparisionExpression {

    public NotEqualExpression(Expression left, Expression right) {
	super(left, right);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	if(getChild(0).evaluate(variables, quoteBundle, symbol, day) !=
	   getChild(1).evaluate(variables, quoteBundle, symbol, day))
	    return TRUE;
	else
	    return FALSE;
    }

    public Expression simplify() {
        // First perform comparision simplifications
        Expression simplified = super.simplify();

        // If we haven't simplified the whole expression away and
        // the left and right arguments are the same expression
        // then the comparision must be false.
        if(simplified == this && getChild(0).equals(getChild(1)))
            return new NumberExpression(false);
        else
            return simplified;
    }

    public boolean equals(Object object) {

        // Are they both not equals expressions?
        if(object instanceof NotEqualExpression) {
            NotEqualExpression expression = (NotEqualExpression)object;

            // (x != y) == (x != y)
            if(getChild(0).equals(expression.getChild(0)) &&
               getChild(1).equals(expression.getChild(1)))
                return true;

            // (x != y) == (y != x)
            if(getChild(0).equals(expression.getChild(1)) &&
               getChild(1).equals(expression.getChild(0)))
                return true;
        }
    
        return false;
    }

    public String toString() {
	return super.toString("!=");
    }

    public Object clone() {
        return new NotEqualExpression((Expression)getChild(0).clone(), 
                                      (Expression)getChild(1).clone());
    }
}



