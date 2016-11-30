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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;


import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 * The Support and Resistence graph user interface.
 *
 * @author Mark Hummel
 * @see SupportAndResistenceGraph
 */
public class SRGraphUI implements GraphUI {

    // String name of settings
    public final static String LAG = "Lag";
    public final static String TYPE = "Type";
	
    // Limits
    private final static int MIN_LAG = 1;
    private final static int DEFAULT_LAG = 365;
    private final static int DEFAULT_TYPE = SupportAndResistenceGraph.HEURISTIC;

    // The graph's user interface
    private JPanel panel;
    private JPanel panelTextBoxes;
    // Details of lag
    private JTextField lagTextField;
    private JComboBox typeComboBox;

    /**
     * Create a new Support and Resistence user interface with the initial settings.
     *
     * @param settings the initial settings
     */
    public SRGraphUI(HashMap settings) {
        buildPanel();
        setSettings(settings);
    }

    /**
     * Build the user interface JPanel.
     */
    private void buildPanel() {
        panel = new JPanel();
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));        
        panelTextBoxes = new JPanel();

	Vector options = new Vector();
	options.add(Locale.getString("SR_HEURISTIC"));
	options.add(Locale.getString("SR_BINS"));
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panelTextBoxes.setLayout(layout);

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.SOUTH;
        
        lagTextField = GridBagHelper.addTextRow(panelTextBoxes, Locale.getString("PERIOD"), "",
						layout, c, 8);

	typeComboBox = GridBagHelper.addComboBox(panel, 
						 Locale.getString("SR_TYPE"), 
						 options,
						 layout, c);

	
        panelTextBoxes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelTextBoxes);
    }

    public String checkSettings() {
	return checkSettings(getSettings());
    }
    
    public String checkSettings(HashMap settings) {
        // Check lag + type
        String lagString = (String)settings.get(LAG);
	String typeString = (String)settings.get(TYPE);

        int period;

        try {
            period = Integer.parseInt(lagString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", lagString);
        }

        if (period < MIN_LAG)
            return Locale.getString("PERIOD_TOO_SMALL");
                
	if (typeString.compareTo(Locale.getString("SR_HEURISTIC")) != 0 &&
	    typeString.compareTo(Locale.getString("SR_BINS")) != 0) {
	    //It's a non editable drop down list, so shouldn't happen
	    assert false;
	}

        // Settings are OK
        return null;
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(LAG, lagTextField.getText());
	settings.put(TYPE, (String)typeComboBox.getSelectedItem());

        return settings;
    }

    public void setSettings(HashMap settings) {
        lagTextField.setText(Integer.toString(getLag(settings)));
	String type = (String)settings.get(TYPE);
	if (type != null && !type.equals("")) {
	    typeComboBox.setSelectedItem(type);
	}
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Retrieve the lag of the from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the lag
     */
    public static int getLag(HashMap settings) {
        int period = DEFAULT_LAG;
        String text = (String)settings.get(LAG);

        if(text != null) {
            try {
                period = Integer.parseInt(text);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return period;
    }

    /**
     * Retrieve the heuristic type from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the type
     */
    public static int getType(HashMap settings) {
        int type = DEFAULT_TYPE;
        String text = (String)settings.get(TYPE);

        if(text != null) {
	    if (text.compareTo(Locale.getString("SR_HEURISTIC")) == 0)
		type = SupportAndResistenceGraph.HEURISTIC;
	    else 
		type = SupportAndResistenceGraph.BINS;		    
        }
        return type;
    }

}
