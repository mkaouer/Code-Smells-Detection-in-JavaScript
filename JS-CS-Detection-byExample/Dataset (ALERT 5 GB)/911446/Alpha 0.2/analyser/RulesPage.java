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

package org.mov.analyser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Class;
import java.lang.String;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.ExpressionException;
import org.mov.parser.Parser;
import org.mov.parser.Variables;
import org.mov.prefs.PreferencesManager;
import org.mov.ui.GridBagHelper;
import org.mov.ui.EquationComboBox;

public class RulesPage extends JPanel implements AnalyserPage {

    private JDesktopPane desktop;
    
    // Swing components
    private JCheckBox ruleFamilyEnabledCheckBox;
    private EquationComboBox buyRuleEquationComboBox;
    private EquationComboBox sellRuleEquationComboBox;
    private JTextField aRangeTextField;
    private JTextField bRangeTextField;
    private JTextField cRangeTextField;

    // Parsed input
    private Expression buyRule;
    private Expression sellRule;
    private boolean isFamilyEnabled;
    private int aRange;
    private int bRange;
    private int cRange;

    public RulesPage(JDesktopPane desktop) {
        this.desktop = desktop;

        layoutPage();
    }

    public void load(String key) {

        // Load last GUI settings from preferences
	HashMap settings = 
            PreferencesManager.loadAnalyserPageSettings(key + getClass().getName());
                          
	Iterator iterator = settings.keySet().iterator();
                              
	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

	    if(setting.equals("buy_rule"))
		buyRuleEquationComboBox.setEquationText(value);
            else if(setting.equals("is_rule_family")) 
                ruleFamilyEnabledCheckBox.setSelected(value.equals("1"));
	    else if(setting.equals("sell_rule"))
		sellRuleEquationComboBox.setEquationText(value);
	    else if(setting.equals("arange"))
		aRangeTextField.setText(value);
	    else if(setting.equals("brange"))
		bRangeTextField.setText(value);
	    else if(setting.equals("crange"))
		cRangeTextField.setText(value);
            else
                assert false;
        }

