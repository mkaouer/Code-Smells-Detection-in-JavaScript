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
 * Edit dialog for setting Symbol Metadata such as Indeces etc.
 * 
 * @author Mark Hummel
 */

package nz.org.venice.ui;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import nz.org.venice.main.ModuleFrame;
import nz.org.venice.main.CommandManager;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.quote.SymbolMetadata;
import nz.org.venice.util.Locale;

public class IndexEditorDialog extends JInternalFrame implements ActionListener, KeyListener {
    private boolean okClicked = false;
    private boolean wasCancelled = false;

    private SymbolMetadata indexSymbol = null;

    private JTextField symbolField;
    private JTextField nameField;

    private JButton okButton;
    private JButton cancelButton;
    private JButton helpButton;

    public IndexEditorDialog() {
	super();
	initialise();
    }

    public IndexEditorDialog(SymbolMetadata indexSymbolEdit) {
	super();

	indexSymbol = indexSymbolEdit;

	initialise();
    }
    
    public boolean okClicked() {
	return okClicked;
    }

    public SymbolMetadata getIndexSymbol() {
	assert okClicked == true;
	return indexSymbol;
    }

    private void initialise() {
	setSize(350,249);
	setMaximizable(false);
	setResizable(true);
	setClosable(true);
	setTitle(Locale.getString("EDIT_INDEX"));
	
	this.setContentPane(getPanel());
	
	setVisible(true);
	DesktopManager.getDesktop().add(this);
    }

    private JPanel getPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	panel.add(getTextPanel(), BorderLayout.CENTER);
	panel.add(getButtonPanel(), BorderLayout.SOUTH);

	return panel;
    }

    private JPanel getTextPanel() {
	JPanel panel = new JPanel();
	
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;
	
	panel.setLayout(layout);

	String symbolText = (indexSymbol != null) 
	    ? indexSymbol.getSymbol().toString()
	    : "";

	symbolField = GridBagHelper.addTextRow(panel, Locale.getString("STOCK"),
					       symbolText, layout, c, 8);

	symbolField.addKeyListener(this);
	symbolField.setToolTipText(Locale.getString("SYMBOL_FIELD_TOOLTIP"));
	
	String nameText = (indexSymbol != null) 
	    ? indexSymbol.getName()
	    : "";
	

	nameField = GridBagHelper.addTextRow(panel, Locale.getString("NAME"),
					       nameText, layout, c, 8);
				   
				   
	nameField.setToolTipText(Locale.getString("INDEX_NAME_TOOLTIP"));

	helpButton = GridBagHelper.addHelpButtonRow(panel, 
						    Locale.getString("INDEX_SYMBOLS_TITLE"),
						    layout,
						    c);	

	helpButton.addActionListener(this);

	return panel;
    }
 
    private JPanel getButtonPanel() {
	JPanel panel = new JPanel();
	//panel.setLayout(new BorderLayout());
	
	okButton = new JButton(Locale.getString("OK"));
	cancelButton = new JButton(Locale.getString("CANCEL"));	
	okButton.addActionListener(this);
	okButton.setEnabled(false);
	cancelButton.addActionListener(this);
       
	panel.add(okButton);
	panel.add(cancelButton);
	
	return panel;	
    }

    public void actionPerformed(ActionEvent e) {
	boolean closeDialog = false;
	if (e.getSource() == okButton) {
	    indexSymbol = 
		new SymbolMetadata(symbolField.getText(),
				   nameField.getText(),
				   true);

	    okClicked = true;
	    closeDialog = true;

	} else if (e.getSource() == cancelButton) {
	    closeDialog = true;
	} else if (e.getSource() == helpButton) {
	    CommandManager.getInstance().openHelp("Index Definition");
	} else {

	}

	if (closeDialog) {
	    try {
		setClosed(true);
	    } catch (java.beans.PropertyVetoException pve) {
		
	    }
	}
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

    private boolean checkRequiredFieldsEntered() {
	if (symbolField.getText().equals("")) {
	    return false;
	}
	return true;
    }
   
}