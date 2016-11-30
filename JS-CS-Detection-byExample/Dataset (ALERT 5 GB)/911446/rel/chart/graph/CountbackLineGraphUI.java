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

package nz.org.venice.chart.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 * 3 Bar Nett Count back line User Interface
 *
 * @author Mark Hummel
 * @see CountbackLineGraph
 */
public class CountbackLineGraphUI implements GraphUI {

    // The graph's user interface
    private JPanel panel;
    private JComboBox typeComboBox;

    // String name of settings
    private final static String TYPE = "type";
    private final static String QUOTE= "quote";
    
    // Default values
    private final static int DEFAULT_TYPE = CountbackLineGraph.BREAKOUT;
        
    /**
     * Create a new count back line user interface with the
     * initial settings.
     *
     * @param settings the initial settings
     */
    public CountbackLineGraphUI(HashMap settings) {
        buildPanel();
        setSettings(settings);
    }

    /**
     * Build the user interface JPanel.
     */
    private void buildPanel() {
        panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(layout);
	Vector options = new Vector();
	options.add(Locale.getString("CBL_BREAKOUT"));
	options.add(Locale.getString("CBL_STOP_LOSS"));
	
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
	
      	typeComboBox = GridBagHelper.addComboBox(panel, 
						 Locale.getString("CBL_TYPE"), 
						 options,
						 layout, c);
        
    }

    public String checkSettings(HashMap settings) {
        // Check type
        String typeString = (String)settings.get(TYPE);
        int type;

	if (typeString.compareTo(Locale.getString("CBL_BREAKOUT")) != 0 &&
	    typeString.compareTo(Locale.getString("CBL_STOP_LOSS")) != 0) {
	    	    

	    //It's a non editable drop down list, so shouldn't happen
	    assert false;

	}
	
	// Settings are OK
        return null;
    }

    public String checkSettings() {
        return checkSettings(getSettings());
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
	
        settings.put(TYPE, (String)typeComboBox.getSelectedItem());
	return settings;
    }

    
    public void setSettings(HashMap settings) {
	String type = (String)settings.get(TYPE);
	if (type != null && !type.equals("")) {
	    typeComboBox.setSelectedItem(type);
	}
    }
    

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Retrieve the period from the settings hashmap. If the hashmap
     * is empty, then return the default period.
     *
     * @param settings the settings
     * @return the period
     */
    public static int getType(HashMap settings) {
        int type = DEFAULT_TYPE;
        String typeString = (String)settings.get(TYPE);
	
	if (typeString != null) {

	    if (typeString.compareTo(Locale.getString("CBL_BREAKOUT")) == 0) {
		type = CountbackLineGraph.BREAKOUT;
	    } else {
		type = CountbackLineGraph.STOPLOSS;
	    }
	}

	return type;
    }

}