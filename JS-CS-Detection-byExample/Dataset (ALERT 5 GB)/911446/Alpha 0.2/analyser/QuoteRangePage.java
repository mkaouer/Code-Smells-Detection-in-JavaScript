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
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.mov.prefs.PreferencesManager;
import org.mov.parser.Expression;
import org.mov.parser.ExpressionException;
import org.mov.parser.Parser;
import org.mov.quote.QuoteBundle;
import org.mov.quote.QuoteCache;
import org.mov.quote.QuoteRange;
import org.mov.quote.QuoteSourceManager;
import org.mov.quote.SymbolFormatException;
import org.mov.quote.WeekendDateException;
import org.mov.ui.EquationComboBox;
import org.mov.ui.GridBagHelper;
import org.mov.ui.QuoteRangeComboBox;
import org.mov.util.TradingDate;

public class QuoteRangePage extends JPanel implements AnalyserPage {

    private JDesktopPane desktop;

    // Swing items
    private JTextField startDateTextField;
    private JTextField endDateTextField;
    private QuoteRangeComboBox quoteRangeComboBox;
    private JRadioButton orderByKeyButton;
    private JRadioButton orderByEquationButton;
    private JComboBox orderByKeyComboBox; 
    private EquationComboBox orderByEquationComboBox;

    // Parsed data
    private QuoteRange quoteRange;
    private Expression orderByEquation;
    private TradingDate startDate;
    private TradingDate endDate;

    public QuoteRangePage(JDesktopPane desktop) {
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

            if(setting.equals("start_date")) 
                startDateTextField.setText(value); 
            else if(setting.equals("end_date"))
                endDateTextField.setText(value);
            else if(setting.equals("symbols"))
                quoteRangeComboBox.setText(value);
            else if(setting.equals("by")) {
                if(value.equals("orderByKey"))
                    orderByKeyButton.setSelected(true);
                else
                    orderByEquationButton.setSelected(true);
            }
            else if(setting.equals("by_key"))
                orderByKeyComboBox.setSelectedItem(value);
            else if(setting.equals("by_equation"))
                orderByEquationComboBox.setEquationText(value);
            else
                assert false;
        }

