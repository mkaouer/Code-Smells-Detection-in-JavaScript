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
 * The exponential smoothed moving average graph user interface.
 *
 * @author Andrew Leppard
 * @see ExpMovingAverageGraph
 */
public class ExpMovingAverageGraphUI implements GraphUI {

    // The graph's user interface
    private JPanel panel;
    private JTextField periodTextField;
    private JTextField smoothingConstantTextField;

    // String name of settings
    private final static String PERIOD = "period";
    private final static String SMOOTHING_CONSTANT = "smoothing_constant";

    // Limits
    private final static int MINIMUM_PERIOD = 2;
    private final static double MINIMUM_SMOOTHING_CONSTANT = 0.01D;
    private final static double MAXIMUM_SMOOTHING_CONSTANT = 1.00D;

    // Default values
    private final static int DEFAULT_PERIOD = 40;
    private final static double DEFAULT_SMOOTHING_CONSTANT = 0.1;

    /**
     * Create a new exponential smoothed moving average user interface with the
     * initial settings.
     *
     * @param settings the initial settings
     */
    public ExpMovingAverageGraphUI(HashMap settings) {
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
        smoothingConstantTextField =
            GridBagHelper.addTextRow(panel,
                                     Locale.getString("SMOOTHING_CONSTANT"), "",
                                     layout, c, 8);
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

        // Check smoothing constant
        String smoothingConstantString =
            (String)settings.get(SMOOTHING_CONSTANT);
        double smoothingConstant;

        try {
            smoothingConstant = Double.parseDouble(smoothingConstantString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER",
                                    smoothingConstantString);
        }

        if (smoothingConstant < MINIMUM_SMOOTHING_CONSTANT ||
            smoothingConstant > MAXIMUM_SMOOTHING_CONSTANT)
            return Locale.getString("ERROR_SMOOTHING_CONSTANT",
                                    MINIMUM_SMOOTHING_CONSTANT,
                                    MAXIMUM_SMOOTHING_CONSTANT);

        // Settings are OK
        return null;
    }

    public String checkSettings() {
	return checkSettings(getSettings());
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(PERIOD, periodTextField.getText());
        settings.put(SMOOTHING_CONSTANT, smoothingConstantTextField.getText());
        return settings;
    }

    public void setSettings(HashMap settings) {
        periodTextField.setText(Integer.toString(getPeriod(settings)));
        smoothingConstantTextField.setText(Double.toString(getSmoothingConstant(settings)));
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
     * Retrieve the smoothing constant from the settings hashmap. If the hashmap
     * is empty, then return the default smoothing constant.
     *
     * @param settings the settings
     * @return the smoothign constant
     */
    public static double getSmoothingConstant(HashMap settings) {
        double smoothingConstant = DEFAULT_SMOOTHING_CONSTANT;
        String text = (String)settings.get(SMOOTHING_CONSTANT);

        if(text != null) {
            try {
                smoothingConstant = Double.parseDouble(text);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return smoothingConstant;
    }
}