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

package nz.org.venice.ui;

import java.util.ArrayList;
import java.util.List;

import nz.org.venice.quote.IDQuote;
import nz.org.venice.quote.QuoteSourceManager;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.WeekendDateException;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingTime;

/**
 * Table model to display a mixture of end of day and intra-day quotes to the user.
 * This model tells a table how to display the quotes by describing the columns and
 * how to populate the table with quotes using data from a quote bundle.
 *
 * @author Andrew Leppard
 * @see AbstractTable
 * @see Column
 * @see ExpressionColumn
 * @see nz.org.venice.quote.MixedQuoteBundle
 */
public class MixedQuoteModel extends AbstractQuoteModel {

    // Quote bundle
    private QuoteBundle quoteBundle;

    // Column ennumeration

    /** Symbol column number. */
    public static final int SYMBOL_COLUMN         = 0;

    /** Date column number. */
    public static final int DATE_COLUMN           = 1;

    /** Time column number. */
    public static final int TIME_COLUMN           = 2;

    /** Day close column number. */
    public static final int LAST_COLUMN           = 3;

    /** Point change column number. */
    public static final int POINT_CHANGE_COLUMN   = 4;

    /** Percent change column number. */
    public static final int PERCENT_CHANGE_COLUMN = 5;

    /** Day low column number. */
    public static final int BID_COLUMN            = 6;

    /** Day low column number. */
    public static final int ASK_COLUMN            = 7;

    /** Day open column number. */
    public static final int DAY_OPEN_COLUMN       = 8;

    /** Day high column number. */
    public static final int DAY_HIGH_COLUMN       = 9;

    /** Day low column number. */
    public static final int DAY_LOW_COLUMN        = 10;

    /** Volume column number. */
    public static final int VOLUME_COLUMN         = 11;

    /** Activity column number. */
    public static final int ACTIVITY_COLUMN       = 12;

    /**
     * Create a new mixed quote model.
     *
     * @param quoteBundle   Quote bundle
     * @param quotes        A list of {@link Quote} which contain
     *                      the quote symbols and dates to table.
     * @param displayDate   Display the date column? Either {@link Column#HIDDEN},
     *                      {@link Column#VISIBLE} or {@link Column#ALWAYS_HIDDEN}.
     * @param displaySymbol Display the symbol column? Either {@link Column#HIDDEN},
     *                      {@link Column#VISIBLE} or {@link Column#ALWAYS_HIDDEN}.
     */ 
    public MixedQuoteModel(QuoteBundle quoteBundle, List quotes, 
                           int displayDate, int displaySymbol) {
        super(quoteBundle, quotes, ACTIVITY_COLUMN + 1);

        this.quoteBundle = quoteBundle;

        List columns = new ArrayList();
        columns.add(new Column(SYMBOL_COLUMN, 
			       Locale.getString("SYMBOL"),
			       Locale.getString("SYMBOL_COLUMN_HEADER"),
			       Symbol.class, displaySymbol));
        columns.add(new Column(DATE_COLUMN, 
			       Locale.getString("DATE"), 
			       Locale.getString("DATE_COLUMN_HEADER"),
                               Symbol.class, displayDate));
        columns.add(new Column(TIME_COLUMN, 
			       Locale.getString("TIME"), 
			       Locale.getString("TIME_COLUMN_HEADER"),
                               Symbol.class, Column.HIDDEN));
        columns.add(new Column(LAST_COLUMN, 
			       Locale.getString("LAST"), 
			       Locale.getString("LAST_COLUMN_HEADER"),
                               QuoteFormat.class, Column.VISIBLE));
        columns.add(new Column(POINT_CHANGE_COLUMN, 
			       Locale.getString("POINT_CHANGE"),
			       Locale.getString("POINT_CHANGE_COLUMN_HEADER"),
                               PointChangeFormat.class, Column.HIDDEN));
        columns.add(new Column(PERCENT_CHANGE_COLUMN, 
			       Locale.getString("PERCENT_CHANGE"), 
			       Locale.getString("PERCENT_CHANGE_COLUMN_HEADER"),
			       ChangeFormat.class, Column.VISIBLE));
        columns.add(new Column(BID_COLUMN, 
			       Locale.getString("BID"), 
			       Locale.getString("BID_COLUMN_HEADER"),
                               QuoteFormat.class, Column.HIDDEN));
        columns.add(new Column(ASK_COLUMN, 
			       Locale.getString("ASK"), 
			       Locale.getString("ASK_COLUMN_HEADER"),
                               QuoteFormat.class, Column.HIDDEN));
        columns.add(new Column(DAY_OPEN_COLUMN, 
			       Locale.getString("DAY_OPEN"), 
			       Locale.getString("DAY_OPEN_COLUMN_HEADER"),
                               QuoteFormat.class, Column.VISIBLE));
        columns.add(new Column(DAY_HIGH_COLUMN, 
			       Locale.getString("DAY_HIGH"), 
			       Locale.getString("DAY_HIGH_COLUMN_HEADER"),
                               QuoteFormat.class, Column.VISIBLE));
        columns.add(new Column(DAY_LOW_COLUMN, 
			       Locale.getString("DAY_LOW"), 
			       Locale.getString("DAY_LOW_COLUMN_HEADER"),
                               QuoteFormat.class, Column.VISIBLE));
        columns.add(new Column(VOLUME_COLUMN, 
			       Locale.getString("VOLUME"), 
			       Locale.getString("VOLUME_COLUMN_HEADER"),
                               Long.class, Column.VISIBLE));
	columns.add(new Column(ACTIVITY_COLUMN, 
			       Locale.getString("ACTIVITY"), 
			       Locale.getString("ACTIVITY_COLUMN_HEADER"),
                               Integer.class, Column.ALWAYS_HIDDEN));
        setColumns(columns);
    }

