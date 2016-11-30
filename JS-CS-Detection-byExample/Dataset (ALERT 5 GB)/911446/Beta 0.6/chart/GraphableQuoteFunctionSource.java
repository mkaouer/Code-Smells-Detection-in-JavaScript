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

package org.mov.chart;

import org.mov.parser.EvaluationException;
import org.mov.quote.QuoteFunctionSource;
import org.mov.util.TradingDate;

/**
 * Allow the {@link org.mov.quote.QuoteFunctions} package to use quotes directly 
 * from a {@link Graphable}. The following code, from
 * {@link org.mov.chart.graph.MovingAverageGraph} shows an example of how to call
 * a quote function from a graph:
 *
 * <pre>
 *      Graphable movingAverage = new Graphable();
 *      TradingDate date = (TradingDate)source.getStartX();
 *      GraphableQuoteFunctionSource quoteFunctionSource 
 *          = new GraphableQuoteFunctionSource(source, date, period);
 *
 *      for(Iterator iterator = source.iterator(); iterator.hasNext();) {
 *          date = (TradingDate)iterator.next();
 *          quoteFunctionSource.setStartDate(date);
 *
 *          try {
 *              double average = QuoteFunctions.avg(quoteFunctionSource, period);
 *              movingAverage.putY(date, new Double(average));
 *          }
 *          catch(EvaluationException e) {
 *          }
 *      }
 * </pre>
 * @author Andrew Leppard
 * @see org.mov.quote.QuoteFunctions
 */
public class GraphableQuoteFunctionSource implements QuoteFunctionSource {

    // The graphable containing the quotes
    private Graphable graphable;

    // The current date. The previous quotes for period number of days will
    // be accessed
    private TradingDate date;
 
    // Number of quote dates available to quote functions
    private int period;

    /**
     * Create a new quote function source that uses quotes directly from a
     * {@link Graphable}.
     *
     * @param graphable the graphable containing the quotes
     * @param date the current date, the previous quotes for period number of days
     *        will be accessed
     * @param period the number of quote dates available from this source
     */
    public GraphableQuoteFunctionSource(Graphable graphable, TradingDate date, int period) {
        this.graphable = graphable;
        this.date = date;
        this.period = period;
    }

    /**
     * Set the current date. Since quote functions use previous day's quote
     * values, this function source will provide the previous quotes for period
     * number of days. The date given here will be the most recent quote date available 
     * from the source.
     *
     * @param date the current date
     */
    public void setDate(TradingDate date) {
        this.date = date;
    }

    public double getValue(int index)
        throws EvaluationException {

	assert index >= 0 && index < period;
        
        TradingDate currentDate = date.previous(period - index - 1);
        Double value = graphable.getY(currentDate);

        /* Return the value on that date if we have one */
        if(value != null)
            return value.doubleValue();
        else
            return Double.NaN;
    }
}