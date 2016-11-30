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

package nz.org.venice.prefs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;


import nz.org.venice.quote.EODQuoteCache;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 * Provides a preferences page to let the user view and set tuning
 * parameters.
 */
public class TuningPage extends JPanel implements PreferencesPage, ActionListener
{
    private JDesktopPane desktop;
    private JTextField maxCachedQuotesTextField;
    private JLabel currentCachedQuotesLabel;
    private JTextField maxCacheAgeTextField;
    private JCheckBox enableCacheExpiryButton;
    private JButton flushCacheButton;

    /**
     * Create a new tuning preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public TuningPage(JDesktopPane desktop) {
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

        int currentCachedQuotes = (EODQuoteCache.isInstantiated()?
                                   EODQuoteCache.getInstance().size() :
                                   0);

        currentCachedQuotesLabel = GridBagHelper.addLabelRow(borderPanel, 
                                  Locale.getString("CURRENT_CACHED_QUOTES"), 
                                  Integer.toString(currentCachedQuotes),
                                  gridbag, c);

        int maximumCachedQuotes = PreferencesManager.getMaximumCachedQuotes();
        maxCachedQuotesTextField = 
            GridBagHelper.addTextRow(borderPanel, 
                                     Locale.getString("MAXIMUM_CACHED_QUOTES"), 
                                     Integer.toString(maximumCachedQuotes),
                                     gridbag, c, 10);

	maxCachedQuotesTextField.setToolTipText(Locale.getString("TUNING_MAX_QUOTES_TOOLTIP"));

	boolean cacheExpires = PreferencesManager.getCacheExpiryEnabled();
	enableCacheExpiryButton = 
	    GridBagHelper.addCheckBoxRow(borderPanel, 
					 Locale.getString("CACHE_EXPIRY_ENABLED"), 
					 cacheExpires,
					 gridbag, c);

	enableCacheExpiryButton.setToolTipText(Locale.getString("TUNING_CACHE_EXPIRY_CHECK_TOOLTIP"));

	enableCacheExpiryButton.addActionListener(this);

	int maximumCacheAge = PreferencesManager.getCacheExpiryTime();
	maxCacheAgeTextField = 
	    GridBagHelper.addTextRow(borderPanel, 
                                     Locale.getString("MAXIMUM_CACHE_LIFETIME"), 
                                     Integer.toString(maximumCacheAge),
                                     gridbag, c, 10);

	maxCacheAgeTextField.setToolTipText(Locale.getString("TUNING_CACHE_EXPIRY_INTERVAL_TOOLTIP"));

	if (!cacheExpires) {
	    maxCacheAgeTextField.setEnabled(false);
	}

	flushCacheButton = 
	    GridBagHelper.addButtonRow(borderPanel, 
				       Locale.getString("FLUSH_CACHE"), 
                                     gridbag, c);

	flushCacheButton.setToolTipText(Locale.getString("TUNING_FLUSH_CACHE_TOOLTIP"));

	flushCacheButton.addActionListener(this);
	    

        quotesPanel.add(borderPanel, BorderLayout.NORTH);
        return quotesPanel;
    }

    public JComponent getComponent() {
	return this;
    }

    public String getTitle() {
	return Locale.getString("TUNING_PAGE_TITLE");
    }

    public void save() {
        int maximumCachedQuotes = 0;
	int maximumCacheAge = 60 * 8; //Default of 8 hours
	boolean cacheExpires = false;

        try {
            maximumCachedQuotes = Integer.parseInt(maxCachedQuotesTextField.getText());
	    maximumCacheAge = Integer.parseInt(maxCacheAgeTextField.getText());
        }
        catch(NumberFormatException e) {
            // ignore
        }

	cacheExpires = (enableCacheExpiryButton.isSelected()) ? true : false;

        if(maximumCachedQuotes > 0)
            PreferencesManager.putMaximumCachedQuotes(maximumCachedQuotes);

	PreferencesManager.putCacheExpiryEnabled(cacheExpires);
	if (maximumCacheAge > 0) 
	    PreferencesManager.putCacheExpiryTime(maximumCacheAge);
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == enableCacheExpiryButton) {
	    maxCacheAgeTextField.setEnabled(enableCacheExpiryButton.isSelected());
	}

	if (e.getSource() == flushCacheButton) {
	    int confirmed = JOptionPane.showConfirmDialog(this, 
							  Locale.getString("SURE_FLUSH_CACHE"), 
							  Locale.getString("SURE_FLUSH_CACHE"), 
							  JOptionPane.YES_NO_OPTION);

	    
	    if (confirmed == JOptionPane.YES_OPTION) {
		EODQuoteCache.expire();
		
		int currentCachedQuotes = (EODQuoteCache.isInstantiated()?
					   EODQuoteCache.getInstance().size() :
					   0);

		currentCachedQuotesLabel.
		    setText(Integer.toString(currentCachedQuotes));
	    }
	}
    }
}
