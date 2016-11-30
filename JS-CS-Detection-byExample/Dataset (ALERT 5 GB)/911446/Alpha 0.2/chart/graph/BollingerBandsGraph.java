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
 * Bollinger Bands graph. This graph is used to show the volatility
 * of a stock. It draws two bands on the graph, they are centred around
 * the moving average of the graph. The top band is the moving average
 * plus 2 standard deviations, the lower band is the moving average
 * minus 2 standard deviations.
 */

public class BollingerBandsGraph extends AbstractGraph {

    private Graphable upperBand;
    private Graphable lowerBand;
   
    /**
     * Create a new bollinger bands graph.
     *
     * @param	source	the source to create a standard deviation from
     * @param	period	the period of the standard deviation
     */
    public BollingerBandsGraph(GraphSource source, int period) {
	
	super(source);

	// create bollinger bands
	upperBand = new Graphable();
	lowerBand = new Graphable();	

	// Date set and value array will be in sync
	float[] values = source.getGraphable().toArray();
	Iterator iterator = source.getGraphable().getXRange().iterator();

	int i = 0;	
	float average;
	float sd;

	while(iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();

	    sd = QuoteFunctions.sd(values,  
				   i - Math.min(period - 1, i),
				   i + 1);
	    average = QuoteFunctions.avg(values,  
                                         i - Math.min(period - 1, i),
                                         i + 1);

	    upperBand.putY(x, new Float(average + 2 * sd));
	    lowerBand.putY(x, new Float(average - 2 * sd));

	    i++;
	}
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       float horizontalScale, float verticalScale,
		       float bottomLineValue, List xRange) {

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
				 float verticalScale,
				 float bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Highest Y value is in the bollinger bands graph
    public float getHighestY(List x) {
	return upperBand.getHighestY(x);
    }

    // Lowest Y value is in the bollinger bands graph
    public float getLowestY(List x) {
	return lowerBand.getLowestY(x);
    }
}


