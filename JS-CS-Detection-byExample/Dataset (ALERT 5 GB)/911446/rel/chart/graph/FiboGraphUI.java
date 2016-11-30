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
import javax.swing.JPanel;
import javax.swing.JTextField;

import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 *  Prompt user for the zero percent and 100 percent levels for Fibonacci chart *
 * @author Andrew Goh
 */
public class FiboGraphUI implements GraphUI {

    // The graph's user interface
    private JPanel panel;
    private JTextField TextFieldZero;
    private JTextField TextField100;

    
    // String name of settings
    public final static String ZEROPCT = "zeropct";
    public final static String HUNDREDPCT = "hundredpct";

    /**
     * Create a new exponential smoothed moving average user interface with the
     * initial settings.
     *
     * @param settings the initial settings
     */
    public FiboGraphUI(HashMap settings) {
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

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
	
	String zeroField = "";
	String hundredField = "";

	TextFieldZero = GridBagHelper.addTextRow(panel, Locale.getString("ZERO_PCT"), zeroField,
                                                   layout, c, 8);
        TextField100 = GridBagHelper.addTextRow(panel,
                                     Locale.getString("HUNDRED_PCT"), hundredField,
                                     layout, c, 8);
    }

    public String checkSettings() {
	return checkSettings(getSettings());
    }

    public String checkSettings(HashMap settings) {
        // Check input
        String temp = (String)settings.get(ZEROPCT);

        try {
            double dtemp = Double.parseDouble(temp);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", temp);
        }

        temp = (String)settings.get(HUNDREDPCT);

        try {
            double dtemp = Double.parseDouble(temp);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", temp);
        }

        // Settings are OK
        return null;
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(ZEROPCT, TextFieldZero.getText());
        settings.put(HUNDREDPCT, TextField100.getText());
        return settings;
    }

    public void setSettings(HashMap settings) {
        TextFieldZero.setText((String) settings.get(ZEROPCT));
        TextField100.setText((String) settings.get(HUNDREDPCT));
    }

    public JPanel getPanel() {
        return panel;
    }

}