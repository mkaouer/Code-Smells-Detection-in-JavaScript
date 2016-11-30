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

package org.mov.ui;

import java.util.SortedSet;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import org.mov.quote.Symbol;
import org.mov.quote.SymbolFormatException;
import org.mov.util.Locale;

/**
 * A set of dialogs used for querying the user for commodities either
 * by name or symbol.
 */
public class SymbolListDialog {

    private SymbolListDialog() {
	// Cannot instantiate this class
    }

    /**
     * Open a new <code>SymbolListDialog</code> dialog. Ask the user
     * to enter a partial name of a symbol and return the appropriate
     * symbol.
     *
     * @param	parent	the parent desktop
     * @param	title	the title of the dialog
     * @return	a sorted set containing a single commodity string or
     * <code>null</code> if the user cancelled the dialog
     */
    /*
    public static SortedSet getSymbolByName(JDesktopPane parent, 
					    String title) {
	SortedSet symbolSet;
	String symbolName;
	boolean invalidResponse;

	do {
	    symbolSet = null;
	    symbolName = "";
	    invalidResponse = false; // assume user does OK

	    // First prompt user for list
	    TextDialog dlg = new TextDialog(parent, 
					    "Please enter symbol name",
                                            title);

	    symbolName = dlg.showDialog();
	    
	    // Parse what the user inputed
	    if(symbolName != null) {
		String symbol = 
		    QuoteSourceManager.getSource().getSymbol(symbolName);
		
		// Not recognised?
		if(symbol == null) {
		    String noData = 
			"No match for '" + symbol + "'";

		    JOptionPane.
			showInternalMessageDialog(parent, noData, 
						  "Unknown symbol",
						  JOptionPane.ERROR_MESSAGE);
		    invalidResponse = true;
		}

		// Recognised! Build symbol set
		else {
		    symbolSet = new TreeSet();
		    symbolSet.add(symbol);
		}
	    }

	    // Keep going while user hasnt entered a valid symbol and
	    // is selecting "ok"
	} while(invalidResponse); 

	// Return either null for no symbol selected or a set of one
	if(symbolSet != null && symbolSet.size() == 0)
	    return null;
	
	return symbolSet;
    }
    */

    /**
     * Open a new <code>SymbolListDialog</code> dialog. Ask the user
     * to enter a single symbol. It will test to make sure it is
     * a valid symbol.
     *
     * @param	parent	the parent desktop
     * @param	title	the title of the dialog
     * @return	a symbol or <code>null</code> if the user cancelled
     *          the dialog.
     */
    public static Symbol getSymbol(JDesktopPane parent, String title) {
        Symbol symbol;
	String symbolString;
	boolean invalidResponse;

	do {
	    symbol = null;
	    symbolString = "";
	    invalidResponse = false; // assume user does OK

	    // First prompt user for symbol
	    TextDialog dialog = new TextDialog(parent, 
                                               Locale.getString("SYMBOL"),
                                               title);
	    symbolString = dialog.showDialog();
            
	    // Parse what the user inputed
	    if(symbolString != null) {
		
                // Parse
                try {
                    symbol = Symbol.toSymbol(symbolString);
                }
                catch(SymbolFormatException e) {
                    invalidResponse = true;

                    JOptionPane.showInternalMessageDialog(parent, 
                                                          e.getMessage(),
                                                          Locale.getString("ERROR_PARSING_SYMBOL"),
                                                          JOptionPane.ERROR_MESSAGE);
                }
	    }

	    // Keep going while user hasnt entered a valid symbol and
	    // is selecting "ok"
	} while(invalidResponse); 

	return symbol;
    }

    /**
     * Open a new <code>SymbolListDialog</code> dialog. Ask the user
     * to enter a list of symbol symbols. It will test to make each is
     * a valid symbol.
     *
     * @param	parent	the parent desktop
     * @param	title	the title of the dialog
     * @return	a sorted set containing at least one symbol symbol string or
     * <code>null</code> if the user cancelled the dialog
     */
    public static SortedSet getSymbols(JDesktopPane parent, 
				       String title) {
	SortedSet symbolSet;
	String symbols;
	boolean invalidResponse;

	do {
	    symbolSet = null;
	    symbols = "";
	    invalidResponse = false; // assume user does OK

	    // First prompt user for list
	    TextDialog dlg = new TextDialog(parent, 
					    Locale.getString("SYMBOLS"),
                                            title);
	    symbols = dlg.showDialog();
					    
	    // Parse what the user inputed
	    if(symbols != null) {
		
                // Parse
                try {
                    symbolSet = Symbol.toSortedSet(symbols, true);
                }
                catch(SymbolFormatException e) {
                    invalidResponse = true;

                    JOptionPane.showInternalMessageDialog(parent, 
                                                          e.getMessage(),
                                                          Locale.getString("ERROR_PARSING_SYMBOLS"),
                                                          JOptionPane.ERROR_MESSAGE);
                }
	    }

	    // Keep going while user hasnt entered a valid symbol and
	    // is selecting "ok"
	} while(invalidResponse); 

	// If the set is empty return a null pointer
	if(symbolSet != null && symbolSet.size() == 0)
	    return null;

	return symbolSet;
    }
}
