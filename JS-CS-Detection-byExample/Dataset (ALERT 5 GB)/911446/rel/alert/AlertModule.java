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

package nz.org.venice.alert;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.SwingUtilities;

import nz.org.venice.main.CommandManager;
import nz.org.venice.main.Module;
import nz.org.venice.main.ModuleFrame;
import nz.org.venice.prefs.PreferencesException;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.settings.AbstractSettings;
import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.prefs.settings.AlertModuleSettings;


import nz.org.venice.quote.Symbol;
import nz.org.venice.ui.AbstractTable;
import nz.org.venice.ui.AbstractTableModel;
import nz.org.venice.ui.Column;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.ui.MainMenu;
import nz.org.venice.ui.MenuHelper;
import nz.org.venice.ui.SymbolListDialog;
import nz.org.venice.ui.TextDialog;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingTime;

/**
 * Venice module for displaying Alerts set by the user. This module allows a
 * user to display, modify and delete alerts which will trigger when a 
 * condition is met.
 * (e.g. close above a certain value)  
 *
 * @author Mark Hummel
 * @see Alert
 */
public class AlertModule extends AbstractTable implements Module, ActionListener {

    private static final int SYMBOL_COLUMN     = 0;
    private static final int START_DATE_COLUMN = 1;
    private static final int END_DATE_COLUMN   = 2;
    private static final int TARGET_COLUMN     = 3;

    private static final int BOUND_TYPE_COLUMN = 4;
    private static final int ALERT_TYPE_COLUMN = 5;
    private static final int FIELD_COLUMN      = 6;
    private static final int DATE_SET_COLUMN   = 7;
    private static final int STATUS_COLUMN     = 8;
    
    

    private JDesktopPane desktop;

    // Main menu items
    private JMenuBar menuBar;
    private JMenuItem addAlert;
    private JMenuItem editAlert;
    private JMenuItem removeAlert;
    private JMenuItem enableAlert;
    private JMenuItem disableAlert;

    private AlertReader alertReader;
    private AlertWriter alertWriter;
    private List symbols;
    private List alerts;
    private Model model;

    private PropertyChangeSupport propertySupport;
    private AbstractSettings settings = null;
    
    

    // Frame Icon
    private String frameIcon = "nz/org/venice/images/TableIcon.gif";

    private class Model extends AbstractTableModel {
	List alerts;

	public Model(List columns, List alerts) {
	    super(columns);
	    this.alerts = alerts;
	}
		
	public void setAlerts(List alerts) {
	    this.alerts = alerts;
	    getSelectedRowCount();
	    fireTableDataChanged();	    
	}

	public int getRowCount() {
	    return alerts.size();
	}

	public Object getValueAt(int row, int column) {
	    if (row > getRowCount()) {
		return "";
	    }

	    if (column >= 9) {
		return "";
	    }
	    
	    Alert alert = (Alert)alerts.get(row);

	    switch (column) {
	    case SYMBOL_COLUMN: 
		return alert.getSymbol();
	    case START_DATE_COLUMN:
		return alert.getStartDate();
	    case END_DATE_COLUMN:
		return alert.getEndDate();
	    case TARGET_COLUMN:
		return alert.getTarget();
	    case BOUND_TYPE_COLUMN:
		switch (alert.getBoundType()) {
		case Alert.LOWER_BOUND:
		    return Locale.getString("ALERT_LOWER_BOUND"); 
		case Alert.UPPER_BOUND:
		    return Locale.getString("ALERT_UPPER_BOUND");
		case Alert.EXACT_BOUND:
		    return Locale.getString("ALERT_EXACT_BOUND");
		case Alert.GONDOLA_TRIGGER:
		    return Locale.getString("INPUT_EXPRESSION");
		default:
		    assert false;
		    return null;
		}		    		
	    case ALERT_TYPE_COLUMN:
		switch (alert.getType()) {
		case Alert.OHLCV:
		    return Locale.getString("QUOTES");
		case Alert.GONDOLA:
		    return Locale.getString("INPUT_EXPRESSION");
		default:
		    assert false;
		    return null;		
		}
	    case FIELD_COLUMN:
		if (alert.getType() == Alert.GONDOLA) {
		    return "";
		} else {
		    return alert.getField();
		}
	    case DATE_SET_COLUMN:
		return alert.getDateSet();
	    case STATUS_COLUMN:		
		return new Boolean(alert.getEnabled());
	    default:
		assert false;
		return null;
	    }
	}
    }
    

    /**
     * Create an alert module.
     *
     */
    public AlertModule(JDesktopPane desktop, 
		       AlertReader alertReader, 
		       AlertWriter alertWriter) throws AlertException {
	assert alertReader != null;
	assert alertWriter != null;
	this.alertReader = alertReader;
	this.alertWriter = alertWriter;
	this.symbols = null;
	this.desktop = desktop;

	alerts = getAlerts();

	propertySupport = new PropertyChangeSupport(this);
	
        model = new Model(createColumns(), getAlerts());
	setModel(model, SYMBOL_COLUMN, SORT_UP);
	showColumns(model);
	addMenu();
	model.addTableModelListener(this);
        resort();
    }

