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

package org.mov.chart.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mov.ui.GridBagHelper;
import org.mov.util.Locale;

/**
 * A generic graph user interface that queries the user for a period.
 * Since many of the graphs have only a single setting, the period, it
 * makes sense for them to share a user interface.
 *
 * @author Andrew Leppard
 */
public class PeriodGraphUI implements GraphUI {

    // The graph's user interface
    private JPanel panel;
    private JTextField periodTextField;

    // String name of settings
    private final static String PERIOD = "period";

    // Minimum valid period (there doesn't seem any point in setting a maximum)
    private final static int MINIMUM_PERIOD = 2;

    // Default period in days. At the moment all the graphs that use this
    // user interface share the same period. If it makes sense to use a
    // different period then create a new constructor like so:
    //
    // PeriodGraphUI(HashMap settings, int defaultPeriod);
    //
    // And consider removing the existing constructor and this value.
    private final static int DEFAULT_PERIOD = 21;

    /**
     * Create a new moving period user interface with the initial settings.
     *
     * @param settings the initial settings
     */
    public PeriodGraphUI(HashMap settings) {
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
    }

    public String checkSettings() {
        HashMap settings = getSettings();
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

        // Settings are OK
        return null;
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(PERIOD, periodTextField.getText());
        return settings;
    }

    public void setSettings(HashMap settings) {
        int period = getPeriod(settings);
        periodTextField.setText(Integer.toString(period));
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
}