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

package nz.org.venice.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.org.venice.quote.Symbol;

/** 
 * Representation of a watch screen. A watch screen contains a list of
 * stock symbols that the user has grouped together to monitor. Each watch
 * screen also has its own name.
 *
 * @author Andrew Leppard
 * @see Symbol
 */
public class WatchScreen {

    private String name;
    private List symbols;
    
    /**
     * Create a new watch screen with the given name.
     *
     * @param name the name of the watch screen.
     */
    public WatchScreen(String name) {
        this.name = name;
        symbols = new ArrayList();
    }

    /**
     * Get the name of the watcch screen.
     *
     * @return the name of the watch screen.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the watch screen to the new name.
     *
     * @param name the new name of the watch screen.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the list of stock symbols in this watch screen.
     *
     * @return list of symbols
     * @see Symbol
     */
    public List getSymbols() {
        return symbols;
    }

    /**
     * Add a symbol to this watch screen.
     *
     * @param symbol the symbol to add.
     */
    public void addSymbol(Symbol symbol) {
        if(!symbols.contains(symbol))
            symbols.add(symbol);
    }

    /**
     * Add a list of symbols to this watch screen.
     *
     * @param symbols the list of symbols to add.
     */
    public void addSymbols(List symbols) {
        for(Iterator iterator = symbols.iterator(); iterator.hasNext();)
            addSymbol((Symbol)iterator.next());
    }

    /**
     * Remove the given symbol from this watch screen.
     *
     * @param symbol the symbol to remove.
     */
    public void removeSymbol(Symbol symbol) {
        boolean wasRemoved = symbols.remove(symbol);
        assert wasRemoved;
    }

    /**
     * Remove all the given symbols from this watch screen.
     *
     * @param symbols the list of symbols to remove.
     * @see Symbol
     */
    public void removeAllSymbols(List symbols) {
        boolean wasRemoved = this.symbols.removeAll(symbols);
        assert wasRemoved;
    }
}
