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

package org.mov.portfolio;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.mov.ui.AbstractTable;
import org.mov.ui.AbstractTableModel;
import org.mov.ui.Column;
import org.mov.ui.ChangeFormat;
import org.mov.ui.PointChangeFormat;
import org.mov.ui.QuoteFormat;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;
import org.mov.main.CommandManager;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.Quote;
import org.mov.quote.EODQuoteBundle;
import org.mov.quote.Symbol;

/**
 * Display stock holdings in a swing table for a ShareAccount. This table
 * will display a row for each stock held, givings its symbol, number of
 * shares held, current day close value, current market value and its
 * change in today's trading.
 * @see ShareAccount
 */
public class StockHoldingTable extends AbstractTable {
    private static final int SYMBOL_COLUMN = 0;
    private static final int SHARES_COLUMN = 1;
    private static final int AVERAGE_COST_COLUMN = 2;
    private static final int MARKET_PRICE_COLUMN = 3;
    private static final int MARKET_VALUE_COLUMN = 4;
    private static final int POINT_CHANGE_COLUMN = 5;
    private static final int PERCENT_CHANGE_COLUMN = 6;
    private static final int PERCENT_RETURN_COLUMN = 7;

    private JMenu showColumnsMenu;
    private Model model;

    class Model extends AbstractTableModel {
	private EODQuoteBundle quoteBundle;
	private HashMap stockHoldings;
	private Object[] symbols;
	private TradingDate date;

	public Model(List columns, HashMap stockHoldings, EODQuoteBundle quoteBundle) {
            super(columns);

	    this.stockHoldings = stockHoldings;
	    this.quoteBundle = quoteBundle;

	    symbols = stockHoldings.keySet().toArray();

            // Display the latest quote dates in the bundle. The bundle
            // should contain two days of quotes - yesterday's are used
            // to properly calculate the change.
	    date = quoteBundle.getLastDate();
	}
	
	public int getRowCount() {
	    return symbols.length;
	}

	public Object getValueAt(int row, int column) {
	    if(row >= getRowCount())
		return "";
	
	    Symbol symbol = (Symbol)symbols[row];
	
	    StockHolding stockHolding =
		(StockHolding)stockHoldings.get(symbol);

	    // Shouldnt happen
	    if(stockHolding == null) {
                assert false;
		return "";
            }

            switch(column) {
            case(SYMBOL_COLUMN):
                return symbol;

            case(SHARES_COLUMN):
                return new Integer(stockHolding.getShares());

            case(AVERAGE_COST_COLUMN):
                return new QuoteFormat(stockHolding.getCost());

            case(MARKET_PRICE_COLUMN):
                try {
                    return new QuoteFormat(quoteBundle.getQuote(symbol, Quote.DAY_CLOSE, date));
                }
                catch(MissingQuoteException e) {
                    return new QuoteFormat(0.0D);
                }

            case(MARKET_VALUE_COLUMN):
                try {
                    return new Money(quoteBundle.getQuote(symbol, Quote.DAY_CLOSE, date) *
                                     stockHolding.getShares());
                }
                catch(MissingQuoteException e) {
                    return Money.ZERO;
                }

            case(PERCENT_RETURN_COLUMN):
                try {
                    return new ChangeFormat(stockHolding.getCost(),
                                            quoteBundle.getQuote(symbol, Quote.DAY_CLOSE, date));
                }
                catch(MissingQuoteException e) {
                    return new ChangeFormat(1.0D, 1.0D);
                }

            case(POINT_CHANGE_COLUMN):
                try {
                    // Change is calculated by the percent gain between
                    // yesterday's day close and today's day close. If we don't
                    // have yesterday's day close available, we just use today's
                    // day open. These first two should always work.
                    double finalQuote = quoteBundle.getQuote(symbol, Quote.DAY_CLOSE, date);
                    double initialQuote = quoteBundle.getQuote(symbol, Quote.DAY_OPEN, date);

                    // There might not be any quotes for yesterday, so don't throw an
                    // assert if we can't get any.
                    try {
                        initialQuote =
                            quoteBundle.getQuote(symbol,
                                                 Quote.DAY_CLOSE,
                                                 date.previous(1));
                    }
                    catch(MissingQuoteException e) {
                        // No big deal - we default to day open
                    }

                    return new PointChangeFormat(initialQuote, finalQuote);
                }
                catch(MissingQuoteException e) {
                    return new PointChangeFormat(1.0D, 1.0D);
                }

            case(PERCENT_CHANGE_COLUMN):
                try {
                    // Change is calculated by the percent gain between
                    // yesterday's day close and today's day close. If we don't
                    // have yesterday's day close available, we just use today's
                    // day open. These first two should always work.
                    double finalQuote = quoteBundle.getQuote(symbol, Quote.DAY_CLOSE, date);
                    double initialQuote = quoteBundle.getQuote(symbol, Quote.DAY_OPEN, date);

                    // There might not be any quotes for yesterday, so don't throw an
                    // assert if we can't get any.
                    try {
                        initialQuote =
                            quoteBundle.getQuote(symbol,
                                                 Quote.DAY_CLOSE,
                                                 date.previous(1));
                    }
                    catch(MissingQuoteException e) {
                        // No big deal - we default to day open
                    }

                    return new ChangeFormat(initialQuote, finalQuote);
                }
                catch(MissingQuoteException e) {
                    return new ChangeFormat(1.0D, 1.0D);
                }

            }
            assert false;
	    return "";
	}
    }

