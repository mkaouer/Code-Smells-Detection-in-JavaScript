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
 * This interface describes the quote source for the quote functions
 * in {@link QuoteFunctions}. That class is used by both Gondola language 
 * expressions and charting functions, so it needs to accept quotes in multiple 
 * forms. This interface enables the quote function class to be unaware of 
 * this difference.
 *
 * @author Andrew Leppard
 * @see QuoteFunctions
 */
public interface QuoteFunctionSource {

    /**
     * Return the quote value on the given date. The quote function source
     * contains a set of quotes which can be accessed from offset 0, which
     * is the earliest date, to the number of quotes in the source minus one.
     * Typically a quote source is set up to contain a fixed number of quotes,
     * each of which is used by a quote function.
     *
     * @param  offset the offset of the date in the quote source.
     * @return the quote value or <code>NaN</code> if the quote is missing / not available
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link nz.org.venice.analyser.gp.GPQuoteBundle}.
     */
    public double getValue(int offset)
        throws EvaluationException;
}