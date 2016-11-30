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
import nz.org.venice.util.Locale;


/**
 * Class that represents a quote kind where the user supplies a symbol 
 * different to that of the implicit symbol.
 * 
 */
public class QuoteSymbolExpression 
    extends UnaryExpression 
    implements QuoteSymbol  {

    // Quote kind - Quote.DAY_OPEN, Quote.DAY_CLOSE, Quote.DAY_LOW, etc...
    private int quoteKind;
    
    /**
     * Create a new quote expression.
     *
     * @param quoteKind Kind of quote. One of {@link Quote#DAY_OPEN},
     *        {@link Quote#DAY_CLOSE}, {@link Quote#DAY_LOW},
     *        {@link Quote#DAY_HIGH} or {@link Quote#DAY_VOLUME}
     */
    
    
    public QuoteSymbolExpression(int quoteKind) {
	super(null);
        assert(quoteKind == Quote.DAY_OPEN || quoteKind == Quote.DAY_CLOSE ||
               quoteKind == Quote.DAY_LOW || quoteKind == Quote.DAY_HIGH ||
               quoteKind == Quote.DAY_VOLUME);

        this.quoteKind = quoteKind;

    }

    public QuoteSymbolExpression(int quoteKind, Expression symbolExpression) {
	super(symbolExpression);
        assert(quoteKind == Quote.DAY_OPEN || quoteKind == Quote.DAY_CLOSE ||
               quoteKind == Quote.DAY_LOW || quoteKind == Quote.DAY_HIGH ||
               quoteKind == Quote.DAY_VOLUME);
	
	
        this.quoteKind = quoteKind;

    }
       
    /**
     * Get the quote kind.
     *
     * @return	the quote kind, one of: {@link Quote#DAY_OPEN},
     * {@link Quote#DAY_CLOSE}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_LOW}
     * or {@link Quote#DAY_VOLUME}.
     */
    public int getQuoteKind() {
	return quoteKind;
    }

    /**
     * Get the type of the expression.
     *
     * @return {@link #FLOAT_QUOTE_TYPE} or {@link #INTEGER_QUOTE_TYPE}.
     */
    public int getType() {
        if(getQuoteKind() == Quote.DAY_VOLUME)
            return INTEGER_QUOTE_TYPE;
        else
            return FLOAT_QUOTE_TYPE;
    }

    public Symbol getSymbol() throws EvaluationException {
	try { 
	    StringExpression symbolExpression = (StringExpression)getChild(0);
	    Symbol symbol = Symbol.find(symbolExpression.getText());
	    return symbol;
	} catch (SymbolFormatException e) {
	    throw new EvaluationException(Locale.getString("SYMBOL_NOT_FOUND"));
	}
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

	try {
	    Symbol explicitSymbol = getSymbol();
	    
	    return quoteBundle.getQuote(explicitSymbol, 
					getQuoteKind(), 
					day, 
					0);
	} catch(MissingQuoteException e) {
	    // What should I do in this case?
	    String message = symbol + " : " + Locale.getString("NO_QUOTES_DATE", quoteBundle.offsetToDate(day).toString());
	    
	    throw new EvaluationException(message);
	    //return 0.0D;
	}
    }

    public String toString() {
        switch(quoteKind) {
        case Quote.DAY_OPEN:
            return "open";
        case Quote.DAY_CLOSE:
            return "close";
        case Quote.DAY_HIGH:
            return "high";
        case Quote.DAY_LOW:
            return "low";
        default:
            assert quoteKind == Quote.DAY_VOLUME;
            return "volume";
        }
    }

    public Object clone() {
        return new QuoteSymbolExpression(quoteKind, (Expression)getChild(0).clone());
    }

    public int checkType() {
	return getType();
    }

}
