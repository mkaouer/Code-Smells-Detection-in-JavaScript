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
import java.util.*;

import org.mov.chart.source.*;
import org.mov.util.*;
import org.mov.quote.*;

/** 
 * Provides a common interface to allow the charting module to support
 * a variety of graphs. Classes that implement this interface will
 * be able to render the graph onto the chart, provide axes information
 * and any annotations the graph may have. 
 */
public interface Graph {

    /** 
     * Maximum distance between the mouse pointer and the graph which will 
     * still make the tooltip come up 
     */
    public static int TOOL_TIP_BUFFER = 50;

    /**
     * Draw the graph.
     * 
     * @param	g	the Graphics object to render to
     * @param	colour	the colour the graph should be rendered in; override
     *			this if the graph should be drawn in
     *			a certain colour - such as a Moving Average graph
     * @param	xoffset	the x offset in the graphics object where the graph
     *			starts
     * @param	yoffset	the y offset in the graphics object where the graph
     *			starts
     * @param	horizontalScale	horizontal scale factor; use this to convert
     *                  between X value in the <code>xRange</code> to a 
     *			cartesian coordinate x
     * @param	verticalScale	vertical scale factor; use this to convert
     *                  between Y value to a cartesian coordinate y
     * @param	bottomLineValue	the Y value of the lowest line in the graph
     * @param	xRange	a <code>Vector</code> of <code>Comparable</code> 
     *			that contain the X values to plot
     */
    public void render(Graphics g, Color colour, 
		       int xoffset, int yoffset,
		       float horizontalScale, float verticalScale,
		       float bottomLineValue, Vector xRange);

    /**
     * Get the tool tip text for the given X value and y coordinate.
     *
     * @param	x	the X value
     * @param	y	the y coordinate
     * @param	yoffset	y offset from top of graph
     * @param	verticalScale	vertical scale factor
     * @param	bottomLineValue	the Y value of the lowest line in the graph
     * @return	the tooltip text
     */
    public String getToolTipText(Comparable x, int y, int yoffset,
				 float verticalScale,
				 float bottomLineValue);
    /**
     * Get the first X value that this graph will draw.
     *
     * @return	X value of the first x coordinate in graph
     */
    public Comparable getStartX();

    /**
     * Get the last X value that this graph will draw.
     *
     * @return	X value of the last x coordinate in graph
     */
    public Comparable getEndX();

    /**
     * Get all X values that this graph will draw.
     *
     * @return	<code>Vector</code> of <code>Comparable</code> X values
     */
    public Set getXRange();

    /**
     * Convert the Y value to a label to be displayed in the vertical
     * axis.
     *
     * @param	value	y value
     * @return	the label text
     */
    public String getYLabel(float value);

    /**
     * Return the name of the graph we are drawing.
     *
     * @return	the name of the graph
     */
    public String getName();

    /**
     * Return the Y value for the given X value.
     *
     * @param	X value
     * @return	Y value
     */
    public Float getY(Comparable xRange);

    /**
     * Return the highest Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the highest Y value
     */
    public float getHighestY(Vector xRange);

    /**
     * Return the loweset Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the lowest Y value
     */
    public float getLowestY(Vector xRange);

    /**
     * Return an array of acceptable major deltas for the vertical
     * axis. The vertical axis is created in such a way that each
     * horizontal line is placed in the graph at a sensible value,
     * e.g. a graph may have four horizontal lines, each separated by $1.20:
     *
     * <pre>
     * --------------- $4.80
     *
     * -----+==+------ $3.60
     *     /    \
     * ---+------+---- $2.40
     *   /        \---
     * -+------------- $1.20
     * </pre>
     *
     * Here the values of the horizontal lines are $1.20, $2.40, $3.60 &
     * $4.80. These are <i>sensible</i> values, values such as
     * $1.36, $2.72, $4.08 & $5.44 are not as sensible. All the allowable
     * values are calculated by the cross product of Major Deltas x
     * Minor Deltas.
     *
     * @return	an array of floats representing the major deltas
     */
    public float[] getAcceptableMajorDeltas();

    /**
     * Return an array of acceptable minor deltas for the vertical
     * axis.
     *
     * @return	an array of floats representing the minor deltas
     * @see	#getAcceptableMajorDeltas
     */ 
    public float[] getAcceptableMinorDeltas();

    /**
     * Return the annotations for this graph or <code>null</code> if it
     * does not have any. The annotations should be in a map of X values
     * to <code>String</code> values.
     *
     * @return	map of annotations
     */
    public HashMap getAnnotations();

    /**
     * Return if this graph has any annotations.
     *
     * @return	<code>true</code> if this graph has annotations;
     *		<code>false</code> otherwise
     */
    public boolean hasAnnotations();
}


