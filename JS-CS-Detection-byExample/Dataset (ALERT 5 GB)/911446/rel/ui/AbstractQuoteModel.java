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

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.util.Locale;

/**
 * Helper for constructing quote table models. This abstract table model allows
 * you to pass a list of columns for describing a table. The model append
 * a list of expression columns that let the user apply expressions to quotes in the
 * table. The model will then care care of returning information to the table
 * about each column and recomputing the expression columns as necessary.
 *
 * @author Andrew Leppard
 * @see Column
 * @see ExpressionColumn
 */
public abstract class AbstractQuoteModel extends AbstractTableModel {

    /** The number of expression columns to display for tables that support them. */
    public final static int EXPRESSION_COLUMN_COUNT = 5;

    // Quote bundle
    private QuoteBundle quoteBundle;

    // List of quotes to be displayed in table
    private List quotes;

    // Array of expression columns
    private ExpressionColumn[] expressionColumns;
    
    /**
     * Create a new quote table model with no columns.
     *
     * @param quoteBundle           Quote bundle
     * @param quotes                A list of {@link nz.org.venice.quote.Quote}s which contain
     *                              the quote symbols and dates to table.
     * @param firstExpressionColumn The column number of the first expression
     *                              column.
     */
    public AbstractQuoteModel(QuoteBundle quoteBundle,
                              List quotes,
                              int firstExpressionColumn) {
        super();
        this.quoteBundle = quoteBundle;
        this.quotes = quotes;

        expressionColumns = createExpressionColumns(firstExpressionColumn);
    }

    /**
     * Return the array of expression columns.
     *
     * @return Array of expression columns.
     * @see Column
     * @see ExpressionColumn
     */
    public ExpressionColumn[] getExpressionColumns() {
        return expressionColumns;
    }
    
    /**
     * Set the expression columns. This function also calculates their values.
     *
     * @param expressionColumns New expression columns
     */
    public void setExpressionColumns(ExpressionColumn[] expressionColumns) {
        Thread thread = Thread.currentThread();
        ProgressDialog progress = ProgressDialogManager.getProgressDialog();
        progress.setIndeterminate(true);
        progress.show(Locale.getString("APPLYING_EQUATIONS"));

        this.expressionColumns = expressionColumns;

        for(int i = 0; i < this.expressionColumns.length; i++) {
            try {
                this.expressionColumns[i].calculate(quoteBundle, quotes);
            }
            catch(EvaluationException e) {
                displayErrorMessage(e.getReason());
            }

            if(thread.isInterrupted())
                break;
        }

        ProgressDialogManager.closeProgressDialog(progress);        
    }


    /**
     * Return the number of columns in the table.
     *
     * @return Number of columns in table.
     */
    public int getColumnCount() {
        return super.getColumnCount() + expressionColumns.length;
    }

    /**
     * Return a column.
     *
     * @param columnNumber Number of column.
     * @return Column
     */
    public Column getColumn(int columnNumber) {
        if(columnNumber < super.getColumnCount())
            return super.getColumn(columnNumber);
        else {
            columnNumber -= super.getColumnCount();
            assert columnNumber <= expressionColumns.length;
            return expressionColumns[columnNumber];
        }
    }

    /**
     * Return the list of quotes in the table.
     *
     * @return Tabled quotes.
     */
    public List getQuotes() {
        return quotes;
    }
    
    /**
     * Set the list of quotes to table.
     *
     * @param quotes New quotes to table.
     */
    public void setQuotes(List quotes) {
        this.quotes = quotes;

        // Recalculate the expressions for each quote
        for(int i = 0; i < expressionColumns.length; i++) {
            try {
                expressionColumns[i].calculate(quoteBundle, quotes);
            }
            catch(EvaluationException e) {
                displayErrorMessage(e.getReason());
            }
        }

        fireTableDataChanged();                       
    }

    /**
     * Return the number of rows in the table.
     *
     * @return Number of rows in table.
     */
    public int getRowCount() {
        return quotes.size();
    }

    /**
     * Display an error message to the user.
     *
     * @param message The message to display.
     */
    private void displayErrorMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showInternalMessageDialog(DesktopManager.getDesktop(),
                                                          message + ".",
                                                          Locale.getString("ERROR_EVALUATING_EQUATIONS"),
                                                          JOptionPane.ERROR_MESSAGE);
                }
            });
    }

    /**
     * Create the expression columns.
     *
     * @param columnNumber Column number.
     * @return Array of expression columns.
     */
    private ExpressionColumn[] createExpressionColumns(int columnNumber) {
        ExpressionColumn[] expressionColumns = new ExpressionColumn[EXPRESSION_COLUMN_COUNT];

        for(int i = 0; i < expressionColumns.length; i++)
            expressionColumns[i] = new ExpressionColumn(columnNumber++, 
                                                        Locale.getString("EQUATION_NUMBER", (i + 1)),
                                                        Locale.getString("EQUATION_COLUMN_HEADER", 
                                                                         (i + 1)),
                                                        Column.HIDDEN,
                                                        "",
                                                        null);
        return expressionColumns;
    }
}
