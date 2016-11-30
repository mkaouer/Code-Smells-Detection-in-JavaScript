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

package org.mov.prefs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mov.quote.EODQuoteCache;
import org.mov.ui.GridBagHelper;
import org.mov.util.Locale;

/**
 * Provides a preferences page to let the user set user interface
 * parameters.
 */
public class UserInterfacePage extends JPanel implements PreferencesPage
{
    private JDesktopPane desktop;
    private JTextField minDecimalDigitsTextField;
    private JTextField maxDecimalDigitsTextField;
   
    /**
     * Create a new user interface preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public UserInterfacePage(JDesktopPane desktop) {
	this.desktop = desktop;

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(createQuotesPanel());
    }
    
    private JPanel createQuotesPanel() {
        JPanel quotesPanel = new JPanel();
        quotesPanel.setLayout(new BorderLayout());
        JPanel borderPanel = new JPanel();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        borderPanel.setLayout(gridbag);
        
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        int minDecimalDigits = PreferencesManager.getMinDecimalDigits();
        int maxDecimalDigits = PreferencesManager.getMaxDecimalDigits();
        minDecimalDigitsTextField = 
            GridBagHelper.addTextRow(borderPanel, 
                                     Locale.getString("MIN_DECIMAL_DIGITS"), 
                                     Integer.toString(minDecimalDigits),
                                     gridbag, c, 10);
        maxDecimalDigitsTextField = 
            GridBagHelper.addTextRow(borderPanel, 
                                     Locale.getString("MAX_DECIMAL_DIGITS"), 
                                     Integer.toString(maxDecimalDigits),
                                     gridbag, c, 10);

        quotesPanel.add(borderPanel, BorderLayout.NORTH);
        return quotesPanel;
    }

    public JComponent getComponent() {
	return this;
    }

    public String getTitle() {
	return Locale.getString("USER_INTERFACE_PAGE_TITLE");
    }

    public void save() {
        int minDecimalDigits = 0;
        int maxDecimalDigits = 0;

        try {
            minDecimalDigits = Integer.parseInt(minDecimalDigitsTextField.getText());
            maxDecimalDigits = Integer.parseInt(maxDecimalDigitsTextField.getText());
        }
        catch(NumberFormatException e) {
            // ignore
        }

        if(minDecimalDigits > 0) {
            PreferencesManager.putMinDecimalDigits(minDecimalDigitsTextField.getText());
        }
        if(maxDecimalDigits > 0) {
            PreferencesManager.putMaxDecimalDigits(maxDecimalDigitsTextField.getText());
        }
    }
}
