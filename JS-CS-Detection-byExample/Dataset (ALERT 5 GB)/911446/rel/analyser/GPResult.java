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

import java.util.List;
import java.util.Iterator;

import nz.org.venice.analyser.gp.Individual;
import nz.org.venice.portfolio.Transaction;
import nz.org.venice.portfolio.Portfolio;
import nz.org.venice.quote.*;
import nz.org.venice.util.Money;
import nz.org.venice.util.TradingDate;

public class GPResult {
    private Individual individual;
    private EODQuoteBundle quoteBundle;
    private Money initialCapital;
    private Money tradeCost;
    private int generation;
    private TradingDate startDate;	
    private TradingDate endDate;
    
    public GPResult(Individual individual, EODQuoteBundle quoteBundle,
                    Money initialCapital, Money tradeCost,
                    int generation,
                    TradingDate startDate,
                    TradingDate endDate) {
        this.individual = individual;
        this.quoteBundle = quoteBundle;
        this.initialCapital = initialCapital;
        this.tradeCost = tradeCost;
        this.generation = generation;
        this.startDate = startDate;
        this.endDate = endDate;
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
        return individual.getBuyRule().toString();
    }

    public String getSellRule() {
        return individual.getSellRule().toString();
    }

    public int getGeneration() {
        return generation;
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
        Money finalCapital = Money.ZERO;
        
        try {
            finalCapital = getPortfolio().getValue(getQuoteBundle(), getEndDate());
        }
        catch(MissingQuoteException e) {
            // Already checked...
            assert false;
        }
        
        return finalCapital;
    }

    public Portfolio getPortfolio() {
        return individual.getPortfolio();
    }

    public EODQuoteBundle getQuoteBundle() {
        return quoteBundle;
    }
}
