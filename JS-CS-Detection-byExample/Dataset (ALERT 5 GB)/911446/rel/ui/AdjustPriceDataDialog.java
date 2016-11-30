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

/**
 * A dialog for adjusting price data for dividends and splits.
 * 
 * @author Mark Hummel 
 */

package nz.org.venice.ui;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.Vector;

import nz.org.venice.chart.source.Adjustment;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;
import nz.org.venice.main.CommandManager;
import nz.org.venice.util.Locale;

public class AdjustPriceDataDialog extends JInternalFrame 
    implements ActionListener {     

    private JDesktopPane desktop;    
    private JComboBox adjustmentTypeComboBox;
    private JComboBox adjustmentDirectionComboBox;
    private JTextField dateField;
    private JTextField valueField;
    private JLabel valueLabel;

    private JButton okButton, cancelButton, helpButton;

    private JPanel mainPanel, buttonPanel;
    private boolean isDone = false;


    /**
     * Create a new price adjustment dialog, and reading the type, value and
     * start point from the user.
     *
     * @param desktop The desktop
     */
    public AdjustPriceDataDialog(JDesktopPane desktop) {
	super();
	this.desktop = desktop;
	createPanels();
    }	
    
    /**
     * Display the dialog, allowing the user to enter the details.
     */

    public Adjustment showDialog() {	
	desktop.add(this);
	show();	

	try {
	    while (isDone == false) {
		Thread.sleep(10);
	    }
	} catch (InterruptedException e) {

	}
	    
	return getAdjustment();
    }

    
    private void createPanels() {
	this.setSize(560, 250);

	setResizable(true);
        setClosable(true);
        setTitle(Locale.getString("SPLIT_DIV_ADJUST"));

	setLayer(JLayeredPane.MODAL_LAYER);
	getContentPane().setLayout(new BorderLayout());

	mainPanel = getMainPanel();
	buttonPanel = getButtonPanel();
	add(mainPanel, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);

    }

    private JPanel getMainPanel() {
	JPanel mainPanel = new JPanel();
	
	mainPanel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	mainPanel.setLayout(gridbag);
	
	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	Vector adjustmentTypes = new Vector();
	Vector adjustmentDirections = new Vector();
	adjustmentTypes.add(Locale.getString("DIVIDEND"));
	adjustmentTypes.add(Locale.getString("SPLIT"));
	adjustmentDirections.add(Locale.getString("FORWARD"));
	adjustmentDirections.add(Locale.getString("BACK"));

	adjustmentTypeComboBox = 
	    GridBagHelper.addComboBox(mainPanel,
				      Locale.getString("ADJUST_TYPE"),
				      adjustmentTypes,
				      gridbag, c);

	adjustmentTypeComboBox.setToolTipText(Locale.getString("ADJUST_TYPE_COMBO_TOOLTIP"));
	
	adjustmentTypeComboBox.addActionListener(this);

	adjustmentDirectionComboBox = 
	    GridBagHelper.addComboBox(mainPanel,
				      Locale.getString("ADJUST_DIRECTION"),
				      adjustmentDirections,
				      gridbag, c);

	adjustmentDirectionComboBox.setToolTipText(Locale.getString("ADJUST_DIRECTION_COMBO_TOOLTIP"));


			
	valueLabel = new JLabel();
	setValueLabel(Locale.getString("DIVIDEND"));

	dateField = GridBagHelper.addTextRow(mainPanel, Locale.getString("DATE"), 
					     "",
					     gridbag, c,
					     15);

	dateField.setToolTipText(Locale.getString("DATE_FIELD_TOOLTIP"));

	valueField = GridBagHelper.addTextRow(mainPanel, valueLabel,
					      "",
					      gridbag, c,
					      15);
	
	valueField.setToolTipText(Locale.getString("VALUE_TOOLTIP"));
	
	helpButton = GridBagHelper.addHelpButtonRow(mainPanel,
						    Locale.getString("SPLIT_DIV_ADJUST"),
						    gridbag, c);

	helpButton.addActionListener(this);	

	return mainPanel;
    }

    
    private JPanel getButtonPanel() {
	buttonPanel = new JPanel();
	okButton = new JButton(Locale.getString("OK"));
	cancelButton = new JButton(Locale.getString("CANCEL"));

	okButton.addActionListener(this);
	cancelButton.addActionListener(this);

	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);

	

	return buttonPanel;	
    }

    /**
     * React to button presses and update labels according to the 
     * adjustment type selected.
     * 
     * @param e The event that triggers this method.
     */

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == okButton) {
	    if (validateInput()) {
		isDone = true;		
		dispose();		
	    } else {
		
	    }
	} else if (e.getSource() == cancelButton) {
	    dispose();
	} else if (e.getSource() == adjustmentTypeComboBox) {
	    String selectedType = (String)adjustmentTypeComboBox.getSelectedItem();

	    setValueLabel(selectedType);
	} else if (e.getSource() == helpButton) {
	    //Can link to chapters only so far
	    CommandManager.getInstance().openHelp("Graphs");
	} else { 

	} 
    }

    private void setValueLabel(String type) {
	if (type.equals(Locale.getString("SPLIT"))) {
	    valueLabel.setText(Locale.getString("SPLIT_RATIO"));
	} else if (type.equals(Locale.getString("DIVIDEND"))) {
	    valueLabel.setText(Locale.getString("VALUE"));
	} else {
	    assert false;
	}
    }


    /* 
      For splits, the user enters a ratio of how many new shares were created
      e.g. Two for one, for which the user would enter '2'. For a reverse split
      one for two, the user enters '0.5'. For that reason, zero is not permitted.      
     */

    private boolean validateInput() {
	boolean inputOk = true;
			
	try {
	    Double value = new Double(valueField.getText());
	    
	    //Infinite split
	    if (value.doubleValue() == 0.0) {
		JOptionPane.showInternalMessageDialog(desktop,
						      Locale.getString("DIVIDE_BBY_ZERO_ERROR"),
						      Locale.getString("INVALID_NUMBER_TITLE"),
						      JOptionPane.ERROR_MESSAGE);	    
		inputOk = false;		
	    }
	} catch (NumberFormatException e) {
	    inputOk = false;
	    JOptionPane.showInternalMessageDialog(desktop,
						  Locale.getString("ERROR_PARSING_NUMBER", e.getMessage()),
						  Locale.getString("INVALID_NUMBER_TITLE"),
						  JOptionPane.ERROR_MESSAGE);
	}

	try {
	    TradingDate date = 
		new TradingDate(dateField.getText(), TradingDate.BRITISH);
	} catch (TradingDateFormatException e) {
	    inputOk = false;
	    JOptionPane.showInternalMessageDialog(desktop,
						  Locale.getString("ERROR_PARSING_DATE", e.getDate()),
						  Locale.getString("INVALID_DATE"),
						  JOptionPane.ERROR_MESSAGE);
	}
	

	return inputOk;
    }

    //Precondition: validateInput() has been called and returns true.
    private Adjustment getAdjustment() {

	String type = (String)adjustmentTypeComboBox.getSelectedItem();
	String direction = (String)adjustmentDirectionComboBox.getSelectedItem();
       
	int typeValue = Adjustment.ADJUST_SPLIT;
	boolean forwardDirection = true;
	
	if (type.equals(Locale.getString("SPLIT"))) {
	    typeValue = Adjustment.ADJUST_SPLIT;
	} else if (type.equals(Locale.getString("DIVIDEND"))) {
	    typeValue = Adjustment.ADJUST_DIVIDEND;
	} else {
	    assert false;
	}
	
	if (direction.equals(Locale.getString("FORWARD"))) {
	    forwardDirection = true;
	} else if (direction.equals(Locale.getString("BACK"))) {
	    forwardDirection = false;
	} else {
	    assert false;
	}
		
	Double value = null;
	try {
	    value = new Double(valueField.getText());
	} catch (NumberFormatException e) {
	    //Shouldnt happen because validate would fail
	    assert false;
	}

	TradingDate date = null;
	try {
	    date = new TradingDate(dateField.getText(), TradingDate.BRITISH);
	} catch (TradingDateFormatException e) {
	    //Shouldnt happen because validate would fail
	    assert false;
	}	
    
	return new Adjustment(typeValue, value.doubleValue(), date, forwardDirection);
    }
    
}		
