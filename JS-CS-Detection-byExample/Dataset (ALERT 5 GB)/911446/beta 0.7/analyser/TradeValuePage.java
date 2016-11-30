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
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.mov.prefs.PreferencesManager;
import org.mov.parser.Expression;
import org.mov.parser.ExpressionException;
import org.mov.parser.Parser;
import org.mov.parser.Variable;
import org.mov.parser.Variables;
import org.mov.util.Locale;

public class TradeValuePage extends JPanel implements AnalyserPage {

    private final static int MAX_CHARS_IN_TEXTBOXES = 15;
    
    private JDesktopPane desktop;

    // Swing items
    private ButtonGroup tradeValueBuyButtonGroup;
    private JRadioButton tradeValueBuyByKeyButton;
    private JRadioButton tradeValueBuyByEquationButton;
    private JComboBox tradeValueBuyComboBox;
    private JTextField tradeValueBuyTextField;
    
    private ButtonGroup tradeValueSellButtonGroup;
    private JRadioButton tradeValueSellByKeyButton;
    private JRadioButton tradeValueSellByEquationButton;
    private JComboBox tradeValueSellComboBox;
    private JTextField tradeValueSellTextField;
    
    public TradeValuePage(JDesktopPane desktop) {
	this.desktop = desktop;
        setGraphic();
    }

