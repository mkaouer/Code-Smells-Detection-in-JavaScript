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

import org.mov.util.*;
import org.mov.parser.*;
import org.mov.quote.*;

/**
 * An expression which subtracts two sub-expressions.
 */
public class SubtractExpression extends ArithmeticExpression {

    public SubtractExpression(Expression left, Expression right) {
	super(left, right);
    }

    public float evaluate(QuoteBundle quoteBundle, String symbol, int day) 
	throws EvaluationException {

	return getLeft().evaluate(quoteBundle, symbol, day) -
	    getRight().evaluate(quoteBundle, symbol, day);
    }

    public String toString() {
	return super.toString("-");
    }
}
