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

package org.mov.chart.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mov.chart.Graphable;
import org.mov.chart.GraphableQuoteFunctionSource;
import org.mov.chart.GraphTools;
import org.mov.chart.source.GraphSource;
import org.mov.parser.EvaluationException;
import org.mov.quote.QuoteFunctions;
import org.mov.util.Locale;
import org.mov.util.TradingDate;

/**
 * Bollinger Bands graph. This graph is used to show the volatility
 * of a stock. It draws two bands on the graph, they are centred around
 * the moving average of the graph. The top band is the moving average
 * plus 2 standard deviations, the lower band is the moving average
 * minus 2 standard deviations.
 *
 * @author Andrew Leppard
 * @see PeriodGraphUI
 */
public class BollingerBandsGraph extends AbstractGraph {

    // Upper and lower band values ready to graph
    private Graphable upperBand;
    private Graphable lowerBand;

    /**
     * Create a new bollinger bands graph.
     *
     * @param	source	the source to create a standard deviation from
     */
    public BollingerBandsGraph(GraphSource source) {
        super(source);
        setSettings(new HashMap());
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double bottomLineValue, List xRange) {

	// We ignore the graph colours and use our own custom colours
	g.setColor(Color.green.darker());

	GraphTools.renderLine(g, upperBand, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
	GraphTools.renderLine(g, lowerBand, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Highest Y value is in the bollinger bands graph
    public double getHighestY(List x) {
	return upperBand.getHighestY(x);
    }

    // Lowest Y value is in the bollinger bands graph
    public double getLowestY(List x) {
	return lowerBand.getLowestY(x);
    }

    /**
     * Return the name of this graph.
     *
     * @return <code>Bollinger Bands</code>
     */
    public String getName() {
        return Locale.getString("BOLLINGER_BANDS");
    }

    public boolean isPrimary() {
        return true;
    }

    /**
     * Return the graph's user interface.
     *
     * @param settings the initial settings
     * @return user interface
     */
    public GraphUI getUI(HashMap settings) {
        return new PeriodGraphUI(settings);
    }

    public void setSettings(HashMap settings) {
        super.setSettings(settings);

        Graphable source = getSource().getGraphable();

        // Retrieve period from settings hashmap
        int period = PeriodGraphUI.getPeriod(settings);

	// Create bollinger bands
	upperBand = new Graphable();
	lowerBand = new Graphable();	

        TradingDate date = (TradingDate)source.getStartX();
        GraphableQuoteFunctionSource quoteFunctionSource 
            = new GraphableQuoteFunctionSource(source, date, period);

        for(Iterator iterator = source.iterator(); iterator.hasNext();) {
            date = (TradingDate)iterator.next();
            quoteFunctionSource.setDate(date);

            try {
                double bollingerTop = QuoteFunctions.bollingerUpper(quoteFunctionSource, period);
                upperBand.putY(date, new Double(bollingerTop));

                double bollingerBottom = QuoteFunctions.bollingerLower(quoteFunctionSource, 
                                                                       period);
                lowerBand.putY(date, new Double(bollingerBottom));
            }
            catch(EvaluationException e) {
                // This can't happen since our source does not throw this exception
                assert false;
            }
        }
    }
}


