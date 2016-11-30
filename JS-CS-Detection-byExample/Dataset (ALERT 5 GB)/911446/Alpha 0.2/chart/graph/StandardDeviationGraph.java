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

import java.awt.*;
import java.lang.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mov.chart.*;
import org.mov.chart.source.*;
import org.mov.util.*;
import org.mov.parser.*;
import org.mov.quote.*;

/**
 * Standard Deviation graph. This graph is used to show the
 * volatility of a stock. The higher the standard deviation, the
 * more volatile the stock.
 */
public class StandardDeviationGraph extends AbstractGraph {

    private Graphable standardDeviation;
   
    /**
     * Create a new standard deviation graph.
     *
     * @param	source	the source to create a standard deviation from
     * @param	period	the period of the standard deviation
     */
    public StandardDeviationGraph(GraphSource source, int period) {
	
	super(source);

	// create standard deviation
	standardDeviation = createStandardDeviation(source.getGraphable(),
						    period);
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       float horizontalScale, float verticalScale,
		       float bottomLineValue, List xRange) {

	g.setColor(colour);
	GraphTools.renderLine(g, standardDeviation, xoffset, yoffset, 
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 float verticalScale,
				 float bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Highest Y value is in the standard deviation graph
    public float getHighestY(List x) {
	return standardDeviation.getHighestY(x);
    }

    // Lowest Y value is in the standard deviation graph
    public float getLowestY(List x) {
	return standardDeviation.getLowestY(x);
    }

    // Override vertical axis
    public float[] getAcceptableMajorDeltas() {
	float[] major = {0.1F,
			 0.5F,
			 1F,
			 10F,
			 100F};
	return major;
    }

    // Override vertical axis
    public float[] getAcceptableMinorDeltas() {
	float[] minor = {1F, 
			 2F,
			 3F,
			 4F,
			 5F,
			 6F,
			 7F,
			 8F,
			 9F};
	return minor;
    }

    // Override vertical axis
    public String getYLabel(float value) {
	return Float.toString(value);
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

	// Date set and value array will be in sync
	float[] values = source.toArray();
	Iterator iterator = source.getXRange().iterator();

	int i = 0;	
	float sd;

	while(iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();

	    sd = QuoteFunctions.sd(values,  
				   i - Math.min(period - 1, i),
				   i + 1);
	    i++;

	    standardDeviation.putY(x, new Float(sd));
	}

	return standardDeviation;
    }
}


