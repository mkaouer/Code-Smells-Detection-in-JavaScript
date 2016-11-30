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
import org.mov.chart.GraphTools;
import org.mov.chart.source.GraphSource;
import org.mov.util.Locale;

/**
 * Momentum graph. This graph is used to show the direction that the
 * stock price is moving. The momentum for a given day is calculated
 * as the day close of today minus the day close of <i>period</i>
 * days ago.
 *
 * @author Andrew Leppard
 * @see PeriodGraphUI
 */
public class MomentumGraph extends AbstractGraph {

    // Momentum values ready to graph
    private Graphable momentum;

    /**
     * Create a new momentum graph.
     *
     * @param	source	the source to create a momentum graph from
     */
    public MomentumGraph(GraphSource source) {
	super(source);
        setSettings(new HashMap());
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double bottomLineValue, List xRange) {

	g.setColor(colour);
	GraphTools.renderBar(g, momentum, xoffset, yoffset,
			     horizontalScale,
			     verticalScale, bottomLineValue, xRange);
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Centre momentum graph
    public double getHighestY(List x) {
	return Math.max(Math.abs(momentum.getHighestY(x)),
			Math.abs(momentum.getLowestY(x)));
    }

    // Centre momentum graph
    public double getLowestY(List x) {
	return -getHighestY(x);
    }

    // Override vertical axis
    public double[] getAcceptableMajorDeltas() {
	double[] major = {0.01D,
			 0.1D,
			 1D,
			 10D,
			 100D,
			 1000D};
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
     * Create a momentum graphable.
     *
     * @param	source	the input graph source
     * @param	period	the desired period of the momentum graph
     * @return	the graphable containing averaged data from the source
     */
    public static Graphable createMomentum(Graphable source,
					   int period) {
	Graphable momentum = new Graphable();

	// Date set and value array will be in sync
	double[] values = source.toArray();
	Iterator iterator = source.getXRange().iterator();

	int i = 0;	

	while(iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();
	    double todaysMomentum =
		values[i] - values[Math.max(1 + i - period, 0)];

	    momentum.putY(x, new Double(todaysMomentum));

	    i++;
	}

	return momentum;
    }

    /**
     * Return the name of this graph.
     *
     * @return <code>Momentum</code>
     */
    public String getName() {
        return Locale.getString("MOMENTUM");
    }

    public boolean isPrimary() {
        return false;
    }

    public void setSettings(HashMap settings) {
        super.setSettings(settings);

        // Retrieve period from settings hashmap
        int period = PeriodGraphUI.getPeriod(settings);

	// create momentum
	momentum = createMomentum(getSource().getGraphable(),
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
}


