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

package org.mov.quote;

import java.util.*;

import org.mov.util.TradingDate;

/**
 * Provides a list of quote filters available to us. This class is used
 * as a single point of reference to find and use all of the available
 * quote filters. 
 * Example:
 * <pre>
 * QuoteFilter filter = 
 *	QuoteFilterList.getInstance().getFilter("Insight Trader");
 * Quote quote = filter.filter("XXX 07/15/99 173 182 171 181 36489");
 * </pre>
 * OR
 * <pre>
 * Vector filters = QuoteFilterList.getInstance().getList();
 * </pre>
 */
public class QuoteFilterList {

    private static QuoteFilterList instance = null;

    private Vector filters;

    // Creates new instance of the filter list - registers all available
    // filters.
    private QuoteFilterList() {
	filters = new Vector();

	filters.add(new EzyChartQuoteFilter());
	filters.add(new InsightTraderQuoteFilter());
	filters.add(new MetaStockQuoteFilter());
	filters.add(new MetaStock2QuoteFilter());
    }

    /**
     * Return instance of filter given its name. 
     *
     * @param	filterName	the registered name of the filter.
     * @return	instance of the filter.
     */
    public QuoteFilter getFilter(String filterName) {
	Iterator iterator = filters.iterator();
	QuoteFilter filter;

	while(iterator.hasNext()) {
	    filter = (QuoteFilter)iterator.next();

	    if(filter.getName().equals(filterName))
		return filter;
	}
	
	// String did not match any filter! Default to first
	return (QuoteFilter)filters.firstElement();
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return	the singleton instance.
     */
    public static QuoteFilterList getInstance() {
	if(instance == null) {
	    instance = new QuoteFilterList();
	}
	return instance;
    }

    /**
     * Get a vector of available filter classes.
     *
     * @return	vector of classes implementing QuoteFilter.
     */
    public Vector getList() {
	return filters;
    }

    /**
     * Is the given filter name a valid filter?
     *
     * @param	filterName	the name of the possible filter.
     * @return	true if the name is a valid filter.
     */
    public boolean isFilter(String filterName) {
	Iterator iterator = getList().iterator();
	QuoteFilter filter;

	while(iterator.hasNext()) {
	    filter = (QuoteFilter)iterator.next();

	    if(filter.getName().equals(filterName))
		return true;
	}

	return false;
    }

    /**
     * Perform unit tests on the filters to make sure they are reading and
     * writing properly.
     */
    /*
    public static void main(String[] args) {
	QuoteFilterList filters = new QuoteFilterList();
	Vector list = filters.getList();
	Iterator iterator = list.iterator();
	QuoteFilter filter;
	Quote quote = new Quote(new Symbol("AAA"), new TradingDate(), 10000,
				10.00F, 20.00F, 30.00F, 40.00F);
	Quote filteredQuote;
	String filteredString;

	// For each filter, convert the quote to text then back to
	// the quote and make sure they match.
	while(iterator.hasNext()) {
	    filter = (QuoteFilter)iterator.next();

	    filteredString = filter.toString(quote);
	    filteredQuote = filter.toQuote(filteredString);

	    if(filteredQuote.equals(quote))
		System.out.println("PASS: " + filter.getName() + " " +
				   filteredString);
	    else {
		System.out.println("FAIL: " + filter.getName()); 
		System.out.println("> " + quote.toString());
		System.out.println("< " + filteredQuote.toString());
	    }
	}       
    }
    */
}
