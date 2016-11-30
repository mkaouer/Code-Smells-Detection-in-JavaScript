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
 * Abstract base class for expressions dealing with quotes:
 * <code>avg, lag, max, min</code>
 */
abstract public class QuoteExpression extends Expression {

    // Quote kind - one of: open, close, low, high
    private int quoteKind;

    /**
     * Create a new quote expression with the given quote expression.
     * This expression should be either a {@link DayOpenExpression},
     * {@link DayCloseExpression}, {@link DayLowExpression},
     * {@link DayHighExpression} or a {@link DayVolumeExpression}.
     *
     * @param	quote	the quote expression
     */
    public QuoteExpression(Expression quote) {
	if(quote instanceof DayOpenExpression)
	    quoteKind = Quote.DAY_OPEN;
	else if(quote instanceof DayCloseExpression)
	    quoteKind = Quote.DAY_CLOSE;
	else if(quote instanceof DayLowExpression)
	    quoteKind = Quote.DAY_LOW;
	else if(quote instanceof DayHighExpression)
	    quoteKind = Quote.DAY_HIGH;
	else {
            assert quote instanceof DayVolumeExpression;
	    quoteKind = Quote.DAY_VOLUME;
	}
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
     * @return {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE}.
     */
    public int getType() {
        if(getQuoteKind() == Quote.DAY_VOLUME)
            return INTEGER_TYPE;
        else
            return FLOAT_TYPE;
    }

}
