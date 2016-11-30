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

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;

import nz.org.venice.chart.Graphable;
import nz.org.venice.chart.GraphTools;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.util.Locale;
import nz.org.venice.parser.Parser;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.ExpressionException;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.Symbol;

/**
 * Graph a Gondola expression. This graph allows the user to construct and graph
 * custom indicators.
 *
 * @author Andrew Leppard
 * @see CustomGraphUI
 * @see nz.org.venice.parser.Expression
 */
public class CustomGraph extends AbstractGraph {

    // Custom indicator values ready to graph
    private Graphable indicatorGraphable = null;

    // Quote bundle containing quotes to graph
    private EODQuoteBundle quoteBundle;

    // Current symbol
    private Symbol symbol;

    /**
     * Create a new custom graph. Currently this class requires both a graph source
     * and a quote bundle. The quote bundle argument will soon become deprecated and
     * the function will require five graph sources - one for day open, close, low
     * high and volume. This change will enable us to run equations on arbitrary
     * indeces (i.e. groups of stocks).
     *
     * @param	source	    the source containing the dates to work with
     * @param   symbol      the symbol to apply the expression
     * @param   quoteBundle the quote bundle containing the quotes
     */
    public CustomGraph(GraphSource source, Symbol symbol, EODQuoteBundle quoteBundle) {
        super(source);
        this.symbol = symbol;
        this.quoteBundle = quoteBundle;
        setSettings(new HashMap());
    }


        /**
     * Create a new custom graph. Currently this class requires both a graph source
     * and a quote bundle. The quote bundle argument will soon become deprecated and
     * the function will require five graph sources - one for day open, close, low
     * high and volume. This change will enable us to run equations on arbitrary
     * indeces (i.e. groups of stocks).
     *
     * @param	source	    the source containing the dates to work with
     * @param   symbol      the symbol to apply the expression
     * @param   quoteBundle the quote bundle containing the quotes
     * @param   settings    the settings for the graph
     */
    public CustomGraph(GraphSource source, Symbol symbol, EODQuoteBundle quoteBundle, HashMap settings) {
        super(source);
        this.symbol = symbol;
        this.quoteBundle = quoteBundle;
        super.setSettings(settings);

	// Retrieve expression from settings hashmap
        try {
            String indicatorText = CustomGraphUI.getIndicatorText(settings);

            if(indicatorText.length() > 0) {
                Expression indicator = Parser.parse(indicatorText);
		
                // Create indicator graphable
                indicatorGraphable = createCustom(indicator, getSource().getGraphable(),
                                                  quoteBundle, symbol);
            }
        }
        catch(ExpressionException p) {
            // Expression should already have been checked
            assert false;
        }

    }


    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double topLineValue, double bottomLineValue, 
		       List xRange, 
		       boolean vertOrientation) {

	// We ignore the graph colours and use our own custom colours
	g.setColor(Color.green.darker());
	GraphTools.renderLine(g, indicatorGraphable, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, 
			      topLineValue, bottomLineValue, 
			      xRange, 
			      vertOrientation);
    }

    public double getHighestY(List x) {
	return indicatorGraphable.getHighestY(x);
    }

    public double getLowestY(List x) {
	return indicatorGraphable.getLowestY(x);
    }

    /**
     * Create a new custom graph based on the given graph source and quote bundle.
     * This function calls the method with the same name in {@link CustomGraphUI}.
     *
     * @param     indicator           the custom indicator expression
     * @param     source              the source containing the dates to work with
     * @param     symbol              the symbol to apply the expression
     * @param     quoteBundle         the quote bundle containing the quotes
     * @exception EvaluationException if there was an error evaluating the equation
     * @return    the custom graph
     */
    public static Graphable createCustom(Expression indicator,
                                         Graphable source,
                                         EODQuoteBundle quoteBundle,
                                         Symbol symbol) {
        try {
            return CustomGraphUI.createCustom(indicator, source, quoteBundle, symbol);
        }
        catch(ExpressionException e) {
            // Should have already been checked
            assert false;
            return null;
        }
    }

    public void setSettings(HashMap settings) {
        super.setSettings(settings);

        // Retrieve expression from settings hashmap
        try {
            String indicatorText = CustomGraphUI.getIndicatorText(settings);

            if(indicatorText.length() > 0) {
                Expression indicator = Parser.parse(indicatorText);

                // Create indicator graphable
                indicatorGraphable = createCustom(indicator, getSource().getGraphable(),
                                                  quoteBundle, symbol);
            }
        }
        catch(ExpressionException p) {
            // Expression should already have been checked
            assert false;
        }
    }

    /**
     * Return the graph's user interface.
     *
     * @param settings the initial settings
     * @return user interface
     */
    public GraphUI getUI(HashMap settings) {
        return new CustomGraphUI(getSettings(), getSource().getGraphable(),
                                 quoteBundle, symbol);
    }

    /**
     * Return the name of this graph.
     *
     * @return <code>Custom</code>
     */
    public String getName() {
        return Locale.getString("CUSTOM");
    }

    public boolean isPrimary() {
        return CustomGraphUI.isPrimary(getSettings());
    }
}


