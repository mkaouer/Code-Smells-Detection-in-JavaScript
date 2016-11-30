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
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.mov.main.*;
import org.mov.util.*;
import org.mov.parser.*;
import org.mov.quote.*;
import org.mov.ui.*;

public class QuoteModule extends AbstractTable implements Module, ActionListener {

    private static final int EQUATION_SLOTS = 5;

    /* Column order */
    private static final int SYMBOL_COLUMN         = 0;
    private static final int DATE_COLUMN           = 1;
    private static final int VOLUME_COLUMN         = 2;
    private static final int DAY_LOW_COLUMN        = 3;
    private static final int DAY_HIGH_COLUMN       = 4;
    private static final int DAY_OPEN_COLUMN       = 5;
    private static final int DAY_CLOSE_COLUMN      = 6;
    private static final int POINT_CHANGE_COLUMN   = 7;
    private static final int PERCENT_CHANGE_COLUMN = 8;
    private static final int ACTIVITY_COLUMN       = 9;
    private static final int EQUATION_COLUMN       = 10;

    private JMenuBar menuBar;
    private JCheckBoxMenuItem showSymbolColumn;
    private JCheckBoxMenuItem showDateColumn;
    private JCheckBoxMenuItem showVolumeColumn;
    private JCheckBoxMenuItem showDayLowColumn;
    private JCheckBoxMenuItem showDayHighColumn;
    private JCheckBoxMenuItem showDayOpenColumn;
    private JCheckBoxMenuItem showDayCloseColumn;
    private JCheckBoxMenuItem showPointChangeColumn;
    private JCheckBoxMenuItem showPercentChangeColumn;
    private JCheckBoxMenuItem[] showEquationColumns =
	new JCheckBoxMenuItem[EQUATION_SLOTS];

    // Main menu items
    private JMenuItem graphSymbols;
    private JMenuItem tableSymbols;
    private JMenuItem applyEquations;
    private JMenuItem applyFilter;
    private JMenuItem sortByMostActive;
    private JMenuItem tableClose;

    // Poup menu items
    private JMenuItem popupGraphSymbols = null;
    private JMenuItem popupTableSymbols = null;

    private PropertyChangeSupport propertySupport;
    private ScriptQuoteBundle quoteBundle;

    private Model model;

    // Frame Icon
    private String frameIcon = "org/mov/images/TableIcon.gif";

    // Current equation we are filtering by
    private String filterEquationString;

    // If set to true we only display the quotes for the last date in the
    // quote bundle
    private boolean singleDate;

    // DND objects
    //    DragSource dragSource;
    //DragGestureListener dragGestureListener;
    //DragSourceListener dragSourceListener;

    public class EquationSlot {
	String columnName;
	String equation;
        Expression expression;
        List results;

	public Object clone() {
	    EquationSlot clone = new EquationSlot();
	    clone.columnName = new String(columnName);
	    clone.equation = new String(equation);

	    return clone;
	}
    }

    private EquationSlot[] equationSlots;

    private class Model extends AbstractTableModel {
	private String[] headers = {
	    "Symbol", "Date", "Volume", "Day Low", "Day High", "Day Open",
	    "Day Close", "+/-", "Change", "Activity"};

	private Class[] columnClasses = {
	    Symbol.class, TradingDate.class, Integer.class, QuoteFormat.class, QuoteFormat.class,
	    QuoteFormat.class, QuoteFormat.class, PointChangeFormat.class,
            ChangeFormat.class, Float.class};

	private ScriptQuoteBundle quoteBundle;
	private List quotes;
	private EquationSlot[] equationSlots;

	public Model(ScriptQuoteBundle quoteBundle, List quotes, EquationSlot[] equationSlots) {
            this.quoteBundle = quoteBundle;
	    this.quotes = quotes;
	    this.equationSlots = equationSlots;
	}

        public List getQuotes() {
            return quotes;
        }

	public void setQuotes(List quotes) {
	    this.quotes = quotes;
	}

