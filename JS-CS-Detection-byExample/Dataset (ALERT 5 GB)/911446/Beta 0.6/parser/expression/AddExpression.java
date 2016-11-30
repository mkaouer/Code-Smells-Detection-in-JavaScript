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
 *
 * An expression which adds two sub-expressions.
 *
 */

public class AddExpression extends ArithmeticExpression {
    public AddExpression(Expression left, Expression right) {
        super(left, right);
    }
    
    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
    throws EvaluationException {
        return getChild(0).evaluate(variables, quoteBundle, symbol, day) +
        getChild(1).evaluate(variables, quoteBundle, symbol, day);
    }

    public Expression simplify() {
        // First perform arithmetic simplifications
        Expression simplified = super.simplify();

        if(simplified == this) {
            NumberExpression left = (getChild(0) instanceof NumberExpression?
                (NumberExpression)getChild(0) : null);
            NumberExpression right = (getChild(1) instanceof NumberExpression?
                (NumberExpression)getChild(1) : null);

            // a+0 -> a.
            if(right != null && right.equals(0.0D))
                return getChild(0);

            // a+a -> 2*a. This doesn't seem like a simplification but
            // remember a could be a complicated expression like:
            // (lag(day_close, 0) * lag(day_open, 0))
            else if(getChild(0).equals(getChild(1)))
                return new MultiplyExpression(new NumberExpression(2.0D, getChild(0).getType()),
                getChild(0));
        }
        return simplified;
    }

    public boolean equals(Object object) {

        // Are they both add expressions?
        if(object instanceof AddExpression) {
            AddExpression expression = (AddExpression)object;

            // (x+y) == (x+y)
            if(getChild(0).equals(expression.getChild(0)) &&
            getChild(1).equals(expression.getChild(1)))
                return true;

            // (x+y) == (y+x)
            if(getChild(0).equals(expression.getChild(1)) &&
            getChild(1).equals(expression.getChild(0)))
                return true;
        }

        return false;
    }

    public String toString() {
        return super.toString("+");
    }

    public Object clone() {
        return new AddExpression((Expression)getChild(0).clone(),
        (Expression)getChild(1).clone());
    }
}

