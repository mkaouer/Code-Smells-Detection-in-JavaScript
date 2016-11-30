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

package nz.org.venice.table;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nz.org.venice.main.*;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;
import nz.org.venice.parser.*;
import nz.org.venice.quote.*;
import nz.org.venice.chart.ChartTracking;
import nz.org.venice.ui.AbstractTable;
import nz.org.venice.ui.Column;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.ui.EODQuoteModel;
import nz.org.venice.ui.ExpressionQuery;
import nz.org.venice.ui.MenuHelper;
import nz.org.venice.ui.SymbolListDialog;
import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.prefs.settings.QuoteModuleSettings;

/**
 * Venice module for displaying the value of the quote under a cursor. As the
 * selected row changes, the cursor moves and vice versa.
 *
 * @see QuoteModule
 * @author Mark Hummel
 */
public class TrackedQuoteModule extends QuoteModule {

    private ChartTracking chartTracker = null;
    private PropertyChangeSupport propertySupport;
    private int position; //Which row in the table is selected
    private QuoteModuleSettings settings = null;

    /**
     * Create a new module that lists all the quotes in the given quote bundle.
     *
     * @param quoteBundle quotes to table
     * @param singleDate if this is set to true then only display the quotes
     *                     on the last date in the quote bundle, otherwise
     *                     display them all.
     */
    public TrackedQuoteModule(EODQuoteBundle quoteBundle,
                       boolean singleDate) {
	this(quoteBundle, null, singleDate);
    }

    /**
     * Create a new module that only lists the quotes in the given bundle where
     * the filter expression returns true. Set the <code>singleDate</code> flag
     * if you want to display a single day's trading - and don't want to display
     * the quotes from the bundle that may appear from executing some expressions.
     * (e.g. comparing today's prices to yesterdays).
     *
     * @param quoteBundle quotes to table
     * @param filterExpressionString expression string to filter by
     * @param singleDate if this is set to true then only display the quotes
     *                     on the last date in the quote bundle, otherwise
     *                     display them all.
     */
    public TrackedQuoteModule(EODQuoteBundle quoteBundle,
                       String filterExpressionString,
                       boolean singleDate) {

	super(quoteBundle, filterExpressionString, singleDate);		
	propertySupport = new PropertyChangeSupport(this);
	position = 0;
    }

    
    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle() {
        // Title depends on the quote bundle we are listing
	String title = Locale.getString("TRACKED_TABLE_OF", quoteBundle.getQuoteRange().getDescription());

        // If there is only one date it makes sense to tell the user it
        if(singleDate)
            title = title.concat(" (" + quoteBundle.getLastDate().toString("dd/mm/yyyy") + ")");

        return title;
    }


    /**
     * Set the reference to the ChartTracking object which will manage
     * the cursor location on the chart.
     * @param chartTracker The ChartTracking reference
     */
    public void setTracker(ChartTracking chartTracker) {
	this.chartTracker = chartTracker;
    }

    /**
     * Close the window of this object. Call from ChartTracking when the 
     * corresponding ChartModule is closed.
     * 
     */
    public void close() {
	propertySupport.
	    firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
    }

    //Changes when row selected by mouse or keyboard
    protected void checkMenuDisabledStatus() {
	super.checkMenuDisabledStatus();

	int rowCount = getRowCount();
	       
	int[] selectedRows = getSelectedRows();
	
	//This happens when the table is first created.
	if (selectedRows.length <= 0) {
	    return;
	}

	//Only interested in the last selection, if there's more than one
	int position = selectedRows[selectedRows.length - 1];
	
	chartTracker.setPosition(position);
    }

    /**
     * Change the currently selected row.
     * @param position The row of the table to select.
     */
    public void setPosition(int position) {	
	if (position < 0 || position >= getRowCount()) {
	    return;
	}
	this.position = position;
	changeSelection(position, 0 , false, false);	
    }

    /** 
     * @return The currently selected row of the table.
     */
    public int getPosition() {
	return position;
    }

    /**
     * Add a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void addModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void removeModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    //Don't want to save the state of the tracker    
    public void save() {
	
    }

}
