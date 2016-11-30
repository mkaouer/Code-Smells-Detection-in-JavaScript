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
import javax.swing.border.TitledBorder;

import org.mov.quote.QuoteCache;
import org.mov.ui.GridBagHelper;

public class TuningPage extends JPanel implements PreferencesPage
{
    private JDesktopPane desktop;
    private JTextField maxCachedQuotesTextField;
    
    public TuningPage(JDesktopPane desktop) {
	this.desktop = desktop;

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new TitledBorder(getTitle()));

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

        int currentCachedQuotes = (QuoteCache.isInstantiated()?
                                   QuoteCache.getInstance().size() :
                                   0);
        GridBagHelper.addLabelRow(borderPanel, 
                                  "Current Cached Quotes", 
                                  Integer.toString(currentCachedQuotes),
                                  gridbag, c);

        int maximumCachedQuotes = PreferencesManager.loadMaximumCachedQuotes();
        maxCachedQuotesTextField = 
            GridBagHelper.addTextRow(borderPanel, 
                                     "Maximum Cached Quotes", 
                                     Integer.toString(maximumCachedQuotes),
                                     gridbag, c, 10);

        quotesPanel.add(borderPanel, BorderLayout.NORTH);
        return quotesPanel;
    }

    public JComponent getComponent() {
	return this;
    }

    public String getTitle() {
	return "Tuning";
    }

    public void save() {
        int maximumCachedQuotes = 0;

        try {
            maximumCachedQuotes = Integer.parseInt(maxCachedQuotesTextField.getText());
        }
        catch(NumberFormatException e) {
            // ignore
        }

        if(maximumCachedQuotes > 0)
            PreferencesManager.saveMaximumCachedQuotes(maximumCachedQuotes);
    }
}
