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
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 * The MACD graph user interface.
 *
 * @author Andrew Leppard
 * @see MACDGraph
 */
public class MACDGraphUI implements GraphUI {

    // String name of settings
    private final static String AVERAGE_TYPE = "MACD average type";
    public final static String SMA = "SMA";
    public final static String EMA = "EMA";
    private final static String PERIOD_FIRST_AVERAGE = "MACD period first";
    private final static String PERIOD_SECOND_AVERAGE = "MACD period second";
    private final static String SMOOTHING_CONSTANT_FIRST_AVERAGE = "MACD smooth first";
    private final static String SMOOTHING_CONSTANT_SECOND_AVERAGE = "MACD smooth second";

    // Limits
    private final static int MINIMUM_PERIOD = 2;
    private final static double MINIMUM_SMOOTHING_CONSTANT = 0.01D;
    private final static double MAXIMUM_SMOOTHING_CONSTANT = 1.00D;

    // Default values from Technical Analysis Explained by Gerald Appel.
    private final static String DEFAULT_AVERAGE = EMA;
    private final static int DEFAULT_PERIOD_FIRST_AVERAGE = 26;
    private final static int DEFAULT_PERIOD_SECOND_AVERAGE = 12;
    private final static double DEFAULT_SMOOTHING_CONSTANT = 0.1;

    // The graph's user interface
    private JPanel panel;
    private JPanel panelTextBoxes;
    // Avg and EMA, i.e. Simple Moving Average and Exponential Moving Average
    private ButtonGroup group = new ButtonGroup();
    private JRadioButton[] radioButtons = new JRadioButton[2];
    private String actualAverage = DEFAULT_AVERAGE;
    // Details of Averages
    private JTextField periodFirstAverageTextField;
    private JTextField periodSecondAverageTextField;
    private JTextField smoothingConstantFirstAverageTextField;
    private JTextField smoothingConstantSecondAverageTextField;

    /**
     * Create a new MACD user interface with the initial settings.
     *
     * @param settings the initial settings
     */
    public MACDGraphUI(HashMap settings) {
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
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panelTextBoxes.setLayout(layout);

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        radioButtons[0] = new JRadioButton(Locale.getString("EXP_MOVING_AVERAGE"));
        radioButtons[0].setActionCommand(EMA);
        group.add(radioButtons[0]);
        radioButtons[0].setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(radioButtons[0]);
        
        radioButtons[1] = new JRadioButton(Locale.getString("SIMPLE_MOVING_AVERAGE"));
        radioButtons[1].setActionCommand(SMA);
        group.add(radioButtons[1]);
        radioButtons[1].setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(radioButtons[1]);

        periodFirstAverageTextField = GridBagHelper.addTextRow(panelTextBoxes, Locale.getString("PERIOD_FIRST_AVERAGE"), "",
                                                   layout, c, 8);
        smoothingConstantFirstAverageTextField = GridBagHelper.addTextRow(panelTextBoxes, Locale.getString("SMOOTHING_CONSTANT_FIRST_AVERAGE"), "",
                                                     layout, c, 8);
        periodSecondAverageTextField = GridBagHelper.addTextRow(panelTextBoxes, Locale.getString("PERIOD_SECOND_AVERAGE"), "",
                                                   layout, c, 8);
        smoothingConstantSecondAverageTextField = GridBagHelper.addTextRow(panelTextBoxes, Locale.getString("SMOOTHING_CONSTANT_SECOND_AVERAGE"),
                                                       "", layout, c, 8);
        
        panelTextBoxes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelTextBoxes);
        
