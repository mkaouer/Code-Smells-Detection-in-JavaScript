/*
 * Merchant of Venice - technical analysis software for the stock market.
 * Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.mov.analyser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.mov.parser.Expression;
import org.mov.parser.ExpressionException;
import org.mov.parser.Parser;
import org.mov.parser.Variable;
import org.mov.parser.Variables;
import org.mov.prefs.PreferencesManager;
import org.mov.ui.ExpressionComboBox;
import org.mov.ui.GridBagHelper;
import org.mov.util.Locale;

/**
* An analysis tool page that lets the user enter a buy and sell rule, or
* a family of related buy and sell rules. This page is used by the
* {@link PaperTradeModule}. The page contains the following user fields:
*
* <ul><li>Buy Rule</li>
*     <li>Sell Rule</li>
*     <li>Enable Rule Families</li>
*     <ul>
*        <li>Range of A Variable</li>
*        <li>Range of B Variable</li>
*        <li>Range of C Variable</li>
*     </ul>
* </ul>
*
* The buy and sell rules determine when a stock should be bought or sold
* respectively. The rule family option allows the user to embedd variables
* in the rules. This enables them to specify a family of simillar rules. For
* example, a buy rule might be <code>avg(close, 15) > avg(close, 30)</code>.
* If the rule family is enabled, the user could enter 
* <code>avg(close, a) > avg(close, b)</code>. Then the paper trade would
* try each rule combination of [a, b].
*
* @author Andrew Leppard
* @see PaperTradeModule
*/
public class RulesPage extends JPanel implements AnalyserPage {

    private JDesktopPane desktop;

    // Swing components
    private JCheckBox ruleFamilyEnabledCheckBox;
    
    private ExpressionComboBox buyRuleExpressionComboBox;
    
    private ExpressionComboBox sellRuleExpressionComboBox;
    
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

    /**
     * Construct a new rules page.
     *
     * @param desktop the desktop
     */    
    public RulesPage(JDesktopPane desktop) {
        this.desktop = desktop;
        
        layoutPage();
    }
    
    public void load(String key) {
        // Load last GUI settings from preferences
        HashMap settings = PreferencesManager.getAnalyserPageSettings(key
                                                                      + getClass().getName());
        
        Iterator iterator = settings.keySet().iterator();
        
        while (iterator.hasNext()) {
            String setting = (String) iterator.next();
            String value = (String) settings.get((Object) setting);
            
            if (setting.equals("buy_rule"))
                buyRuleExpressionComboBox.setExpressionText(value);
            else if (setting.equals("is_rule_family"))
                ruleFamilyEnabledCheckBox.setSelected(value.equals("1"));
            else if (setting.equals("sell_rule"))
                sellRuleExpressionComboBox.setExpressionText(value);
            else if (setting.equals("arange"))
                aRangeTextField.setText(value);
            else if (setting.equals("brange"))
                bRangeTextField.setText(value);
            else if (setting.equals("crange"))
                cRangeTextField.setText(value);
            else
                assert false;
        }
        
        checkDisabledStatus();
    }
    
    public void save(String key) {
        HashMap settings = new HashMap();
        
        settings.put("buy_rule", buyRuleExpressionComboBox.getExpressionText());
        settings.put("sell_rule", sellRuleExpressionComboBox.getExpressionText());
        settings.put("is_rule_family",
                     ruleFamilyEnabledCheckBox.isSelected() ? "1" : "0");
        settings.put("arange", aRangeTextField.getText());
        settings.put("brange", bRangeTextField.getText());
        settings.put("crange", cRangeTextField.getText());
        
        PreferencesManager.putAnalyserPageSettings(key + getClass().getName(),
                                                   settings);
    }
    
