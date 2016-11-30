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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.mov.chart.*;
import org.mov.chart.source.*;
import org.mov.util.*;

/**
 * Support and Resistence "graph". This graph draws single lines across common price occurrances. 
 *
 * NOTE: Experimental and work in progress.

 * @author Mark Hummel
 */
public class SupportAndResistenceGraph extends AbstractGraph {

    private Graphable support;
    private Graphable resistence;

    /**
     * Create a new support and Resistence graph.
     *
     * @param	source containing the data source, typically day close.
     */
    public SupportAndResistenceGraph(GraphSource source) {
	super(source);

	support = new Graphable();
	resistence = new Graphable(); 

	createSupportAndResistence(source.getGraphable(), support, resistence);

    }

    // See Graph.java
    public void render(Graphics g, Color c, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double bottomLineValue, List xRange) {


	g.setColor(Color.green.darker());
	GraphTools.renderLine(g, support, xoffset, yoffset, 
		     horizontalScale,
		     verticalScale, bottomLineValue, xRange);

	g.setColor(Color.red.darker());
	GraphTools.renderLine(g, resistence, xoffset, yoffset, 
		     horizontalScale,
		     verticalScale, bottomLineValue, xRange);

    }

    /**
     * Get the tool tip text for the given X value and y coordinate.
     *
     * @param	x	the X value
     * @param	y	the y coordinate
     * @param	yoffset	y offset from top of graph
     * @param	verticalScale	vertical scale factor
     * @param	bottomLineValue	the Y value of the lowest line in the graph
     * @return	tool tip text containing the day low, day high and day close
     *		quotes
     */
    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null;
    }

    public double getHighestY(List x) {
	return resistence.getHighestY(x);
    }

    public double getLowestY(List x) {
        return support.getLowestY(x);
    }

    /**
     * Return the name of this graph.
     *
     * @return <code>Bar Chart</code>
     */
    public String getName() {
        return Locale.getString("SUPPORT_AND_RESISTENCE");
    }

    public boolean isPrimary() {
        return true;
    }

    /**
     * Creates a new support and resistence based on the given data source.
     *
     * @param	source	the graph source to average     
     * @param   support the data points of a support line
     * @param   resistence the data points of a resistence line

     Currently only the points of the most numerous peaks and troughs are used       as a heuristic to determine the points of support and resistence. It's a 
     heuristic only, and still needs work.  
     */
    public Graphable[] createSupportAndResistence(Graphable source, Graphable support, Graphable resistence) {
	Graphable supportAndResistence[] = new Graphable[2];
	double[] values = source.toArray();
	double[] peaks, troughs;
	double peaksPriceData[] = new double[2];
	double troughsPriceData[] = new double[2];
	int peaksIndex = 0;
	int troughsIndex = 0;
	ArrayList peaksCount = new ArrayList(values.length);
	ArrayList troughsCount = new ArrayList(values.length);
	double prev, diff;		
	Set xRange = source.getXRange();
	Iterator iterator = xRange.iterator();
	boolean upmove = false;
	int i;

	peaks = new double[values.length];
	troughs = new double[values.length];

	prev = 0.0;	
	peaksPriceData[0] = 0.0;
	peaksPriceData[1] = 0.0;
	troughsPriceData[0] = 0.0;
	troughsPriceData[1] = 0.0;
	
	// Get all the peaks and troughs

	for (i = 0; i < values.length; i++) {
	    diff = values[i] - prev;
	    if (i == 0) {
		// hole in logic here, when current price = prev
		if (values[0] - values[1] > 0) {
		    upmove = true;
		} else {
		    upmove = false;
		}
	    }	    

	    if (i > 0) {		
		// previous price was end of downmove, hence trough
		if (diff > 0 && !upmove) {		
		    troughs[troughsIndex++] = prev;
		    upmove = true;
		}
		// previous price was end of upmove, hence peak		
		if (diff < 0 && upmove) {
		    peaks[peaksIndex++] = prev;		    
		    upmove = false;
		}
	    }
	    prev = values[i];
	}

	Arrays.sort(peaks, 0, peaksIndex);
	Arrays.sort(troughs, 0, troughsIndex);

	countOccurances(peaksCount, peaks, peaksIndex-1);
	countOccurances(troughsCount, troughs, troughsIndex-1);       	

	//Choose the most numerous peak and troughs 
	
	if (peaksCount.size() > 0) {
	    peaksPriceData = (double[])peaksCount.get(peaksCount.size()-1);
	} else {
	    // no peak data 
	}
	if (troughsCount.size() > 0) {
	    troughsPriceData = (double[])troughsCount.get(troughsCount.size()-1);
	} else {
	    // no trough data.
	}

	i = 0;
	while (iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();
	    if (values[i] == peaksPriceData[0]) {
		resistence.putY(x, new Double(values[i]));
	    }
	    if (values[i] == troughsPriceData[0]) {
		support.putY(x, new Double(values[i]));
	    }
	    i++;
	}
	
	supportAndResistence[0] = support;
	supportAndResistence[0] = resistence;

	return supportAndResistence;
    }
    
    // Creates a two element list (price, occurranceCount)  
    // in list from the array values[]
    private void countOccurances(ArrayList list, double values[], int length) {
	int i, priceIndex = 0;
	double prev;
	double priceData[] = new double[2];
	
	prev = 0.0;	
	priceData[0] = 0.0;
	priceData[1] = 0.0;

	for (i = 0; i < length-3; i++) {
	    if (prev != values[i]) {
		if (i > 0) {
		    double[] tmp = new double[2];
		    tmp[0] = priceData[0];
		    tmp[1] = priceData[1];

		    list.add(priceIndex++, (double[])tmp);
		}
		
		priceData[0] = values[i];
		priceData[1] = 0;

	    } else {
		priceData[1]++;
	    }	   
	    prev = values[i];
	}

	// Last price data may have been same as prev, so it won't
	// have been added.       
	if (length > 0 && values[i-1] == prev) {
	    list.add(priceIndex, priceData);
	}
	
	Collections.sort(list, new Comparator() {
		public int compare(Object a, Object b) {
		    double[] v1, v2;
		    v1 = (double[])a;
		    v2 = (double[])b;
		    
		    if (v1[1] < v2[1]) {
			return -1; 
		    } 
		    if (v1[1] > v2[1]) {
			return 1;
		    }
		    return 0;
		}
	    });	
    }
}
