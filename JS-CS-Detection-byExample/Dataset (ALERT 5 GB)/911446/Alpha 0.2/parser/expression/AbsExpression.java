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

import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.TypeMismatchException;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;

/**
 * An expression which calculates the absolute value of a number.
 */
public class AbsExpression extends UnaryExpression {
    
    public AbsExpression(Expression number) {
        super(number);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

        float number = get().evaluate(variables, quoteBundle, symbol, day);

        return Math.abs(number);
    }

    public String toString() {
	return new String("abs(" + get().toString() + ")");
    }

    /**
     * Check the input argument to the expression. It can only be
     * {@link #INTEGER_TYPE} or {@link #FLOAT_TYPE}. 
     *
     * @return	the type of the expression
     */
    public int checkType() throws TypeMismatchException {
        int type = get().checkType();

        if(type == FLOAT_TYPE || type == INTEGER_TYPE)
            return getType();
        else
            throw new TypeMismatchException();
    }

    public Expression simplify() {
        // First simplify child argument
        super.simplify();

        // If the child argument is a constant we can precompute.
        if(get() instanceof NumberExpression) {
            try {
                return new NumberExpression(evaluate(null, null, null, 0), getType());
            }
            catch(EvaluationException e) {
                // abs() should never raise EvaluationException
                assert false;
                return this;
            }
        }
        else
            return this;

        // abs(x * x)
        // abs(abs()) simplification.
        // abs(sqrt()) simplification.
        // sqrt(x * x) == abs(x).
        // abs(day()) 
        // etc...
    }

    /**
     * Get the type of the expression.
     *
     * @return either {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE}.
     */
    public int getType() {
        return get().getType();
    }

    public Object clone() {
        return new AbsExpression((Expression)get().clone());
    }
}