    /**
     * Create a new stock holding table.
     *
     * @param	stockHoldings	stock holdings for ShareAccount
     * @param	quoteBundle	the quote bundle
     */
    public StockHoldingTable(HashMap stockHoldings, EODQuoteBundle quoteBundle) {
        List columns = new ArrayList();
        columns.add(new Column(SYMBOL_COLUMN,
			       Locale.getString("SYMBOL"),
			       Locale.getString("SYMBOL_COLUMN_HEADER"),
                               Symbol.class, Column.VISIBLE));
        columns.add(new Column(SHARES_COLUMN,
			       Locale.getString("SHARES"),
			       Locale.getString("SHARES_COLUMN_HEADER"),
                               Integer.class, Column.VISIBLE));
        columns.add(new Column(AVERAGE_COST_COLUMN,
			       Locale.getString("AVERAGE_COST"),
			       Locale.getString("AVERAGE_COST_COLUMN_HEADER"),
                               QuoteFormat.class, Column.HIDDEN));
        columns.add(new Column(MARKET_PRICE_COLUMN,
			       Locale.getString("MARKET_PRICE"),
			       Locale.getString("MARKET_PRICE_COLUMN_HEADER"),
                               QuoteFormat.class, Column.VISIBLE));
        columns.add(new Column(MARKET_VALUE_COLUMN,
			       Locale.getString("MARKET_VALUE"),
			       Locale.getString("MARKET_VALUE_COLUMN_HEADER"),
                               Money.class, Column.VISIBLE));
        columns.add(new Column(POINT_CHANGE_COLUMN,
			       Locale.getString("POINT_CHANGE"),
			       Locale.getString("POINT_CHANGE_COLUMN_HEADER"),
                               PointChangeFormat.class, Column.HIDDEN));
        columns.add(new Column(PERCENT_CHANGE_COLUMN,
			       Locale.getString("PERCENT_CHANGE"),
			       Locale.getString("PERCENT_CHANGE_COLUMN_HEADER"),
                               ChangeFormat.class, Column.VISIBLE));
        columns.add(new Column(PERCENT_RETURN_COLUMN,
			       Locale.getString("PERCENT_RETURN"),
			       Locale.getString("PERCENT_RETURN_COLUMN_HEADER"),
                               ChangeFormat.class, Column.HIDDEN));

        model = new Model(columns, stockHoldings, quoteBundle);
	setModel(model);

	// If the user double clicks on a row then graph the stock
	addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent event) {
                    handleMouseClicked(event);
                }
            });

        showColumns(model);
        showColumnsMenu = createShowColumnMenu(model);
    }

    // If the user double clicks on a stock with the LMB, graph the stock.
    // If the user right clicks over the table, open up a popup menu.
    private void handleMouseClicked(MouseEvent event) {
        Point point = event.getPoint();

        // Right click on the table - raise menu
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu menu = new JPopupMenu();

            // Show Columns Menu
            {
                menu.add(showColumnsMenu);
            }

            menu.addSeparator();

            // Graph
            {
                JMenuItem popupGraphSymbols = new JMenuItem(Locale.getString("GRAPH"));
                popupGraphSymbols.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent e) {
                            int[] selectedRows = getSelectedRows();
                            List symbols = new ArrayList();

                            for(int i = 0; i < selectedRows.length; i++) {
                                int row = getSortedRow(selectedRows[i]);

                                Symbol symbol =
                                    (Symbol)getModel().getValueAt(row, SYMBOL_COLUMN);

                                symbols.add(symbol);
                            }

                            // Graph the highlighted symbols
                            CommandManager.getInstance().graphStockBySymbol(symbols);
                        }
                    });

                popupGraphSymbols.setEnabled(getSelectedRowCount() > 0);
                menu.add(popupGraphSymbols);
            }

            // Table
            {
                JMenuItem popupTableSymbols = new JMenuItem(Locale.getString("TABLE"));
                popupTableSymbols.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent e) {
                            int[] selectedRows = getSelectedRows();
                            List symbols = new ArrayList();

                            for(int i = 0; i < selectedRows.length; i++) {
                                int row = getSortedRow(selectedRows[i]);

                                Symbol symbol =
                                    (Symbol)getModel().getValueAt(row, SYMBOL_COLUMN);

                                symbols.add(symbol);
                            }

                            // Table the highlighted symbols
                            CommandManager.getInstance().tableStocks(symbols);
                        }
                    });

                popupTableSymbols.setEnabled(getSelectedRowCount() > 0);
                menu.add(popupTableSymbols);
            }

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
}
