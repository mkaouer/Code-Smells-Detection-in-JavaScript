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

import org.mov.prefs.*;
import org.mov.quote.*;
import org.mov.util.*;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class QuoteRangeComboBox extends JComboBox {

    // Drop down menu choices 
    private final static String ALL_ORDINARIES = "All Ordinaries";
    private final static String ALL_SYMBOLS    = "All Symbols";
    private final static String MARKET_INDICES = "Market Indices";

    public QuoteRangeComboBox() {
	this(new String(""));
    }

    public QuoteRangeComboBox(String equationText) {
	super();

        setEditable(true);
        updateItems();
        setSelectedItem(equationText);

        // The combo box must be big enough to hold this text. This makes it
        // as wide as the equation combo box. Yes but on 1.4.1 it makes them short!
	//setPrototypeDisplayValue("avg(day_close, 15, 15) > 121");
    }
    
    public QuoteRange getQuoteRange() {
        String text = getText();
        
        if(text.equals(ALL_ORDINARIES))
            return new QuoteRange(QuoteRange.ALL_ORDINARIES);
        else if(text.equals(ALL_SYMBOLS))
            return new QuoteRange(QuoteRange.ALL_SYMBOLS);
        else if(text.equals(MARKET_INDICES))
            return new QuoteRange(QuoteRange.MARKET_INDICES);
        else if(text == null)
            return new QuoteRange(QuoteRange.ALL_ORDINARIES);
        else {
            // Convert the text string to a sorted set of symbol
            // strings and also check to see if they exist
            SortedSet symbols = SymbolListDialog.checkSymbols(this, text);

            // If it returned empty there was an error...
            if(symbols == null || symbols.size() == 0)
                return null;
            else
                return new QuoteRange(new Vector(symbols));
        }
    }

    public String getText() {
        return (String)getSelectedItem();
    }

    public void setText(String text) {
        setSelectedItem(text);
    }

    // Rebuild option items in this combo box
    private void updateItems() {
        removeAllItems();
        addItem(ALL_ORDINARIES);
        addItem(ALL_SYMBOLS);
        addItem(MARKET_INDICES);
    }
}