    public AlertModule(JDesktopPane desktop, List symbols, 
		       AlertReader alertReader, 
		       AlertWriter alertWriter) throws AlertException {
	

	assert alertReader != null;
	assert alertWriter != null;
	this.symbols = symbols;
	this.alertReader = alertReader;
	this.alertWriter = alertWriter;
	this.desktop = desktop;

	alerts = getAlerts(symbols);

	propertySupport = new PropertyChangeSupport(this);
	
        model = new Model(createColumns(), getAlerts(symbols));
	setModel(model, SYMBOL_COLUMN, SORT_UP);
	showColumns(model);
	addMenu();
	model.addTableModelListener(this);
        resort();
    }

    private List createColumns() {
	List columns = new ArrayList();

	columns.add(new Column(SYMBOL_COLUMN,
                               Locale.getString("SYMBOL"),
                               Locale.getString("SYMBOLS_COLUMN_HEADER"),
                               String.class,
                               Column.VISIBLE));

	columns.add(new Column(START_DATE_COLUMN,
			       Locale.getString("START_DATE"),
                               Locale.getString("START_DATE_COLUMN_HEADER"),
                               TradingDate.class,
                               Column.VISIBLE));
	
        columns.add(new Column(END_DATE_COLUMN,
                               Locale.getString("END_DATE"),
                               Locale.getString("END_DATE_COLUMN_HEADER"),
                               TradingDate.class,
                               Column.VISIBLE));
	
	columns.add(new Column(TARGET_COLUMN,
                               Locale.getString("ALERT_TARGET"),
                               Locale.getString("ALERT_TARGET_COLUMN_HEADER"),
                               String.class,
                               Column.VISIBLE));
	
	columns.add(new Column(BOUND_TYPE_COLUMN,
                               Locale.getString("ALERT_BOUND_TYPE"),
                               Locale.getString("ALERT_BOUND_TYPE"),
                               String.class,
                               Column.VISIBLE));

	columns.add(new Column(ALERT_TYPE_COLUMN,
                               Locale.getString("ALERT_TYPE"),
                               Locale.getString("ALERT_TYPE"),
                               String.class,
                               Column.VISIBLE));
	
	columns.add(new Column(FIELD_COLUMN,
                               Locale.getString("ALERT_FIELD_TYPE"),
                               Locale.getString("ALERT_FIELD_TYPE"),
                               String.class,
                               Column.VISIBLE));
       
	columns.add(new Column(DATE_SET_COLUMN,
                               Locale.getString("DATE_COLUMN_HEADER"),
                               Locale.getString("DATE_COLUMN_HEADER"),
                               TradingDate.class,
                               Column.VISIBLE));

	
	columns.add(new Column(STATUS_COLUMN,			      
                               Locale.getString("ALERT_ENABLED"),
                               Locale.getString("ALERT_ENABLED"),
                               Boolean.class,
                               Column.VISIBLE));
	
	
	
	
	return columns;
	
    }

