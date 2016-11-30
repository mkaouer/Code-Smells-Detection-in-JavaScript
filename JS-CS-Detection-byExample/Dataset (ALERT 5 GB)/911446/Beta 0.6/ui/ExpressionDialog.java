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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mov.util.Locale;

/**
 * A dialog for querying the user for an expression string.
 *
 * @author Andrew Leppard
 * @see ExpressionComboBox
 */
public class ExpressionDialog implements ActionListener
{
    // TODO: Merge expression query code into this module and delete ExpressionQuery.

    String option;
    JButton OKButton, CancelButton;
    ExpressionComboBox expressionComboBox;
    JDialog textDialog;
    JInternalFrame textFrame;
    JPanel optionPanel;
    JComponent parent;

    boolean isDone;

    /**
     * Create new expression dialog.
     *
     * @param parent The parent component to tie the dialog to
     * @param message The question to ask the user
     * @param title The title to place on the dialog
     */
    public ExpressionDialog(JComponent parent, String message, String title)
    {
	newDialog(parent, message, title, "");
    }

    /**
     * Create new expression dialog.
     *
     * @param parent The parent component to tie the dialog to
     * @param message The question to ask the user
     * @param title The title to place on the dialog
     * @param defaultExpression The default expression to display in the expression ComboBox
     */
    public ExpressionDialog(JComponent parent, String message, String title,
			  String defaultExpression) {
	newDialog(parent, message, title, defaultExpression);
    }
	
    // Create a new expression dialog
    private void newDialog(JComponent parent, String message, String title, 
			   String defaultExpression) 
    {
	this.parent = parent;

	OKButton = new JButton(Locale.getString("OK"));
	CancelButton = new JButton(Locale.getString("CANCEL"));
	expressionComboBox = new ExpressionComboBox(defaultExpression);

	JLabel label = new JLabel(message);	    

        // Make sure the label and the expression combo box are aligned
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        expressionComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        Box box = Box.createVerticalBox();
        box.add(label);
        box.add(Box.createVerticalStrut(5));
        box.add(expressionComboBox);

	OKButton.addActionListener (this);
	CancelButton.addActionListener (this);

	Object options[] = {OKButton, CancelButton};
	JOptionPane optionPane = new JOptionPane(box,
						 JOptionPane.QUESTION_MESSAGE,
						 JOptionPane.OK_CANCEL_OPTION,
						 null, options, null);

	textFrame = optionPane.createInternalFrame(parent,
						   title);
	optionPane.getRootPane().setDefaultButton(OKButton);
    }
    
    /*
     * Pops up the dialog and waits for the user to enter in an expression. 
     *
     * @return the expression
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
	    option = expressionComboBox.getExpressionText();
	    isDone = true;
	}
	else if (e.getSource () == CancelButton) {
	    option = null;
	    isDone = true;
	}

	textFrame.dispose();
    }
}




