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
 * The RSI graph user interface.
 *
 * @author Andrew Leppard
 * @see RSIGraph
 */
public class RSIGraphUI implements GraphUI {

    // The graph's user interface
    private JPanel panel;
    private JTextField periodTextField;
    private JTextField overSoldTextField;
    private JTextField overBoughtTextField;

    // String name of settings
    private final static String PERIOD = "period";
    private final static String OVER_SOLD = "oversold";
    private final static String OVER_BOUGHT = "overbought";

    // Limits
    private final static int MINIMUM_PERIOD = 2;
    private final static int MINIMUM_OVER_SOLD = 1;
    private final static int MAXIMUM_OVER_SOLD = 49;
    private final static int MINIMUM_OVER_BOUGHT = 51;
    private final static int MAXIMUM_OVER_BOUGHT = 99;

    // Default values from Technical Analysis Explained by Martin J. Pring.
    private final static int DEFAULT_PERIOD = 14;
    private final static int DEFAULT_OVER_SOLD = 32;
    private final static int DEFAULT_OVER_BOUGHT = 72;

    /**
     * Create a new RSI user interface with the initial settings.
     *
     * @param settings the initial settings
     */
    public RSIGraphUI(HashMap settings) {
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

        periodTextField = GridBagHelper.addTextRow(panel, Locale.getString("PERIOD"), "",
                                                   layout, c, 8);
        overSoldTextField = GridBagHelper.addTextRow(panel, Locale.getString("OVER_SOLD"), "",
                                                     layout, c, 8);
        overBoughtTextField = GridBagHelper.addTextRow(panel, Locale.getString("OVER_BOUGHT"),
                                                       "", layout, c, 8);
    }

    public String checkSettings() {
	return checkSettings(getSettings());
    }
    
    public String checkSettings(HashMap settings) {
        // Check period
        String periodString = (String)settings.get(PERIOD);
        int period;

        try {
            period = Integer.parseInt(periodString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", periodString);
        }

        if (period < MINIMUM_PERIOD)
            return Locale.getString("PERIOD_TOO_SMALL");

        // Check over sold
        String overSoldString = (String)settings.get(OVER_SOLD);
        int overSold;

        try {
            overSold = Integer.parseInt(overSoldString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", overSoldString);
        }

        if (overSold < MINIMUM_OVER_SOLD || overSold > MAXIMUM_OVER_SOLD)
            return Locale.getString("ERROR_OVER_SOLD_LIMITS",
                                    MINIMUM_OVER_SOLD,
                                    MAXIMUM_OVER_SOLD);

        // Check over bought
        String overBoughtString = (String)settings.get(OVER_BOUGHT);
        int overBought;

        try {
            overBought = Integer.parseInt(overBoughtString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", overBoughtString);
        }

        if (overBought < MINIMUM_OVER_BOUGHT || overBought > MAXIMUM_OVER_BOUGHT)
            return Locale.getString("ERROR_OVER_BOUGHT_LIMITS",
                                    MINIMUM_OVER_BOUGHT,
                                    MAXIMUM_OVER_BOUGHT);

        // Settings are OK
        return null;
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(PERIOD, periodTextField.getText());
        settings.put(OVER_SOLD, overSoldTextField.getText());
        settings.put(OVER_BOUGHT, overBoughtTextField.getText());
        return settings;
    }

    public void setSettings(HashMap settings) {
        periodTextField.setText(Integer.toString(getPeriod(settings)));
        overSoldTextField.setText(Integer.toString(getOverSold(settings)));
        overBoughtTextField.setText(Integer.toString(getOverBought(settings)));
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
    public static int getPeriod(HashMap settings) {
        int period = DEFAULT_PERIOD;
        String periodString = (String)settings.get(PERIOD);

        if(periodString != null) {
            try {
                period = Integer.parseInt(periodString);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return period;
    }

    /**
     * Retrieve the over sold line from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the over sold line
     */
    public static int getOverSold(HashMap settings) {
        int overSold = DEFAULT_OVER_SOLD;
        String text = (String)settings.get(OVER_SOLD);

        if(text != null) {
            try {
                overSold = Integer.parseInt(text);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return overSold;
    }

    /**
     * Retrieve the over bought line from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the over bought line
     */
    public static int getOverBought(HashMap settings) {
        int overBought = DEFAULT_OVER_BOUGHT;
        String text = (String)settings.get(OVER_BOUGHT);

        if(text != null) {
            try {
                overBought = Integer.parseInt(text);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return overBought;
    }
}