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

import java.awt.Point;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.mov.main.CommandManager;
import org.mov.prefs.PreferencesManager;
import org.mov.prefs.PreferencesModule;
import org.mov.prefs.StoredExpression;
import org.mov.util.Locale;

/**
 * Extension of JComboBox used for displaying an editable expression field
 * in the venice application. This ComboBox allows the user to select
 * expressions entered in the Functions preferences page. This way the user
 * does not have to keep typing in the same expressions multiple times.
 *
 * <pre>
 *	ExpressionComboBox comboBox = new ExpressionComboBox();
 *	panel.add(comboBox);
 * </pre>
 *
 * @author Andrew Leppard
 */
public class ExpressionComboBox extends JComboBox implements PopupMenuListener {

    // Array of user's stored expressions, entered via the prefs page or elsewhere
    static List storedExpressions;

    private boolean isDialogUp = false;
    private JTextField textField;

    /**
     * Create a new expression combo box.
     */
    public ExpressionComboBox() {
	this("");
    }

    /**
     * Create a new expression combo box with the given default expression
     * text.
     *
     * @param	expressionText	expression text to display
     */
    public ExpressionComboBox(String expressionText) {
	super();

	setEditable(true);

	// We have to load in the stored expressions from preferences
	// before we set the first expression - as it may have a name
	// Only do this the first time we are constructed - the stored
	// expressions are not stored per instance
	if(storedExpressions == null)
	    updateExpressions();

	setExpressionText(expressionText);

        // The combo box must be big enough to hold this text. This makes it
        // as wide as the expression combo box. Yes but on 1.4.1 it makes them short!
        //	setPrototypeDisplayValue("avg(day_close, 15, 15) > 121");

	// We want to know just before the popup items become visible
	// so we can update them
	addPopupMenuListener(this);

	// Locate the JTextField so we can catch menu events on it and
	// also so we can read the text directly from it.
        for(int i = 0; i < getComponentCount(); i++) {
            Component component = getComponent(i);

            if(component instanceof JTextField)
		textField = (JTextField)component;
	}

        // We want to catch right mouse buttons on the text field
	textField.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent event) {
		    handleMouseClicked(event);
		}
	    });

	updateItems();
   }

    /**
     * Return the expression string in the ComboBox. If a name of an
     * expression is in the box then its expression will be returned.
     *
     * @return	expression string
     */
    public String getExpressionText() {

	// Get text displayed in combo box
	String text = getText();

	// Check to see if its a stored expression name - if it is
	// well return the actual expression - not its name
	StoredExpression storedExpression = findStoredExpressionByName(text);

	if(storedExpression != null)
	    return storedExpression.expression;
	else
	    return text;
    }

    /**
     * Return whether the current displayed expression is a stored expression.
     * A stored expression is one the user has entered and can refer
     * to by using a keyword.
     *
     * @return <code>true</code> if it is a stored expression.
     */
    public boolean isStoredExpression() {
	return findStoredExpressionByName(getText()) != null;
    }

    /**
     * Set the expression string in the ComboBox. If the given expression
     * has a name, then the name will be displayed in the comboBox
     * instead of the expression.
     *
     * @param	expressionText	expression text to display
     */
    public void setExpressionText(String expressionText) {
	// Check to see if the expression has a name. If it has then
	// display the name instead of the expression
	StoredExpression storedExpression = findStoredExpressionByExpression(expressionText);
	
	if(storedExpression != null)
	    setSelectedItem(storedExpression.name);
	else
	    setSelectedItem(expressionText);
    }

    /**
     * Tell all expression ComboBoxes that the stored expressions have
     * been modified by the user and that their popup menus need to be
     * changed.
     */
    public static void updateExpressions() {
	// Load expressions from preferences
	storedExpressions = PreferencesManager.getStoredExpressions();
    }

    // Searches through list of expressions for the one with the given name
    private StoredExpression findStoredExpressionByName(String name) {
	for(Iterator iterator = storedExpressions.iterator(); iterator.hasNext();) {
	    StoredExpression storedExpression = (StoredExpression)iterator.next();
	    if(storedExpression.name.equals(name))
		return storedExpression;
	}

	// If we got here we couldn't find it
	return null;
    }

    // Searches through list of expressions for the one with the given expression
    private StoredExpression findStoredExpressionByExpression(String expression) {
	for(Iterator iterator = storedExpressions.iterator(); iterator.hasNext();) {
	    StoredExpression storedExpression = (StoredExpression)iterator.next();
	    if(storedExpression.expression.equals(expression))
		return storedExpression;
	}

	// If we got here we couldn't find it
	return null;
    }

    // Rebuild option items in this combo box
    private void updateItems() {
	// First construct a new menu that begins with the current expression
	// shown, then a sorted list of all the stored expressions. If the
	// current expression is a stored expression, make sure we don't show it
	// twice.

	// Construct menu items
	List menuItems = new ArrayList();

	String current = getText();
	menuItems.add(current);

	List stored = new ArrayList();
	
	for(Iterator iterator = storedExpressions.iterator(); iterator.hasNext();) {
	    StoredExpression storedExpression = (StoredExpression)iterator.next();

	    if(!storedExpression.name.equals(current))
		stored.add(storedExpression.name);
	}

	Collections.sort(stored);
	menuItems.addAll(stored);

	// Remove previous menu items
	removeAllItems();

	// Display new menu items
	for(Iterator iterator = menuItems.iterator(); iterator.hasNext();)
	    addItem((String)iterator.next());
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
	// nothing to do
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	// nothing to do
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	// Make sure menu is up to date
	updateItems();
    }

    private void showAddDialog() {
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    if(!isDialogUp) {
                        isDialogUp = true;

			StoredExpression storedExpression = 
                            ExpressionEditorDialog.showAddDialog(storedExpressions, 
								 Locale.getString("ADD_EQUATION"),
								 getText());

			if(storedExpression != null) {
			    setExpressionText(storedExpression.name);
			    storedExpressions.add(storedExpression);
			    PreferencesManager.putStoredExpressions(storedExpressions);
			}

                        isDialogUp = false;
                    }
                }});
                                   
        thread.start();
    }

    private void showDeleteDialog() {
	if(!isDialogUp) {
	    isDialogUp = true;

	    StoredExpression storedExpression = findStoredExpressionByName(getText());

	    if(storedExpression != null) {
		int option = 
		    JOptionPane.showInternalConfirmDialog(DesktopManager.getDesktop(),
							  Locale.getString("SURE_DELETE_EQUATION",
									   getText()),
							  Locale.getString("DELETE_EQUATION"),
							  JOptionPane.YES_NO_OPTION);
		if(option == JOptionPane.YES_OPTION) {
		    storedExpressions.remove(storedExpression);
		    PreferencesManager.putStoredExpressions(storedExpressions);
		    setExpressionText("");
		}       
	    }		

	    isDialogUp = false;
	}       
    }

    private void showEditDialog() {
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    if(!isDialogUp) {
                        isDialogUp = true;

			if(isStoredExpression()) {
			    // Edit the stored expression - provides an expression
			    // name field as well as the expression field.
			    StoredExpression storedExpression = 
				findStoredExpressionByName(getText());
			    if (storedExpression != null) {
				storedExpression = 
				    ExpressionEditorDialog.showEditDialog(storedExpressions,
									  Locale.getString("EDIT_EQUATION"),
									  storedExpression);
				setExpressionText(storedExpression.expression);
				PreferencesManager.putStoredExpressions(storedExpressions);
			    }
			}
			else {
			    // Edit the expression - but do not provide an
			    // expression name field as this isn't a stored
			    // expression.
			    String expressionText = getExpressionText();
			    String newExpressionText = 
				ExpressionEditorDialog.showEditDialog(Locale.getString("EDIT_EQUATION"),
								      expressionText);

			    setExpressionText(newExpressionText);
			}
                        isDialogUp = false;
                    }
                }});
                                   
        thread.start();
    }

    // When we want to read the text displayed in this widget we read
    // directly from the textfield. We do this because the getSelectedItem()
    // function sometimes does not return the currently displayed text.
    private String getText() {
	return textField.getText();
    }

    private void handleMouseClicked(final MouseEvent event) {

        // Right click on the table - raise menu
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu menu = new JPopupMenu();
            
            JMenuItem editMenuItem = new JMenuItem(Locale.getString("EDIT"));
            editMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showEditDialog();
                    }});
            
            menu.add(editMenuItem);

            boolean isStoredExpression = isStoredExpression();

            JMenuItem addMenuItem = new JMenuItem(Locale.getString("ADD"));
            addMenuItem.setEnabled(!isStoredExpression);
            addMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
			showAddDialog();
                    }});
            menu.add(addMenuItem);
            
            JMenuItem deleteMenuItem = new JMenuItem(Locale.getString("DELETE"));
            deleteMenuItem.setEnabled(isStoredExpression);
            deleteMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
			showDeleteDialog();
                    }});
            menu.add(deleteMenuItem);

	    menu.addSeparator();
	    
	    JMenuItem manageMenuItem = new JMenuItem(Locale.getString("MANAGE"));
	    manageMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			CommandManager commandManager = CommandManager.getInstance();
			commandManager.openPreferences(PreferencesModule.EQUATION_PAGE);
		    }});
	    menu.add(manageMenuItem);
          
            Point point = event.getPoint();
            menu.show(this, point.x, point.y);
        }
    }
}
