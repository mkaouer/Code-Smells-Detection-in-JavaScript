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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides a list of quote filters available to us. This class is used
 * as a single point of reference to find and use all of the available
 * quote filters. 
 * Example:
 * <pre>
 * EODQuoteFilter filter = 
 *	EODQuoteFilterList.getInstance().getFilter("Insight Trader");
 * EODQuote quote = filter.filter("XXX 07/15/99 173 182 171 181 36489");
 * </pre>
 * OR
 * <pre>
 * List filters = EODQuoteFilterList.getInstance().getList();
 * </pre>
 */
public class EODQuoteFilterList {

    private static EODQuoteFilterList instance = null;

    private List filters;

    // Creates new instance of the filter list - registers all available
    // filters.
    private EODQuoteFilterList() {
	filters = new ArrayList();

	filters.add(new EzyChartQuoteFilter());
	filters.add(new InsightTraderQuoteFilter());
	filters.add(new MetaStockQuoteFilter());
	filters.add(new MetaStock2QuoteFilter());
	filters.add(new MetastockBinaryEODFilter());
    }

    /**
     * Return instance of filter given its name. 
     *
     * @param	filterName	the registered name of the filter.
     * @return	instance of the filter.
     */
    public IFileEODQuoteFilter getFilter(String filterName) {
	Iterator iterator = filters.iterator();
	IFileEODQuoteFilter filter;

	while(iterator.hasNext()) {
	    filter = (IFileEODQuoteFilter)iterator.next();

	    if(filter.getName().equals(filterName))
		return filter;
	}
	
	// String did not match any filter! Default to first
	return (IFileEODQuoteFilter)filters.get(0);
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return	the singleton instance.
     */
    public static synchronized EODQuoteFilterList getInstance() {
	if(instance == null) {
	    instance = new EODQuoteFilterList();
	}
	return instance;
    }

    /**
     * Get a vector of available filter classes.
     *
     * @return	list of classes implementing EODQuoteFilter.
     */
    public List getList() {
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
	IFileEODQuoteFilter filter;

	while(iterator.hasNext()) {
	    filter = (IFileEODQuoteFilter)iterator.next();

	    if(filter.getName().equals(filterName))
		return true;
	}

	return false;
    }
}
