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

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.mov.quote.*;
import org.mov.util.*;

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
					    "Please enter symbol(s)",
                                            title);
	    symbols = dlg.showDialog();
					    
	    // Parse what the user inputed
	    if(symbols != null) {
		
                // Get the set of symbols and check to see if they
                // all exist
                symbolSet = checkSymbols(parent, symbols);

                if(symbolSet == null)
                    invalidResponse = true;
	    }

	    // Keep going while user hasnt entered a valid symbol and
	    // is selecting "ok"
	} while(invalidResponse); 

	// If the set is empty return a null pointer
	if(symbolSet != null && symbolSet.size() == 0)
	    return null;

	return symbolSet;
    }

    /**
     * This function takes a string containing a comma or space
     * separated list of symbols and turns it into a sorted list of
     * strings containing each symbol. It will then check to make
     * sure that each symbol exists.
     *
     * If any symbols do not exist in the quote source, it will display
     * a dialog to the user informing them of the error.
     *
     * @param parent the parent component used for displaying the error
     *               dialog
     * @param symbols space or comma separated list of symbols
     * @return sorted set of symbol strings or <code>null</code> if there
     *         was an error
     */
    public static SortedSet checkSymbols(JComponent parent, String symbols) {
        // Convert string to sorted set of symbols        
        SortedSet symbolSet = Converter.stringToSortedSet(symbols);
	String symbol;
        Iterator iterator = symbolSet.iterator();
	String unknownSymbols = new String();
        
        while(iterator.hasNext()) {
            symbol = (String)iterator.next();
            
            // See if symbol exists
            if(!QuoteSourceManager.getSource().symbolExists(symbol)) {
                
                // Add to list of symbols we don't know
                if(unknownSymbols.length() > 0)
                    unknownSymbols = unknownSymbols.concat(" ");
                
                unknownSymbols = unknownSymbols.concat(symbol);
                
                // Remove symbol from set of valid symbols the user
                // has entered
                iterator.remove();
            }
        }

        // If there was any unknown symbols put up a message dialog
        // telling the user which ones were unknown
        if(unknownSymbols.length() > 0) {
            String noData = 
                "No data available for symbol(s) '" + 
                unknownSymbols + 
                "'";
            
            JOptionPane.
                showInternalMessageDialog(parent, noData, 
                                          "Unknown symbol(s)",
                                          JOptionPane.ERROR_MESSAGE);

            // Return null indicating there was at least one
            // unknown symbol
            symbolSet = null;
        }
        
        return symbolSet;
    }
}