    public boolean parse() {
        // We need to specify the variables that are given to the buy/sell rule
        // expressions so they can be parsed properly.
        Variables variables = new Variables();
        
        isFamilyEnabled = ruleFamilyEnabledCheckBox.isSelected();
        
        String buyRuleString = buyRuleExpressionComboBox.getExpressionText();
        String sellRuleString = sellRuleExpressionComboBox.getExpressionText();
        
        if (isFamilyEnabled) {
            variables.add("a", Expression.INTEGER_TYPE, Variable.CONSTANT);
            variables.add("b", Expression.INTEGER_TYPE, Variable.CONSTANT);
            variables.add("c", Expression.INTEGER_TYPE, Variable.CONSTANT);
        }
        
        variables.add("held", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("order", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("daysfromstart", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("transactions", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("capital", Expression.FLOAT_TYPE, Variable.CONSTANT);
        variables.add("stockcapital", Expression.FLOAT_TYPE, Variable.CONSTANT);
        
        if (buyRuleString.length() == 0) {
            JOptionPane.showInternalMessageDialog(desktop, Locale
                                                  .getString("MISSING_BUY_RULE"), Locale
                                                  .getString("ERROR_PARSING_RULES"),
					JOptionPane.ERROR_MESSAGE);
            
            return false;
        }

        if (sellRuleString.length() == 0) {
            JOptionPane.showInternalMessageDialog(desktop, Locale
                                                  .getString("MISSING_SELL_RULE"), Locale
                                                  .getString("ERROR_PARSING_RULES"),
                                                  JOptionPane.ERROR_MESSAGE);
            
            return false;
        }
        
        try {
            Variables tmpVar = null;
            try {
                tmpVar = (Variables) variables.clone();
            } catch (CloneNotSupportedException e) {
            }
            buyRule = Parser.parse(tmpVar, buyRuleString);
        } catch (ExpressionException e) {
            buyRule = null;
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  Locale.getString("ERROR_PARSING_BUY_RULE", 
                                                                   e.getReason()), 
                                                  Locale.getString("ERROR_PARSING_RULES"),
                                                  JOptionPane.ERROR_MESSAGE);
            
            return false;
        }
        
        try {
            Variables tmpVar = null;
            try {
                tmpVar = (Variables) variables.clone();
            } catch (CloneNotSupportedException e) {
            }
            sellRule = Parser.parse(tmpVar, sellRuleString);
        } catch (ExpressionException e) {
            sellRule = null;
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  Locale.getString("ERROR_PARSING_SELL_RULE", 
                                                                   e.getReason()), 
                                                  Locale.getString("ERROR_PARSING_RULES"),
                                                  JOptionPane.ERROR_MESSAGE);
            
            return false;
        }
        
        // Now try reading the ranges
        aRange = bRange = cRange = 0;
        
        try {
            if (!aRangeTextField.getText().equals(""))
                aRange = Integer.parseInt(aRangeTextField.getText());
            
            if (!bRangeTextField.getText().equals(""))
                bRange = Integer.parseInt(bRangeTextField.getText());
            
            if (!cRangeTextField.getText().equals(""))
                cRange = Integer.parseInt(cRangeTextField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  Locale.getString("ERROR_PARSING_NUMBER", 
                                                                   e.getMessage()), 
                                                  Locale.getString("ERROR_PARSING_RULES"),
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Noramlise ranges
        if (aRange <= 0)
            aRange = 1;
        if (bRange <= 0)
            bRange = 1;
        if (cRange <= 0)
            cRange = 1;
        
        return true;
    }
    
    public JComponent getComponent() {
		return this;
    }
    
    public String getTitle() {
        return Locale.getString("RULES_PAGE_TITLE");
    }

    /**
     * Return the parsed buy rule expression.
     *
     * @return the buy rule
     */
    public Expression getBuyRule() {
        return buyRule;
    }
    
    /**
     * Return the parsed sell rule expression.
     *
     * @return the sell rule
     */
    public Expression getSellRule() {
        return sellRule;
    }
    
    /**
     * Return the A range parameter.
     *
     * @return the maximum value of A
     */
    public int getARange() {
        return aRange;
    }
    
    /**
     * Return the B range parameter.
     *
     * @return the maximum value of B
     */
    public int getBRange() {
        return bRange;
    }
    
    /**
     * Return the C range parameter.
     *
     * @return the maximum value of C
     */
    public int getCRange() {
        return cRange;
    }
    
    /**
     * Return if rule families have been selected.
     *
     * @return <code>true</code> if rule families are enabled, <code>false</code> otherwise
     */
    public boolean isFamilyEnabled() {
        return isFamilyEnabled;
    }
    
    private void layoutPage() {
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Rules panel
        {
            TitledBorder expressionTitled = new TitledBorder(Locale.getString("RULES_PAGE_TITLE"));
            JPanel panel = new JPanel();
            panel.setBorder(expressionTitled);
            panel.setLayout(new BorderLayout());
            
            JPanel innerPanel = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            innerPanel.setLayout(gridbag);
            
            c.weightx = 1.0;
            c.ipadx = 5;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            
            buyRuleExpressionComboBox = GridBagHelper.addExpressionRow(innerPanel,
                                                                       Locale.getString("BUY_RULE"), "",
                                                                       gridbag, c);
            sellRuleExpressionComboBox = GridBagHelper.addExpressionRow(innerPanel,
                                                                        Locale.getString("SELL_RULE"), 
                                                                        "", gridbag, c);
            
            panel.add(innerPanel, BorderLayout.NORTH);
            add(panel);
        }
        
        // Rule Family panel
        {
            TitledBorder ruleFamilyTitled = new TitledBorder(Locale
                                                             .getString("RULE_FAMILY"));
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
                GridBagHelper.addCheckBoxRow(innerPanel, Locale.getString("ENABLE_MULTIPLE_RULES"),
                                             false, gridbag, c);
            ruleFamilyEnabledCheckBox.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        checkDisabledStatus();
                    }
                });
            
            aRangeTextField = 
                GridBagHelper.addTextRow(innerPanel, Locale.getString("RANGE_A_1_TO"), "", gridbag, 
                                         c, 3);
            bRangeTextField = 
                GridBagHelper.addTextRow(innerPanel, Locale.getString("RANGE_B_1_TO"), "", gridbag, 
                                         c, 3);
            cRangeTextField = 
                GridBagHelper.addTextRow(innerPanel, Locale.getString("RANGE_C_1_TO"), "", gridbag, 
                                         c, 3);
            
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
