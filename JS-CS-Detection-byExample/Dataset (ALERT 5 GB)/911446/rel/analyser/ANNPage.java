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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;

import nz.org.venice.parser.Expression;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

public class ANNPage extends Page implements AnalyserPage {
    
    private JTextField buyThresholdTextRow;
    private JTextField sellThresholdTextRow;
    
    private double buyThreshold = 0.5D;
    private double sellThreshold = 0.5D;
    
    private ANNPageModule ANNPageModule;
    
    /**
     * Construct a new input/output artificial neural network parameters page.
     * It manages:
     * the input of artificial neural network choice with a ordered table of expressions.
     * the output thresholds of artificial neural network, there are 2 output thresholds,
     * one for buy signal and one for sell signal.
     *
     * @param	desktop	the current desktop
     * @param maxHeight the height used to resize correctly the window according to
     * the other panels
     */
    public ANNPage(JDesktopPane desktop, double maxHeight) {
        this.desktop = desktop;
        Dimension preferredSize = new Dimension();
        preferredSize.setSize(this.getPreferredSize().getWidth(), maxHeight/2);
        
        this.ANNPageModule = new ANNPageModule(this);
        
        setGraphic(preferredSize);
        
    }
    
    /** 
     * Save the preferences
     */
    public void save(String key) {
        String idStr = "ANNPage";

        HashMap settingsInitPop =
                PreferencesManager.getAnalyserPageSettings(key + idStr);
        HashMap settingsInitPopCommon = new HashMap();

        ANNPageModule.save(settingsInitPop, idStr);
        settingsInitPopCommon.put("buy_threshold", buyThresholdTextRow.getText());
        settingsInitPopCommon.put("sell_threshold", sellThresholdTextRow.getText());

        PreferencesManager.putAnalyserPageSettings(key + idStr,
                                                   settingsInitPop);
        PreferencesManager.putAnalyserPageSettings(key + getClass().getName(),
                                                   settingsInitPopCommon);
    }
    
    /** 
     * Load the preferences
     */
    public void load(String key) {
        String idStr = "ANNPage";
        
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
       
        HashMap settingsInitPop =
                PreferencesManager.getAnalyserPageSettings(key + idStr);

        Iterator iteratorInitPop = settingsInitPop.keySet().iterator();

	while(iteratorInitPop.hasNext()) {
	    String settingInitPop = (String)iteratorInitPop.next();
	    String valueInitPop = (String)settingsInitPop.get((Object)settingInitPop);

            ANNPageModule.load(valueInitPop);
        }
    }
    
    /** 
     * Load the thresholds' preferences
     */
    private void loadCommon(String setting, String value) {
        if(setting.equals("buy_threshold") && !value.equals("")) {
            buyThresholdTextRow.setText(value);
        }
        if(setting.equals("sell_threshold") && !value.equals("")) {
            sellThresholdTextRow.setText(value);
        }
    }
    
    /*
     * Add a row to the table of expressions, input of the ANN
     */
    public void addRowTable(String expression) {
        ANNPageModule.addRowTable(expression);
    }
    
