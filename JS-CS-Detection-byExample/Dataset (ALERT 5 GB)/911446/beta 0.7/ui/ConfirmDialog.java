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
package org.mov.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mov.util.Locale;

/**
 * Replacement dialog for Java's JOptionPane InternalConfirmDialog
 *
 */
public class ConfirmDialog implements ActionListener
{
    boolean option;
    JButton OKButton, CancelButton;
    JDialog textDialog;
    JInternalFrame textFrame;
    JPanel optionPanel;

    boolean isDone;

    /**
     * Create new confirm dialog.
     *
     * @param parent The parent component to tie the dialog to
     * @param message The question to ask the user
     * @param title The title to place on the dialog
     */
    public ConfirmDialog(JComponent parent, String message, String title)
    {
	newDialog(parent, message, title);
    }
	
    // Create a new confirm dialog
    private void newDialog(JComponent parent, String message, String title)
    {
	OKButton = new JButton(Locale.getString("OK"));
	CancelButton = new JButton(Locale.getString("CANCEL"));

	JPanel panel = new JPanel();
	JLabel label = new JLabel(message);	    

	BorderLayout layout = new BorderLayout();
	layout.setHgap(50);
	layout.setVgap(5);

	panel.setLayout(layout);
	panel.add(label, BorderLayout.CENTER);

	OKButton.addActionListener (this);
	CancelButton.addActionListener (this);

	Object options[] = {OKButton, CancelButton};
	JOptionPane optionPane = new JOptionPane(panel,
						 JOptionPane.QUESTION_MESSAGE,
						 JOptionPane.OK_CANCEL_OPTION,
						 null, options, null);

	textFrame = optionPane.createInternalFrame(parent,
						   title);
	optionPane.getRootPane().setDefaultButton(OKButton);
    }
    
    /*
     * Pops up the dialog and waits for feedback
     * @return the boolean value the user has selected
     */
    public boolean showDialog()
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
	    option = true;
	    isDone = true;
	}
	else if (e.getSource () == CancelButton) {
	    option = false;
	    isDone = true;
	}
	textFrame.dispose();
    }
}




