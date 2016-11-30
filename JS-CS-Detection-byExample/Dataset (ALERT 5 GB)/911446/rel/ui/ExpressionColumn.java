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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.WeekendDateException;
import nz.org.venice.util.TradingDate;

/**
 * Representation of an expression column in a table. An expression column is a
 * column in quote tables that displays the results of a user expression applied to
 * the data in the table. The data type of the data displayed in the column will
 * be {@link ExpressionResult}.
 *
 * @author Andrew Leppard
 * @see AbstractTable
 * @see AbstractTableModel
 * @see EODQuoteModel
 * @see ExpressionResult
 */
public class ExpressionColumn extends Column implements Cloneable {

    // Text of expression
    private String expressionText;

    // Compiled expression
    private Expression expression;

    // A map which allows you to find the result of an expression for a given symbol
    // on a given trading date. The map is a mapping of the concatenation of
    // the symbol and the trading date string, to an ExpressionResult.
    private Map results;

    /**
     * Create a new expression column.
     *
     * @param number         The column number
     * @param fullName       The full name of the column which appears in menus etc.
     * @param shortName      The short name of the column which appears in the table header.
     * @param visible        Either {@link Column#HIDDEN}, {@link Column#VISIBLE} or
     *                       {@link Column#ALWAYS_HIDDEN}.
     * @param expressionText Text of expression.
     * @param expression     Compiled expression.
     */
    public ExpressionColumn(int number, 
                            String fullName, 
                            String shortName,
                            int visible,
                            String expressionText, 
                            Expression expression) {
        super(number, fullName, shortName, ExpressionResult.class, visible);
        this.expressionText = expressionText;
        this.expression = expression;
        this.results = new HashMap();
    }

    /**
     * Return the text version of the expression.
     *
     * @return Text version of the expression.
     */
    public String getExpressionText() {
        return expressionText;
    }

    /**
     * Set the text version of the expression.
     *
     * @param expressionText New expression text.
     */
    public void setExpressionText(String expressionText) {
        this.expressionText = expressionText;
    }

    /**
     * Get the compiled expression.
     *
     * @return Compiled expression.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Set the compiled expression.
     *
     * @param expression Compiled expression.
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * Execute the expression and calculate the result for each quote. This function takes a list
     * of quotes, rather than extracting them from the quote bundle, because typically the
     * table (and therefore this column) does not display all the quotes in the quote bundle.
     * The reason is that to display a single day's quotes requires the loading of two day's
     * worth of quotes. Two days are needed to calculate the quote change values.
     *
     * @param quoteBundle Quote Bundle containing quotes
     * @param quotes      A list of {@link Quote}s which contain the symbols and dates to
     *                    evaluate. A result will be calculated for each quote in the list.
     * @throws EvaluationException If there was an error evaluating an expression, such
     *         as divide by zero.
     * @see Quote
     */
    public void calculate(QuoteBundle quoteBundle, List quotes) throws EvaluationException {
        results = new HashMap();

        if(expression != null) {
            for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
                Quote quote = (Quote)iterator.next();
                
                try {		   
                    int offset = quoteBundle.getOffset(quote);

                    double result = expression.evaluate(new Variables(), 
                                                        quoteBundle, quote.getSymbol(), 
                                                        offset);
                    results.put(quote.getSymbol().toString() + quote.getDate().toString(),
                                new ExpressionResult(expression.getType(), result));
                }
                catch(WeekendDateException e) {
                    // Shouldn't happen
                    assert false;
                }
		catch (EvaluationException e) {
		    //Some expressions evaluation will be undefined for
		    //the parameters. (e.g. the expression evaluations to 
		    //determining the maximum of the empty set. )
		    //This could because of the applied date range 
		    //doesn't contain any data.
		    //We don't want to halt the application of equations
		    //for all instances. 
		    double result = 0.0;
		    results.put(quote.getSymbol().toString() + quote.getDate().toString(),
                                new ExpressionResult(expression.getType(), result));
		}
            }
        }
    }
   
    /**
     * Return the result of the expression for the given symbol on the given date.
     *
     * @param symbol Query the result for this symbol.
     * @param date   Query the result for this date.
     * @return The expression result or {@link ExpressionResult#EMPTY} if there is
     *         currently no result for the given symbol and date.
     */
    public ExpressionResult getResult(Symbol symbol, TradingDate date) {
        // If we don't have that many results, just return an empty
        // result. This will show up as an empty cell in the table
        // and be sorted as so the result was 0.0.
        ExpressionResult expressionResult = null;
        
        if(results != null)
            expressionResult = (ExpressionResult)results.get(symbol.toString() + date.toString());

        if(expressionResult == null)
            expressionResult = ExpressionResult.EMPTY;

        return expressionResult;
    }

    /**
     * Clone this expression column.
     *
     * @return Cloned expression column.
     */
    public Object clone() {
        return new ExpressionColumn(getNumber(), getFullName(), getShortName(),
                                    getVisible(), getExpressionText(), getExpression());
    }
}
