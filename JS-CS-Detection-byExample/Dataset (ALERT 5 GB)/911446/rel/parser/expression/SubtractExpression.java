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
 * An expression which subtracts two sub-expressions.
 */
public class SubtractExpression extends ArithmeticExpression {

    public SubtractExpression(Expression left, Expression right) {
	super(left, right);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	return getChild(0).evaluate(variables, quoteBundle, symbol, day) -
	    getChild(1).evaluate(variables, quoteBundle, symbol, day);
    }

    public Expression simplify() {
        // First perform arithmetic simplifications
        Expression simplified = super.simplify();

        if(simplified.equals(this)) {
            NumberExpression left = (simplified.getChild(0) instanceof NumberExpression? 
                                     (NumberExpression)simplified.getChild(0) : null);
            NumberExpression right = (simplified.getChild(1) instanceof NumberExpression? 
                                      (NumberExpression)simplified.getChild(1) : null);

            // a-0 -> a.
            if(right != null && right.equals(0.0D))
                return simplified.getChild(0);

            // a-a -> 0.
            else if(getChild(0).equals(getChild(1)))
                return new NumberExpression(0.0D, simplified.getType());
        }
        return simplified;
    }

    public String toString() {
	return super.toString("-");
    }

    public Object clone() {
        return new SubtractExpression((Expression)getChild(0).clone(), 
                                      (Expression)getChild(1).clone());
    }
}
