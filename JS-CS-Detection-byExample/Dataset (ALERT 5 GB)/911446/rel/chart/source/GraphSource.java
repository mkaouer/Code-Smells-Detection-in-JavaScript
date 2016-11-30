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

package nz.org.venice.chart.source;

import nz.org.venice.chart.*;

/**
 * Provides an abstraction of the data being graphed, this way graphs
 * do not need to know anything about the underlying data they are graphing.
 */
public interface GraphSource {

    public final static int SYMBOL = 0;
    public final static int INDEX = 1;
    public final static int PORTFOLIO = 2;
    public final static int ADVANCEDECLINE = 3;

    /**
     * Return the name of the data.
     *
     * @return the name
     */
    public String getName();

    /**
     * Return the type of the data.
     *
     * @return the type
     */
    public int getType();

    /**
     * Get the tool tip text for the given X value
     *
     * @param	x	the X value
     * @return	the tooltip text
     */
    public String getToolTipText(Comparable x);

    /**
     * Convert the Y value to a label to be displayed in the vertical
     * axis.
     *
     * @param	value	y value
     * @return	the label text
     */
    public String getYLabel(double value);

    /**
     * Return an array of acceptable major deltas for the vertical
     * axis.
     *
     * @return	array of doubles
     * @see	nz.org.venice.chart.graph.Graph#getAcceptableMajorDeltas
     */ 
    public double[] getAcceptableMajorDeltas();

    /**
     * Return an array of acceptable minor deltas for the vertical
     * axis.
     *
     * @return	array of doubles
     * @see	nz.org.venice.chart.graph.Graph#getAcceptableMajorDeltas
     */ 
    public double[] getAcceptableMinorDeltas();

    /**
     * Get the actual graphable data.
     *
     * @return	the graphable data
     */
    public Graphable getGraphable();
    
    /**
     * Apply an adjustment to the data. 
     * 
     * @param type A split or a dividend.
     * @param adjustValue The value applied to each data point from start point.
     * @param startPoint The point in the data from which to start adjusting.
     * @param direction If true, will adjust the data in a positive direction.
     * 
     */
    public void adjust(int type, double adjustValue, Comparable startPoint, boolean direction);

}


