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
 * Standard Deviation graph. This graph is used to show the
 * volatility of a stock. The higher the standard deviation, the
 * more volatile the stock.
 *
 * @author Andrew Leppard
 * @see PeriodGraphUI
 */
public class StandardDeviationGraph extends AbstractGraph {

    // Standard deviation ready to graph
    private Graphable standardDeviation;

    /**
     * Create a new standard deviation graph.
     *
     * @param	source	the source to create a standard deviation from
     */
    public StandardDeviationGraph(GraphSource source) {
        super(source);
        setSettings(new HashMap());
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double bottomLineValue, List xRange) {

	g.setColor(colour);
	GraphTools.renderLine(g, standardDeviation, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Highest Y value is in the standard deviation graph
    public double getHighestY(List x) {
	return standardDeviation.getHighestY(x);
    }

    // Lowest Y value is in the standard deviation graph
    public double getLowestY(List x) {
	return standardDeviation.getLowestY(x);
    }

    // Override vertical axis
    public double[] getAcceptableMajorDeltas() {
	double[] major = {0.1D,
			 0.5D,
			 1D,
			 10D,
			 100D};
	return major;
    }

    // Override vertical axis
    public double[] getAcceptableMinorDeltas() {
	double[] minor = {1D,
			 2D,
			 3D,
			 4D,
			 5D,
			 6D,
			 7D,
			 8D,
			 9D};
	return minor;
    }

    // Override vertical axis
    public String getYLabel(double value) {
	return Double.toString(value);
    }

    /**
     * Creates a new standard deviation based on the given data source.
     *
     * @param	source	the input graph source
     * @param	period	the desired period of the standard deviation
     * @return	the graphable containing averaged data from the source
     */
    public static Graphable createStandardDeviation(Graphable source,
						    int period) {

	Graphable standardDeviation = new Graphable();
        TradingDate date = (TradingDate)source.getStartX();
        GraphableQuoteFunctionSource quoteFunctionSource 
            = new GraphableQuoteFunctionSource(source, date, period);

        for(Iterator iterator = source.iterator(); iterator.hasNext();) {
            date = (TradingDate)iterator.next();
            quoteFunctionSource.setDate(date);

            try {
                double value = QuoteFunctions.sd(quoteFunctionSource, period);
                standardDeviation.putY(date, new Double(value));
            }
            catch(EvaluationException e) {
                // This can't happen since our source does not throw this exception
                assert false;
            }
        }

        return standardDeviation;
    }

    public void setSettings(HashMap settings) {
        super.setSettings(settings);

        // Retrieve period from settings hashmap
        int period = PeriodGraphUI.getPeriod(settings);

	// Create standard deviation
	standardDeviation = createStandardDeviation(getSource().getGraphable(),
						    period);
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

    /**
     * Return the name of this graph.
     *
     * @return <code>Standard Deviation</code>
     */
    public String getName() {
        return Locale.getString("STANDARD_DEVIATION");
    }

    public boolean isPrimary() {
        return false;
    }
}