        checkDisabledStatus();
    }

    public void save(String key) {
        HashMap settings = new HashMap();
        
        settings.put("start_date", startDateTextField.getText());
        settings.put("end_date", endDateTextField.getText());
        settings.put("symbols", quoteRangeComboBox.getText());
        settings.put("by", orderByKeyButton.isSelected()? "orderByKey" : "orderByEquation");
        settings.put("by_key", orderByKeyComboBox.getSelectedItem());
        settings.put("by_equation", orderByEquationComboBox.getEquationText());

        PreferencesManager.saveAnalyserPageSettings(key + getClass().getName(),
                                                    settings);
    }

    public boolean parse() {
        quoteRange = null;

	startDate = new TradingDate(startDateTextField.getText(),
                                    TradingDate.BRITISH);
	endDate = new TradingDate(endDateTextField.getText(),
                                  TradingDate.BRITISH);

	if(startDate.getYear() == 0 || endDate.getYear() == 0) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "Invalid date.",
                                                  "Invalid date range",
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
	}

        if(startDate.after(endDate)) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "Start date should be before end date.",
                                                  "Invalid date range",
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        if(!QuoteSourceManager.getSource().containsDate(startDate)) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  "No data available for date '" + 
                                                  startDateTextField.getText() + "'",
                                                  "Invalid date range",
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(!QuoteSourceManager.getSource().containsDate(endDate)) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  "No data available for date '" + 
                                                  endDateTextField.getText() + "'",
                                                  "Invalid date range",
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int offset = QuoteCache.getInstance().dateToOffset(startDate);
        }
        catch(WeekendDateException e) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "Start date is on a weekend.",
                                                  "Invalid date range",
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        try {
            int offset = QuoteCache.getInstance().dateToOffset(endDate);
        }
        catch(WeekendDateException e) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  "End date is on a weekend.",
                                                  "Invalid date range",
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        try {
            quoteRange = quoteRangeComboBox.getQuoteRange();
        }
        catch(SymbolFormatException e) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  e.getReason(),
                                                  "Invalid quote range",
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(orderByEquationButton.isSelected()) {
            try {
                orderByEquation = Parser.parse(orderByEquationComboBox.getEquationText());
            }
            catch(ExpressionException e) {
                JOptionPane.showInternalMessageDialog(desktop, 
                                                      "Error parsing order equation: " +
                                                      e.getReason(),
                                                      "Error parsing order equation",
                                                      JOptionPane.ERROR_MESSAGE);
                
                return false;
            }
        }

        quoteRange.setFirstDate(startDate);
        quoteRange.setLastDate(endDate);

        return true;
    }

    public JComponent getComponent() {
        return this;
    }

    public QuoteRange getQuoteRange() {
        return quoteRange;
    }

    public TradingDate getStartDate() {
        return startDate;
    }

    public TradingDate getEndDate() {
        return endDate;
    }

    public OrderComparator getOrderComparator(QuoteBundle quoteBundle) {
        if(orderByKeyButton.isSelected()) {
            // If the user has told us not to order, then don't return a comparator as
            // we don't need one. Passing null as a comparator into the paper trading
            // routines will signal we don't care about the order.
            if(orderByKeyComboBox.getSelectedIndex() == 0)
                return null;
            else
                return new OrderComparator(quoteBundle, orderByKeyComboBox.getSelectedIndex());
        }
        else {
            assert orderByEquationButton.isSelected();
            return new OrderComparator(quoteBundle, orderByEquation);
        }
    }

    private void layoutPage() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	// Date panel
	{
	    TitledBorder dateTitled = new TitledBorder("Date Range");
	    JPanel panel = new JPanel();
	    panel.setBorder(dateTitled);
            panel.setLayout(new BorderLayout());

            JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

	    startDateTextField = 
	    	GridBagHelper.addTextRow(innerPanel, "Start Date", "", gridbag, c, 15);
	    endDateTextField = 
		GridBagHelper.addTextRow(innerPanel, "End Date", "", gridbag, c, 15);

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
	}

	// Symbols Panel
	{
	    TitledBorder symbolTitled = new TitledBorder("Symbols");
	    JPanel panel = new JPanel();
	    panel.setBorder(symbolTitled);
            panel.setLayout(new BorderLayout());

	    JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

            JLabel label = new JLabel("Symbols");
            c.gridwidth = 1;
            gridbag.setConstraints(label, c);
            innerPanel.add(label);

            quoteRangeComboBox = new QuoteRangeComboBox();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(quoteRangeComboBox, c);
            innerPanel.add(quoteRangeComboBox);

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
	}

        // Symbols Order Panel
        {
            TitledBorder orderTitled = new TitledBorder("Order Symbols");
            JPanel panel = new JPanel();
            panel.setBorder(orderTitled);
            panel.setLayout(new BorderLayout());

	    JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);

            ButtonGroup buttonGroup = new ButtonGroup();

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

            orderByKeyButton = new JRadioButton("By");
            orderByKeyButton.setSelected(true);
            orderByKeyButton.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {                        
                        checkDisabledStatus();
                    }});
            buttonGroup.add(orderByKeyButton);

            c.gridwidth = 1;
            gridbag.setConstraints(orderByKeyButton, c);
            innerPanel.add(orderByKeyButton);

            orderByKeyComboBox = new JComboBox();
            orderByKeyComboBox.addItem("No Real Order");
            orderByKeyComboBox.addItem("Stock Symbol");
            orderByKeyComboBox.addItem("Volume Decreasing");
            orderByKeyComboBox.addItem("Volume Increasing");
            orderByKeyComboBox.addItem("Day Low Decreasing");
            orderByKeyComboBox.addItem("Day Low Increasing");
            orderByKeyComboBox.addItem("Day High Decreasing");
            orderByKeyComboBox.addItem("Day High Increasing");
            orderByKeyComboBox.addItem("Day Open Decreasing");
            orderByKeyComboBox.addItem("Day Open Increasing");
            orderByKeyComboBox.addItem("Day Close Decreasing");
            orderByKeyComboBox.addItem("Day Close Increasing");
            orderByKeyComboBox.addItem("Change Decreasing");
            orderByKeyComboBox.addItem("Change Increasing");

            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(orderByKeyComboBox, c);
            innerPanel.add(orderByKeyComboBox);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

            orderByEquationButton = new JRadioButton("By Equation");
            orderByEquationButton.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {                        
                        checkDisabledStatus();
                    }});

            buttonGroup.add(orderByEquationButton);

            c.gridwidth = 1;
            gridbag.setConstraints(orderByEquationButton, c);
            innerPanel.add(orderByEquationButton);

            orderByEquationComboBox = new EquationComboBox();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(orderByEquationComboBox, c);
            innerPanel.add(orderByEquationComboBox);

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
        }
    }         

    private void checkDisabledStatus() {
        orderByKeyComboBox.setEnabled(orderByKeyButton.isSelected());
        orderByEquationComboBox.setEnabled(orderByEquationButton.isSelected());        
    }
}
