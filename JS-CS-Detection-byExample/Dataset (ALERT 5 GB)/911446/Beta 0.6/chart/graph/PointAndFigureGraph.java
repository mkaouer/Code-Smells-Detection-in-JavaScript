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
import java.util.Set;
import java.util.Vector;

import org.mov.chart.Graphable;
import org.mov.chart.PFGraphable;
import org.mov.chart.GraphTools;
import org.mov.chart.source.GraphSource;
import org.mov.quote.QuoteFunctions;
import org.mov.util.Locale;

/**
 * Point and Figure graph. This graph draws a series of characters (X/O)
 * mapping the general movement. A change in column shows a reversal
 * such that price difference met the price scale.
 *
 * @author Mark Hummel
 * @see PointAndFigureGraphUI
 */
public class PointAndFigureGraph extends AbstractGraph {

    // Point and Figure ready to graph
    private PFGraphable pointAndFigure;

    /**
     * Create a new point and figure graph.
     *
     * @param	source	the source to create the point and figure from
     */
    public PointAndFigureGraph(GraphSource source) {
	super(source);
        setSettings(new HashMap());
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double bottomLineValue, List xRange) {

	// We ignore the graph colours and use our own custom colours
	g.setColor(Color.green.darker());
	GraphTools.renderChar(g, pointAndFigure, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
    }

    // Highest Y value is in the moving average graph
    public double getHighestY(List x) {
	return pointAndFigure.getHighestY(x);
    }

    // Lowest Y value is in the moving average graph
    public double getLowestY(List x) {
	return pointAndFigure.getLowestY(x);
    }

    /**
     * Return the name of this graph.
     *
     * @return	<code>Point and Figure</code>
     */
    public String getName() {
	return Locale.getString("POINT_AND_FIGURE");
    }

    /**
     * Creates a new poing and figure graphable based on the given data source.
     *
     * <p>Rules of Point and Figure:
     *
     * <ul>
     * <li><code>X</code> = Upmoves, <code>O</code> = DownMoves</li>
     * <li>Stay in the same column until price changes direction, and then move one column
     *    to the right.</li>
     * <li>Plot only prices which meet the price scale. A plot is marked regardless
     *    of the direction. If a direction change occurs, the affect is to move the
     *    column one to the right.</li>
     * </ul>
     *
     * @param	source	the graph source to average
     * @return	the graphable containing averaged data from the source
     */
    public static PFGraphable createPointAndFigureGraph(Graphable source,
                                                        double priceReversalScale,
							double boxPriceScale) {
	PFGraphable pointAndFigure = new PFGraphable();

	// Date set and value array will be in sync
	double[] values = source.toArray();
	Vector yList = new Vector();
		
	Set xRange = source.getXRange();
	Set column = source.getXRange(); // Associate Column with date
	Iterator iterator = xRange.iterator();
	Iterator iterator2 = column.iterator();
	double diff, prev = 0.0;
	boolean plot; // Indicate whether this point gets plotted
	boolean upmove = true, changeDirection = false;	
	String marker;
	Comparable col;	

	int i = 0;	
	double average;
		
	upmove = getFirstDirection(source, boxPriceScale);

	col = (Comparable)iterator2.next();
	
	while(iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();

	    diff = (i == 0)  ? 0 : (values[i] - prev);
	    plot = (Math.abs(diff) >= boxPriceScale) ? true : false;

	    // Now check the direction
	    if (plot) {
		if (Math.abs(diff) >= priceReversalScale) {
		    if (upmove == true && diff < 0) {
			changeDirection = true;
		    } else if (upmove == false && diff > 0) {
			changeDirection = true;
		    } else {
			changeDirection = false;		
		    }
		} else {
		    changeDirection = false;
		}
		
		// Stay in the same column until theres a change in direction
		if (changeDirection) {

		    // Associate a new set of points.
		    yList = new Vector();

		    upmove = (upmove) ? false: true;
		    // This places the marker on when the price changed
		    // direction
		    // Which seems to work ok for small datasets
		    //col = x;	
		    
		    // This places it in the next column
		    // This seems to work better for larger (more realistic)
		    // datasets, even though the user may have to zoom
		    // a few times.
		    col = (Comparable)iterator2.next();
		
		}
		
		marker = (upmove) ? "X" : "O";
		
		/*Rules for generating PF state that when 
		  price moves by greater than twice the box value
		  the inteverning boxes are marked.
		*/

		/* 
		   When prev = 0.0, no plot has yet been made.
		   We avoid putting in intervening marks in this
		   case to avoid a large column of 'X's arising out of
		   0. 
		*/

		diff = Math.abs(diff) - boxPriceScale;
		int counter = 1;	
		if (prev != 0.0) {
		    while (diff  >= 0.0) {		    
			double tmp = (upmove) ? values[i] - (boxPriceScale * counter) : values[i] + (boxPriceScale * counter);
			
			Double yTemp = new Double(tmp);
			yList.add(yTemp);
			diff = Math.abs(diff) - boxPriceScale;
			counter++;		
		    }
		}

		Double yTemp = new Double(values[i]);
		yList.add(yTemp);
		
		pointAndFigure.putYList(col, yList);
		pointAndFigure.putString(col, marker);
		
	    }
	    //The price meeting the box value is defined by the last 
	    //marker placed, which is not necessarily the previous value.
	    if (plot) {
		prev = values[i];	
	    }
	    
	    i++;
	}
	
	return pointAndFigure;
    }

    public boolean isPrimary() {
        return true;
    }

    /**
     * Calculates default price scale based on graph source.
     *
     * @return default price scale
     */
    private double calculateDefaultPriceReversalScale() {
        double defaultPriceReversalScale;

        // Heuristic for a starting value for the default price scale
        Graphable graphable = getSource().getGraphable();
        double[] values = graphable.toArray();

	// Default = 3 box reversal
	defaultPriceReversalScale = calculateDefaultBoxPriceScale() * 3;
        defaultPriceReversalScale = QuoteFunctions.roundDouble(defaultPriceReversalScale, 2);
        return defaultPriceReversalScale;
    }

    private double calculateDefaultBoxPriceScale() {
        double defaultBoxPriceScale;

        // Heuristic for a starting value for the default price scale
	// Use 1 percent of the current price.
        Graphable graphable = getSource().getGraphable();
        double[] values = graphable.toArray();
	
	defaultBoxPriceScale = values[values.length-1] / 100;
        defaultBoxPriceScale = QuoteFunctions.roundDouble(defaultBoxPriceScale, 2);
	if (defaultBoxPriceScale <= 0.0) {
	    defaultBoxPriceScale = 0.01;
	}

        return defaultBoxPriceScale;
    }



    public void setSettings(HashMap settings) {
        super.setSettings(settings);

        // Calculate default price scale from data
        double defaultPriceReversalScale = calculateDefaultPriceReversalScale();        double defaultBoxPriceScale = calculateDefaultBoxPriceScale();

        // Retrieve values from hashmap
        double priceReversalScale = PointAndFigureGraphUI.getPriceReversalScale(settings, defaultPriceReversalScale);
        double boxPriceScale = PointAndFigureGraphUI.getBoxPriceScale(settings, defaultBoxPriceScale);	

	// Create point and figure graphable
	pointAndFigure = createPointAndFigureGraph(getSource().getGraphable(), priceReversalScale, boxPriceScale);
    }

    /**
     * Return the graph's user interface.
     *
     * @param settings the initial settings
     * @return user interface
     */
    public GraphUI getUI(HashMap settings) {
        return new PointAndFigureGraphUI(settings, 
					 calculateDefaultPriceReversalScale(),
					 calculateDefaultBoxPriceScale());
    }

    // Return true if the stock is moving upwards, false otherwise.
    private static boolean getFirstDirection(Graphable source, double boxValue) {
	Set xRange = source.getXRange();
	Iterator iterator = xRange.iterator();
	double diff, prev = 0.0;
	int i = 0;
	double[] values = source.toArray();
	
	while(iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();
	    
	    diff = (i <= 1)  ? 0 : (values[i] - prev);
	    
	    if (Math.abs(diff) >= boxValue) {
		prev = values[i];
	    }
	    
	    i++;
	    
	    if (Math.abs(diff) >= boxValue) {
		if (diff > 0) {
		    return true;
		}
		if (diff < 0) {
		    return false;
		}
	    }
	}
	
	return true;
    }

}


