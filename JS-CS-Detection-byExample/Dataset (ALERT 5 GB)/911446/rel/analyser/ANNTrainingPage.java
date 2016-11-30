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

package nz.org.venice.analyser;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

public class ANNTrainingPage extends Page implements AnalyserPage {
        
    private JTextField learningRateTextRow;
    private JTextField momentumTextRow;
    private JTextField preLearningTextRow;
    private JTextField totCyclesTextRow;
    
    private JTextField minEarningPercentageTextRow;
    private JTextField windowForecastTextRow;
    
    private double learningRate = 0.7D;
    private double momentum = 0.5D;
    private int preLearning = 0;
    private int totCycles = 150;
    
    private double minEarningPercentage = 2.0D;
    private int windowForecast = 7;
    
    /**
     * Construct a new ANN training parameters page.
     * It manages:
     * the training parameters for the artificial neural network monitor,
     * the cross target parameters.
     *
     * @param desktop the desktop
     */
    public ANNTrainingPage(JDesktopPane desktop) {
        
        Dimension preferredSize = new Dimension();
        
        this.desktop = desktop;
        
        setGraphic();
        
    }
    
    /** 
     * Save the preferences
     */
    public void save(String key) {
        
        HashMap settingsInitPop = new HashMap();

        settingsInitPop.put("learning_rate", learningRateTextRow.getText());
        settingsInitPop.put("momentum", momentumTextRow.getText());
        settingsInitPop.put("pre_learning", preLearningTextRow.getText());
        settingsInitPop.put("tot_cycles", totCyclesTextRow.getText());
        
        settingsInitPop.put("min_earning_percentage", minEarningPercentageTextRow.getText());
        settingsInitPop.put("window_forecast", windowForecastTextRow.getText());

        PreferencesManager.putAnalyserPageSettings(key + getClass().getName(),
                                                   settingsInitPop);
    }
    
