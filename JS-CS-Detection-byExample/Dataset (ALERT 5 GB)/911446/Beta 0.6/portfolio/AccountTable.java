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

import java.util.ArrayList;
import java.util.List;

import org.mov.ui.AbstractTable;
import org.mov.ui.AbstractTableModel;
import org.mov.ui.AccountNameFormat;
import org.mov.ui.ChangeFormat;
import org.mov.ui.Column;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.EODQuoteBundle;

/**
 * Display an account summary in a swing table for a portfolio. The table
 * will display a row for each account, giving its name and its current
 * value.
 * @see Portfolio
 */
public class AccountTable extends AbstractTable {

    private static final int ACCOUNT_COLUMN        = 0;
    private static final int MARKET_VALUE_COLUMN   = 1;
    private static final int PERCENT_CHANGE_COLUMN = 2;

    class Model extends AbstractTableModel {

	private EODQuoteBundle quoteBundle   = null;
	private Portfolio todayPortfolio     = null;
        private Portfolio yesterdayPortfolio = null;
	private TradingDate date             = null;

	public Model(List columns, Portfolio portfolio, EODQuoteBundle quoteBundle) {
            super(columns);

            // Use the latest date in the quote bundle
	    date = quoteBundle.getLastDate();

	    this.quoteBundle = quoteBundle;
	    todayPortfolio = portfolio;

            yesterdayPortfolio = portfolio.getPortfolio(date.previous(1));
	}
	
	public int getRowCount() {
	    // One row per account plus a total row
	    return (todayPortfolio.getAccounts().size() + 1);
	}

        private Object getAccountValueAt(int row, int column) {
            Account todayAccount = (Account)todayPortfolio.getAccounts().get(row);

            switch(column) {
            case(ACCOUNT_COLUMN):
                return new AccountNameFormat(todayAccount.getName());
		
            case(MARKET_VALUE_COLUMN):
                try {
                    return todayAccount.getValue(quoteBundle, date);
                }
                catch(MissingQuoteException e) {
                    return Money.ZERO;
                }

            case(PERCENT_CHANGE_COLUMN):
                Account yesterdayAccount = (Account)yesterdayPortfolio.getAccounts().get(row);

                try {
                    Money todayValue = todayAccount.getValue(quoteBundle, date);
                    Money yesterdayValue = yesterdayAccount.getValue(quoteBundle,
                                                                     date.previous(1));
                    return new ChangeFormat(yesterdayValue, todayValue);
                }
                catch(MissingQuoteException e) {
                    return new ChangeFormat(0.0D);
                }

            default:
                assert false;
                return Money.ZERO;
            }
        }

        private Object getPortfolioValueAt(int column) {
            switch(column) {
            case(ACCOUNT_COLUMN):
                return AccountNameFormat.TOTAL;
		
            case(MARKET_VALUE_COLUMN):
                try {
                    return todayPortfolio.getValue(quoteBundle, date);
                }
                catch(MissingQuoteException e) {
                    return Money.ZERO;
                }

            case(PERCENT_CHANGE_COLUMN):
                try {
                    Money todayValue = todayPortfolio.getValue(quoteBundle, date);
                    Money yesterdayValue =
                        yesterdayPortfolio.getValue(quoteBundle, date.previous(1));

                    return new ChangeFormat(yesterdayValue, todayValue);
                }
                catch(MissingQuoteException e) {
                    return new ChangeFormat(0.0D);
                }

            default:
                assert false;
                return Money.ZERO;
            }
        }

	public Object getValueAt(int row, int column) {
	    if(row >= getRowCount())
		return "";
	
	    // Account
	    if(row != (getRowCount() - 1))
                return getAccountValueAt(row, column);

	    // Total row
	    else
                return getPortfolioValueAt(column);
        }
    }

    /**
     * Create a new account table.
     *
     * @param	portfolio	the portfolio to create an account summary
     *				table for
     * @param	quoteBundle	the quote bundle
     */
    public AccountTable(Portfolio portfolio, EODQuoteBundle quoteBundle) {
        List columns = new ArrayList();
        columns.add(new Column(ACCOUNT_COLUMN,
			       Locale.getString("ACCOUNT"),
			       Locale.getString("ACCOUNT_COLUMN_HEADER"),
			       AccountNameFormat.class, Column.VISIBLE));
        columns.add(new Column(MARKET_VALUE_COLUMN,
			       Locale.getString("MARKET_VALUE"),
			       Locale.getString("MARKET_VALUE_COLUMN_HEADER"),
			       Money.class, Column.VISIBLE));
        columns.add(new Column(PERCENT_CHANGE_COLUMN,
			       Locale.getString("PERCENT_CHANGE"),
			       Locale.getString("PERCENT_CHANGE_COLUMN_HEADER"),
			       ChangeFormat.class, Column.VISIBLE));

	setModel(new Model(columns, portfolio, quoteBundle));
    }
}
