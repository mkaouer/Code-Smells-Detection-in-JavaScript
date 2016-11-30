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
 * An expression which calculates the square root of a number.
 *
 * @author Andrew Leppard
 */
public class SqrtExpression extends UnaryExpression {
    
    public SqrtExpression(Expression number) {
        super(number);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

        double number = getChild(0).evaluate(variables, quoteBundle, symbol, day);

        if(number >= 0)
            return (double)Math.sqrt(number);
        else
            throw EvaluationException.SQUARE_ROOT_NEGATIVE_EXCEPTION;
    }

    public String toString() {
	return new String("sqrt(" + getChild(0).toString() + ")");
    }

    /**
     * Check the input argument to the expression. It can only be
     * {@link #INTEGER_TYPE} or {@link #FLOAT_TYPE}. 
     *
     * @return	the type of the expression
     */
    public int checkType() throws TypeMismatchException {
        int type = getChild(0).checkType();

        if(type == FLOAT_TYPE || type == INTEGER_TYPE)
            return getType();
        else
            throw new TypeMismatchException();
    }

    public Expression simplify() {
        // First simplify child argument
        super.simplify();

        // If the child argument is a constant we can precompute.
        if(getChild(0) instanceof NumberExpression) {
            try {
                return new NumberExpression(evaluate(null, null, null, 0), getType());
            }
            catch(EvaluationException e) {
                // Can happen if we hit sqrt(-1). In which case don't bother to simplify.
                return this;
            }
        }
        else
            return this;
    }

    /**
     * Get the type of the expression.
     *
     * @return either {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE}.
     */
    public int getType() {
        return getChild(0).getType();
    }

    public Object clone() {
        return new SqrtExpression((Expression)getChild(0).clone());
    }
}