    /**
     * Return the value at the given table cell.
     *
     * @param row    Row number.
     * @param column Column number.
     * @return Value to display in cell.
     */
    public Object getValueAt(int row, int column) {
        if(row >= getRowCount())
            return null;

        // Quotes in this table are a mixture of intra-day and end of day quotes.
        Quote quote = (Quote)getQuotes().get(row);
        IDQuote idQuote = null;
        if(quote instanceof IDQuote)
            idQuote = (IDQuote)quote;
        
        switch(column) {
        case(SYMBOL_COLUMN):
            return quote.getSymbol();
            
        case(DATE_COLUMN):
            return quote.getDate();

        case(TIME_COLUMN):
            if(idQuote != null)
                return idQuote.getTime();
            else
                return null;

        case(LAST_COLUMN):
            return new QuoteFormat(quote.getDayClose());

        case(POINT_CHANGE_COLUMN):
            // Change is calculated by the percent gain between
            // yesterday's day close and today's day close. If we don't
            // have yesterday's day close available, we just use today's
            // day open.
            double finalQuote = quote.getDayClose();
            double initialQuote = quote.getDayOpen();

            try {
                int offset = quoteBundle.getOffset(quote);

                initialQuote =  quoteBundle.getQuote(quote.getSymbol(),
                                                     Quote.DAY_CLOSE, 
                                                     offset - 1);
            }
            catch(MissingQuoteException e) {
                // No big deal - we default to day open
            }
            catch(WeekendDateException e) {
                // Should not happen
                assert false;
            }

            return new PointChangeFormat(initialQuote, finalQuote, 
					 QuoteSourceManager.getSource().isMarketIndex(quote.getSymbol()));
            
        case(PERCENT_CHANGE_COLUMN):
            finalQuote = quote.getDayClose();
            initialQuote = quote.getDayOpen();
            
            try {
                int offset = quoteBundle.getOffset(quote);

                initialQuote = quoteBundle.getQuote(quote.getSymbol(),
                                                    Quote.DAY_CLOSE, 
                                                    offset - 1);
            }
            catch(MissingQuoteException e) {
                // No big deal - we default to day open
            }
            catch(WeekendDateException e) {
                // Should not happen
                assert false;
            }

            return new ChangeFormat(initialQuote, finalQuote);

        case(BID_COLUMN):
            if(idQuote != null)
                return new QuoteFormat(idQuote.getBid());
            else
                return new QuoteFormat(0.0D);

        case(ASK_COLUMN):
            if(idQuote != null)
                return new QuoteFormat(idQuote.getAsk());
            else
                return new QuoteFormat(0.0D);            
            
        case(VOLUME_COLUMN):
            return new Long(quote.getDayVolume());
            
        case(DAY_LOW_COLUMN):
            return new QuoteFormat(quote.getDayLow());
            
        case(DAY_HIGH_COLUMN):
            return new QuoteFormat(quote.getDayHigh());
            
        case(DAY_OPEN_COLUMN):
            return new QuoteFormat(quote.getDayOpen());
		
        case(ACTIVITY_COLUMN):
            // This column is never visible but is used to determine
            // the most active stocks - I don't actually know how to
            // calculate "the most active stocks" or whether we even
            // have enough data to do it. But this seems to be roughly
            // right.
            return new Double(quote.getDayHigh() * quote.getDayVolume());
            
        default:
            ExpressionColumn expressionColumn = (ExpressionColumn)getColumn(column);
            return expressionColumn.getResult(quote.getSymbol(), quote.getDate());
        }
    }
}
