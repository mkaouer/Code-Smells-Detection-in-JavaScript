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

package nz.org.venice.chart.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nz.org.venice.chart.source.GraphSource;

/**
 * Set the default values for a graph based on a single
 * {@link GraphSource}. All graphs that are based on a single
 * {@link GraphSource} object will almost certainly want to use these
 * default values. Graphs that use multiple {@link GraphSource}
 * objects will still probably want to extend this class and override
 * any differences.
 *
 * @author Andrew Leppard
 * @see Graph
 * @see GraphUI
 * @see GraphSource
 */
abstract public class AbstractGraph implements Graph {

    // We provide defaults based on a single GraphSource object.
    private GraphSource source;

    // Store the settings
    private HashMap settings = new HashMap();

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
				 double verticalScale,
				 double bottomLineValue) {
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
    public String getYLabel(double value) {
	return source.getYLabel(value);
    }

    /**
     * Return the name of the source data that we are graphing, e.g.
     * <code>CBA</code>.
     *
     * @return the name of the source
     */
    public String getSourceName() {
	return source.getName();
    }

   /**
     * Return the type of the source data that we are graphing, e.g.
     * <code>Portfolio</code>.
     *
     * @return the name of the source
     */
    public int getSourceType() {
	return source.getType();
    }

    /**
     * Return the Y value for the given X value.
     *
     * @param	x value
     * @return	Y value of the default <code>GraphSource</code>
     */
    public Double getY(Comparable x) {
	return source.getGraphable().getY(x);
    }

    /**
     * Return the highest Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the highest Y value of the default <code>GraphSource</code>
     */
    public double getHighestY(List xRange) {
	return source.getGraphable().getHighestY(xRange);
    }

    /**
     * Return the loweset Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the lowest Y value of the default <code>GraphSource</code>
     */
    public double getLowestY(List xRange) {
	return source.getGraphable().getLowestY(xRange);
    }

    /**
     * Return an array of acceptable major deltas for the vertical
     * axis.
     *
     * @return	an array of doubles representing the minor deltas
     *		of the default <code>GraphSource</code>
     */
    public double[] getAcceptableMajorDeltas() {
	return source.getAcceptableMajorDeltas();
    }

    /**
     * Return an array of acceptable minor deltas for the vertical
     * axis.
     *
     * @return	an array of doubles representing the minor deltas
     *		of the default <code>GraphSource</code>
     * @see	Graph#getAcceptableMajorDeltas
     */
    public double[] getAcceptableMinorDeltas() {
	return source.getAcceptableMinorDeltas();
    }

    /**
     * Return the graph's current settings. Each graph must contain
     * its user definable settings in a hashmap. If a graph does not
     * have any user definable settings, then it can just return
     * an empty hashmap here.
     *
     * @return settings
     */
    public HashMap getSettings() {
        return settings;
    }

    /**
     * Set the graph's user definable settings.
     *
     * @param settings the new settings
     */
    public void setSettings(HashMap settings) {
        this.settings = settings;
    }

    /**
     * Returns the graph's user interface. The default action is to
     * return <code>null</code> which indicates that the graph does not
     * have a user interface. If the graph does have a user interface then
     * it should override this method.
     *
     * @param settings initial settings (ignored)
     * @return <code>null</code>
     */
    public GraphUI getUI(HashMap settings) {
        // null indicates no UI
        return null;
    }

    /** Return true if there is data available for date X 
     *
     * @param date - Comparable date 
     * @return true 
     *
     * Trivially true for all graphs except for P&F
     * 
     *
     */
    
    //Is it worth generalising the special case (P&F data mapping) in this
    //manner?    
    public boolean dataAvailable(Vector date) {
	return true;
    }
    
}

