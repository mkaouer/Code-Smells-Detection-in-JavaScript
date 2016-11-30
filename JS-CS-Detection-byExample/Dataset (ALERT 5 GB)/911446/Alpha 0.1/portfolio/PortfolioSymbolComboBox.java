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

package org.mov.portfolio;

import java.util.*;
import javax.swing.*;

import org.mov.util.*;

/**
 * A combo box which displays all the symbols traded in a portfolio. Currently
 * ignores the date parameter given.
 *
 * @see Portfolio
 */
public class PortfolioSymbolComboBox extends JComboBox
{
    Portfolio portfolio;

    /**
     * Creates a new combo box listing all the symbols held in the portfolio on
     * the given date.
     *
     * @param portfolio the portfolio
     * @param date the date
     */
    public PortfolioSymbolComboBox(Portfolio portfolio, TradingDate date) {
	super();

	this.portfolio = portfolio;

	setDate(date);
    }

    /** 
     * List symbols held in the current portfolio on the new date.
     *
     * @param date the new date
     */
    public void setDate(TradingDate date) {
	removeAllItems();
	
	Vector symbols = portfolio.getSymbolsTraded();
	Iterator iterator = symbols.iterator();
	while(iterator.hasNext()) {
            String symbol = (String)iterator.next();
            symbol = symbol.toUpperCase();
	    addItem(symbol);
	}
    }
}
