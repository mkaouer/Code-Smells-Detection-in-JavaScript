/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)
   This portion of code Copyright (C) 2004 Dan Makovec (venice@makovec.net)

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

/**
 * Data definition of symbol metadata, including if the symbol is an index.
 * May include other data in the future, such as shares on issue.
 * Venice presents index symbols slightly differently. 
 *
 * This data is currently set by the user.
 *
 * @author mhummel
 * @see Symbol
 * @see QuoteSource
 * @see IndexPreferencesPage
 */

package nz.org.venice.quote;

public class SymbolMetadata {
    
    private final Symbol symbol;
    private final String name;
    private final boolean index;

    /**
     * Construct a new index definition.
     * 
     * @param symbol The symbol (e.g. 'ASX', or 'DAX')
     * @param name   The name of the index (e.g. Australian Stock Exchange)
     * @param index The optional location or top level index (ie NASDAQ, the ASX)
     */

    public SymbolMetadata(Symbol symbol, String name, boolean index) {
	this.symbol = symbol;
	this.name = name;
	this.index = index;
    }

    public SymbolMetadata(String symbolString, String name, boolean index) {	
	Symbol newSymbol = null;
	try {
	    newSymbol = Symbol.find(symbolString);	    
	} catch (SymbolFormatException sfe) {
	    
	} finally {
	    
	}
	this.symbol = newSymbol;
	this.name = name;
	this.index = index;
    }
    
    /**
     * Return the symbol.
     * 
     * @return the symbol
     */

    public Symbol getSymbol() {
	return symbol;
    }

    /**
     * Return the name of the index.
     * 
     * @return name
     */

    public String getName() {
	return name;
    }

    /**
     * Return isIndex.
     * 
     * @return true if the symbol is an index.
     */

    public boolean isIndex() {
	return index;
    }

    public String toString() {
	return symbol.toString() + "," + name + "," + index;
    }

    

}