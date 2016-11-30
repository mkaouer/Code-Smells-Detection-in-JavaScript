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

/**
 * A utility expression which checks if data exists for the offset. Some expressions, (Lag for example) will either abort or return 0 for missing data which
upsets averages and skews other rules.

 *
 * @author Mark Hummel
 **/

import nz.org.venice.parser.Expression;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.Symbol;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;

public class DataCheckExpression extends BinaryExpression {
    public DataCheckExpression(Expression symbol, Expression arg) {
	super(symbol, arg);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) throws EvaluationException {
		
	QuoteSymbol quoteChild = (QuoteSymbol)getChild(0);
	int quoteKind = quoteChild.getQuoteKind();
	Symbol explicitSymbol = (quoteChild.getSymbol() != null) 
	    ? quoteChild.getSymbol() : symbol;

	int offset = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);

	if (offset > 0) {
	    EvaluationException e = EvaluationException.LAG_OFFSET_EXCEPTION;
	    e.setMessage(this, "", offset);
	    throw e;
	}

	try {
	    quoteBundle.getQuote(explicitSymbol, quoteKind, day + offset);
	    return Expression.TRUE;
	} catch (MissingQuoteException e) {
	    try {
		quoteBundle.getNearestQuote(explicitSymbol, quoteKind, day + offset);

		return Expression.TRUE;
	    } catch (MissingQuoteException e2) {
		return Expression.FALSE;
	    }
	}
    }

    public int checkType() throws TypeMismatchException {
	if (getChild(0).checkType() == FLOAT_QUOTE_TYPE &&
	    getChild(1).checkType() == INTEGER_TYPE ) {
	    return getType();
	} else {
	    String types = getChild(0).getType() + " , " + getChild(1).getType();
	    String expectedTypes = FLOAT_QUOTE_TYPE + " , " + INTEGER_TYPE;

	    throw new TypeMismatchException(this, types, expectedTypes);
	}
    }

    public int getType() {
	return BOOLEAN_TYPE; 
    }

    public String toString() {
	return new String("offsetExists()");
    }

    public Object clone() {
	return new DataCheckExpression(getChild(0), getChild(1));
    }
}