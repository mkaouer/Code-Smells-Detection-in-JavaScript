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

import nz.org.venice.parser.Parser;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.util.Locale;

/**
 * An expression which returns a quote.
 *
 * @author Andrew Leppard
 */
public class LagExpression extends BinaryExpression {

    /**
     * Create a new average expression for the given <code>quote</code> kind,
     * for <code>lag</code> days away.
     *
     * @param	quote	the quote kind to return
     * @param	lag	the offset from the current day
     */
    public LagExpression(Expression quote, Expression lag) {
        super(quote, lag);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {
	
	//Need a better name for this
	QuoteSymbol quoteChild = (QuoteSymbol)getChild(0);

	int lag = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);
        if (lag > 0) {
	    EvaluationException e = EvaluationException.LAG_OFFSET_EXCEPTION;
	    e.setMessage(this, "", lag);
	    throw e;
	}
	
	Symbol explicitSymbol = (quoteChild.getSymbol() != null) 
	    ? quoteChild.getSymbol() : symbol;
	int quoteKind = quoteChild.getQuoteKind();
	
        try {
            return quoteBundle.getQuote(explicitSymbol, quoteKind, day, lag);
        }
        catch(MissingQuoteException e) {
	    //Used to return but causes hard to track down behaviour in
	    //rules when this occurs.	   
	    //return 0.0D;
	    
	    //Instead, return the next available quote, if one exists
	    try {
		if (day + lag > 0) {
		    String message = Locale.getString("LAG_OFFSET_ERROR") + " offset: " + (day + lag);
		    throw new EvaluationException(message);
		}

		double nearQuote = quoteBundle.getNearestQuote(explicitSymbol, quoteKind, day + lag);
		return nearQuote;
	    } catch (MissingQuoteException e2) {
		//No suitable quote found.
		String message = symbol + " : " + Locale.getString("NO_QUOTES_DATE", quoteBundle.offsetToDate((day + lag)).toString());
		

		throw new EvaluationException(message);
	    } finally {
		//There's another race here:
		//paper trade doesn't always catch the exception in the return
		//return 0.0D;
	    }
	}
    }

    public String toString() {
        Expression quoteExpression = getChild(0);
        Expression lagExpression = getChild(1);

	String quoteStr = (getChild(0) != null) ? getChild(0).toString() : "(nulL)";
	String lagStr = (getChild(1) != null) ? getChild(1).toString() : "(null)";
	return new String("lag(" + quoteStr + ", " + lagStr + ")");

    }

    public int checkType() throws TypeMismatchException {
	// Left type must be quote and right type must be number type
	if((getChild(0).checkType() == FLOAT_QUOTE_TYPE ||
            getChild(0).checkType() == INTEGER_QUOTE_TYPE) &&
	   getChild(1).checkType() == INTEGER_TYPE)
	    return getType();
	else {	
	    String types = 
		getChild(0).getType() + " , " + 
		getChild(1).getType() + " , " + 
		((getChildCount() > 2) ? getChild(2).getType() + "" : "");
		
	    
	    String expectedTypes =
		FLOAT_QUOTE_TYPE + " , " + 
		INTEGER_TYPE     + " , " + 
		INTEGER_TYPE;
	    
	    throw new TypeMismatchException(this, types, expectedTypes);
	}
    }

    public int getType() {
        if(getChild(0).getType() == FLOAT_QUOTE_TYPE)
            return FLOAT_TYPE;
        else {
            assert getChild(0).getType() == INTEGER_QUOTE_TYPE;
            return INTEGER_TYPE;
        }
    }
    
    public Object clone() {
        return new LagExpression((Expression)getChild(0).clone(),
                                 (Expression)getChild(1).clone());
    }

}