	public void setEquationColumns(EquationSlot[] equationSlots) {
	    this.equationSlots = equationSlots;
	}
	
	public int getRowCount() {
	    return quotes.size();
	}

	public int getColumnCount() {
	    return headers.length + EQUATION_SLOTS;
	}
	
	public String getColumnName(int c) {
	    if(c < headers.length)
		return headers[c];
	    else
		return equationSlots[c - headers.length].columnName;
	}

	public Class getColumnClass(int c) {
	    if(c < headers.length)
		return columnClasses[c];
	    else
		return String.class;
	}
	
	public Object getValueAt(int row, int column) {

	    if(row >= getRowCount())
		return "";

            Quote quote = (Quote)quotes.get(row);

            switch(column) {
            case(SYMBOL_COLUMN):
                return quote.getSymbol();

            case(DATE_COLUMN):
                return quote.getDate();
		
            case(VOLUME_COLUMN):
                return new Integer(quote.getDayVolume());
		
            case(DAY_LOW_COLUMN):
                return new QuoteFormat(quote.getDayLow());
		
            case(DAY_HIGH_COLUMN):
                return new QuoteFormat(quote.getDayHigh());
		
            case(DAY_OPEN_COLUMN):
                return new QuoteFormat(quote.getDayOpen());
		
            case(DAY_CLOSE_COLUMN):
                return new QuoteFormat(quote.getDayClose());
		
            case(POINT_CHANGE_COLUMN):
                // Change is calculated by the percent gain between
                // yesterday's day close and today's day close. If we don't
                // have yesterday's day close available, we just use today's
                // day open.
                float finalQuote = quote.getDayClose();
                float initialQuote = quote.getDayOpen();

                try {
                    initialQuote = 
                        quoteBundle.getQuote(quote.getSymbol(),
                                             Quote.DAY_CLOSE, 
                                             quote.getDate().previous(1));
                }
                catch(MissingQuoteException e) {
                    // No big deal - we default to day open
                }

                return new PointChangeFormat(initialQuote, finalQuote);

            case(PERCENT_CHANGE_COLUMN):
                finalQuote = quote.getDayClose();
                initialQuote = quote.getDayOpen();

                try {
                    initialQuote = 
                        quoteBundle.getQuote(quote.getSymbol(),
                                             Quote.DAY_CLOSE, 
                                             quote.getDate().previous(1));
                }
                catch(MissingQuoteException e) {
                    // No big deal - we default to day open
                }

                return new ChangeFormat(initialQuote, finalQuote);
                
            case(ACTIVITY_COLUMN):
                // This column is never visible but is used to determine
                // the most active stocks - I don't actually know how to
                // calculate "the most active stocks" or whether we even
                // have enough data to do it. But this seems to be roughly
                // right.
                return new Float(quote.getDayHigh() * quote.getDayVolume());
            }
            
            if(column >= EQUATION_COLUMN) {
                int equationColumn = column - EQUATION_COLUMN;
                List results = equationSlots[equationColumn].results;
                
                // If the list doesn't exist then we haven't created any
                // equations. If it is empty, we haven't created an
                // equation in that column.
                if(results != null && results.size() > row)
                    return (Float)results.get(row);

                // No equation? Just display 0.
                else
                    return new Float(0.0F);
            }

            assert false;
	    return "";
	}
    }

    /**
     * Create a new table that lists all the quotes in the given quote bundle.
     *
     * @param quoteBundle quotes to table
     * @param singleDate if this is set to true then only display the quotes
     *                     on the last date in the quote bundle, otherwise
     *                     display them all. 
     */
    public QuoteModule(ScriptQuoteBundle quoteBundle,
                       boolean singleDate) {
	this(quoteBundle, null, singleDate);
    }

