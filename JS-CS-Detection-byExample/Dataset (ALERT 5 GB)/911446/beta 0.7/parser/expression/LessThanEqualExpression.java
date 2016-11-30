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
 * An expression which compares whether the first expression is less than or
 * equal to the second expression.
 */
public class LessThanEqualExpression extends ComparisionExpression {

    public LessThanEqualExpression(Expression left, Expression right) {
	super(left, right);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	if(getChild(0).evaluate(variables, quoteBundle, symbol, day) <=
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
        // then the comparision must be true.
        if(simplified == this && getChild(0).equals(getChild(1)))
            return new NumberExpression(true);
        else
            return simplified;
    }

    public String toString() {
	return super.toString("<=");
    }

    public Object clone() {
        return new LessThanEqualExpression((Expression)getChild(0).clone(), 
                                           (Expression)getChild(1).clone());
    }
}
