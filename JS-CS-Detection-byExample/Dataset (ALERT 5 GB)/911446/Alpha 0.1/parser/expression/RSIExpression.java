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

    public float evaluate(QuoteBundle quoteBundle, String symbol, int day) 
	throws EvaluationException {
	
	int days = (int)getArg(0).evaluate(quoteBundle, symbol, day);
	int lastDay = day + (int)getArg(1).evaluate(quoteBundle, symbol, day);
	System.err.println("calling rsi on symbol "+symbol);
	return QuoteFunctions.rsi(quoteBundle, symbol, Quote.DAY_CLOSE, days,
				  lastDay);
    }

    public String toString() {
	return new String("rsi(" + 
			  getArg(0).toString() + ", " +
			  getArg(1).toString() + ")");
    }

    public int checkType() throws TypeMismatchException {

	// First type must be quote, second and third types must be value
	if(getArg(0).checkType() == VALUE_TYPE &&
	   getArg(1).checkType() == VALUE_TYPE)
	    return VALUE_TYPE;
	else
	    throw new TypeMismatchException();
    }

    public int getNeededChildren() {
	return 2;
    }

    private Expression getArg(int arg) {
	return (Expression)getChildAt(arg);
    }

}

