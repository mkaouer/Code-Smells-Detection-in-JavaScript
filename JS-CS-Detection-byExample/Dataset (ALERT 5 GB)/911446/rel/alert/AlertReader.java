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


package nz.org.venice.alert;

import java.util.List;

import nz.org.venice.quote.Symbol;
import nz.org.venice.util.TradingDate;

/**
 * Provides a common interface to retrieve alerts. Allows AlertModule to display
 * alerts regardless of where they are stored.
 * 
 * @author Mark Hummel
 * @see Alert
 * @see AlertWriter 
 * @see AlertModule
 */

public interface AlertReader {
       
    /**
     * Get all alerts currently stored.
     * @return A list of all the alerts currently stored.
     */    
    public List getAlerts() throws AlertException;
    
    /**
     * Get all alerts currently stored for the symbol.
     * @param symbol The symbol to filter the alerts on.
     * @return A list of all the alerts for this symbol.
     */    
    public List getAlertsBySymbol(Symbol symbol) throws AlertException;

    
    /** Get all alerts currently stored for the given symbols.
     * @param symbols The symbols to filter the alerts on.
     * @return A list of all the alerts for this symbol.
     */    
    public List getAlertsBySymbolList(List symbols) throws AlertException;

}
