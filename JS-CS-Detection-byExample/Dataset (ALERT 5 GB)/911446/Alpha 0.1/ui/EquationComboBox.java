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

import org.mov.prefs.*;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Extension of JComboBox used for displaying an editable equation field
 * in the venice application. This ComboBox allows the user to select
 * equations entered in the Functions preferences page. This way the user
 * does not have to keep typing in the same equations multiple times.
 *
 * <pre>
 *	EquationComboBox comboBox = new EquationComboBox();
 *	panel.add(comboBox);
 * </pre>
 */
public class EquationComboBox extends JComboBox
    implements FocusListener, PopupMenuListener {

    // Map between equation names and their equations entered in by
    // the user in the preferences page or elsewhere
    static HashMap storedEquations;

    /**
     * Create a new equation combo box.
     */
    public EquationComboBox() {
	this(new String(""));
    }

    /**
     * Create a new equation combo box with the given default equation
     * text. 
     *
     * @param	equationText	equation text to display
     */
    public EquationComboBox(String equationText) {
	super();

	setEditable(true);

	// We have to load in the stored equations from preferences
	// before we set the first equation - as it may have a name
	// Only do this the first time we are constructed - the stored
	// equations are not stored per instance
	if(storedEquations == null)
	    updateEquations();
	updateItems();

	setEquationText(equationText);

        // The combo box must be big enough to hold this text. This makes it
        // as wide as the equation combo box. Yes but on 1.4.1 it makes them short!      
        //	setPrototypeDisplayValue("avg(day_close, 15, 15) > 121");
 
	// We want to know just before the popup items become visible
	// so we can update them
	addPopupMenuListener(this);

	// We want to know when the combo box loses focus. We need this
	// to remember the last few equations that the user typed in.
	addFocusListener(this);
   }
    
    /**
     * Return the equation string in the ComboBox. If a name of an
     * equation is in the box then its equation will be returned.
     *
     * @return	equation string
     */
    public String getEquationText() {

	// Get text displayed in combo box
	String text = (String)getSelectedItem();

	// Check to see if its a stored equation name - if it is
	// well return the actual equation - not its name
	if(storedEquations.containsKey(text))
	    return (String)storedEquations.get(text);
	else 
	    return text;
    }

    /**
     * Set the equation string in the ComboBox. If the given equation
     * has a name, then the name will be displayed in the comboBox
     * instead of the equation.
     *
     * @param	equationText	equation text to display
     */
    public void setEquationText(String equationText) {

	// Check to see if the equation has a name. If it has then
	// display the name instead of the equation
	Set entries = storedEquations.entrySet();

	Iterator iterator = entries.iterator();
	while(iterator.hasNext()) {
	    Map.Entry mapEntry = (Map.Entry)iterator.next();

	    // Has this equation a name?
	    if(mapEntry.getValue().equals(equationText)) {
		setSelectedItem(mapEntry.getKey());
		return;
	    }
	}

	// If we got here the equation has no name so just print the
	// equation text
	setSelectedItem(equationText);
    }

    /**
     * Tell all equation ComboBoxes that the stored equations have
     * been modified by the user and that their popup menus need to be
     * changed.
     */
    public static void updateEquations() {
	// Load equations from preferences
	storedEquations = PreferencesManager.loadEquations();
    }

    // Rebuild option items in this combo box
    private void updateItems() {
	removeAllItems();

	Set keys = storedEquations.keySet();
	Iterator iterator = keys.iterator();

	while(iterator.hasNext()) {
	    addItem((String)iterator.next());
	}
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

    public void focusGained(FocusEvent e) {
	// nothing to do
	System.out.println("gained: equation is " + getEquationText());
    }

    public void focusLost(FocusEvent e) {
	System.out.println("lost: equation is " + getEquationText());
    }
}
