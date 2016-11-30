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
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.mov.main.ModuleFrame;
import org.mov.prefs.PreferencesManager;
import org.mov.prefs.StoredExpression;
import org.mov.util.Locale;

/**
 * A dialog for adding, editing and viewing stored expressions. A stored expression is
 * an expression with a name that is saved in user preferences. Stored expressions can be
 * quickly recalled (and modified) anywhere Venice  accepts expressions.
 *
 * @author Andrew Leppard
 * @see org.mov.prefs.StoredExpression
 * @see org.mov.prefs.EquationPage
 * @see org.mov.parser.Expression
 */
public class ExpressionEditorDialog {

    private boolean isUp = true;
    private boolean wasCancelled = false;
    private JInternalFrame internalFrame;

    // Expression we are working with
    private String name;
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

    // Create a new expression editor dialog.
    private ExpressionEditorDialog(String title, boolean displayName, 
                                   String name, String expression,
                                   int buttonArray, boolean isEditable) {
	this.name = name;
        this.expression = expression;
        assert buttonArray == OK_BUTTON || buttonArray == OK_CANCEL_BUTTON;

        buildDialog(title, displayName, buttonArray, isEditable);
    }

    // Build the dialog's GUI
    private void buildDialog(String title, final boolean displayName, int buttonArray, 
			     boolean isEditable) {
        internalFrame = new JInternalFrame(title,
                                           true, /* resizable */
                                           false, /* closable */
                                           false, /* maximisible */
                                           false); /* iconifiable */
	JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

	final JTextField nameField = new JTextField(name, NAME_WIDTH);

        if(displayName) {
            JPanel innerNamePanel = new JPanel();
            innerNamePanel.add(new JLabel(Locale.getString("NAME")));
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

        TitledBorder titledBorder = new TitledBorder(Locale.getString("EQUATION"));
        expressionPanel.setLayout(new BorderLayout());
        expressionPanel.setBorder(titledBorder);
        expressionPanel.add(new JScrollPane(expressionEditor));

        panel.add(expressionPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(Locale.getString("OK"));
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Update expression
		    if(displayName)
			setName(nameField.getText());

                    setExpression(expressionEditor.getText());
		    wasCancelled = false;
                    close();
                }});
        buttonPanel.add(okButton);

