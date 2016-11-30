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

package nz.org.venice.chart.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

import nz.org.venice.chart.Graphable;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.ExpressionException;
import nz.org.venice.parser.Parser;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.WeekendDateException;
import nz.org.venice.ui.ExpressionComboBox;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;

/**
 * The custom graph user interface.
 *
 * @author Andrew Leppard
 * @see CustomGraph
 */
public class CustomGraphUI implements GraphUI {

    // Variables to allow us to run the expression to check it for errors
    private Graphable source;
    private EODQuoteBundle quoteBundle;
    private Symbol symbol;

    // The graph's user interface
    private JPanel panel;
    private ExpressionComboBox indicatorComboBox;
    private JCheckBox isPrimaryCheckBox;

    // String name of settings
    private final static String INDICATOR = "indicator";
    private final static String IS_PRIMARY = "is_primary";

    /**
     * Create a new Custom user interface with the initial settings.
     *
     * @param settings the initial settings
     */
    public CustomGraphUI(HashMap settings, Graphable source, EODQuoteBundle quoteBundle,
                         Symbol symbol) {
        this.source = source;
        this.quoteBundle = quoteBundle;
        this.symbol = symbol;
        buildPanel();
        setSettings(settings);
    }

    /**
     * Build the user interface JPanel.
     */
    private void buildPanel() {
        panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(layout);

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        indicatorComboBox = GridBagHelper.addExpressionRow(panel,
                                                           Locale.getString("CUSTOM"),
                                                           "", layout, c);
        isPrimaryCheckBox = GridBagHelper.addCheckBoxRow(panel,
                                                         Locale.getString("IS_PRIMARY"),
                                                         true, layout, c);
    }

    public String checkSettings() {
	return checkSettings(getSettings());
    }

    public String checkSettings(HashMap settings) {
	// Unfortunately we end up running the expression twice. Once to
        // verify the expression, the other time to graph the values.
        try {
            String indicatorText = getIndicatorText(settings);
            Expression indicator = Parser.parse(indicatorText);
            createCustom(indicator, source, quoteBundle, symbol);

            // If it didn't throw an exception then it is fine
            return null;
        }
        catch(ExpressionException p) {
            return p.getReason();
        }
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(INDICATOR, indicatorComboBox.getExpressionText());
        settings.put(IS_PRIMARY, isPrimaryCheckBox.isSelected()? "1" : "0");
        return settings;
    }

    public void setSettings(HashMap settings) {
        indicatorComboBox.setExpressionText(getIndicatorText(settings));
        isPrimaryCheckBox.setSelected(isPrimaryCheckBox.isSelected());
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Return the custom indicator expression text.
     *
     * @param  settings the settings
     * @return a text representation of the indicator expression
     */
    public static String getIndicatorText(HashMap settings) {
        String indicator = (String)settings.get(INDICATOR);

        if(indicator == null)
            indicator = "";

        return indicator;
    }

    /**
     * Create a new custom graph based on the given graph source and quote bundle.
     * Usually the function to create a graph is not located in the user interface
     * section, but rather with the core graph code. This case is an exception
     * since the user interface needs to generate the graph to verify that
     * the custom indicator expression is valid.
     *
     * @param     indicator           the custom indicator expression
     * @param     source              the source containing the dates to work with
     * @param     symbol              the symbol to apply the expression
     * @param     quoteBundle         the quote bundle containing the quotes
     * @exception EvaluationException if there was an error evaluating the expression
     * @return    the custom graph
     */
    public static Graphable createCustom(Expression indicator,
                                         Graphable source,
                                         EODQuoteBundle quoteBundle,
                                         Symbol symbol)
        throws EvaluationException {

	Graphable indicatorGraphable = new Graphable();

	// Date set and value array will be in sync
	double[] values = source.toArray();
	Set xRange = source.getXRange();
	Iterator iterator = xRange.iterator();
        Variables variables = new Variables();

	while(iterator.hasNext()) {
	    TradingDate date = (TradingDate)iterator.next();

            try {
                int dateOffset = quoteBundle.dateToOffset(date);
                double value = indicator.evaluate(variables, quoteBundle, symbol, dateOffset);

                indicatorGraphable.putY(date, new Double(value));
            }
            catch(WeekendDateException e) {
                // ignore
            }
	}

        return indicatorGraphable;
    }

    /**
     * Return whether the graph should appear in the primary graph or not. Every
     * chart has two kinds of graphs - a primary and a secondary. All primary
     * graphs are drawed together (in the top graph). All secondary graphs
     * are drawn individually (below the top graph).
     *
     * @param  settings the settings
     * @return <code>true</code> if the graph is primary; <code>false</code> otherwise
     */
    public static boolean isPrimary(HashMap settings) {
        String isPrimary = (String)settings.get(IS_PRIMARY);
        return isPrimary.equals("1");
    }
}
