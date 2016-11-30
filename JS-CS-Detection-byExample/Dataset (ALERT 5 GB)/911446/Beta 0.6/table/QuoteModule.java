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

package org.mov.table;

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

import org.mov.main.*;
import org.mov.util.Locale;
import org.mov.util.TradingDate;
import org.mov.parser.*;
import org.mov.quote.*;
import org.mov.ui.AbstractTable;
import org.mov.ui.Column;
import org.mov.ui.DesktopManager;
import org.mov.ui.EODQuoteModel;
import org.mov.ui.ExpressionQuery;
import org.mov.ui.MenuHelper;
import org.mov.ui.SymbolListDialog;

/**
 * Venice module for displaying a table of stock quotes. This module allows a user
 * to display a table of specific stock quotes on specific days.
 *
 * @author Andrew Leppard
 */
public class QuoteModule extends AbstractTable implements Module, ActionListener {

    // Main menu items
    private JMenuBar menuBar;
    private JMenuItem findSymbol;
    private JMenuItem graphSymbols;
    private JMenuItem tableSymbols;
    private JMenuItem applyExpressions;
    private JMenuItem applyFilter;
    private JMenuItem sortByMostActive;
    private JMenuItem tableClose;

    // Poup menu items
    private JMenuItem popupGraphSymbols = null;
    private JMenuItem popupTableSymbols = null;

    private PropertyChangeSupport propertySupport;
    private EODQuoteBundle quoteBundle;

    private EODQuoteModel model;

    // Frame Icon
    private String frameIcon = "org/mov/images/TableIcon.gif";

    // Current expression we are filtering by
    private String filterExpressionString;

    // If set to true we only display the quotes for the last date in the
    // quote bundle
    private boolean singleDate;

    // DND objects
    //    DragSource dragSource;
    //DragGestureListener dragGestureListener;
    //DragSourceListener dragSourceListener;

