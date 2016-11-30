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

package org.mov.prefs;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mov.util.Locale;

/**
 * Provides a preference page to let the user manage stored equations.
 * Stored equations are equations which are mapped to names so that
 * the user does not have to type in the same equations each time they
 * are required, but can instead reference them with a name.
 *
 * @author Daniel Makovec
 */
public class EquationPage extends JPanel implements PreferencesPage
{
    /** The desktop that new windows are opened upon */
    private JDesktopPane desktop;
    
    private EquationTable equationTable;
    private JButton addEquationButton;
    private JButton editEquationButton;
    private JButton deleteEquationsButton;
    
    /**
     * Create a new stored equation preferences page.
     *
     * @param desktop the parent desktop.
     */
    public EquationPage(JDesktopPane desktop) {
        this.desktop = desktop;

	setLayout(new BorderLayout());
	equationTable = new EquationTable();
	add(new JScrollPane(equationTable), BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel();
	addEquationButton = new JButton(Locale.getString("ADD"));
	addEquationButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    equationTable.add();
		    checkButtonDisabledStatus();
		}});

	editEquationButton = new JButton(Locale.getString("EDIT"));
	editEquationButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    int[] selectedRows = equationTable.getSelectedRows();
		    if(selectedRows.length == 1)
			equationTable.edit(selectedRows[0]);
		}});

	deleteEquationsButton = new JButton(Locale.getString("DELETE"));
	deleteEquationsButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			equationTable.delete(equationTable.getSelectedRows());
			checkButtonDisabledStatus();
		}});
						
	buttonPanel.add(addEquationButton);
	buttonPanel.add(editEquationButton);
	buttonPanel.add(deleteEquationsButton);
	
	add(buttonPanel, BorderLayout.SOUTH);
	checkButtonDisabledStatus();

	// If the user double clicks on an equation, edit the equation
	equationTable.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent event) {
                    handleMouseClicked(event);
                }
            });
	
	// Update the button enabled/disabled status depending on what is selected
	equationTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    checkButtonDisabledStatus();
		}
	});
    }

    // If the user double clicks on an equation with the LMB, edit the equation
    private void handleMouseClicked(MouseEvent event) {
	if(event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
	    Point point = event.getPoint();
	    int row = equationTable.getUnsortedRow(equationTable.rowAtPoint(point));
	    equationTable.edit(row);
	}
    }

    // Enable or disable the buttons depending on how many equations are highlighted
    // in the table
    private void checkButtonDisabledStatus() {
	int numberOfSelectedRows = equationTable.getSelectedRowCount();
	editEquationButton.setEnabled(numberOfSelectedRows == 1);
	deleteEquationsButton.setEnabled(numberOfSelectedRows > 0);
    }

    /**
     * Update the preferences file.
     */
    public void save() {
	equationTable.save();
    }    

    /**
     * Return displayed component for this page.
     *
     * @return the component to display.
     */
    public JComponent getComponent() {
        return this;
    }    

    /**
     * Return the window title.
     *
     * @return	the window title.
     */
    public String getTitle() {
        return Locale.getString("EQUATION_PAGE_TITLE");
    }
}
