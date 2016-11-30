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

package nz.org.venice.analyser;

import java.util.*;

import nz.org.venice.portfolio.*;
import nz.org.venice.quote.*;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;

public class PaperTradeResult {
    private Portfolio portfolio;
    private EODQuoteBundle quoteBundle;
    private Money initialCapital;
    private Money tradeCost;
    private String buyRule;
    private String sellRule;
    private int a;
    private int b;
    private int c;
    private TradingDate startDate;	
    private TradingDate endDate;
    private String tip;

    public PaperTradeResult(Portfolio portfolio, EODQuoteBundle quoteBundle,
                            Money initialCapital, Money tradeCost,
                            String buyRule, String sellRule,
                            int a, int b, int c,
                            TradingDate startDate,
                            TradingDate endDate,
                            String tip) {
        this.portfolio = portfolio;
        this.quoteBundle = quoteBundle;
        this.initialCapital = initialCapital;
        this.tradeCost = tradeCost;
        this.buyRule = buyRule;
        this.sellRule = sellRule;
        this.a = a;
        this.b = b;
        this.c = c;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tip = tip;
    }

    public TradingDate getStartDate() {
        return startDate;
    }

    public TradingDate getEndDate() {
        return endDate;
    }

    public String getSymbols() {
        List symbolsTraded = getPortfolio().getSymbolsTraded();
        
        String string = new String();
        Iterator iterator = symbolsTraded.iterator();
        while(iterator.hasNext()) {
            Symbol symbol = (Symbol)iterator.next();
            
            if(string.length() > 0)
                string = string.concat(", " + symbol.toString());
            else
                string = symbol.toString();
        }
        
        return string;
    }

    public String getBuyRule() {
        return buyRule;
    }

    public String getSellRule() {
        return sellRule;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public Money getTradeCost() {
        return tradeCost;
    }

    public int getNumberTrades() {
        int accumulateTrades = 
            getPortfolio().countTransactions(Transaction.ACCUMULATE);
        int reduceTrades =
            getPortfolio().countTransactions(Transaction.REDUCE);
        
        return accumulateTrades + reduceTrades;
    }

    public Money getInitialCapital() {
        return initialCapital;
    }

    public Money getFinalCapital() {
        try {
            return portfolio.getValue(getQuoteBundle(), getEndDate());
        }
        catch(MissingQuoteException e) {
            // Already checked...
            assert false;
            return Money.ZERO;
        }
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public EODQuoteBundle getQuoteBundle() {
        return quoteBundle;
    }
    
    public String getTip() {
        return tip;
    }
}
