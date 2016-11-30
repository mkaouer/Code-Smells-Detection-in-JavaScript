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
 * An expression which returns the cosine value.
 */
public class CosineExpression extends UnaryExpression {

    /**
     * An expression which performs cosine function <code>cos</code> on the 
     * sub-expressions.
     */
    public CosineExpression(Expression arg) {
	super(arg);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	return Math.cos(getChild(0).evaluate(variables, quoteBundle, symbol, day));
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
                // Shouldn't happen.
                return this;
            }
        }
        else {
            return this;
        }
    }

    public String toString() {
	return new String("cos(" + getChild(0).toString() + ")");
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

    /**
     * Get the type of the expression.
     *
     * @return {@link #FLOAT_TYPE}.
     */
    public int getType() {
        return FLOAT_TYPE;
    }

    public Object clone() {
        return new CosineExpression((Expression)getChild(0).clone());
    }
}