    /**
     * Create a new module that lists all the quotes in the given quote bundle.
     *
     * @param quoteBundle quotes to table
     * @param singleDate if this is set to true then only display the quotes
     *                     on the last date in the quote bundle, otherwise
     *                     display them all. 
     */
    public QuoteModule(EODQuoteBundle quoteBundle,
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
    public QuoteModule(EODQuoteBundle quoteBundle,
                       String filterExpressionString,
                       boolean singleDate) {
	
	this.filterExpressionString = filterExpressionString;
	this.quoteBundle = quoteBundle;
        this.singleDate = singleDate;
	propertySupport = new PropertyChangeSupport(this);

	// Get list of quotes to display
	List quotes = extractQuotesUsingRule(filterExpressionString, quoteBundle);

        // If we are listing stocks on a single day then don't bother showing
        // the date column. On the other hand if we are only listing a single
        // stock then don't bother showing the symbol column
        model = new EODQuoteModel(quoteBundle, quotes, 
                                  singleDate? Column.HIDDEN : Column.VISIBLE,
                                  quoteBundle.getAllSymbols().size() == 1? 
                                  Column.HIDDEN : Column.VISIBLE);
	setModel(model, 
                 quoteBundle.getAllSymbols().size() == 1? EODQuoteModel.DATE_COLUMN :
                 EODQuoteModel.ACTIVITY_COLUMN, SORT_UP);
	model.addTableModelListener(this);
        showColumns(model);
        resort();

	addMenu();

        // If the user clicks on the table trap it.
	addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent evt) {
                    handleMouseClicked(evt);
                }
	    });

        // Set up DND
        //dragSource = DragSource.getDefaultDragSource();
        //dragGestureListener = new DragGestureListener();
        //dragSourceListener = new DragSourceListener();

        // component, action, listener
        //dragSource.createDefaultDragGestureRecognizer(this,
        //                                             DnDConstants.ACTION_COPY,
        //                                             dgListener);
    }


    // Graph menu item is only enabled when items are selected in the table.
    private void checkMenuDisabledStatus() {
	int numberOfSelectedRows = getSelectedRowCount();

        graphSymbols.setEnabled(numberOfSelectedRows > 0? true : false);
        tableSymbols.setEnabled(numberOfSelectedRows > 0? true : false);
    }

    // If the user double clicks on a stock with the LMB, graph the stock.
    // If the user right clicks over the table, open up a popup menu.
    private void handleMouseClicked(MouseEvent event) {

        Point point = event.getPoint();

        // Right click on the table - raise menu
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu menu = new JPopupMenu();

            popupGraphSymbols =
                MenuHelper.addMenuItem(this, menu,
                                       Locale.getString("GRAPH"));
            popupGraphSymbols.setEnabled(getSelectedRowCount() > 0);

            popupTableSymbols =
                MenuHelper.addMenuItem(this, menu,
                                       Locale.getString("TABLE"));
            popupTableSymbols.setEnabled(getSelectedRowCount() > 0);

            menu.show(this, point.x, point.y);
        }

        // Left double click on the table - graph stock
        else if(event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();
            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol
                    = (Symbol)model.getValueAt(selectedRows[i],
                                               
                                               EODQuoteModel.SYMBOL_COLUMN);
                symbols.add(symbol);
            }

            // Graph the highlighted symbols
            CommandManager.getInstance().graphStockBySymbol(symbols);
        }
    }

    // This function extracts all quotes from the quote bundle and returns
    // them as a list of Quotes.
    private List extractAllQuotes(EODQuoteBundle quoteBundle) {
        List quotes = new ArrayList();
        Iterator iterator = quoteBundle.iterator();
        TradingDate lastDate = quoteBundle.getLastDate();

        // Traverse all symbols on all dates
        while(iterator.hasNext()) {
            Quote quote = (Quote)iterator.next();
            
            if(!singleDate || (lastDate.equals(quote.getDate())))
                quotes.add(quote);
        }

        return quotes;
    }

    // Extract all quotes from the quote bundle which cause the given
    // expression to equate to true. If there is no expression (string is null or
    // empty) then extract all the quotes.
    private List extractQuotesUsingRule(String filterExpression,
                                        EODQuoteBundle quoteBundle) {      

        // If there is no rule, then just return all quotes
	if(filterExpression == null || filterExpression.length() == 0) 
            return extractAllQuotes(quoteBundle);

        // First parse the expression
        Expression expression = null;
        
        try {
            expression = Parser.parse(filterExpressionString);
        }
        catch(ExpressionException e) {
            // We should have already checked the string for errors before here
            assert false;
        }

	// Add symbols to list when expression proves true
        ArrayList quotes = new ArrayList();
        Iterator iterator = quoteBundle.iterator();
        TradingDate lastDate = quoteBundle.getLastDate();

	try {
            // Traverse all symbols on all dates
	    while(iterator.hasNext()) {
                Quote quote = (Quote)iterator.next();
                Symbol symbol = quote.getSymbol();
                TradingDate date = quote.getDate();
                int dateOffset = 0;

                try {
                    dateOffset = quoteBundle.dateToOffset(date);
                }
                catch(WeekendDateException e) {
                    assert false;
                }

                if(!singleDate || (lastDate.equals(quote.getDate()))) {
                    if(expression.evaluate(new Variables(), quoteBundle, symbol, dateOffset) >=
                       Expression.TRUE_LEVEL)
                        quotes.add(quote);
                }
	    }

	    return quotes;
	}
	catch(EvaluationException e) {
	    // Tell user expression didnt evaluate properly
	    JOptionPane.
		showInternalMessageDialog(DesktopManager.getDesktop(),
					  e.getReason() + ": " +
					  expression.toString(),
					  Locale.getString("ERROR_EVALUATION_EQUATION"),
					  JOptionPane.ERROR_MESSAGE);
	
	    // delete erroneous expression
	    expression = null;

            // If the expression didn't evaluate then just return all the quotes
            return extractAllQuotes(quoteBundle);
	}
    }

    // Create a menu
    private void addMenu() {
	menuBar = new JMenuBar();

        // Table Menu
        {
            JMenu tableMenu = MenuHelper.addMenu(menuBar, Locale.getString("TABLE"));

            // Show columns menu
            tableMenu.add(createShowColumnMenu(model));
            
            tableMenu.addSeparator();

            applyExpressions =
                MenuHelper.addMenuItem(this, tableMenu,
                                       Locale.getString("APPLY_EQUATIONS"));
            
            applyFilter =
                MenuHelper.addMenuItem(this, tableMenu,
                                       Locale.getString("APPLY_FILTER"));
            
            sortByMostActive =
                MenuHelper.addMenuItem(this, tableMenu,
                                       Locale.getString("SORT_BY_MOST_ACTIVE"));
            
            tableMenu.addSeparator();
            
            tableClose = MenuHelper.addMenuItem(this, tableMenu,
                                                Locale.getString("CLOSE"));	
            
        }

        // Symbols Menu
        {
            JMenu symbolsMenu = MenuHelper.addMenu(menuBar, Locale.getString("SYMBOLS"));
            
            findSymbol =
                MenuHelper.addMenuItem(this, symbolsMenu, Locale.getString("FIND"));

            symbolsMenu.addSeparator();
            
            graphSymbols =
                MenuHelper.addMenuItem(this, symbolsMenu,
                                       Locale.getString("GRAPH"));
            tableSymbols =
                MenuHelper.addMenuItem(this, symbolsMenu,
                                       Locale.getString("TABLE"));
        }

        // Listen for changes in selection so we can update the menus
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {		
                
                public void valueChanged(ListSelectionEvent e) {
                        checkMenuDisabledStatus();
                }
            });

	checkMenuDisabledStatus();
    }

    // Allow the user to show only stocks where the given expression is true
    private void applyFilter() {
	// Handle all action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread thread = new Thread() {

		public void run() {

		    JDesktopPane desktop =
			org.mov.ui.DesktopManager.getDesktop();

		    String expressionString =
			ExpressionQuery.getExpression(desktop,
						      Locale.getString("FILTER_BY_RULE"),
						      Locale.getString("BY_RULE"),
						      filterExpressionString);

		    if(expressionString != null) {
			filterExpressionString = expressionString;

			// Get new list of symbols to display
			final List quotes =
                            extractQuotesUsingRule(filterExpressionString, quoteBundle);

			// Update table
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    model.setQuotes(quotes);
				}});
		    }
		}};
	
	thread.start();
    }

    // Allow the user to type in a symbol string, then make sure the symbol
    // is visible and highlighted in the table
    private void findSymbol() {
	// Handle all action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread thread = new Thread() {

		public void run() {
                    JDesktopPane desktop =
                        org.mov.ui.DesktopManager.getDesktop();
        
                    Symbol symbol = SymbolListDialog.getSymbol(desktop, Locale.getString("FIND_SYMBOL"));

                    if(symbol != null) {
                        List quotes = model.getQuotes();
                        int i = 0;

                        for(Iterator iterator = quotes.iterator(); 
                            iterator.hasNext(); i++) {
                            Quote quote = (Quote)iterator.next();

                            if(symbol.equals(quote.getSymbol())) {
                                // Select row and make it visible
                                setRowSelectionInterval(i, i);
                                setVisible(i, EODQuoteModel.SYMBOL_COLUMN);
                                return;
                            }
                        }

                        // If we got here the symbol wasn't in the table
                        JOptionPane.showInternalMessageDialog(DesktopManager.getDesktop(),
							      Locale.getString("SYMBOL_X_NOT_FOUND",
									       symbol.toString()),
                                                              Locale.getString("SYMBOL_NOT_FOUND"),
                                                              JOptionPane.INFORMATION_MESSAGE);
                    }
                }};
        
        thread.start();
    }

    /**
     * Tell module to save any current state data / preferences data because
     * the window is being closed.
     */
    public void save() {
        // nothing to save to preferences
    }

    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle() {
        // Title depends on the quote bundle we are listing
	String title = Locale.getString("TABLE_OF", quoteBundle.getQuoteRange().getDescription());

        // If there is only one date it makes sense to tell the user it
        if(singleDate)
            title = title.concat(" (" + quoteBundle.getLastDate().toString("dd/mm/yyyy") + ")");

        return title;
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
	if(e.getSource() == tableClose) {
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}

	else if(e.getSource() == applyExpressions) {
	    applyExpressions(model);
	}

	else if(e.getSource() == applyFilter) {
	    applyFilter();
	}

	else if(e.getSource() == sortByMostActive) {
	    setColumnSortStatus(EODQuoteModel.ACTIVITY_COLUMN, SORT_UP);
	    resort();
	    validate();
	    repaint();
	}

        // Find symbol
        else if(e.getSource() == findSymbol)
            findSymbol();

        // Graph symbols, either by the popup menu or the main menu
        else if((popupGraphSymbols != null && e.getSource() == popupGraphSymbols) ||
                e.getSource() == graphSymbols) {

            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();

            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol = (Symbol)model.getValueAt(selectedRows[i], 
                                                         EODQuoteModel.SYMBOL_COLUMN);

                symbols.add(symbol);
            }

            // Graph the highlighted symbols
            CommandManager.getInstance().graphStockBySymbol(symbols);
        }

        // Table symbols, either by the popup menu or the main menu
        else if((popupTableSymbols != null && e.getSource() == popupTableSymbols) ||
                e.getSource() == tableSymbols) {

            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();

            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol = (Symbol)model.getValueAt(selectedRows[i], 
                                                         EODQuoteModel.SYMBOL_COLUMN);

                symbols.add(symbol);
            }

            // Table the highlighted symbols
            CommandManager.getInstance().tableStocks(symbols);
        }

	else
            assert false;
    }

}
