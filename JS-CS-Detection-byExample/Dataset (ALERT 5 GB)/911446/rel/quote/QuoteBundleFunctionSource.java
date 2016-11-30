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

package nz.org.venice.quote;

import nz.org.venice.parser.EvaluationException;

/**
 * Allow the {@link QuoteFunctions} package to use quotes directly from
 * a {@link nz.org.venice.quote.QuoteBundle}. The following code shows an exmaple of how to
 * call a quote function from a Gondola expression:
 * 
 * <pre>
 *      QuoteBundleFunctionSource source = 
 *          new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_CLOSE, day, offset,
 *                                        period);
 *      return QuoteFunctions.rsi(source, period);
 * </pre>
 *
 * @author Andrew Leppard
 * @see QuoteFunctions
 * @see nz.org.venice.parser.Expression
 */
public class QuoteBundleFunctionSource implements QuoteFunctionSource {

    // The quote bundle containing the quotes
    private QuoteBundle quoteBundle;

    // The symbol of the quotes to access
    private Symbol symbol;

    // The quote kind, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
    // {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
    private int quoteKind;

    // The fields day plus offset construct the offset of the current date in
    // the quote bundle we are examining. This will be the last date accessed
    // by the quote function.
    private int day;
    private int offset;

    // Number of quote dates available from this source
    private int period;

    /**
     * Create a new quote function source that uses quotes directly from a 
     * {@link nz.org.venice.quote.QuoteBundle}.
     *
     * @param quoteBundle the quote bundle containing the quotes
     * @param symbol the symbol of the quotes to access
     * @param quoteKind the quote kind, one of {@link Quote#DAY_OPEN}, {@link Quote#DAY_CLOSE},
     *                  {@link Quote#DAY_LOW}, {@link Quote#DAY_HIGH}, {@link Quote#DAY_VOLUME}
     * @param day the day and offset arguments construct the offset of the current date in
     *            the quote bundle we are examining. This will be the last date accessed
     *            by the quote function.
     * @param offset see above
     * @param period the number of quote dates available from this source
     */
    public QuoteBundleFunctionSource(QuoteBundle quoteBundle, Symbol symbol, int quoteKind, 
				     int day, int offset, int period) {
	this.quoteBundle = quoteBundle;
        this.symbol = symbol;
	this.quoteKind = quoteKind;
	this.day = day;
	this.offset = offset;
        this.period = period;
    }
    
    public double getValue(int index)
        throws EvaluationException {

	assert index >= 0 && index < period;

	try {
	    return quoteBundle.getQuote(symbol, quoteKind, day, index - period + offset + 1);
	}
	catch(MissingQuoteException e) {
	    return Double.NaN;
	}
    }
}