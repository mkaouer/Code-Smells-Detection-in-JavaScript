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

import nz.org.venice.util.Locale;

/**
 * An expression which divides two sub-expressions.
 *
 * @author Andrew Leppard
 */
public class DivideExpression extends ArithmeticExpression {

    public DivideExpression(Expression left, Expression right) {
	super(left, right);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	double right = getChild(1).evaluate(variables, quoteBundle, symbol, day);

	if(right != 0.0D)
	    return getChild(0).evaluate(variables, quoteBundle, symbol, day) / right;
	else {
	    
	    EvaluationException e = EvaluationException.DIVIDE_BY_ZERO_EXCEPTION;
	    e.setMessage(this, "", right);
	    throw e;
	}
    }

    public Expression simplify() {
        // First perform arithmetic simplifications
        Expression simplified = super.simplify();

        if(simplified.equals(this)) {
            NumberExpression left = (simplified.getChild(0) instanceof NumberExpression? 
                                     (NumberExpression)simplified.getChild(0) : null);
            NumberExpression right = (simplified.getChild(1) instanceof NumberExpression? 
                                      (NumberExpression)simplified.getChild(1) : null);

            // 0/a -> 0.
            if(left != null && left.equals(0.0D))
                return new NumberExpression(0.0D, simplified.getType());

            // a/1 -> a.
            else if(right != null && right.equals(1.0D))
                return simplified.getChild(0);
            
            // a/a -> 1 (pragmatism over idealism)
            else if(simplified.getChild(0).equals(simplified.getChild(1)))
                return new NumberExpression(1.0D, simplified.getType());
        }
        return simplified;
    }

    public String toString() {
	return super.toString("/");
    }

    public Object clone() {
        return new DivideExpression((Expression)getChild(0).clone(), 
                                    (Expression)getChild(1).clone());
    }
}
