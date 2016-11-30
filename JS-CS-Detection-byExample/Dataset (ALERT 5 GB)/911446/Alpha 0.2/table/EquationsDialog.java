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

package org.mov.table;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import org.mov.main.*;
import org.mov.parser.*;
import org.mov.quote.*;
import org.mov.table.*;
import org.mov.ui.*;
import org.mov.util.*;

public class EquationsDialog extends JInternalFrame 
    implements ActionListener {

    private JDesktopPane desktop;

    private JButton okButton;
    private JButton cancelButton;

    private JPanel mainPanel;
    private JPanel transactionPanel;

    // Fields of a transaction
    private JComboBox equationSlotComboBox;
    private JTextField columnNameTextField;
    private EquationComboBox equationComboBox;

    private boolean isDone = false;

    private int equationSlotCount;

    private QuoteModule.EquationSlot[] equationSlots;
    private int currentEquationSlot = 0;

    private boolean OKButtonPressed;

    public EquationsDialog(JDesktopPane desktop, int equationSlotCount) {
	super("Apply Equations");

	this.desktop = desktop;
	this.equationSlotCount = equationSlotCount;

	// Make sure we can't be hidden behind other windows
	setLayer(JLayeredPane.MODAL_LAYER);

	getContentPane().setLayout(new BorderLayout());

	mainPanel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	mainPanel.setLayout(gridbag);
	mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	JLabel typeLabel = new JLabel("Equation Slot");
	c.gridwidth = 1;
	gridbag.setConstraints(typeLabel, c);
	mainPanel.add(typeLabel);

	equationSlotComboBox = new JComboBox();

	String[] numbers = {"One", "Two", "Three", "Four", "Five",
			    "Six", "Seven", "Eight", "Nine", "Ten"};

	for(int i = 0; i < equationSlotCount; i++) {
	    equationSlotComboBox.addItem(numbers[i]);
	}
	equationSlotComboBox.addActionListener(this);

	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(equationSlotComboBox, c);
	mainPanel.add(equationSlotComboBox);

	columnNameTextField = 
	    addTextRow(mainPanel, "Column name", "", gridbag, c, 18);

	equationComboBox =
	    addEquationRow(mainPanel, "Equation", "", gridbag, c);

	JPanel buttonPanel = new JPanel();
	okButton = new JButton("OK");
	okButton.addActionListener(this);
	cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(this);
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);
  
	getContentPane().add(mainPanel, BorderLayout.NORTH);
	getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	// Open dialog in centre of window
	Dimension size = getPreferredSize();
	int x = (desktop.getWidth() - size.width) / 2;
	int y = (desktop.getHeight() - size.height) / 2;
	setBounds(x, y, size.width, size.height);
    }

    // Helper method which adds a new text field in a new row to the given 
    // grid bag layout.
    private EquationComboBox addEquationRow(JPanel panel, String field, 
					    String value,
					    GridBagLayout gridbag,
					    GridBagConstraints c) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

	EquationComboBox comboBox = new EquationComboBox(value);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(comboBox, c);
	panel.add(comboBox);

	return comboBox;
    }

    // Helper method which adds a new text field in a new row to the given 
    // grid bag layout.
    private JTextField addTextRow(JPanel panel, String field, String value,
				  GridBagLayout gridbag,
				  GridBagConstraints c,
				  int length) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

	JTextField text = new JTextField(value, length);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(text, c);
	panel.add(text);

	return text;
    }

    public boolean showDialog(QuoteModule.EquationSlot[] equationSlots) {

	// Creat copy of equation slots to work with
	this.equationSlots = new QuoteModule.EquationSlot[equationSlotCount];
	for(int i = 0; i < equationSlotCount; i++) {
	    this.equationSlots[i] = 
		(QuoteModule.EquationSlot)equationSlots[i].clone();
	}

	displayEquationSlot(0);

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

	return OKButtonPressed;
    }

    public QuoteModule.EquationSlot[] getEquationSlots() {
	return equationSlots;
    }

    private void saveEquationSlot(int slot) {
	// Store new values the user has typed in
	equationSlots[slot].columnName = columnNameTextField.getText();
	equationSlots[slot].equation = equationComboBox.getEquationText();
    }

    private void displayEquationSlot(int slot) {
	currentEquationSlot = slot;

	columnNameTextField.setText(equationSlots[slot].columnName);
	equationComboBox.setEquationText(equationSlots[slot].equation);
    }

    // Make sure the expression field is correct in each equation slot. If
    // any of the equations do not parse then display an error dialog to
    // the user.
    private boolean parseEquations() {
        boolean success = true;
        int i = 0;

        try {
            for(i = 0; i < equationSlotCount; i++) {
                String equationString = 
                    equationSlots[i].equation;
                
                if(equationString != null && equationString.length() > 0) 
                    equationSlots[i].expression = Parser.parse(equationString);
                else
                    equationSlots[i].expression = null;
            }
        }
        catch(ExpressionException e) {
            JOptionPane.
                showInternalMessageDialog(this, "Error parsing equation", 
                                          e.getReason(),
                                          JOptionPane.ERROR_MESSAGE);
            success = false;
        }

        return success;
    }

    public void actionPerformed(ActionEvent e) {

	if(e.getSource() == okButton) {
	    saveEquationSlot(currentEquationSlot);

            if(parseEquations()) {
                OKButtonPressed = true;
                dispose();
                isDone = true;
            }
	}
	else if(e.getSource() == cancelButton) {
	    saveEquationSlot(currentEquationSlot);

	    OKButtonPressed = false;
	    dispose();
	    isDone = true;
	}

	else if(e.getSource() == equationSlotComboBox) {
	    // Save the current values and display new ones
	    saveEquationSlot(currentEquationSlot);
	    displayEquationSlot(equationSlotComboBox.getSelectedIndex());
	}
    }	
}