        radioButtons[0].addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    actualAverage = group.getSelection().getActionCommand();
                    // Enable smoothing constants, if a EMA is selected
                    smoothingConstantFirstAverageTextField.setEnabled(true);
                    smoothingConstantSecondAverageTextField.setEnabled(true);
                }
            });
            
        radioButtons[1].addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    actualAverage = group.getSelection().getActionCommand();
                    // Disable smoothing constants, if a SMA is selected
                    smoothingConstantFirstAverageTextField.setEnabled(false);
                    smoothingConstantSecondAverageTextField.setEnabled(false);
                }
            });
    }

    public String checkSettings() {
	return checkSettings(getSettings());
    }

    public String checkSettings(HashMap settings) {
        // Check periods
        String periodFirstAverageString = (String)settings.get(PERIOD_FIRST_AVERAGE);
        String periodSecondAverageString = (String)settings.get(PERIOD_SECOND_AVERAGE);
        int period;

        try {
            period = Integer.parseInt(periodFirstAverageString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", periodFirstAverageString);
        }

        if (period < MINIMUM_PERIOD)
            return Locale.getString("PERIOD_TOO_SMALL");

        try {
            period = Integer.parseInt(periodSecondAverageString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", periodSecondAverageString);
        }
        
        if (period < MINIMUM_PERIOD)
            return Locale.getString("PERIOD_TOO_SMALL");
        
        
        // Check smoothing constants
        String smoothingFirstAverageConstantString =
            (String)settings.get(SMOOTHING_CONSTANT_FIRST_AVERAGE);
        String smoothingSecondAverageConstantString =
            (String)settings.get(SMOOTHING_CONSTANT_FIRST_AVERAGE);
        double smoothingConstant;

        try {
            smoothingConstant = Double.parseDouble(smoothingFirstAverageConstantString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER",
                                    smoothingFirstAverageConstantString);
        }

        if (smoothingConstant < MINIMUM_SMOOTHING_CONSTANT ||
            smoothingConstant > MAXIMUM_SMOOTHING_CONSTANT)
            return Locale.getString("ERROR_SMOOTHING_CONSTANT",
                                    MINIMUM_SMOOTHING_CONSTANT,
                                    MAXIMUM_SMOOTHING_CONSTANT);

        try {
            smoothingConstant = Double.parseDouble(smoothingSecondAverageConstantString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER",
                                    smoothingSecondAverageConstantString);
        }

        if (smoothingConstant < MINIMUM_SMOOTHING_CONSTANT ||
            smoothingConstant > MAXIMUM_SMOOTHING_CONSTANT)
            return Locale.getString("ERROR_SMOOTHING_CONSTANT",
                                    MINIMUM_SMOOTHING_CONSTANT,
                                    MAXIMUM_SMOOTHING_CONSTANT);

	
        // Settings are OK
        return null;
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(AVERAGE_TYPE, actualAverage);
        settings.put(PERIOD_FIRST_AVERAGE, periodFirstAverageTextField.getText());
        settings.put(PERIOD_SECOND_AVERAGE, periodSecondAverageTextField.getText());
        settings.put(SMOOTHING_CONSTANT_FIRST_AVERAGE, smoothingConstantFirstAverageTextField.getText());
        settings.put(SMOOTHING_CONSTANT_SECOND_AVERAGE, smoothingConstantSecondAverageTextField.getText());
	
        return settings;
    }

    public void setSettings(HashMap settings) {
        actualAverage = getAverageType(settings);
        if (actualAverage.compareTo(EMA)==0)
            radioButtons[0].setSelected(true);
        if (actualAverage.compareTo(SMA)==0) {
            radioButtons[1].setSelected(true);
	    smoothingConstantFirstAverageTextField.setEnabled(false);
	    smoothingConstantSecondAverageTextField.setEnabled(false);	
	}
        periodFirstAverageTextField.setText(Integer.toString(getPeriodFirstAverage(settings)));
        periodSecondAverageTextField.setText(Integer.toString(getPeriodSecondAverage(settings)));
        smoothingConstantFirstAverageTextField.setText(Double.toString(getSmoothingConstantFirstAverage(settings)));
        smoothingConstantSecondAverageTextField.setText(Double.toString(getSmoothingConstantSecondAverage(settings)));
	

    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Retrieve the average type (EMA or SMA) from the settings hashmap. If the hashmap
     * is empty, then return the default average type (i.e. EMA).
     *
     * @param settings the settings
     * @return the average type
     */
    public static String getAverageType(HashMap settings) {
        String averageType = DEFAULT_AVERAGE;
        String averageTypeSaved = (String)settings.get(AVERAGE_TYPE);

        if(averageTypeSaved != null) {
            averageType = averageTypeSaved;
        }

        return averageType;
    }

    /**
     * Retrieve the period of the first average from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the period
     */
    public static int getPeriodFirstAverage(HashMap settings) {
        int period = DEFAULT_PERIOD_FIRST_AVERAGE;
        String text = (String)settings.get(PERIOD_FIRST_AVERAGE);

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
     * Retrieve the smoothing constant of the first average from the settings hashmap. If the hashmap
     * is empty, then return the default smoothing constant.
     *
     * @param settings the settings
     * @return the smoothign constant
     */
    public static double getSmoothingConstantFirstAverage(HashMap settings) {
        double smoothingConstant = DEFAULT_SMOOTHING_CONSTANT;
        String text = (String)settings.get(SMOOTHING_CONSTANT_FIRST_AVERAGE);

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

    /**
     * Retrieve the period of the second average from the settings hashmap. If the hashmap
     * is empty, then return the default.
     *
     * @param settings the settings
     * @return the period
     */
    public static int getPeriodSecondAverage(HashMap settings) {
        int period = DEFAULT_PERIOD_SECOND_AVERAGE;
        String text = (String)settings.get(PERIOD_SECOND_AVERAGE);

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
     * Retrieve the smoothing constant of the second average from the settings hashmap. If the hashmap
     * is empty, then return the default smoothing constant.
     *
     * @param settings the settings
     * @return the smoothign constant
     */
    public static double getSmoothingConstantSecondAverage(HashMap settings) {
        double smoothingConstant = DEFAULT_SMOOTHING_CONSTANT;
        String text = (String)settings.get(SMOOTHING_CONSTANT_SECOND_AVERAGE);

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
