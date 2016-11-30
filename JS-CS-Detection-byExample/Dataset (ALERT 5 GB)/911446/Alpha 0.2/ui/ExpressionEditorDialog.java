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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.mov.main.ModuleFrame;

public class ExpressionEditorDialog {

    private boolean isUp = true;
    private JInternalFrame internalFrame;
    private String expression;

    // Width of text field: Name: [<-width->]
    private final static int NAME_WIDTH = 20;

    // Minimum & preferred size to display expression */
    private final static int EXPRESSION_ROWS = 14;
    private final static int EXPRESSION_COLUMNS = 30;

    // Whether we should display just the OK button or the OK and 
    // the cancel button
    private final static int OK_BUTTON        = 0;
    private final static int OK_CANCEL_BUTTON = 1;

    private ExpressionEditorDialog(String title, boolean displayName, 
                                   String name, String expression,
                                   int buttonArray, boolean isEditable) {
        this.expression = expression;
        assert buttonArray == OK_BUTTON || buttonArray == OK_CANCEL_BUTTON;

        buildDialog(title, displayName, name, buttonArray, isEditable);
    }

    private void buildDialog(String title, boolean displayName, String name,
                             int buttonArray, boolean isEditable) {
        internalFrame = new JInternalFrame(title,
                                           true, /* resizable */
                                           false, /* closable */
                                           false, /* maximisible */
                                           false); /* icnofiable */
	JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        if(displayName) {
            JPanel innerNamePanel = new JPanel();

            JTextField nameField = new JTextField(name, NAME_WIDTH);
            innerNamePanel.add(new JLabel("Name:"));
            innerNamePanel.add(nameField);

            JPanel namePanel = new JPanel();
            namePanel.setLayout(new BorderLayout());
            namePanel.add(innerNamePanel, BorderLayout.WEST);
            panel.add(namePanel, BorderLayout.NORTH);
        }

        JPanel expressionPanel = new JPanel();
        final JTextArea expressionEditor = new JTextArea(EXPRESSION_ROWS,
                                                         EXPRESSION_COLUMNS);
        expressionEditor.setText(expression);
        expressionEditor.setEditable(isEditable);

        TitledBorder titledBorder = new TitledBorder("Expression");
        expressionPanel.setLayout(new BorderLayout());
        expressionPanel.setBorder(titledBorder);
        expressionPanel.add(new JScrollPane(expressionEditor));

        panel.add(expressionPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Update expression
                    setExpression(expressionEditor.getText());
                    close();
                }});
        buttonPanel.add(okButton);

        // The cancel button may not be displayed
        if(buttonArray == OK_CANCEL_BUTTON) {
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // User cancelled dialog so don't modify expression
                        close();
                    }});
            buttonPanel.add(cancelButton);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        internalFrame.getContentPane().add(panel);

        Dimension preferred = internalFrame.getPreferredSize();
        internalFrame.setMinimumSize(preferred);
        ModuleFrame.setSizeAndLocation(internalFrame, DesktopManager.getDesktop(),
                                      true, true);
        DesktopManager.getDesktop().add(internalFrame);
        internalFrame.show();

	try {
	    internalFrame.setSelected(true);
	}
	catch(PropertyVetoException v) {
	    // ignore
	}
	
	internalFrame.moveToFront();		    
    }

    private void close() {
        isUp = false;
        try {
            internalFrame.setClosed(true);
        }
        catch(PropertyVetoException e) {
            // nothing to do
        }
    }

    private boolean isUp() {
        return isUp;
    }

    private String getExpression() {
        return expression;
    }

    private void setExpression(String expression) {
        this.expression = expression;
    }

    //    public static String addExpression(String expression);
    
    //public static String editExpression(String expression);

    private void waitUntilClosed() {
	try {
	    while(isUp()) 
		Thread.sleep(10);

	} catch (InterruptedException e) {
            // Finish.
	}        
    }

    // make sure you run this in its own thread - not in the swing dispatch thread!
    public static String showEditDialog(String title, String expression) {
        ExpressionEditorDialog dialog = new ExpressionEditorDialog(title, false, "",
                                                                   expression,
                                                                   OK_CANCEL_BUTTON,
                                                                   true);
        dialog.waitUntilClosed();
        return dialog.getExpression();
    }

    // make sure you run this in its own thread - not in the swing dispatch thread!
    public static String showEditDialog(String title, String name, 
                                        String expression) {
        ExpressionEditorDialog dialog = new ExpressionEditorDialog(title, true, name,
                                                                   expression,
                                                                   OK_CANCEL_BUTTON,
                                                                   true);
        dialog.waitUntilClosed();
        return dialog.getExpression();
    }


    public static void showViewDialog(String title, String expression) {
        ExpressionEditorDialog dialog = new ExpressionEditorDialog(title, false, "", 
                                                                   expression,
                                                                   OK_BUTTON,
                                                                   false);
        dialog.waitUntilClosed();
    }
}