        checkDisabledStatus();
    }

    public void save(String key) {
        HashMap settings = new HashMap();
        
	settings.put("buy_rule", buyRuleEquationComboBox.getEquationText());
	settings.put("sell_rule", sellRuleEquationComboBox.getEquationText());
	settings.put("is_rule_family", ruleFamilyEnabledCheckBox.isSelected()? "1" : "0");
	settings.put("arange", aRangeTextField.getText());
	settings.put("brange", bRangeTextField.getText());
	settings.put("crange", cRangeTextField.getText());

        PreferencesManager.saveAnalyserPageSettings(key + getClass().getName(),
                                                    settings);
    }

    public boolean parse() {
        // We need to specify the variables that are given to the buy/sell rule
        // expressions so they can be parsed properly.
        Variables variables = new Variables();
        
        isFamilyEnabled = ruleFamilyEnabledCheckBox.isSelected();
	
	String buyRuleString = buyRuleEquationComboBox.getEquationText();
	String sellRuleString = sellRuleEquationComboBox.getEquationText();

        if(isFamilyEnabled) {
            variables.add("a", Expression.INTEGER_TYPE);
            variables.add("b", Expression.INTEGER_TYPE);
            variables.add("c", Expression.INTEGER_TYPE);
        }

        variables.add("held", Expression.INTEGER_TYPE);
        variables.add("order", Expression.INTEGER_TYPE);

        if(buyRuleString.length() == 0) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "You need to specify a rule which indicates " +
                                                  "when to buy.",
                                                  "Error parsing buy rule",
                                                  JOptionPane.ERROR_MESSAGE);
                                                  
	    return false;
        }

        if(sellRuleString.length() == 0) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "You need to specify a rule which indicates " +
                                                  "when to sell.",
                                                  "Error parsing sell rule",
                                                  JOptionPane.ERROR_MESSAGE);
                                                  
	    return false;
        }

	try {
	    buyRule = Parser.parse(variables, buyRuleString);
	}
	catch(ExpressionException e) {	   
            buyRule = null;
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "Error parsing buy rule: " +
                                                  e.getReason(),
                                                  "Error parsing buy rule",
                                                  JOptionPane.ERROR_MESSAGE);
                                                  
	    return false;
	}

	try {
	    sellRule = Parser.parse(variables, sellRuleString);
	}
	catch(ExpressionException e) {
            sellRule = null;
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "Error parsing sell rule: " +
                                                  e.getReason(),
                                                  "Error parsing buy rule",
                                                  JOptionPane.ERROR_MESSAGE);
            
	    return false;
	}

        // Now try reading the ranges
        aRange = bRange = cRange = 0;

	try {
	    if(!aRangeTextField.getText().equals(""))
		aRange = 
		    Integer.parseInt(aRangeTextField.getText());

	    if(!bRangeTextField.getText().equals(""))
		bRange =
		    Integer.parseInt(bRangeTextField.getText());

	    if(!cRangeTextField.getText().equals(""))
		cRange =
		    Integer.parseInt(cRangeTextField.getText());
        }
	catch(NumberFormatException e) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "Error parsing number: '" +
                                                  e.getMessage() + "'",
                                                  "Error parsing number",
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
	}

        // Noramlise ranges
	if(aRange <= 0)
	    aRange = 1;
	if(bRange <= 0)
	    bRange = 1;
	if(cRange <= 0)
	    cRange = 1;

        return true;
    }

    public JComponent getComponent() {
        return this;
    }

    public Expression getBuyRule() {
        return buyRule;
    }

    public Expression getSellRule() {
        return sellRule;
    }

    public int getARange() {
        return aRange;
    }

    public int getBRange() {
        return bRange;
    }

    public int getCRange() {
        return cRange;
    }

    public boolean isFamilyEnabled() {
        return isFamilyEnabled;
    }

    private void layoutPage() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Rules panel
        {
            TitledBorder equationTitled = new TitledBorder("Rules");
            JPanel panel = new JPanel();
            panel.setBorder(equationTitled);
            panel.setLayout(new BorderLayout());
            
	    JPanel innerPanel = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            innerPanel.setLayout(gridbag);
            
            c.weightx = 1.0;
            c.ipadx = 5;
            c.anchor = GridBagConstraints.WEST;

            buyRuleEquationComboBox = 
                GridBagHelper.addEquationRow(innerPanel, "Buy Rule", "", gridbag, c);
            sellRuleEquationComboBox = 
                GridBagHelper.addEquationRow(innerPanel, "Sell Rule", "", gridbag, c);

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
        }

        // Rule Family panel
        {
	    TitledBorder ruleFamilyTitled = new TitledBorder("Rule family");
	    JPanel panel = new JPanel();
	    panel.setBorder(ruleFamilyTitled);
            panel.setLayout(new BorderLayout());

	    JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;
            
            ruleFamilyEnabledCheckBox = 
                GridBagHelper.addCheckBoxRow(innerPanel, "Enable multiple rules", 
                                             false, gridbag, c);
            ruleFamilyEnabledCheckBox.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        checkDisabledStatus();
                    }
                });

            aRangeTextField = 
                GridBagHelper.addTextRow(innerPanel, "Range a: 1 to", "", 
                                         gridbag, c, 3);
            bRangeTextField = 
                GridBagHelper.addTextRow(innerPanel, "Range b: 1 to", "", 
                                         gridbag, c, 3);
            cRangeTextField = 
                GridBagHelper.addTextRow(innerPanel, "Range c: 1 to", "", 
                                         gridbag, c, 3);

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
        }
    }

    private void checkDisabledStatus() {
        boolean isRuleFamilyEnabled = ruleFamilyEnabledCheckBox.isSelected();

        aRangeTextField.setEnabled(isRuleFamilyEnabled);
        bRangeTextField.setEnabled(isRuleFamilyEnabled);
        cRangeTextField.setEnabled(isRuleFamilyEnabled);
    }
}
