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

package nz.org.venice.ui;

import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;

import nz.org.venice.util.Locale;

/**
 * Replacement dialog for Java's JOptionPane dialog for querying the user to enter a
 * text field. It provides two fixes for the existing Java code.
 *
 * Firstly it allows for default text to be placed in the text input field, secondly it
 * allows the user to press return to exit the dialog.
 *
 * @author Daniel Makovec
 */
public class TextDialog implements ActionListener
{
    String option;
    JButton OKButton, cancelButton;
    JTextField textField;
    JDialog textDialog;
    JInternalFrame textFrame;
    JPanel optionPanel;

    boolean isDone;

    /**
     * Create new text dialog.
     *
     * @param parent The parent component to tie the dialog to
     * @param message The question to ask the user
     * @param title The title to place on the dialog
     */
    public TextDialog(JComponent parent, String message, String title)
    {
	newDialog(parent, message, title, "");
    }

    /**
     * Create new text dialog.
     *
     * @param parent The parent component to tie the dialog to
     * @param message The question to ask the user
     * @param title The title to place on the dialog
     * @param defaultText The default string to display in the text entry
     */
    public TextDialog(JComponent parent, String message, String title,
    		      String defaultText) {
	newDialog(parent, message, title, defaultText);
    }
	
    // Create a new text dialog
    private void newDialog(JComponent parent, String message, String title, 
			   String defaultText) 
    {
	OKButton = new JButton(Locale.getString("OK"));
	cancelButton = new JButton(Locale.getString("CANCEL"));
	textField = new JTextField();
	textField.setText(defaultText);

	JLabel label = new JLabel(message);

        // Make sure the label and text field are aligned
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        Box box = Box.createVerticalBox();
        box.add(label);
        box.add(Box.createVerticalStrut(5));
        box.add(textField);

	OKButton.addActionListener (this);
	cancelButton.addActionListener (this);

	Object options[] = {OKButton, cancelButton};
	JOptionPane optionPane = new JOptionPane(box,
						 JOptionPane.QUESTION_MESSAGE,
						 JOptionPane.OK_CANCEL_OPTION,
						 null, options, null);

	textFrame = optionPane.createInternalFrame(parent,
						   title);
	optionPane.getRootPane().setDefaultButton(OKButton);
    }
    
    /*
     * Pops up the dialog and waits for feedback
     * @return the string value the user has typed in
     */
    public String showDialog()
    {
	isDone = false;

	textFrame.show();

	try {
	    while(!isDone) 
		Thread.sleep(10);

	} catch (InterruptedException e) {
	}

	return option;
    }
    
    /**
     * ActionListener interface used for internal buttons.
     */
    public void actionPerformed(ActionEvent e)
    {
	if (e.getSource () == OKButton) {
	    option = textField.getText();
	    isDone = true;
	}
	else if (e.getSource () == cancelButton) {
	    option = null;
	    isDone = true;
	}
	textFrame.dispose();
    }
}




