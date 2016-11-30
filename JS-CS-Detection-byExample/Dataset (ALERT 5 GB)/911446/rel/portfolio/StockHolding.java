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

package nz.org.venice.portfolio;

import nz.org.venice.quote.Symbol;
import nz.org.venice.util.TradingDate;

/**
 * Representation of a single stock holding in a share account. 
 *
 * @author Andrew Leppard
 * @see ShareAccount
 */
public class StockHolding {

    // Stock held
    private Symbol symbol;

    // Number of shares of stock
    private int shares;
    
    // Date shares were purchased
    private TradingDate date;

    // Average cost per share
    private double cost;

    /**
     * Create a new stock holding
     *
     * @param	symbol	the stock to own
     * @param	shares	the number of shares of that stock
     * @param   cost    average cost per share
     * @param   date    the date the shares were purchased
     */
    public StockHolding(Symbol symbol, int shares, double cost, TradingDate date) {
	this.symbol = symbol;
	this.shares = shares;
        this.cost = cost;
        this.date = date;
    }

    /**
     * Increase ownership of stock.
     *
     * @param	shares	number of new shares to accumulate
     * @param   cost    average cost of shares
     */
    public void accumulate(int shares, double cost) {
        this.cost = (this.shares * this.cost + shares * cost) / (this.shares + shares);
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
    public Symbol getSymbol() {
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

    /**
     * Get average cost per share
     *
     * @return	average cost per share
     */
    public double getCost() {
	return cost;
    }

    /**
     * Get the initial date that at least part of these shares was purchased
     * on.
     *
     * @return date
     */
    public TradingDate getDate() {
        return date;
    }

    /**
     * Compares this stock holding to another.
     *
     * @param object another stock holding
     * @return <code>true</code> if the stock holdings are equal; <code>false</code> otherwise
     */
    public boolean equals(Object object) {
        StockHolding holding = (StockHolding)object;
        return(holding.getSymbol().equals(getSymbol()) &&
               holding.getShares() == getShares() &&
               holding.getCost() == getCost() &&
               holding.getDate().equals(getDate()));
    }
}
