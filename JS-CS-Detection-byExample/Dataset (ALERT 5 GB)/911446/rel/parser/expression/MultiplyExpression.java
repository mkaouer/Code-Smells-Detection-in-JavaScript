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
 * An expression which multiplies two sub-expressions.
 */
public class MultiplyExpression extends ArithmeticExpression {

    public MultiplyExpression(Expression left, Expression right) {
	super(left, right);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	return getChild(0).evaluate(variables, quoteBundle, symbol, day) *
	    getChild(1).evaluate(variables, quoteBundle, symbol, day);
    }

    public Expression simplify() {       
        // First perform arithmetic simplifications
        Expression simplified = super.simplify();

	NumberExpression left = null;
	NumberExpression right = null;

        if(simplified.equals(this)) {
            left = (simplified.getChild(0) instanceof NumberExpression? 
                                     (NumberExpression)simplified.getChild(0) : null);
            right = (simplified.getChild(1) instanceof NumberExpression? 
		     (NumberExpression)simplified.getChild(1) : null);

            // 0*a -> 0.
            if(left != null && left.equals(0.0D)) {	       
                return new NumberExpression(0.0D, simplified.getType());
	    }

            // a*0 -> 0.
            else if(right != null && right.equals(0.0D)) {
                return new NumberExpression(0.0D, simplified.getType());
	    }

            // a*1 -> a.
            else if(right != null && right.equals(1.0D)) {
                return simplified.getChild(0);
	    }

	    //1 * a -> a
	    else if (left != null && left.equals(1.0D)) {
		return simplified.getChild(1);
	    }
        }
	return simplified;    
    }

    public boolean equals(Object object) {

        // Are they both multiply expressions?
        if(object instanceof MultiplyExpression) {
            MultiplyExpression expression = (MultiplyExpression)object;

            // (x*y) == (x*y)
            if(getChild(0).equals(expression.getChild(0)) &&
               getChild(1).equals(expression.getChild(1)))
                return true;

            // (x*y) == (y*x)
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
	return super.toString("*");
    }

    public Object clone() {
        return new MultiplyExpression((Expression)getChild(0).clone(), 
                                      (Expression)getChild(1).clone());
    }
}