    /**
     * Create a new table that only lists the quotes in the given bundle where
     * the filter equation returns true. Set the <code>singleDate</code> flag
     * if you want to display a single day's trading - and don't want to display
     * the quotes from the bundle that may appear from executing some equations.
     * (e.g. comparing today's prices to yesterdays).
     *
     * @param quoteBundle quotes to table
     * @param filterEquationString equation string to filter by
     * @param singleDate if this is set to true then only display the quotes
     *                     on the last date in the quote bundle, otherwise
     *                     display them all. 
     */
    public QuoteModule(ScriptQuoteBundle quoteBundle,
                       String filterEquationString,
                       boolean singleDate) {
	
	this.filterEquationString = filterEquationString;
	this.quoteBundle = quoteBundle;
        this.singleDate = singleDate;

	propertySupport = new PropertyChangeSupport(this);

	// Set up equation columns
	equationSlots = new EquationSlot[EQUATION_SLOTS];

	for(int i = 0; i < EQUATION_SLOTS; i++) {
	    equationSlots[i] = new EquationSlot();
	    equationSlots[i].columnName = new String("Eqn. " + (i + 1));
	    equationSlots[i].equation = new String();
	}

	// Get list of quotes to display
	List quotes = extractQuotesUsingRule(filterEquationString, quoteBundle);

	model = new Model(quoteBundle, quotes, equationSlots);
	setModel(model, ACTIVITY_COLUMN, SORT_UP);
	addMenu();

	model.addTableModelListener(this);

	// Set menu items to hide equation columns
	for(int i = 0; i < EQUATION_SLOTS; i++) {
	    showColumn(EQUATION_COLUMN + i, false);
	}
	
	// Activity column is always hidden - its just used for
	// sorting purposes
	showColumn(ACTIVITY_COLUMN, false);

        // If we are listing stocks on a single day then don't bother showing
        // the date column. On the other hand if we are only listing a single
        // stock then don't bother showing the symbol column
        if(singleDate) {
            showColumn(DATE_COLUMN, false);
	    showDateColumn.setState(false);
            setColumnSortStatus(ACTIVITY_COLUMN, SORT_UP);
        }
        if(quoteBundle.getAllSymbols().size() == 1) {
            showColumn(SYMBOL_COLUMN, false);
	    showSymbolColumn.setState(false);
            setColumnSortStatus(DATE_COLUMN, SORT_UP);
        }

        // By default we don't show this one
	showColumn(POINT_CHANGE_COLUMN, false);

        resort();

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
                                       "Graph");
            popupGraphSymbols.setEnabled(getSelectedRowCount() > 0);

            popupTableSymbols =
                MenuHelper.addMenuItem(this, menu,
                                       "Table");
            popupTableSymbols.setEnabled(getSelectedRowCount() > 0);

