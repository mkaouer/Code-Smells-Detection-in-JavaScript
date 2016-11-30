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
 * An expression which finds the RSI over a given trading period.
 */
public class RSIExpression extends Expression {
    
    private int quoteKind;

    public RSIExpression(Expression days, Expression lag) {
	add(days);
	add(lag);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {
	
	int days = (int)get(0).evaluate(variables, quoteBundle, symbol, day);
	int lastDay = day + (int)get(1).evaluate(variables, quoteBundle, symbol, day);
	System.err.println("calling rsi on symbol "+symbol);
	return QuoteFunctions.rsi(quoteBundle, symbol, Quote.DAY_CLOSE, days,
				  lastDay);
    }

    public String toString() {
	return new String("rsi(" + 
			  get(0).toString() + ", " +
			  get(1).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {
	if(get(0).checkType() == INTEGER_TYPE &&
	   get(1).checkType() == INTEGER_TYPE)
	    return FLOAT_TYPE;
	else
	    throw new TypeMismatchException();
    }

    public int getNeededChildren() {
	return 2;
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
        return new RSIExpression((Expression)get(0).clone(), 
                                 (Expression)get(1).clone());
    }
}

