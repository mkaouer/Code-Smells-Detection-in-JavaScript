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

package org.mov.chart;

import java.beans.PropertyVetoException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;

import org.mov.ui.DesktopManager;
import org.mov.util.Locale;
import org.mov.prefs.PreferencesManager;

/**
 * Provides a text area for which a user can make notes regarding thie stock symbol being graphed.
 *
 * @author Mark Hummel
 */
public class UserNotes extends JInternalFrame {

    private JTextArea notes;
    private String symbol;    

    public UserNotes(String name) {

	String prevText = "";
	
	symbol = name;

	String frameTitle = "Notes for " + name;
	setTitle(frameTitle);		    
	setSize(250,250);
	setVisible(true);
	setResizable(true);
	setClosable(true);
	setIconifiable(true);
	setMaximizable(true);
	DesktopManager.getDesktop().add(this);

	JPanel notePane = new JPanel();
	JPanel buttonPane = new JPanel();
	
	notes = new JTextArea(10,15);

	prevText = PreferencesManager.loadUserNotes(symbol);
	if (prevText != "") {
	    notes.setText(prevText);
	}

	JScrollPane noteContainer = new JScrollPane(notes);
		
	JButton save = new JButton(Locale.getString("SAVE"));
	JButton close = new JButton(Locale.getString("CLOSE"));
	JButton revert = new JButton(Locale.getString("REVERT"));
	
	save.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    try {
			PreferencesManager.saveUserNotes(symbol, notes.getText());
			setClosed(true);
		    } 
		    catch (PropertyVetoException pve) {

		    }
		}});
	
	close.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    try {
			setClosed(true);
		    }
		    catch (PropertyVetoException pve) {
			
		    }		    
		}});

	revert.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String foo = PreferencesManager.loadUserNotes(symbol);
		    notes.setText(foo);
		    
		}
	    });
	
	
	notePane.add(noteContainer, BorderLayout.CENTER);
	buttonPane.add(save, BorderLayout.WEST);
	buttonPane.add(close, BorderLayout.CENTER);
	buttonPane.add(revert, BorderLayout.EAST);
	getContentPane().add(notePane, BorderLayout.CENTER);		 
	getContentPane().add(buttonPane,BorderLayout.SOUTH);
	
    }

    public void setText(String text) {

    }
}