            menu.show(this, point.x, point.y);
        }

        // Left double click on the table - graph stock
        else if(event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {

            int row = rowAtPoint(point);

            // Get symbol at row
            Symbol symbol =
                (Symbol)getModel().getValueAt(row, SYMBOL_COLUMN);

            List symbols = new ArrayList();
            symbols.add(symbol);

            CommandManager.getInstance().graphStockBySymbol(symbols);
        }
    }

    // This function extracts all quotes from the quote bundle and returns
    // them as a list of Quotes.
    private List extractAllQuotes(ScriptQuoteBundle quoteBundle) {
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
    // equation to equate to true. If there is no equation (string is null or
    // empty) then extract all the quotes.
    private List extractQuotesUsingRule(String filterEquation,
                                        ScriptQuoteBundle quoteBundle) {      

        // If there is no rule, then just return all quotes
	if(filterEquation == null || filterEquation.length() == 0) 
            return extractAllQuotes(quoteBundle);

        // First parse the equation
        Expression expression = null;
        
        try {
            expression = Parser.parse(filterEquationString);
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
					  "Error evaluating expression",
					  JOptionPane.ERROR_MESSAGE);
	
	    // delete erroneous expression
	    expression = null;

            // If the equation didn't evaluate then just return all the quotes
            return extractAllQuotes(quoteBundle);
	}
    }

    // Create a menu
    private void addMenu() {
	menuBar = new JMenuBar();

	JMenu tableMenu = MenuHelper.addMenu(menuBar, "Table");

	graphSymbols =
	    MenuHelper.addMenuItem(this, tableMenu,
				   "Graph");
	tableSymbols =
	    MenuHelper.addMenuItem(this, tableMenu,
				   "Table");

	tableMenu.addSeparator();

	JMenu columnMenu =
	    MenuHelper.addMenu(tableMenu, "Show Columns");
	{
	    showSymbolColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Symbol");
	    showDateColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Date");
	    showVolumeColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Volume");
	    showDayLowColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Day Low");
	    showDayHighColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Day High");
	    showDayOpenColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Day Open");
	    showDayCloseColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Day Close");
	    showPointChangeColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Point Change");
	    showPercentChangeColumn =
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Percent Change");
	    columnMenu.addSeparator();

	    // Set menu items to hide equation columns
	    for(int i = 0; i < EQUATION_SLOTS; i++) {
		showEquationColumns[i] =
		    MenuHelper.addCheckBoxMenuItem(this, columnMenu,
						   "Equation Slot " + (i + 1));
	    }

	    // Put ticks next to the columns that are visible
	    showSymbolColumn.setState(true);
	    showDateColumn.setState(true);
	    showVolumeColumn.setState(true);
	    showDayLowColumn.setState(true);
	    showDayHighColumn.setState(true);
	    showDayOpenColumn.setState(true);
	    showDayCloseColumn.setState(true);
	    showPointChangeColumn.setState(false);
	    showPercentChangeColumn.setState(true);
	}

	applyEquations =
	    MenuHelper.addMenuItem(this, tableMenu,
				   "Apply Equations");

	applyFilter =
	    MenuHelper.addMenuItem(this, tableMenu,
				   "Apply Filter");

	sortByMostActive =
	    MenuHelper.addMenuItem(this, tableMenu,
				   "Sort by Most Active");
	
	tableMenu.addSeparator();

	tableClose = MenuHelper.addMenuItem(this, tableMenu,
					    "Close");	

	// Listen for changes in selection so we can update the menus
	getSelectionModel().addListSelectionListener(new ListSelectionListener() {		

		public void valueChanged(ListSelectionEvent e) {
		    checkMenuDisabledStatus();
		}

	});

	checkMenuDisabledStatus();
    }

    // Allow the user to show only stocks where the given equation is true
    private void applyFilter() {
	// Handle all action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread thread = new Thread() {

		public void run() {

		    JDesktopPane desktop =
			org.mov.ui.DesktopManager.getDesktop();

		    String equationString =
			ExpressionQuery.getExpression(desktop,
						      "Filter by Rule",
						      "By Rule",
						      filterEquationString);

		    if(equationString != null) {
			filterEquationString = equationString;

			// Get new list of symbols to display
			final List quotes =
                            extractQuotesUsingRule(filterEquationString, quoteBundle);

			// Update table
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    model.setQuotes(quotes);
				    model.fireTableDataChanged();
				}});
		    }
		}};
	
	thread.start();
    }

    // Allow the user to enter in equations which will be run on every displayed quote
    private void applyEquations() {
	// Handle all action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread thread = new Thread() {

		public void run() {
		
		    JDesktopPane desktop =
			org.mov.ui.DesktopManager.getDesktop();

		    final EquationsDialog dialog =
			new EquationsDialog(desktop,
					    EQUATION_SLOTS);

		    // Did the user modify the equation columns?
		    if(dialog.showDialog(equationSlots)) {

                        equationSlots = dialog.getEquationSlots();

                        // Load equation columns with data
                        model.setEquationColumns(equationSlots);
                        loadEquationColumns();

                        SwingUtilities.invokeLater(new Runnable() {
				public void run() {

				    // Make sure all columns with an equation
				    // are visible and all without are not.
				    // Also update check box menus
				    for(int i = 0; i < EQUATION_SLOTS; i++) {
					boolean containsEquation = false;
					
					if(equationSlots[i].equation.length() > 0)
					    containsEquation = true;
					
					showColumn(EQUATION_COLUMN + i,
						   containsEquation);
					
					showEquationColumns[i].setState(containsEquation);
				    }
                                }});
		    }
		}
	    };

	thread.start();
    }

    // Runs out equation in each equation slot on every stock in the table
    private void loadEquationColumns() {
        List quotes = model.getQuotes();
        Thread thread = Thread.currentThread();
        ProgressDialog progress = ProgressDialogManager.getProgressDialog();
        progress.setIndeterminate(true);
        progress.show("Applying Equations");

        for(int i = 0; i < EQUATION_SLOTS; i++) {
            List results = new ArrayList();
            Expression expression = equationSlots[i].expression;

            if(expression != null) {
                Iterator iterator = quotes.iterator();

                while(iterator.hasNext()) {
                    Quote quote = (Quote)iterator.next();

                    try {
                        int dateOffset = quoteBundle.dateToOffset(quote.getDate());
                        float result = expression.evaluate(new Variables(), 
                                                           quoteBundle, quote.getSymbol(), 
                                                           dateOffset);
                        results.add(new Float(result));
                    }
                    catch(EvaluationException e) {
                        // Should display error message to user
                        assert false;
                        results.add(new Float(0.0));
                    }
                    catch(WeekendDateException e) {
                        // Shouldn't happen
                        assert false;
                        results.add(new Float(0.0));
                    }
                }
            }

            equationSlots[i].results = results;

            if(thread.isInterrupted())
                break;
        }

        ProgressDialogManager.closeProgressDialog(progress);
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
	String title = "Table of " + quoteBundle.getQuoteRange().getDescription();

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

	else if(e.getSource() == applyEquations) {
	    applyEquations();
	}

	else if(e.getSource() == applyFilter) {
	    applyFilter();
	}

	else if(e.getSource() == sortByMostActive) {
	    setColumnSortStatus(ACTIVITY_COLUMN, SORT_UP);
	    resort();
	    validate();
	    repaint();
	}

        // Graph symbols, either by the popup menu or the main menu
        else if((popupGraphSymbols != null && e.getSource() == popupGraphSymbols) ||
                e.getSource() == graphSymbols) {

            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();

            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol = (Symbol)model.getValueAt(selectedRows[i], SYMBOL_COLUMN);

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
                Symbol symbol = (Symbol)model.getValueAt(selectedRows[i], SYMBOL_COLUMN);

                symbols.add(symbol);
            }

            // Table the highlighted symbols
            CommandManager.getInstance().tableStocks(symbols);
        }

	else {
	    // Otherwise its a checkbox menu item
            assert e.getSource() instanceof JCheckBoxMenuItem;

	    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)e.getSource();
	    boolean state = menuItem.getState();
	    int column = SYMBOL_COLUMN;

	    if(menuItem == showSymbolColumn)
		column = SYMBOL_COLUMN;

	    else if(menuItem == showDateColumn)
		column = DATE_COLUMN;

	    else if(menuItem == showVolumeColumn)
		column = VOLUME_COLUMN;

	    else if(menuItem == showDayLowColumn)
		column = DAY_LOW_COLUMN;

	    else if(menuItem == showDayHighColumn)
		column = DAY_HIGH_COLUMN;

	    else if(menuItem == showDayOpenColumn)
		column = DAY_OPEN_COLUMN;

	    else if(menuItem == showDayCloseColumn)
		column = DAY_CLOSE_COLUMN;

	    else if(menuItem == showPointChangeColumn)
		column = POINT_CHANGE_COLUMN;

	    else if(menuItem == showPercentChangeColumn)
		column = PERCENT_CHANGE_COLUMN;

	    // Otherwise its an equation slot column
	    else {
		for(int i = 0; i < EQUATION_SLOTS; i++) {
		    if(menuItem == showEquationColumns[i]) {
			column = EQUATION_COLUMN + i;
		    }
		}
	    }

	    showColumn(column, state);
	}
    }

}