    public void load(String key) {

        // Load last GUI settings from preferences
	HashMap settings =
            PreferencesManager.getAnalyserPageSettings(key + getClass().getName());

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get(setting);

            if(setting.equals("trade_value_buy_text_field"))
                tradeValueBuyTextField.setText(value);
            else if(setting.equals("trade_value_buy_combo"))
                tradeValueBuyComboBox.setSelectedItem(value);
            else if(setting.equals("trade_value_buy")) {
                if(value.equals("byKey"))
                    tradeValueBuyByKeyButton.setSelected(true);
                else
                    tradeValueBuyByEquationButton.setSelected(true);
            }
            if(setting.equals("trade_value_sell_text_field"))
                tradeValueSellTextField.setText(value);
            else if(setting.equals("trade_value_sell_combo"))
                tradeValueSellComboBox.setSelectedItem(value);
            else if(setting.equals("trade_value_sell")) {
                if(value.equals("byKey"))
                    tradeValueSellByKeyButton.setSelected(true);
                else
                    tradeValueSellByEquationButton.setSelected(true);
            }
        }
   }

    public void save(String key) {
        HashMap settings = new HashMap();

        settings.put("trade_value_buy", tradeValueBuyByKeyButton.isSelected()? "byKey" : "byEquation");
        settings.put("trade_value_buy_combo", tradeValueBuyComboBox.getSelectedItem());
        settings.put("trade_value_buy_text_field", tradeValueBuyTextField.getText());
        settings.put("trade_value_sell", tradeValueSellByKeyButton.isSelected()? "byKey" : "byEquation");
        settings.put("trade_value_sell_combo", tradeValueSellComboBox.getSelectedItem());
        settings.put("trade_value_sell_text_field", tradeValueSellTextField.getText());

        PreferencesManager.putAnalyserPageSettings(key + getClass().getName(),
                                                   settings);
    }

    public boolean parse() {
        try {
            // We need to specify the variables that are given to the expression
            // expressions so they can be parsed properly.
            Variables variables = new Variables();
            variables.add("held", Expression.INTEGER_TYPE, Variable.CONSTANT);
            variables.add("order", Expression.INTEGER_TYPE, Variable.CONSTANT);
            variables.add("daysfromstart", Expression.INTEGER_TYPE, Variable.CONSTANT);
            variables.add("transactions", Expression.INTEGER_TYPE, Variable.CONSTANT);
            variables.add("capital", Expression.FLOAT_TYPE, Variable.CONSTANT);
            variables.add("stockcapital", Expression.FLOAT_TYPE, Variable.CONSTANT);
            if (tradeValueBuyByEquationButton.isSelected()) {
                Expression tradeValueBuyExpression = Parser.parse(variables, tradeValueBuyTextField.getText());
            }
            if (tradeValueSellByEquationButton.isSelected()) {
                Expression tradeValueSellExpression = Parser.parse(variables, tradeValueSellTextField.getText());
            }
        } catch(ExpressionException e) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("ERROR_PARSING_SYSTEM_RULES"),
                                                  Locale.getString("INVALID_BUY_SELL_SYSTEM_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
	}
        return true;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("TRADE_VALUE_PAGE_SHORT_TITLE");
    }

    public String getTradeValueBuy() {
        JRadioButton generalTradeValueByKeyButton = tradeValueBuyByKeyButton;
        JComboBox generalTradeValueComboBox = tradeValueBuyComboBox;
        JTextField generalTradeValueTextField = tradeValueBuyTextField;
        return getTradeValue(generalTradeValueByKeyButton, generalTradeValueComboBox, generalTradeValueTextField);
    }
    
   public String getTradeValueSell() {
        JRadioButton generalTradeValueByKeyButton = tradeValueSellByKeyButton;
        JComboBox generalTradeValueComboBox = tradeValueSellComboBox;
        JTextField generalTradeValueTextField = tradeValueSellTextField;
        return getTradeValue(generalTradeValueByKeyButton, generalTradeValueComboBox, generalTradeValueTextField);
    }
    
    private String getTradeValue(JRadioButton radio, JComboBox combo, JTextField text) {
        String retValue = "open";
        if (radio.isSelected()) {
            if (combo.getSelectedIndex()==0) {
                // TOMORROW_OPEN
                retValue = "open";
            } else if (combo.getSelectedIndex()==1) {
                // TODAY_CLOSE
                retValue = "close";
            } else if (combo.getSelectedIndex()==2) {
                // TODAY_MIN_MAX_AVG
                retValue = "(low+high)/2.0";
            } else if (combo.getSelectedIndex()==3) {
                // TODAY_OPEN_CLOSE_AVG
                retValue = "(open+close)/2.0";
            } else if (combo.getSelectedIndex()==4) {
                // TODAY_MIN
                retValue = "low";
            } else if (combo.getSelectedIndex()==5) {
                // TODAY_MAX
                retValue = "high";
            } 
        } else {
            retValue = text.getText();
        }
        return retValue;
    }

    private void setGraphic() {

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Trade Value Panel
        TitledBorder dateTitled = new TitledBorder(Locale.getString("TRADE_VALUE_PAGE_TITLE"));
        JPanel panel = new JPanel();
        panel.setBorder(dateTitled);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        // Trade Cost Panels
        // Buy
        TitledBorder dateTitledBuy = new TitledBorder(Locale.getString("BUY_TRADE_COST"));
        JPanel panelBuy = new JPanel();
        panelBuy.setBorder(dateTitledBuy);
        panelBuy.setLayout(new BorderLayout());
        
        JPanel innerPanelBuy = new JPanel();
        innerPanelBuy.setLayout(gridbag);
        
        tradeValueBuyButtonGroup = new ButtonGroup();

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        tradeValueBuyByKeyButton = new JRadioButton(Locale.getString("BY"));
        tradeValueBuyByKeyButton.setSelected(true);
        tradeValueBuyButtonGroup.add(tradeValueBuyByKeyButton);

        c.gridwidth = 1;
        gridbag.setConstraints(tradeValueBuyByKeyButton, c);
        innerPanelBuy.add(tradeValueBuyByKeyButton);

        tradeValueBuyComboBox = new JComboBox();
        tradeValueBuyComboBox.addItem(Locale.getString("TOMORROW_OPEN"));
        tradeValueBuyComboBox.addItem(Locale.getString("TODAY_CLOSE"));
        tradeValueBuyComboBox.addItem(Locale.getString("TODAY_MIN_MAX_AVG"));
        tradeValueBuyComboBox.addItem(Locale.getString("TODAY_OPEN_CLOSE_AVG"));
        tradeValueBuyComboBox.addItem(Locale.getString("TODAY_MIN"));
        tradeValueBuyComboBox.addItem(Locale.getString("TODAY_MAX"));
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(tradeValueBuyComboBox, c);
        innerPanelBuy.add(tradeValueBuyComboBox);

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        tradeValueBuyByEquationButton = new JRadioButton(Locale.getString("BY_EQUATION"));
        tradeValueBuyButtonGroup.add(tradeValueBuyByEquationButton);

        c.gridwidth = 1;
        gridbag.setConstraints(tradeValueBuyByEquationButton, c);
        innerPanelBuy.add(tradeValueBuyByEquationButton);

        tradeValueBuyTextField = new JTextField();
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(tradeValueBuyTextField, c);
        innerPanelBuy.add(tradeValueBuyTextField);

        panelBuy.add(innerPanelBuy, BorderLayout.NORTH);
        
        // Sell
        TitledBorder dateTitledSell = new TitledBorder(Locale.getString("SELL_TRADE_COST"));
        JPanel panelSell = new JPanel();
        panelSell.setBorder(dateTitledSell);
        panelSell.setLayout(new BorderLayout());
        
        JPanel innerPanelSell = new JPanel();
        innerPanelSell.setLayout(gridbag);
        
        tradeValueSellButtonGroup = new ButtonGroup();

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        tradeValueSellByKeyButton = new JRadioButton(Locale.getString("BY"));
        tradeValueSellByKeyButton.setSelected(true);
        tradeValueSellButtonGroup.add(tradeValueSellByKeyButton);

        c.gridwidth = 1;
        gridbag.setConstraints(tradeValueSellByKeyButton, c);
        innerPanelSell.add(tradeValueSellByKeyButton);

        tradeValueSellComboBox = new JComboBox();
        tradeValueSellComboBox.addItem(Locale.getString("TOMORROW_OPEN"));
        tradeValueSellComboBox.addItem(Locale.getString("TODAY_CLOSE"));
        tradeValueSellComboBox.addItem(Locale.getString("TODAY_MIN_MAX_AVG"));
        tradeValueSellComboBox.addItem(Locale.getString("TODAY_OPEN_CLOSE_AVG"));
        tradeValueSellComboBox.addItem(Locale.getString("TODAY_MIN"));
        tradeValueSellComboBox.addItem(Locale.getString("TODAY_MAX"));
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(tradeValueSellComboBox, c);
        innerPanelSell.add(tradeValueSellComboBox);

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        tradeValueSellByEquationButton = new JRadioButton(Locale.getString("BY_EQUATION"));
        tradeValueSellButtonGroup.add(tradeValueSellByEquationButton);

        c.gridwidth = 1;
        gridbag.setConstraints(tradeValueSellByEquationButton, c);
        innerPanelSell.add(tradeValueSellByEquationButton);

        tradeValueSellTextField = new JTextField();
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(tradeValueSellTextField, c);
        innerPanelSell.add(tradeValueSellTextField);

        panelSell.add(innerPanelSell, BorderLayout.NORTH);
        

        panel.add(panelBuy);
        panel.add(panelSell);
        add(panel);
    }
}
