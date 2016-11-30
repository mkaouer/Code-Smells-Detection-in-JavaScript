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

/**
 * Representation of a single stock holding in a share account. 
 * @see ShareAccount
 */
public class StockHolding {

    // Stock held
    private String symbol;

    // Number of shares of stock
    private int shares;
    
    /**
     * Create a new stock holding
     *
     * @param	symbol	the stock to own
     * @param	shares	the number of shares of that stock
     */
    public StockHolding(String symbol, int shares) {
	this.symbol = symbol;
	this.shares = shares;
    }

    /**
     * Increase ownership of stock.
     *
     * @param	shares	number of new shares to accumulate
     */
    public void accumulate(int shares) {
	this.shares += shares;
    }

    /**
     * Decrease ownership of stock.
     *
     * @param	shares	number of shares to reduce
     */
    public void reduce(int shares) {
	this.shares -= shares;
    }

    /**
     * Get symbol of stock held.
     *
     * @return	symbol
     */
    public String getSymbol() {
	return symbol;
    }

    /**
     * Get number of shares held.
     *
     * @return	number of shares
     */
    public int getShares() {
	return shares;
    }
}
