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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mov.chart.source.*;
import org.mov.util.*;
import org.mov.quote.*;

/**
 * Set the default values for a graph based on a single 
 * <code>GraphSource</code>. All graphs that are based on a single
 * <code>GraphSource</code> object will almost certainly want to use these
 * default values. Graphs that use multiple <code>GraphSource</code>
 * objects will still probably want to extend this class and override
 * any differences. 
 */
abstract public class AbstractGraph implements Graph {

    // We provide defaults based on a single GraphSource object.
    private GraphSource source;
   
    /**
     * Set the default values to the ones provided by this 
     * <code>GraphSource</code>.
     *
     * @param	source	default <code>GraphSource</code> to use
     */
    public AbstractGraph(GraphSource source) {
	this.source = source;
    }

    /**
     * Get underlying <code>GraphSource</code> we are using for default
     * values.
     *
     * @return	the default <code>GraphSource</code>
     */
    protected GraphSource getSource() {
    	return source;
    }

    /**
     * Get the tool tip text for the given X value and y coordinate.
     *
     * @param	x	the X value
     * @param	y	the y coordinate
     * @param	yoffset	y offset from top of graph
     * @param	verticalScale	vertical scale factor
     * @param	bottomLineValue	the Y value of the lowest line in the graph
     * @return	the tool tip text for the default <code>GraphSource</code>
     */
    public String getToolTipText(Comparable x, int y, int yoffset,
				 float verticalScale,
				 float bottomLineValue) {
	return source.getToolTipText(x);
    }

    /**
     * Get the first X value that this graph will draw.
     *
     * @return	X value of the first x coordinate in the default 
     *		<code>GraphSource</code>'s <code>Graphable</code>
     */
    public Comparable getStartX() {
	return source.getGraphable().getStartX();
    }

    /**
     * Get the last X value that this graph will draw.
     *
     * @return	X value of the last x coordinate in the default 
     *		<code>GraphSource</code>'s <code>Graphable</code>
     */
    public Comparable getEndX() {
	return source.getGraphable().getEndX();
    }

    /**
     * Get all X values that this graph will draw.
     *
     * @return	X values in the default <code>GraphSource</code>'s 
     *		<code>Graphable</code>
     */
    public Set getXRange() {
	return source.getGraphable().getXRange();
    }

    /**
     * Convert the Y value to a label to be displayed in the vertical
     * axis.
     *
     * @param	value	y value
     * @return	the Y label text that the default <code>GraphSource</code>
     *		would display
     */
    public String getYLabel(float value) {
	return source.getYLabel(value);
    }

    /**
     * Return the name of the graph we are drawing.
     *
     * @return	the name of the default <code>GraphSource</code>
     */
    public String getName() {
	return source.getName();
    }

    /**
     * Return the Y value for the given X value.
     *
     * @param	x value
     * @return	Y value of the default <code>GraphSource</code>
     */
    public Float getY(Comparable x) {
	return source.getGraphable().getY(x);
    }

    /**
     * Return the highest Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the highest Y value of the default <code>GraphSource</code>
     */
    public float getHighestY(List xRange) {
	return source.getGraphable().getHighestY(xRange);
    }

    /**
     * Return the loweset Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the lowest Y value of the default <code>GraphSource</code>
     */
    public float getLowestY(List xRange) {
	return source.getGraphable().getLowestY(xRange);
    }

    /**
     * Return an array of acceptable major deltas for the vertical
     * axis.
     *
     * @return	an array of floats representing the minor deltas 
     *		of the default <code>GraphSource</code>
     */ 
    public float[] getAcceptableMajorDeltas() {
	return source.getAcceptableMajorDeltas();
    }

    /**
     * Return an array of acceptable minor deltas for the vertical
     * axis.
     *
     * @return	an array of floats representing the minor deltas
     *		of the default <code>GraphSource</code>
     * @see	Graph#getAcceptableMajorDeltas
     */ 
    public float[] getAcceptableMinorDeltas() {
	return source.getAcceptableMinorDeltas();
    }

    /**
     * Return the annotations for this graph or <code>null</code> if it
     * does not have any. The annotations should be in a map of X values
     * to <code>String</code> values.
     *
     * @return	<code>null</code>
     */
    public HashMap getAnnotations() {
	return null;
    }

    /**
     * Return if this graph has any annotations.
     *
     * @return	<code>false</code>
     */
    public boolean hasAnnotations() {
	return false;
    }
}