        // The cancel button may not be displayed
        if(buttonArray == OK_CANCEL_BUTTON) {
            JButton cancelButton = new JButton(Locale.getString("CANCEL"));
            cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // User cancelled dialog so don't modify expression
			wasCancelled = true;
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

    private String getName() {
	return name;
    }

    private void setName(String name) {
	this.name = name;
    }

    private String getExpression() {
        return expression;
    }

    private void setExpression(String expression) {
        this.expression = expression;
    }

    private boolean waitUntilClosed() {
	try {
	    while(isUp()) 
		Thread.sleep(10);

	} catch (InterruptedException e) {
            // Finish.
	}        

	return wasCancelled;
    }

    /**
     * Show a dialog which allows the user to add a new stored expression.
     * Make sure you run this in its own thread - not in the swing dispatch thread!
     *
     * @param storedExpressions current stored expressions.
     * @param title title of dialog.
     * @param expression initial expression text
     * @return New stored expression or <code>null</code> if the user cancelled
     *         the dialog.
     */
    public static StoredExpression showAddDialog(List storedExpressions, String title,
                                                 String expression) {
	boolean isValid = false;
	String name = "";
	StoredExpression storedExpression = null;
	
	while(!isValid) {
	    ExpressionEditorDialog dialog = new ExpressionEditorDialog(title, true, 
								       name,
								       expression,
								       OK_CANCEL_BUTTON,
								       true);
	    boolean wasCancelled = dialog.waitUntilClosed();
	    name = dialog.getName();
	    expression = dialog.getExpression();

	    if(!wasCancelled) {
		isValid = validateStoredExpression(storedExpressions, null, name);

		if(isValid) 
		    storedExpression = new StoredExpression(name, expression);
	    }
	    else
		isValid = true;
	}
		
	return storedExpression;
    }

    /**
     * Show a dialog which allows the user to add a new stored expression.
     * Make sure you run this in its own thread - not in the swing dispatch thread!
     *
     * @param storedExpressions current stored expressions.
     * @param title title of dialog.
     * @return New stored expression or <code>null</code> if the user cancelled
     *         the dialog.
     */
    public static StoredExpression showAddDialog(List storedExpressions, String title) {
	return showAddDialog(storedExpressions, title, "");
    }

    /**
     * Show a dialog which allows the user to add a new stored expression.
     * Make sure you run this in its own thread - not in the swing dispatch thread!
     *
     * @param title title of dialog.
     * @param expression initial expression text
     * @return New stored expression or <code>null</code> if the user cancelled
     *         the dialog.
     */
    public static StoredExpression showAddDialog(String title, String expression) {
	List storedExpressions = PreferencesManager.loadStoredExpressions();
	StoredExpression storedExpression = showAddDialog(storedExpressions, title, expression);

	// If the user added an expression, save it to the preferences and make
	// sure all the combo boxes are updated.
	if(storedExpression != null) {
	    storedExpressions.add(storedExpression);
	    PreferencesManager.saveStoredExpressions(storedExpressions);
	    ExpressionComboBox.updateExpressions();
	}

	return storedExpression;
    }

    /**
     * Show a dialog which allows the user to edit a current stored expression.
     * Make sure you run this in its own thread - not in the swing dispatch thread!
     *
     * @param title title of dialog.
     * @param expression current expression text
     * @return Edited expression text.
     */
    public static String showEditDialog(String title, String expression) {
        ExpressionEditorDialog dialog = new ExpressionEditorDialog(title, false, "",
                                                                   expression,
                                                                   OK_CANCEL_BUTTON,
                                                                   true);
        dialog.waitUntilClosed();
        return dialog.getExpression();
    }

    /**
     * Show a dialog which allows the user to edit a current stored expression.
     * Make sure you run this in its own thread - not in the swing dispatch thread!
     *
     * @param storedExpressions current stored expressions.
     * @param title title of dialog.
     * @param storedExpression current expression.
     * @return Edited stored expression.
     */
    public static StoredExpression showEditDialog(List storedExpressions, String title, 
                                                  StoredExpression storedExpression) {
	boolean isValid = false;
	String oldName = new String(storedExpression.name);
	String name = storedExpression.name;
	String expression = storedExpression.expression;

	while(!isValid) {
	    ExpressionEditorDialog dialog = new ExpressionEditorDialog(title, true, 
								       name,
								       expression,
								       OK_CANCEL_BUTTON,
								       true);
	    boolean wasCancelled = dialog.waitUntilClosed();
	    name = dialog.getName();
	    expression = dialog.getExpression();

	    if(!wasCancelled) {
		isValid = validateStoredExpression(storedExpressions, oldName, name);

		if(isValid) {
		    storedExpression.name = name;
		    storedExpression.expression = expression;
		}
	    }
	    else
		isValid = true;
	}

	return storedExpression;
    }

    /**
     * Show a dialog which allows the user to view a current stored expression.
     * Make sure you run this in its own thread - not in the swing dispatch thread!
     *
     * @param title title of dialog.
     * @param expression expression text
     */
    public static void showViewDialog(String title, String expression) {
        ExpressionEditorDialog dialog = new ExpressionEditorDialog(title, false, "", 
                                                                   expression,
                                                                   OK_BUTTON,
                                                                   false);
        dialog.waitUntilClosed();
    }

    // Check that a stored expression is valid after the user has modified it.
    // Check for things like missing expression name or duplicate expression names.
    // Don't check the expression for syntax as we can't do this without knowing
    // the variables that will be predefined for that expression.
    private static boolean validateStoredExpression(List storedExpressions, String oldName,
                                                    String newName) {

	boolean isValid = true;

	if(newName.length() == 0) {
	    JOptionPane.showInternalMessageDialog(DesktopManager.getDesktop(),
						  Locale.getString("MISSING_EQUATION_NAME"),
						  Locale.getString("ERROR_STORING_EQUATION"),
						  JOptionPane.ERROR_MESSAGE);
	    isValid = false;
	}

	// If the name was changed, make sure it wasn't changed to an
	// existing stored expression's name.
	else if(oldName == null || !newName.equals(oldName)) {
	    boolean isDuplicateName = false;
	    
	    for(Iterator iterator = storedExpressions.iterator(); iterator.hasNext();) {
		StoredExpression traverse = (StoredExpression)iterator.next();
		if(traverse.name.equals(newName))
		    isDuplicateName = true;
		
	    }
	    
	    if(isDuplicateName) {
		JOptionPane.showInternalMessageDialog(DesktopManager.getDesktop(),
                                                      Locale.getString("DUPLICATE_EQUATION_NAME",
                                                                       newName),
						      Locale.getString("ERROR_STORING_EQUATION"),
						      JOptionPane.ERROR_MESSAGE);
		isValid = false;
	    }
	}

	return isValid;
    }
}
