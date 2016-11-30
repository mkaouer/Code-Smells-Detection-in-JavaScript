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
 * A dialog for adding and editing alerts. 
 * 
 * @author Mark Hummel 
 */

package nz.org.venice.alert;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;

import nz.org.venice.main.CommandManager;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;
import nz.org.venice.parser.Parser;
import nz.org.venice.parser.ExpressionException;


import nz.org.venice.quote.*;
import nz.org.venice.ui.*;

/**
 * A dialog for letting the user add a new Alert
 * <pre>
 * AlertDialog dialog = new AlertDialog(desktop, symbol);
 *
 * // Let user create a new alert
 * dialog.newAlert()
 * </pre>
 *
 * @see Alert
 * @author Mark Hummel
 */
public class AlertDialog extends JInternalFrame 
    implements ActionListener, KeyListener {

    private JDesktopPane desktop;

    private JButton okButton;
    private JButton cancelButton;
    private JButton helpButton;

    private JPanel mainPanel;

    // Fields of a transaction
    private JComboBox alertTypeComboBox;
    private JTextField symbolTextField;
    private JTextField startDateTextField;
    private JTextField endDateTextField;
    private JTextField targetTextField;
    private JComboBox boundTypeComboBox;

    private JTextField expressionTextField;
    private JComboBox fieldTypeComboBox;

    private JLabel alertTypeLabel;
    private JLabel fieldTypeLabel;

    //Data Values
    private String symbolString;
    private String startDateString;
    private String endDateString;
    private String targetString;
    private String alertType;
    private int boundType;    
    private String fieldType;
    private TradingDate startDate;
    private TradingDate endDate;
    private Double targetValue;

    private AlertWriter alertWriter;

    //Previously created alert
    private final Alert alert;    
    private Symbol symbol;



    // Results of dialog. Has it finished. What button was pressed. 
    private boolean isDone = false;
    private boolean okButtonPressed = false;

    public AlertDialog(JDesktopPane desktop, Alert alert, 
		       AlertWriter alertWriter) {
	super();
	this.desktop = desktop;
	this.alert = alert;
	this.symbol = alert.getSymbol();
	this.alertWriter = alertWriter;
	
	setupPanels();
    }

    /**
     * Create a new alert dialog.
     *
     * @param	desktop	the current desktop
     * @param	symbol	the symbol to create an alert for 
     */
    public AlertDialog(JDesktopPane desktop, Symbol symbol, 
		       AlertWriter alertWriter) {
	super();
	this.desktop = desktop;
	this.symbol = symbol;
	this.alert = null;
	this.alertWriter = alertWriter;

	setupPanels();
    }

    /**
     * Create a new alert dialog.
     *
     * @param	desktop	the current desktop
     */
    public AlertDialog(JDesktopPane desktop) {
	super();
	this.desktop = desktop;
	this.alert = null;

	setupPanels();
    }

    

    private void setupPanels() {
	// Make sure we can't be hidden behind other windows
	setLayer(JLayeredPane.MODAL_LAYER);

	getContentPane().setLayout(new BorderLayout());

	mainPanel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	mainPanel.setLayout(gridbag);
	
	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;
	 
	Vector alertTypes = new Vector();
	alertTypes.add(Locale.getString("QUOTES"));
	alertTypes.add(Locale.getString("INPUT_EXPRESSION"));


	alertTypeComboBox = GridBagHelper.addComboBox(mainPanel,
						      Locale.getString("ALERT_TYPE"),
						      alertTypes,
						      gridbag,
						      c);

	alertTypeComboBox.addActionListener(this);
	alertTypeComboBox.setToolTipText(Locale.getString("ALERT_TYPE_TOOLTIP"));

	//If an alert is provided use the alert's start date,
	//otherwise use today
	TradingDate startDate = 
	    (alert != null) ? alert.getStartDate() : new TradingDate();

	if (symbol == null) {
	    symbolTextField =
		GridBagHelper.addTextRow(mainPanel, Locale.getString("SYMBOL"), 
					 "",
					 gridbag, c, 15);
	    symbolTextField.setToolTipText(Locale.getString("SYMBOL_FIELD_TOOLTIP"));
	}

	startDateTextField = 
	    GridBagHelper.addTextRow(mainPanel, Locale.getString("START_DATE"), 
				     startDate.toString("dd/mm/yyyy"), 
                                     gridbag, c, 15);

	startDateTextField.setToolTipText(Locale.getString("START_DATE_FIELD_TOOLTIP"));

	endDateTextField = 
	    GridBagHelper.addTextRow(mainPanel, Locale.getString("END_DATE"), 
				     "", 
                                     gridbag, c, 15);

	endDateTextField.setToolTipText(Locale.getString("START_DATE_FIELD_TOOLTIP"));

	targetTextField = 
	    GridBagHelper.addTextRow(mainPanel, 
				     Locale.getString("ALERT_TRIGGER"), 
				     "", 
                                     gridbag, c, 15);

	targetTextField.setToolTipText(Locale.getString("ALERT_TARGET_TOOLTIP"));


	if (symbolTextField != null) {
	    symbolTextField.addKeyListener(this);
	}
	startDateTextField.addKeyListener(this);
	targetTextField.addKeyListener(this);
				       
	Vector boundTypes = new Vector();
	boundTypes.add(Locale.getString("ALERT_UPPER_BOUND"));
	boundTypes.add(Locale.getString("ALERT_LOWER_BOUND"));
	boundTypes.add(Locale.getString("ALERT_EXACT_BOUND"));
		
	boundTypeComboBox = GridBagHelper.addComboBox(mainPanel,
						      Locale.getString("ALERT_BOUND_TYPE"),
						      boundTypes,
						      gridbag, c);

	boundTypeComboBox.addActionListener(this);
	boundTypeComboBox.setToolTipText(Locale.getString("ALERT_BOUND_TYPE_TOOLTIP"));


	Vector fieldTypes = new Vector();
	fieldTypes.add(Locale.getString("DAY_OPEN"));
	fieldTypes.add(Locale.getString("DAY_HIGH"));
	fieldTypes.add(Locale.getString("DAY_LOW"));	
	fieldTypes.add(Locale.getString("DAY_CLOSE"));
	fieldTypes.add(Locale.getString("VOLUME"));
	

	fieldTypeLabel = new JLabel(Locale.getString("ALERT_FIELD_TYPE"));
	c.gridwidth = 1;
	gridbag.setConstraints(fieldTypeLabel, c);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(boundTypeComboBox, c);       

	fieldTypeComboBox = GridBagHelper.addComboBox(mainPanel,
						      Locale.getString("ALERT_FIELD_TYPE"),
						      fieldTypes,
						      gridbag,
						      c);
		
	fieldTypeComboBox.addActionListener(this);
	fieldTypeComboBox.setToolTipText(Locale.getString("ALERT_OHLCV_FIELD_TOOLTIP"));


	//If an alert was supplied, setup the dialog with it's values
	if (alert != null) {
	    if (alert.getType() == Alert.GONDOLA) {
		alertTypeComboBox.setSelectedIndex(1);
		boundTypeComboBox.setVisible(false);
		fieldTypeComboBox.setVisible(false);
		targetTextField.setText(alert.getTargetExpression());
	    }
	    
	    if (alert.getType() == Alert.OHLCV) {
		alertTypeComboBox.setSelectedIndex(0);
		boundTypeComboBox.setVisible(true);
		fieldTypeComboBox.setVisible(true);
		targetTextField.setText(alert.getTargetValue().toString());
	    }
	}
	
	helpButton = GridBagHelper.addHelpButtonRow(mainPanel, 
						    Locale.getString("ALERT_TITLE"),
						    gridbag,
						    c);	
	helpButton.addActionListener(this);
	
       


	
	JPanel buttonPanel = new JPanel();        
	okButton = new JButton(Locale.getString("OK"));
	okButton.addActionListener(this);
	okButton.setEnabled(false);
	getRootPane().setDefaultButton(okButton);
	
	cancelButton = new JButton(Locale.getString("CANCEL"));
	cancelButton.addActionListener(this);
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);

	getContentPane().add(mainPanel, BorderLayout.NORTH);
	getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	setFrameSize();
	
	
    }


    private void setFrameSize() {
	Dimension preferred; 
	int width = 0;
	int height = 0;

	preferred = getPreferredSize();

	int x = (desktop.getWidth() - width) / 2;
	int y = (desktop.getHeight() - height) / 2;
	
	width = preferred.width;
	height = preferred.height;

	setBounds(x, y, width, height);

	
    }

    /**
     * Display a dialog letting the user enter a new alert.
     * Add the alert.
     *
     * @return	whether the OK button was pressed
     */
    public boolean newAlert() {
	String symbolString = (symbol != null) ? symbol.toString() : "";
	setTitle(Locale.getString("ALERT_NEW") + symbolString);

	desktop.add(this);
	show();
		
	try {
	    while(isDone == false) {
		Thread.sleep(10);
	    }
	}
	catch(Exception e) {
	    // ignore
	}

	// If the user pressed the OK button then add the alert.	
	if(okButtonPressed) {	    
	    if (alertWriter != null) {
		if (alertType.equals(Locale.getString("QUOTES"))) {
		    OHLCVAlert newAlert = new OHLCVAlert();
		    newAlert.setSymbol(symbol);
		    newAlert.setBoundType(boundType);
		    newAlert.setField(fieldType);
		    newAlert.setTargetValue(targetValue);
		    newAlert.setTargetExpression(targetString);		
		    newAlert.setStartDate(startDate);
		    newAlert.setEndDate(endDate);
		    newAlert.setDateSet(new TradingDate());
		    alertWriter.set(newAlert);
		} else {
		    GondolaAlert newAlert = new GondolaAlert();
		    newAlert.setSymbol(symbol);
		    newAlert.setTargetExpression(targetString);		
		    newAlert.setStartDate(startDate);
		    newAlert.setEndDate(endDate);
		    newAlert.setDateSet(new TradingDate());
		    alertWriter.set(newAlert);
		}
	    }
	}
	return okButtonPressed;
    }

    /**
     * Display a dialog letting the user edit an alert.
     * Modify the saved alert.
     *
     * @return	whether the OK button was pressed
     */
    public boolean editAlert() {
	setTitle(Locale.getString("ALERT_EDIT") + " " + symbol);

	desktop.add(this);
	show();
		
	try {
	    while(isDone == false) {
		Thread.sleep(10);
	    }
	}
	catch(Exception e) {
	    // ignore
	}

	// If the user pressed the OK button then add the alert.	
	if(okButtonPressed) {
	    if (alertWriter != null) {			
		if (alertType.equals(Locale.getString("QUOTES"))) {
		    OHLCVAlert newAlert = new OHLCVAlert();
		    newAlert.setSymbol(symbol);
	    newAlert.setBoundType(boundType);
		    newAlert.setField(fieldType);
		    newAlert.setTargetValue(targetValue);
		    newAlert.setTargetExpression(targetString);		
		    newAlert.setStartDate(startDate);
		    newAlert.setEndDate(endDate);
		    newAlert.setDateSet(new TradingDate());
		    alertWriter.update(alert, newAlert);
		} else {
		    GondolaAlert newAlert = new GondolaAlert();
		    newAlert.setSymbol(symbol);
		    newAlert.setTargetExpression(targetString);		
		    newAlert.setStartDate(startDate);
		    newAlert.setEndDate(endDate);
		    newAlert.setDateSet(new TradingDate());
		    alertWriter.update(alert, newAlert);		
		}
	    }
	}
	return okButtonPressed;
    }

    public void actionPerformed(ActionEvent e) {

	if (e.getSource() == okButton) {
	    if (validateInput()) {
		okButtonPressed = true;
		isDone = true;
		dispose();
	    } else {
		//Do nothing - leave the dialog open
		//as there is an error in the input
	    }
	} else if (e.getSource() == cancelButton) {
	    dispose();
	    isDone = true;	    
	    okButtonPressed = false;
	} else if (e.getSource() == alertTypeComboBox) {
	    String alertType = (String)alertTypeComboBox.getSelectedItem();
	    //Hide the bound type field if the alert is an expresion type
	    if (alertType.equals(Locale.getString("INPUT_EXPRESSION"))) {
		boundTypeComboBox.setVisible(false);
		fieldTypeComboBox.setVisible(false);
		fieldTypeLabel.setVisible(false);
	    }  else {
		boundTypeComboBox.setVisible(true);
		fieldTypeComboBox.setVisible(true);
		fieldTypeLabel.setVisible(true);		
	    }			   
	} else if (e.getSource() == helpButton) {
	    CommandManager.getInstance().openHelp("Alerts");
	}
    }	

    private void getInput() {
	if (symbolTextField != null) {
	    symbolString = symbolTextField.getText();
	}

	startDateString = startDateTextField.getText();
	endDateString = endDateTextField.getText();
	targetString = targetTextField.getText();
	
	alertType = (String)alertTypeComboBox.getSelectedItem();
		    

	if (alertType.equals(Locale.getString("QUOTES"))) {
	    String boundTypeString = 
		(String)boundTypeComboBox.getSelectedItem();
	    String fieldTypeString = 
		(String)fieldTypeComboBox.getSelectedItem();
	    
	    boundType = -1;
	    if (boundTypeString.equals(Locale.getString("ALERT_UPPER_BOUND"))) {
		boundType = Alert.UPPER_BOUND;
	    } else if (boundTypeString.equals(Locale.getString("ALERT_LOWER_BOUND"))) {
		boundType = Alert.LOWER_BOUND;
	    } else if (boundTypeString.equals(Locale.getString("ALERT_EXACT_BOUND"))) {
		boundType = Alert.EXACT_BOUND;
	    } else {
		assert false;
	    }	
	    
	    fieldType = "";
	    if (fieldTypeString.equals(Locale.getString("DAY_OPEN"))) {
		fieldType = Alert.OPEN_FIELD;
	    } else if (fieldTypeString.equals(Locale.getString("DAY_HIGH"))) {
		fieldType = Alert.HIGH_FIELD;
	    } else if (fieldTypeString.equals(Locale.getString("DAY_LOW"))) {
		fieldType = Alert.LOW_FIELD;
		
	    } else if (fieldTypeString.equals(Locale.getString("DAY_CLOSE"))) {
		fieldType = Alert.CLOSE_FIELD;
	    } else if (fieldTypeString.equals(Locale.getString("VOLUME"))) {
		fieldType = Alert.LOW_FIELD;
	    }
	    else {
		assert false;
	    }
	}
    }

    private boolean validateInput() {
	getInput();

	if (symbolString != null) {
	    try {
		symbol = Symbol.find(symbolString);
	    } catch (SymbolFormatException e) {
		JOptionPane.
		    showInternalMessageDialog(desktop,
					      Locale.getString("SYMBOL_NOT_FOUND"),
					      Locale.getString("SYMBOL_NOT_FOUND"),
					      JOptionPane.ERROR_MESSAGE);
	    }
	}

	try {
	    startDate = new TradingDate(startDateString,
					TradingDate.BRITISH);
	    
	    endDate = (!endDateString.equals("")) 		
		? new TradingDate(endDateString,
				  TradingDate.BRITISH) 
		: null;
	    
	} catch (TradingDateFormatException e) {		
	    String message = 
		new String(Locale.getString("ERROR_PARSING_DATE",
					    e.getDate()));
	    JOptionPane.showInternalMessageDialog(desktop, 
						  message,
						  Locale.
						  getString("ALERT_DIALOG_ERROR"),
						  JOptionPane.
						  ERROR_MESSAGE);	    
	    return false;
	}
	
	if (alertType.equals(Locale.getString("QUOTES"))) {
	    try {
		targetValue = new Double(targetString);		
	    } catch (NumberFormatException e) {
		JOptionPane.
		    showInternalMessageDialog(desktop,
					      Locale.getString(("ERROR_PARSING_NUMBER"),e.getMessage()),
					      Locale.getString("INVALID_NUMBER_TITLE"),
		    JOptionPane.ERROR_MESSAGE);
		return false;
	    }	
	} else {
	    try {
		Parser.parse(targetString);
	    } catch (ExpressionException e) {
		JOptionPane.
		    showInternalMessageDialog(desktop,
					      e.getReason(),
					      Locale.getString("ERROR_PARSING_EXPRESSION"),
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	} 
	return true;
    }
    
    private boolean checkRequiredFieldsEntered() {
	if (symbol == null &&
	    symbolTextField.getText().equals("")) {
	    return false;
	}
	if (startDateTextField.getText().equals("")) {
	    return false;
	}
	if (targetTextField.getText().equals("")) {
	    return false;
	}
	return true;	    
    }

    public void keyTyped(KeyEvent e) {
	if (checkRequiredFieldsEntered()) {
	    okButton.setEnabled(true);
	} else {
	    okButton.setEnabled(false);
	}

    }

    public void keyPressed(KeyEvent e) {
	
    }

    public void keyReleased(KeyEvent e) {
	
    }

}
