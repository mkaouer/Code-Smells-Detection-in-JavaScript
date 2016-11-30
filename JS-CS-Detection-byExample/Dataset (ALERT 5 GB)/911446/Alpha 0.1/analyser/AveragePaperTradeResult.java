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

package org.mov.analyser;

import org.mov.portfolio.*;
import org.mov.quote.*;
import org.mov.util.*;

public class AveragePaperTradeResult implements PaperTradeResult {
    private ScriptQuoteBundle quoteBundle;
    private String symbols;
    private float initialCapital;
    private float finalCapital;
    private float tradeCost;
    private int numberTrades;
    private String buyRule;
    private String sellRule;
    private TradingDate startDate;	
    private TradingDate endDate;
    
    public AveragePaperTradeResult(ScriptQuoteBundle quoteBundle, String symbols,
                                   float initialCapital, float finalCapital, 
                                   float tradeCost, int numberTrades,
                                   String buyRule, String sellRule,
                                   TradingDate startDate,
                                   TradingDate endDate) {
        this.quoteBundle = quoteBundle;
        this.symbols = symbols;
        this.initialCapital = initialCapital;
        this.finalCapital = finalCapital;
        this.numberTrades = numberTrades;
        this.tradeCost = tradeCost;
        this.buyRule = buyRule;
        this.sellRule = sellRule;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Portfolio getPortfolio() {
        return null;
    }

    public ScriptQuoteBundle getQuoteBundle() {
        return quoteBundle;
    }

    public TradingDate getStartDate() {
        return startDate;
    }

    public TradingDate getEndDate() {
        return endDate;
    }

    public String getSymbols() {
        return symbols;
    }

    public String getBuyRule() {
        return buyRule;
    }

    public String getSellRule() {
        return buyRule;
    }

    public float getTradeCost() {
        return tradeCost;
    }

    public int getNumberTrades() {
        return numberTrades;
    }

    public float getInitialCapital() {
        return initialCapital;
    }

    public float getFinalCapital() {
        return finalCapital;
    }
}
