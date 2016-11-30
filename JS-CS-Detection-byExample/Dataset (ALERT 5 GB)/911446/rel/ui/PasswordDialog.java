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
 * Dialog for user to enter in a password.
 *
 * @see TextDialog
 * @author Mark Hummel
 */
public class PasswordDialog implements ActionListener
{
    String option;
    JButton OKButton, cancelButton;
    JPasswordField passwordField;
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
    public PasswordDialog(JComponent parent, String message, String title)
    {
	newDialog(parent, message, title);
    }
	
    // Create a new text dialog
    private void newDialog(JComponent parent, String message, String title) {
	OKButton = new JButton(Locale.getString("OK"));
	cancelButton = new JButton(Locale.getString("CANCEL"));
	passwordField = new JPasswordField();

	JLabel label = new JLabel(message);

        // Make sure the label and text field are aligned
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        Box box = Box.createVerticalBox();
        box.add(label);
        box.add(Box.createVerticalStrut(5));
        box.add(passwordField);

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
	    option = new String(passwordField.getPassword());
	    isDone = true;
	}
	else if (e.getSource () == cancelButton) {
	    option = null;
	    isDone = true;
	}
	textFrame.dispose();
    }
}