    /*
     * Parse the GUI
     */
    public boolean parse() {
        boolean returnValue = true;
                
        // Check all the numbers are correct doubles.
        try {
            if(!buyThresholdTextRow.getText().equals("")) {
                buyThreshold = Double.parseDouble(
                        buyThresholdTextRow.getText());
            }
            if(!sellThresholdTextRow.getText().equals("")) {
                sellThreshold = Double.parseDouble(
                        sellThresholdTextRow.getText());
            }
        } catch(NumberFormatException e) {
        	showErrorMessage(
            		Locale.getString("ERROR_PARSING_NUMBER",e.getMessage()),
            		Locale.getString("INVALID_ANN_ERROR"));
            returnValue = false;
        }
        
        // Check the range for learning rate
        // It must be a number between 0.0 and 1.0
        if(((buyThreshold<0.0D) || (buyThreshold>1.0D)) ||
            ((sellThreshold<0.0D) || (sellThreshold>1.0D))) {
        	showErrorMessage(
            		Locale.getString("ANN_THRESHOLDS_ERROR"),
                    Locale.getString("INVALID_ANN_ERROR"));
	    returnValue = false;
        }
        
        if(returnValue) {
            returnValue = ANNPageModule.parse();
        }

        return returnValue;
    }
    
    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("ANN_PAGE_PARAMETERS_SHORT");
    }
    
    /*
     * Get input expressions
     *
     * @return  the array of expressions
     */
    public Expression[] getInputExpressions() {
        return ANNPageModule.getInputExpressions();
    }
    
    /*
     * Get buy threshold
     *
     * @return  buy threshold
     */
    public double getBuyThreshold() {
        return buyThreshold;
    }
    
    /*
     * Get sell threshold
     *
     * @return  sell threshold
     */
    public double getSellThreshold() {
        return sellThreshold;
    }
    
    /*
     * Add an expression to the table of input expressions
     */
    private void addExpression() {
        ANNPageModule.addExpression();
    }
    
    /*
     * Edit an expression of the table of input expressions
     */
    private void editExpression() {
        ANNPageModule.editExpression();
    }
    
    /*
     * Delete an expression of the table of input expressions
     */
    private void deleteExpressions() {
        ANNPageModule.removeSelectedResults();
    }
    
    /*
     * Set the GUI
     */
    private void setGraphic(Dimension preferredSize) {
        
        GridBagLayout gridbag = new GridBagLayout();
                
        TitledBorder titledBorder = 
                new TitledBorder(Locale.getString("ANN_PAGE_PARAMETERS_LONG"));
        this.setBorder(titledBorder);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(preferredSize);
        
        JPanel panelOne = new JPanel();
        JPanel panelTwo = new JPanel();
        panelOne.setLayout(new BoxLayout(panelOne, BoxLayout.Y_AXIS));
        panelTwo.setLayout(new BoxLayout(panelTwo, BoxLayout.Y_AXIS));
        TitledBorder titledBorderSectionOne = 
                new TitledBorder(Locale.getString("INPUT_EXPRESSION_COLUMN_HEADER"));
        TitledBorder titledBorderSectionTwo = 
                new TitledBorder(Locale.getString("OUTPUT_EXPRESSION"));
        panelOne.setBorder(titledBorderSectionOne);
        panelTwo.setBorder(titledBorderSectionTwo);
        
        // ANNPageModule is already declared as global variable
        ANNPageModule.setLayout(new BoxLayout(ANNPageModule, BoxLayout.Y_AXIS));
        
        JScrollPane upDownScrollPane = new JScrollPane(ANNPageModule);
        upDownScrollPane.setLayout(new ScrollPaneLayout());
        
        // Buttons to add/modify/delete expressions
        JButton addButton = new JButton(Locale.getString("ADD"));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addExpression();
            }
        });
        
        JButton editButton = new JButton(Locale.getString("EDIT"));
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                editExpression();
            }
        });
        
        JButton deleteButton = new JButton(Locale.getString("DELETE"));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                deleteExpressions();
            }
        });
        
        JPanel rulesButtonsOne = new JPanel();
        rulesButtonsOne.add(addButton);
        rulesButtonsOne.add(editButton);
        rulesButtonsOne.add(deleteButton);

        
        JPanel innerPanelOne = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        innerPanelOne.setLayout(gridbag);
        
        // Text boxes for output parameters.
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        buyThresholdTextRow =
        GridBagHelper.addTextRow(innerPanelOne,
        Locale.getString("OUTPUT_BUY_THRESHOLD"), "",
        gridbag, c,
        12);
        sellThresholdTextRow =
        GridBagHelper.addTextRow(innerPanelOne,
        Locale.getString("OUTPUT_SELL_THRESHOLD"), "",
        gridbag, c,
        12);

        
        panelOne.add(upDownScrollPane);
        rulesButtonsOne.setAlignmentX(CENTER_ALIGNMENT);
        panelOne.add(rulesButtonsOne);
        
        panelTwo.add(innerPanelOne);
        
        this.add(panelOne);
        this.add(panelTwo);
        
        setValues();
    }
    
    /*
     * Set the text boxes of the thresholds
     */
    private void setValues() {
        buyThresholdTextRow.setText(Double.toString(buyThreshold));
        sellThresholdTextRow.setText(Double.toString(sellThreshold));
    }
}