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

/**
 * Provides a preferences page for users to define which symbols are indexes.
 * For example, DAX, XAO etc
 *
 * @author mhummel
 * @see QuoteSource 
 */

//In time when we unify the datastore we can then provide the option to store
//this data elsewhere.

package nz.org.venice.prefs;


import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import nz.org.venice.quote.SymbolMetadata;
import nz.org.venice.ui.AbstractTableModel;
import nz.org.venice.ui.IndexEditorDialog;
import nz.org.venice.util.Locale;


public class IndexPreferencesPage extends JPanel implements PreferencesPage {

    private JDesktopPane desktop;
    private JTable indexTable = null;
    private AbstractTableModel tableModel = null;
    private int selectedRow = -1;
   
    private IndexEditorDialog editDialog;

    private List indexSymbols;

    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    
    private static final int SYMBOL_COLUMN = 0;
    private static final int NAME_COLUMN = 1;

    final String[] names = { Locale.getString("STOCK"),
			     Locale.getString("NAME")};

    public IndexPreferencesPage(JDesktopPane desktop) {
	this.desktop = desktop;
	initialize();
    }
    
    public JComponent getComponent() {
	return this;
    }

    public void save() {
	try {
	    PreferencesManager.putSymbolMetadata(indexSymbols);	
	} catch (PreferencesException e) {

	}
    }

    public String getTitle() {
	return Locale.getString("INDEX_SYMBOLS_TITLE");
    }

    /**
     * Initialise this object, creating panels for table and buttons.
     * 
     * @return void
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(300, 200);
        this.add(getTablePanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes the table panel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTablePanel() {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(getJScrollPane(), BorderLayout.CENTER);
        return tablePanel;
    }
    
    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        JScrollPane jScrollPane = new JScrollPane(getIndexTable());
        return jScrollPane;
    }
    
    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private JTable getIndexTable() {
	// Set up the data
	indexSymbols = new ArrayList();
	try {
	    indexSymbols = PreferencesManager.getSymbolMetadata();
	} catch (PreferencesException e) {
	    
	}

	/*
	SymbolMetadata is1 = new SymbolMetadata("XAO", "AU", true);
	SymbolMetadata is2 = new SymbolMetadata("XAB", "AU", true);
	SymbolMetadata is3 = new SymbolMetadata("XAC", "AU", true);
	indexSymbols = new java.util.ArrayList();
	indexSymbols.add(is1);
	indexSymbols.add(is2);
	indexSymbols.add(is3);
	*/
	

	// Define the table model

	tableModel = new AbstractTableModel() {
		
		public int getColumnCount() {
		    return names.length;
		}
		
		public int getRowCount() {
		    return indexSymbols.size();
		}
		
		public boolean isCellEditable(int row, int col) {
		    return false;
		}
		
		public String getColumnName(int column) {
		    return names[column];
		}
		
		
		public Class getColumnClass(int c) {
		    return getValueAt(0, c).getClass();
		}
		
		public Object getValueAt(int row, int col) {
		    SymbolMetadata index = (SymbolMetadata)indexSymbols.get(row);
		    switch (col) {		    
		    case SYMBOL_COLUMN:
			return index.getSymbol().toString();
			
		    case NAME_COLUMN:
			return index.getName();
			
		    default:
			assert false;
		    }
		    return null;
		}
		
	    };
	    
	;
		
	indexTable = new JTable(tableModel);
	indexTable.addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent e) {
		    selectedRow = indexTable.getSelectedRow();
		    if (selectedRow != -1) {
			deleteButton.setEnabled(true);
			editButton.setEnabled(true);
		    } else {
			deleteButton.setEnabled(false);
			editButton.setEnabled(true);
		    }
		}	    
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	    });
	
	return indexTable;
    }
    
    /**
     * This method creates the button panel
     * 
     * @return a panel containing the buttons.
     */
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getAddButton(), null);
        buttonPanel.add(getEditButton(), null);
        buttonPanel.add(getDeleteButton(), null);
        return buttonPanel;
    }

    /**
     * This method creates the edit button
     * 
     * @return edit button
     */
    private JButton getEditButton() {
	editButton = new JButton();
	editButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    selectedRow = indexTable.getSelectedRow();
                    editIndex();
                }
            });
	editButton.setText(Locale.getString("EDIT"));
	editButton.setEnabled(false);
    
	return editButton;    
    }

    /**
     * This method creates the add button
     * 
     * @return add button
     */
    private JButton getAddButton() {
	addButton = new JButton();
	addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
		    selectedRow = -1;		    
		    editIndex();
                }
            });
	addButton.setText(Locale.getString("ADD"));
	addButton.setEnabled(true);
    
	return addButton;    
    }

        /**
     * This method initializes the delete button
     * 
     * @return javax.swing.JButton
     */
    private JButton getDeleteButton() {
	deleteButton = new JButton();
	deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectedRow == -1) {return; }
                    SymbolMetadata index = (SymbolMetadata) indexSymbols
			.get(selectedRow);

                    int response = 
			JOptionPane.
			showInternalConfirmDialog(deleteButton,
						  Locale.getString("SURE_DELETE_SYMBOL", index.getSymbol().toString()),
						  Locale.getString("DELETE_INDEX_TITLE"),
						  
						  JOptionPane.YES_NO_OPTION);

                    if (response == JOptionPane.YES_OPTION) {
			indexSymbols.remove(selectedRow);
			try {
			    PreferencesManager.putSymbolMetadata(indexSymbols);
			} catch (PreferencesException prefsException) {

			}
			selectedRow = -1;
			tableModel.fireTableDataChanged();			
		    }
		}
	    });
	deleteButton.setText(Locale.getString("DELETE"));
    
	deleteButton.setEnabled(false);
	return deleteButton;
    }

    private void editIndex() {
	editDialog = 
	    (selectedRow != -1) 
	    ? new IndexEditorDialog((SymbolMetadata)indexSymbols.get(selectedRow))
	    : new IndexEditorDialog();
 
	editDialog.addInternalFrameListener(new InternalFrameListener() {
		public void internalFrameClosed(InternalFrameEvent ife) {
		    if (editDialog.okClicked()) {
			SymbolMetadata newIndex = editDialog.getIndexSymbol();
			
			if (selectedRow != -1) {
			    indexSymbols.remove(selectedRow);
			    indexSymbols.add(newIndex);
			} else {
			    indexSymbols.add(newIndex);
			}
			tableModel.fireTableDataChanged();
			try {
			    PreferencesManager.putSymbolMetadata(indexSymbols);
			} catch (PreferencesException e) {

			}
		    } 
		}
		public void internalFrameActivated(InternalFrameEvent arg0) {}
		public void internalFrameClosing(InternalFrameEvent e) {}
		public void internalFrameDeactivated(InternalFrameEvent e) {}
		public void internalFrameDeiconified(InternalFrameEvent arg0) {}
		public void internalFrameIconified(InternalFrameEvent arg0) {}
		public void internalFrameOpened(InternalFrameEvent arg0) {}
	    });    
    }
    
}

