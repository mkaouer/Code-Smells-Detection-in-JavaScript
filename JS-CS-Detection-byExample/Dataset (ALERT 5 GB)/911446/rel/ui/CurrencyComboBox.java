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

package nz.org.venice.ui;

import nz.org.venice.util.Currency;

import java.util.Iterator;
import java.util.List;
import javax.swing.JComboBox;

/**
 * Extension of JComboBox used for displaying a list of available currencies in
 * the applciation. This ComboBox allows the user to select from a list of
 * supported currencies.
 *
 * @author Andrew Leppard
 */
public class CurrencyComboBox extends JComboBox {
    
    // List of currencies displayed in ComboBox
    private List currencyList;

    /**
     * Create a new Currency ComboBox with the given currency initialy selected.
     *
     * @param defaultCurrency the default selected currency
     */
    public CurrencyComboBox(Currency defaultCurrency) {
        super();
        
        currencyList = Currency.getAvailableCurrencies();
        for (Iterator iterator = currencyList.iterator(); iterator.hasNext();)
            addItem(iterator.next());

        // Select default currency
        setSelectedItem(defaultCurrency);
    }

    /**
     * Create a new Currency ComboBox. The currency for the default locale will initially
     * be selected.
     */
    public CurrencyComboBox() {
        this(Currency.getDefaultCurrency());
    }

    /**
     * Return the currency currently selected in the ComboBox.
     *
     * @return the currently selected currency.
     */
    public Currency getSelectedCurrency() {
        int index = getSelectedIndex();

        assert index < currencyList.size();
        return (Currency)currencyList.get(index);
    }
}
    
