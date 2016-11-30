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
public class KDGraphUI implements GraphUI {

    // The graph's user interface
    private JPanel panel;
    private JTextField periodTextField;
    private JTextField kSmoothTextField;
    private JTextField dSmoothTextField;

    // String name of settings
    private final static String PERIOD = "period";
    private final static String KSMOOTH = "ksmooth";
    private final static String DSMOOTH = "dsmooth";

    // Limits
    private final static int MINIMUM_PERIOD = 3;
    private final static int MINIMUM_K_SMOOTH = 1;
    private final static int MINIMUM_D_SMOOTH = 3;

    private final static int DEFAULT_PERIOD = 14;
    private final static int DEFAULT_K_SMOOTH = 3;
    private final static int DEFAULT_D_SMOOTH = 3;

    /**
     * Create a new RSI user interface with the initial settings.
     *
     * @param settings the initial settings
     */
    public KDGraphUI(HashMap settings) {
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
        kSmoothTextField = GridBagHelper.addTextRow(panel, Locale.getString("KSMOOTH"), "",
                                                     layout, c, 8);
        dSmoothTextField = GridBagHelper.addTextRow(panel, Locale.getString("DSMOOTH"),
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

        String kSmoothtring = (String)settings.get(KSMOOTH);
        int kSmooth;

        try {
            kSmooth = Integer.parseInt(kSmoothtring);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", kSmoothtring);
        }

        if (kSmooth < MINIMUM_K_SMOOTH || kSmooth > period)
            return Locale.getString("ERROR_KSMOOTH_LIMITS",
                                    MINIMUM_K_SMOOTH);

        String dSmoothString = (String)settings.get(DSMOOTH);
        int dSmooth;

        try {
            dSmooth = Integer.parseInt(dSmoothString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", dSmoothString);
        }

        if (dSmooth < MINIMUM_D_SMOOTH || dSmooth > period)
            return Locale.getString("ERROR_DSMOOTH_LIMITS",
                                    MINIMUM_D_SMOOTH);

        // Settings are OK
        return null;
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(PERIOD, periodTextField.getText());
        settings.put(KSMOOTH, kSmoothTextField.getText());
        settings.put(DSMOOTH, dSmoothTextField.getText());
        return settings;
    }

    public void setSettings(HashMap settings) {
        periodTextField.setText(Integer.toString(getPeriod(settings)));
        kSmoothTextField.setText(Integer.toString(getKSmooth(settings)));
        dSmoothTextField.setText(Integer.toString(getDSmooth(settings)));
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
     * Retrieve the k smooth value from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the k smooth value
     */
    public static int getKSmooth(HashMap settings) {
        int kSmooth = DEFAULT_K_SMOOTH;
        String text = (String)settings.get(KSMOOTH);

        if(text != null) {
            try {
                kSmooth = Integer.parseInt(text);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return kSmooth;
    }

    /**
     * Retrieve the d smooth value from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the d smooth value
     */
    public static int getDSmooth(HashMap settings) {
        int dSmooth = DEFAULT_D_SMOOTH;
        String text = (String)settings.get(DSMOOTH);

        if(text != null) {
            try {
                dSmooth = Integer.parseInt(text);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return dSmooth;
    }
}