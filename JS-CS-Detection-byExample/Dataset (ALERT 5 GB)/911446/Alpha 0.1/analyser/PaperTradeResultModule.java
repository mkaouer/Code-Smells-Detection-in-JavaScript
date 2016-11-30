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

package org.mov.analyser;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.mov.main.*;
import org.mov.util.*;
import org.mov.portfolio.*;
import org.mov.quote.*;
import org.mov.table.*;
import org.mov.ui.*;

public class PaperTradeResultModule extends AbstractTable 
    implements Module,
	       ActionListener {
    
    private PropertyChangeSupport propertySupport;

    private static final int START_DATE_COLUMN = 0;
    private static final int END_DATE_COLUMN = 1;
    private static final int SYMBOLS_COLUMN = 2;
    private static final int BUY_RULE_COLUMN = 3;
    private static final int SELL_RULE_COLUMN = 4;
    private static final int TRADE_COST_COLUMN = 5;
    private static final int NUMBER_OF_TRADES_COLUMN = 6;
    private static final int INITIAL_CAPITAL_COLUMN = 7;
    private static final int FINAL_CAPITAL_COLUMN = 8;
    private static final int PERCENT_RETURN_COLUMN = 9;

    private Model model;

    // Menu
    private JMenuBar menuBar;
    private JCheckBoxMenuItem showStartDateColumn;
    private JCheckBoxMenuItem showEndDateColumn;
    private JCheckBoxMenuItem showSymbolsColumn;
    private JCheckBoxMenuItem showBuyRuleColumn;
    private JCheckBoxMenuItem showSellRuleColumn;
    private JCheckBoxMenuItem showTradeCostColumn;
    private JCheckBoxMenuItem showNumberOfTradesColumn;
    private JCheckBoxMenuItem showInitialCapitalColumn;
    private JCheckBoxMenuItem showFinalCapitalColumn;
    private JCheckBoxMenuItem showReturnColumn;

    private JMenuItem graphMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem removeMenuItem;
    private JMenuItem removeAllMenuItem;
    private JMenuItem resultCloseMenuItem;

    // Popup Menu
    private JMenuItem popupOpenMenuItem;
    private JMenuItem popupGraphMenuItem;
    private JMenuItem popupRemoveMenuItem;
    private JMenuItem popupRemoveAllMenuItem;

    class Model extends AbstractTableModel {
	private String[] headers = {
	    "Start Date", "End Date", "Symbols", "Buy Rule", "Sell Rule",
	    "Trade Cost", "No. Trades", "Initial Capital", "Final Capital", 
	    "Return"};
	    
	private Class[] columnClasses = {
	    TradingDate.class, TradingDate.class, String.class, String.class,
	    String.class, PriceFormat.class, Integer.class, PriceFormat.class, 
	    PriceFormat.class, ChangeFormat.class};
	
	private Vector results;

	public Model() {
	    results = new Vector();
	}

	public PaperTradeResult getPaperTradeResult(int row) {
	    return (PaperTradeResult)results.elementAt(row);
	}

        public void removeAllResults() {
            results.removeAllElements();

            // Notify table that the whole data has changed
            fireTableDataChanged();
        }

        public Vector getResults() {
            return results;
        }

        public void setResults(Vector results) {
            this.results = results;

            // Notify table that the whole data has changed
            fireTableDataChanged();
        }

        public void addResults(Vector results) {
            this.results.addAll(results);

            // Notify table that the whole data has changed
            fireTableDataChanged();
        }
	
	public int getRowCount() {
	    return results.size();
	}

	public int getColumnCount() {
	    return headers.length;
	}
	
	public String getColumnName(int c) {
	    return headers[c];
	}

	public Class getColumnClass(int c) {
	    return columnClasses[c];
	}

	public Object getValueAt(int row, int column) {
	    if(row >= getRowCount()) 
		return "";

	    PaperTradeResult result = 
		(PaperTradeResult)results.elementAt(row);

	    if(column == START_DATE_COLUMN) {
		return result.getStartDate();
	    }

	    else if(column == END_DATE_COLUMN) {
		return result.getEndDate();
	    }

	    else if(column == SYMBOLS_COLUMN) {
		return result.getSymbols();
	    }
	    
	    else if(column == BUY_RULE_COLUMN) {
		return result.getBuyRule();
	    }

	    else if(column == SELL_RULE_COLUMN) {
		return result.getSellRule();
	    }
	    
	    else if(column == TRADE_COST_COLUMN) {
		return new PriceFormat(result.getTradeCost());
	    }

	    else if(column == NUMBER_OF_TRADES_COLUMN) {
                return new Integer(result.getNumberTrades());
	    }

	    else if(column == FINAL_CAPITAL_COLUMN) {
		return new PriceFormat(result.getFinalCapital());
	    }

	    else if(column == INITIAL_CAPITAL_COLUMN) {
		return new PriceFormat(result.getInitialCapital());
	    }

	    else if(column == PERCENT_RETURN_COLUMN) {
		return new ChangeFormat(result.getInitialCapital(),
                                        result.getFinalCapital());
	    }

	    else {
		assert false;
	    }

	    return "";
	}
    }

    public PaperTradeResultModule() {
	model = new Model();
	setModel(model);

	model.addTableModelListener(this);

	propertySupport = new PropertyChangeSupport(this);

	addMenu();

        // If the user clicks on the table trap it. 
	addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent evt) {
                    handleMouseClicked(evt);
                }
	    });
    }

    // If the user double clicks on a result with the LMB, graph the portfolio.
    // If the user right clicks over the table, open up a popup menu.
    private void handleMouseClicked(MouseEvent event) {

        Point point = event.getPoint();

        // Right click on the table - raise menu
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu menu = new JPopupMenu();

            popupOpenMenuItem =
                MenuHelper.addMenuItem(this, menu,
                                       "Open");
            popupOpenMenuItem.setEnabled(getSelectedRowCount() == 1);

            popupGraphMenuItem =
                MenuHelper.addMenuItem(this, menu,
                                       "Graph");
            popupGraphMenuItem.setEnabled(getSelectedRowCount() == 1);

            menu.addSeparator();

            popupRemoveMenuItem =
                MenuHelper.addMenuItem(this, menu,
                                       "Remove");
            popupRemoveMenuItem.setEnabled(getSelectedRowCount() >= 1);

            popupRemoveAllMenuItem =
                MenuHelper.addMenuItem(this, menu,
                                       "Remove All");
            popupRemoveAllMenuItem.setEnabled(model.getRowCount() > 0);

            menu.show(this, point.x, point.y);
        }

        // Left double click on the table - graph portfolio
        else if(event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
            graphSelectedResult();
        }
    }

    // Graphs first selected result
    private void graphSelectedResult() {
        // This will take care of the issue of if the table
        // is sorted by a different column. It'll return
        // the row number as so it wasnt sorted
        int row = getSelectedRow();
        
        // Get portfolio at row
        PaperTradeResult result = 
            model.getPaperTradeResult(row);
        
        CommandManager.getInstance().graphPortfolio(result.getPortfolio(),
                                                    result.getQuoteBundle(),
                                                    result.getStartDate(),
                                                    result.getEndDate());
    }

    // Opens first selected result
    private void openSelectedResult() {
        // This will take care of the issue of if the table
        // is sorted by a different column. It'll return
        // the row number as so it wasnt sorted
        int row = getSelectedRow();
        
        // Get portfolio at row
        PaperTradeResult result = 
            model.getPaperTradeResult(row);

        CommandManager.getInstance().openPortfolio(result.getPortfolio());
    }

    // Removes all the selected results from the table
    private void removeSelectedResults() {

        // Get selected rows and put them in order from highest to lowest
        int[] rows = getSelectedRows();
        Vector rowIntegers = new Vector();
        for(int i = 0; i < rows.length; i++) 
            rowIntegers.addElement(new Integer(rows[i]));
        Vector sortedRows = new Vector(rowIntegers);
        Collections.sort(sortedRows);
        Collections.reverse(sortedRows);

        // Now remove them from the results list starting from the highest row
        // to the lowest
        Vector results = model.getResults();
        Iterator iterator = sortedRows.iterator();

        while(iterator.hasNext()) {
            Integer rowToRemove = (Integer)iterator.next();

            results.remove(rowToRemove.intValue());
        }

        model.setResults(results);
    }

    // Some menu items are only enabled/disabled depending on what is
    // selected in the table or by the size of the table
    private void checkMenuDisabledStatus() {
	int numberOfSelectedRows = getSelectedRowCount();

        openMenuItem.setEnabled(numberOfSelectedRows == 1);
        graphMenuItem.setEnabled(numberOfSelectedRows == 1);
        removeMenuItem.setEnabled(numberOfSelectedRows >= 1);
        removeAllMenuItem.setEnabled(model.getRowCount() > 0);
    }

    // Add a menu
    private void addMenu() {
	menuBar = new JMenuBar();

	JMenu resultMenu = MenuHelper.addMenu(menuBar, "Result");

	openMenuItem = MenuHelper.addMenuItem(this, resultMenu,
                                              "Open");        
        
	graphMenuItem = MenuHelper.addMenuItem(this, resultMenu,
                                               "Graph");        
        
	resultMenu.addSeparator();

	removeMenuItem = MenuHelper.addMenuItem(this, resultMenu,
                                           "Remove");        

	removeAllMenuItem = MenuHelper.addMenuItem(this, resultMenu,
                                                   "Remove All");        

	resultMenu.addSeparator();

	JMenu columnMenu = 
	    MenuHelper.addMenu(resultMenu, "Show Columns");
	{
	    showStartDateColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Start Date");
	    showEndDateColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "End Date");
	    showSymbolsColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Symbols");
	    showBuyRuleColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Buy Rule");
	    showSellRuleColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Sell Rule");
	    showTradeCostColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Trade Cost");
	    showNumberOfTradesColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Number of Trades");
	    showInitialCapitalColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Initial Capital");
	    showFinalCapitalColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Final Capital");
	    showReturnColumn = 
		MenuHelper.addCheckBoxMenuItem(this, columnMenu,
					       "Return");

	    // Set menu items to default configuration
	    showStartDateColumn.setState(true);
	    showEndDateColumn.setState(true);
	    showSymbolsColumn.setState(true);
	    showBuyRuleColumn.setState(true);
	    showSellRuleColumn.setState(true);
	    showInitialCapitalColumn.setState(true);
	    showReturnColumn.setState(true);

	    // Tell table model not to show these columns - by default
	    // they will all be visible
	    showColumn(TRADE_COST_COLUMN, false);
	    showColumn(NUMBER_OF_TRADES_COLUMN, false);
	    showColumn(FINAL_CAPITAL_COLUMN, false);
	}

	resultMenu.addSeparator();

	resultCloseMenuItem = MenuHelper.addMenuItem(this, resultMenu,
                                                     "Close");

	// Listen for changes in selection so we can update the menus
	getSelectionModel().addListSelectionListener(new ListSelectionListener() {		

		public void valueChanged(ListSelectionEvent e) {
		    checkMenuDisabledStatus();
		}

	});

        checkMenuDisabledStatus();
    }

    public void addResults(Vector results) {
        model.addResults(results);
        checkMenuDisabledStatus();
	validate();
	repaint();
    }

    public void save() {
        // Free up precious memory
        model.removeAllResults();
    }

    public String getTitle() {
	return "Paper Trade Results";
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
	return null;
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

        // Graph selected result?
        if(e.getSource() == graphMenuItem ||
           (popupGraphMenuItem != null && e.getSource() == popupGraphMenuItem)) {
            graphSelectedResult();
        }

        // Open selected result?
        else if(e.getSource() == openMenuItem ||
           (popupOpenMenuItem != null && e.getSource() == popupOpenMenuItem)) {
            openSelectedResult();
        }

        // Remove selected results?
        else if(e.getSource() == removeMenuItem ||
           (popupRemoveMenuItem != null && e.getSource() == popupRemoveMenuItem)) {
            removeSelectedResults();
        }

        // Remove all results?
        else if(e.getSource() == removeAllMenuItem ||
                (popupRemoveAllMenuItem != null && e.getSource() == popupRemoveAllMenuItem)) {
            model.removeAllResults();
            checkMenuDisabledStatus();
        }

        // Close window?
	else if(e.getSource() == resultCloseMenuItem) {
            // When we close, free all the results to reduce memory
            model.removeAllResults();
            
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}

	else {
	    // Otherwise its a checkbox menu item
            assert e.getSource() instanceof JCheckBoxMenuItem;

	    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)e.getSource();
	    boolean state = menuItem.getState();
	    int column = START_DATE_COLUMN;

	    if(menuItem == showStartDateColumn) 
		column = START_DATE_COLUMN;

	    else if(menuItem == showEndDateColumn) 
		column = END_DATE_COLUMN;

	    else if(menuItem == showSymbolsColumn) 
		column = SYMBOLS_COLUMN;

	    else if(menuItem == showBuyRuleColumn) 
		column = BUY_RULE_COLUMN;

	    else if(menuItem == showSellRuleColumn) 
		column = SELL_RULE_COLUMN;

	    else if(menuItem == showTradeCostColumn) 
		column = TRADE_COST_COLUMN;

	    else if(menuItem == showNumberOfTradesColumn) 
		column = NUMBER_OF_TRADES_COLUMN;

	    else if(menuItem == showInitialCapitalColumn) 
		column = INITIAL_CAPITAL_COLUMN;

	    else if(menuItem == showFinalCapitalColumn) 
		column = FINAL_CAPITAL_COLUMN;

	    else if(menuItem == showReturnColumn) 
		column = PERCENT_RETURN_COLUMN;

	    showColumn(column, state);
	}

    }
}
