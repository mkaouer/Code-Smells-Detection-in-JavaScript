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
import javax.swing.JComboBox;
import java.util.Vector;

import org.mov.quote.EODQuoteCache;
import org.mov.ui.GridBagHelper;
import org.mov.util.Locale;


/**
 * Provides a preferences page to let the user set chart  
 * parameters.
 */

/*
  Maybe these preferences belong in User Interface?
*/
public class ChartPreferencesPage extends JPanel implements PreferencesPage
{
    private JDesktopPane desktop;
    private JComboBox defaultChart;     
   
    /**
     * Create a new user interface preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public ChartPreferencesPage(JDesktopPane desktop) {
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

	Vector chartList = new Vector();

	String defaultC = PreferencesManager.getDefaultChart();
	if (defaultC == null) {
	    defaultC = "LINE_CHART";
	}
	
	chartList.add(Locale.getString(defaultC));
	
	/* Avoid duplicates in the list */
	if (defaultC.compareTo("LINE_CHART") != 0) {	
	    chartList.add(Locale.getString("LINE_CHART"));
	}  
	if (defaultC.compareTo("BAR_CHART") != 0) {
	    chartList.add(Locale.getString("BAR_CHART"));
	}
	if (defaultC.compareTo("CANDLE_STICK") != 0) {
	    chartList.add(Locale.getString("CANDLE_STICK"));
	}
	if (defaultC.compareTo("HIGH_LOW_BAR") != 0) {
	    chartList.add(Locale.getString("HIGH_LOW_BAR"));
	}
	if (defaultC.compareTo("POINT_AND_FIGURE") != 0) {
	    chartList.add(Locale.getString("POINT_AND_FIGURE"));
	}
	
        
        defaultChart = 
            GridBagHelper.addComboBox(borderPanel, 
                                     Locale.getString("CHART_DEFAULT"), 
                                     chartList,
                                     gridbag, c);
        quotesPanel.add(borderPanel, BorderLayout.NORTH);
        return quotesPanel;
    }

    public JComponent getComponent() {
	return this;
    }

    public String getTitle() {
	return Locale.getString("CHART_PREFS_PAGE_TITLE");
    }

    public void save() {
	String tmp = (String)defaultChart.getSelectedItem();
	String saveValue;


	//We want to save the Venice name of the chart 
	//and not a location specific one
 
	if (tmp.compareTo(Locale.getString("LINE_CHART")) == 0) {
	    saveValue = "LINE_CHART";
	} else if (tmp.compareTo(Locale.getString("BAR_CHART")) == 0) {
	    saveValue = "BAR_CHART";
	}  else if (tmp.compareTo(Locale.getString("CANDLE_STICK")) == 0) {
	    saveValue = "CANDLE_STICK";	    
	} else if (tmp.compareTo(Locale.getString("HIGH_LOW_BAR")) == 0) {
	    saveValue = "HIGH_LOW_BAR";
	} else if (tmp.compareTo(Locale.getString("POINT_AND_FIGURE")) == 0 ) {
	    saveValue = "POINT_AND_FIGURE";
	} else {
	    saveValue = "LINE_CHART";
	}

	PreferencesManager.putDefaultChart(saveValue);        
    }
}