    /** 
     * Load the preferences
     */
    public void load(String key) {
        // Load last GUI settings from preferences
	HashMap settings =
            PreferencesManager.getAnalyserPageSettings(key + getClass().getName());

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

            if (value != null) {
                this.loadCommon(setting, value);
            }
        }
    }
    
    /** 
     * Load the values of preferences
     */
    private void loadCommon(String setting, String value) {
        if(setting.equals("learning_rate") && !value.equals("")) {
            learningRateTextRow.setText(value);
        }
        if(setting.equals("momentum") && !value.equals("")) {
            momentumTextRow.setText(value);
        }
        if(setting.equals("pre_learning") && !value.equals("")) {
            preLearningTextRow.setText(value);
        }
        if(setting.equals("tot_cycles") && !value.equals("")) {
            totCyclesTextRow.setText(value);
        }

        if(setting.equals("min_earning_percentage") && !value.equals("")) {
            minEarningPercentageTextRow.setText(value);
        }
        if(setting.equals("window_forecast") && !value.equals("")) {
            windowForecastTextRow.setText(value);
        }
    }
    
    /** 
     * Parse the GUI
     */
    public boolean parse() {
        boolean returnValue = true;
        
        // Check all the numbers are correct doubles or integers.
        try {
            if(!learningRateTextRow.getText().equals("")) {
                learningRate = Double.parseDouble(
                        learningRateTextRow.getText());
            }
            if(!momentumTextRow.getText().equals("")) {
                momentum = Double.parseDouble(
                        momentumTextRow.getText());
            }
            if(!preLearningTextRow.getText().equals("")) {
                preLearning = Integer.parseInt(
                        preLearningTextRow.getText());
            }
            if(!totCyclesTextRow.getText().equals("")) {
                totCycles = Integer.parseInt(
                        totCyclesTextRow.getText());
            }

            if(!minEarningPercentageTextRow.getText().equals("")) {
                minEarningPercentage = Double.parseDouble(
                        minEarningPercentageTextRow.getText());
            }
            if(!windowForecastTextRow.getText().equals("")) {
                windowForecast = Integer.parseInt(
                        windowForecastTextRow.getText());
            }

        } catch(NumberFormatException e) {
        	showErrorMessage(
            		Locale.getString("ERROR_PARSING_NUMBER",e.getMessage()),
            		Locale.getString("INVALID_ANN_ERROR"));
            returnValue = false;
        }
       
        // Check the range for learning rate
        // It must be a number between 0.0 and 1.0
        if((learningRate<0.0D) || (learningRate>1.0D)) {
        	showErrorMessage(
            		Locale.getString("ANN_LEARNING_RATE_RANGE_ERROR"),
                    Locale.getString("INVALID_ANN_ERROR"));
	    returnValue = false;
        }
        // Check the range for momentum
        // It must be a number between 0.0 and 1.0
        if((momentum<0.0D) || (momentum>1.0D)) {
        	showErrorMessage(
            		Locale.getString("ANN_MOMENTUM_RANGE_ERROR"),
                    Locale.getString("INVALID_ANN_ERROR"));
	    returnValue = false;
        }
        
        // Check the range for preLearning
        // It must be a positive number
        if(preLearning<0) {
        	showErrorMessage(
            		Locale.getString("ANN_PRE_LEARNING_RANGE_ERROR"),
                    Locale.getString("INVALID_ANN_ERROR"));
	    returnValue = false;
        }
        // Check the range for totCycles
        // It must be a positive number and not equal to zero
        if(totCycles<=0) {
        	showErrorMessage(
            		Locale.getString("ANN_TOT_CYCLES_RANGE_ERROR"),
                    Locale.getString("INVALID_ANN_ERROR"));
	    returnValue = false;
        }
        
        // Check the range for windowForecast
        // It must be a number equal or greater then one
        if(windowForecast<1) {
        	showErrorMessage(
            		Locale.getString("ANN_WINDOW_FORECAST_RANGE_ERROR"),
                    Locale.getString("INVALID_ANN_ERROR"));
	    returnValue = false;
        }
        
        return returnValue;
    }
    
    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("ANN_TRAINING_PARAMETERS_SHORT");
    }
    
    /** 
     * Get learning rate of ANN
     *
     * @return the learning rate
     */
    public double getLearningRate() {
        return learningRate;
    }
    
    /** 
     * Get momentum of ANN
     *
     * @return the momentum
     */
    public double getMomentum() {
        return momentum;
    }
    
    /** 
     * Get pre learning of ANN.
     * pre learning is the initial ignored input patterns (during the training phase)
     *
     * @return pre learning
     */
    public int getPreLearning() {
        return preLearning;
    }
    
    /** 
     * Get tot cycles of ANN.
     * tot cycles is how many times the net must be trained on the input patterns
     *
     * @return tot cycles
     */
    public int getTotCycles() {
        return totCycles;
    }
    
    /** 
     * Get the earning percentage of ANN.
     * Earning percentage is the percentage we want to gain,
     * That's what we want to gain in a number of days equal or less
     * than window forecast parameter. 
     *
     * @return earning percentage
     */
    public double getMinEarningPercentage() {
        return minEarningPercentage;
    }
    
    /** 
     * Get the window forecast. The window forecast is the number of days in future
     * before which we gain the wished percentage.
     *
     * @return window forecast
     */
    public int getWindowForecast() {
        return windowForecast;
    }
    
    /*
     * Set the GUI
     */
    private void setGraphic() {
        
        GridBagLayout gridbag = new GridBagLayout();
                
        TitledBorder titledBorder = new TitledBorder(
                Locale.getString("ANN_TRAINING_PARAMETERS_LONG"));
        this.setBorder(titledBorder);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel panelOne = new JPanel();
        JPanel panelTwo = new JPanel();
        panelOne.setLayout(new BoxLayout(panelOne, BoxLayout.Y_AXIS));
        panelTwo.setLayout(new BoxLayout(panelTwo, BoxLayout.Y_AXIS));
        TitledBorder titledBorderSectionOne = new TitledBorder(
                Locale.getString("ANN_TRAINING_PARAMETERS_SHORT"));
        TitledBorder titledBorderSectionTwo = new TitledBorder(
                Locale.getString("CROSS_TARGET"));
        panelOne.setBorder(titledBorderSectionOne);
        panelTwo.setBorder(titledBorderSectionTwo);

        JPanel innerPanelOne = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        innerPanelOne.setLayout(gridbag);
        
        // Text boxes for training parameters.
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        learningRateTextRow =
        GridBagHelper.addTextRow(innerPanelOne,
        Locale.getString("TRAINING_PARAMETER_LEARNING_RATE"), "",
        gridbag, c,
        12);
        momentumTextRow =
        GridBagHelper.addTextRow(innerPanelOne,
        Locale.getString("TRAINING_PARAMETER_MOMENTUM"), "",
        gridbag, c,
        12);
        preLearningTextRow =
        GridBagHelper.addTextRow(innerPanelOne,
        Locale.getString("TRAINING_PARAMETER_PRE_LEARNING"), "",
        gridbag, c,
        12);
        totCyclesTextRow =
        GridBagHelper.addTextRow(innerPanelOne,
        Locale.getString("TRAINING_PARAMETER_TOT_CYCLES"), "",
        gridbag, c,
        12);

        
        // Cross Target Panel
        JPanel innerPanelTwo = new JPanel();
        innerPanelTwo.setLayout(gridbag);
        
        // Text boxes for training parameters.
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        minEarningPercentageTextRow =
        GridBagHelper.addTextRow(innerPanelTwo,
        Locale.getString("EARNING_PERCENTAGE"), "",
        gridbag, c,
        12);
        windowForecastTextRow =
        GridBagHelper.addTextRow(innerPanelTwo,
        Locale.getString("WINDOW_FORECAST"), "",
        gridbag, c,
        12);
        
        panelOne.add(innerPanelOne);
        
        panelTwo.add(innerPanelTwo);
        
        this.add(panelOne);
        this.add(panelTwo);
        
        setValues();
    }
    
    /*
     * Set the values in the text boxes of the GUI
     */
    private void setValues() {
        learningRateTextRow.setText(Double.toString(learningRate));
        momentumTextRow.setText(Double.toString(momentum));
        preLearningTextRow.setText(Integer.toString(preLearning));
        totCyclesTextRow.setText(Integer.toString(totCycles));
        
        minEarningPercentageTextRow.setText(Double.toString(minEarningPercentage));
        windowForecastTextRow.setText(Integer.toString(windowForecast));
    }

}