    // Create a menu
    private void addMenu() {
	menuBar = new JMenuBar();

	JMenu alertMenu = MenuHelper.addMenu(menuBar, 
					     Locale.getString("ALERT_TITLE"));

	alertMenu.add(createShowColumnMenu(model));

	addAlert = MenuHelper.addMenuItem(this, alertMenu,
					  Locale.getString("ALERT_ADD"));
	
	removeAlert = MenuHelper.addMenuItem(this, alertMenu,
					     Locale.getString("ALERT_DEL"));

	editAlert = MenuHelper.addMenuItem(this, alertMenu,
					   Locale.getString("ALERT_EDIT"));

	enableAlert = MenuHelper.addMenuItem(this, alertMenu,
					     Locale.getString("ALERT_ENABLE"));

	disableAlert = MenuHelper.addMenuItem(this, alertMenu,
					      Locale.getString("ALERT_DISABLE"));


	// Listen for changes in selection so we can update the menus
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		
                public void valueChanged(ListSelectionEvent e) {
		    checkMenuDisabledStatus();
                }
            });
	
	checkMenuDisabledStatus();	
    }

    private void checkMenuDisabledStatus() {
	int numberOfSelectedRows = getSelectedRowCount();

	//Need to have a symbol to create alert	
	addAlert.setEnabled(true);
	editAlert.setEnabled(numberOfSelectedRows == 1 ? true : false);
	removeAlert.setEnabled(numberOfSelectedRows > 0 ? true : false);
	enableAlert.setEnabled(numberOfSelectedRows > 0 ? true : false);
	disableAlert.setEnabled(numberOfSelectedRows > 0 ? true : false);
	
    }

    private List getAlerts() {
	List rv = new ArrayList();
	try {
	    rv = alertReader.getAlerts();
	} catch (AlertException e) {
	    //Not sure yet if we should throw a warning or just ignore	    
	} finally {
	    return rv;
	}
    }

    private List getAlerts(List symbols) {
	List rv = new ArrayList();
	try {
	    rv = alertReader.getAlertsBySymbolList(symbols);
	} catch (AlertException e) {
	    //Not sure yet if we should throw a warning or just ignore	    
	} finally {
	    return rv;
	}
    }

    /**
     * Tell module to save any current state data / preferences data because
     * the window is being closed.
     */
    public void save() {
	settings = new AlertModuleSettings(symbols);	
    }

    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle() {
        return Locale.getString("ALERT_TITLE");
    }


    /**
     * Return frame icon for table module.
     *
     * @return	the frame icon.
     */
    public ImageIcon getFrameIcon() {
	return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(frameIcon));
    }


    /**
     * Return displayed component for this module.
     *
     * @return the component to display.
     */
    
    public JComponent getComponent() {
	return this;
    }					
    

    /**
     * Return menu bar for chart module.
     *
     * @return	the menu bar.
     */
    public JMenuBar getJMenuBar() {
	return menuBar;
    }

    /**
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane() {
	return true;
    }

    /**
     * Handle widget events.
     *
     * @param	e	action event
     */
    public void actionPerformed(final ActionEvent e) {

	int[] selectedRows = getSelectedRows();

	for (int i = 0; i < selectedRows.length; i++) {
	    Symbol symbol = (Symbol)model.getValueAt(selectedRows[i],
						     SYMBOL_COLUMN);
	}

	if (e.getSource() == addAlert) {
	    if (selectedRows.length > 0) {
		Symbol symbol = (Symbol)model.getValueAt(selectedRows[0],
							 SYMBOL_COLUMN);
		addAlert(symbol);
	    } else {
		addAlert(null);
	    }
	}
	
	if (e.getSource() == editAlert) {
	    Alert alert = (Alert)alerts.get(selectedRows[0]);
	    editAlert(alert);
	}
	
	if (e.getSource() == removeAlert) {
	    if (JOptionPane.showConfirmDialog(desktop,
					      Locale.
					      getString("ALERT_CONFIRM_DEL"),
					      Locale.getString("ALERT_DEL"),
					      JOptionPane.YES_NO_OPTION) == 
		JOptionPane.YES_OPTION) {

		for (int i = 0; i < selectedRows.length; i++) {
		    Alert alert = (Alert)alerts.get(selectedRows[i]);
		    removeAlert(alert);		
		}
		//Have to refresh the table once all alerts are remomved
		//otherwise the selection count will be out of date
		//and cause indexoutofbounds exceptions
		refresh();
	    }
	}
	
	if (e.getSource() == enableAlert) {
	    for (int i = 0; i < selectedRows.length; i++) {
		Alert alert = (Alert)alerts.get(selectedRows[i]);
		setEnabled(alert, true);			    
	    }
	    refresh();
	}
	if (e.getSource() == disableAlert) {
	    for (int i = 0; i < selectedRows.length; i++) {
		Alert alert = (Alert)alerts.get(selectedRows[i]);
		setEnabled(alert, false);				
	    }
	    refresh();
	}
    }

    private void addAlert(final Symbol s) {	
	// Handle action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread showAddDialog = new Thread() {

		public void run() {
		    AlertDialog dialog = 
			new AlertDialog(desktop, s, alertWriter);
		    
		    if (dialog.newAlert()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    refresh();
				}});
		    } 
		}
	    };
	
	showAddDialog.start();
    }

    private void editAlert(final Alert alert) {
	// Handle action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread showEditDialog = new Thread() {

		public void run() {
		    AlertDialog dialog = 
			new AlertDialog(desktop, alert, alertWriter);
		    		    
		    if (dialog.editAlert()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    refresh();

								    
				}});
		    } 
		}
	    };
	
	showEditDialog.start();
    }

    private void removeAlert(final Alert alert) {
	alertWriter.remove(alert);

    }

    private void setEnabled(final Alert alert, boolean enable) {
	if (enable) {
	    alertWriter.enable(alert);
	} else {
	    alertWriter.disable(alert);
	}
	
    }

    private void refresh() {
	alerts = getAlerts();
	model.setAlerts(alerts);
	model.fireTableDataChanged();
    }

    public void removeModuleChangeListener(PropertyChangeListener listener) {
	propertySupport.addPropertyChangeListener(listener);
    }

    public void addModuleChangeListener(PropertyChangeListener listener) {
	propertySupport.removePropertyChangeListener(listener);
    }

    public Settings getSettings() {
	return settings;
    }
}
