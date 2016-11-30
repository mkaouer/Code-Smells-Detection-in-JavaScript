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

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nz.org.venice.chart.source.GraphSource;

/**
 * Provides a common interface to allow the charting module to support
 * a variety of graphs. Classes that implement this interface will
 * be able to render the graph onto the chart and provide axes information.
 * Each graph can have its own user interface to allow the user to set
 * user-definable parameters.
 *
 * @author Andrew Leppard
 * @see AbstractGraph
 * @see GraphUI
 * @see GraphSource
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
     * @param	topLineValue	the Y value of the lowest line in the graph
     * @param	bottomLineValue	the Y value of the lowest line in the graph
     * @param	xRange	a <code>List</code> of <code>Comparable</code>
     *			that contain the X values to plot
     * @param   vertOrientation true if increasing y values mean decreasing Y values 
     */

    public void render(Graphics g, Color colour,
		       int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double topLineValue, double bottomLineValue, 
		       List xRange, 
		       boolean vertOrientation);

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
				 double verticalScale,
				 double bottomLineValue);
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
     * @return	<code>List</code> of <code>Comparable</code> X values
     */
    public Set getXRange();

    /**
     * Convert the Y value to a label to be displayed in the vertical
     * axis.
     *
     * @param	value	y value
     * @return	the label text
     */
    public String getYLabel(double value);

    /**
     * Return the name of the graph we are graphing, e.g.
     * <code>Simple Moving Average</code>.
     *
     * @return	the name of the graph
     */
    public String getName();

    /**
     * Return the name of the source data that we are graphing, e.g.
     * <code>CBA</code>.
     *
     * @return the name of the source
     */
    public String getSourceName();

    /**
     * Return the type of the source data that we are graphing, e.g.
     * <code>Portfolio</code>.
     *
     * @return the type of the source
     */
    public int getSourceType();


    /**
     * Return the Y value for the given X value.
     *
     * @param	xRange value
     * @return	Y value
     */
    public Double getY(Comparable xRange);

    /**
     * Return the highest Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the highest Y value
     */
    public double getHighestY(List xRange);

    /**
     * Return the loweset Y value in the given X range.
     *
     * @param	xRange	range of X values
     * @return	the lowest Y value
     */
    public double getLowestY(List xRange);

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
     * @return	an array of doubles representing the major deltas
     */
    public double[] getAcceptableMajorDeltas();

    /**
     * Return an array of acceptable minor deltas for the vertical
     * axis.
     *
     * @return	an array of doubles representing the minor deltas
     * @see	#getAcceptableMajorDeltas
     */
    public double[] getAcceptableMinorDeltas();

    /**
     * Return the graph's current settings. Each graph must contain
     * its user definable settings in a hashmap. If a graph does not
     * have any user definable settings, then it can just return
     * an empty hashmap here.
     *
     * @return settings
     */
    public HashMap getSettings();

    /**
     * Set the graph's user definable settings.
     *
     * @param settings the new settings
     */
    public void setSettings(HashMap settings);

    /**
     * Return the graph's user interface to allow the user to
     * modify its settings. Pass the initial settings or an
     * empty hashmap to use the graph's default settings.
     * If the graph does not have any user-definable settings, then
     * it should not have a user interface and should return
     * <code>null</code> here.
     *
     * @param settings initial settings
     * @return user interface or <code>null</code>
     */
    public GraphUI getUI(HashMap settings);

    /**
     * Return whether the graph is a <i>primary</i> graph. Primary
     * graphs will appear together in the top chart. <i>Secondary</i>
     * graphs appear in their own charts which are added below.
     * The day close graph is primary; while the day close graph is
     * secondary.
     *
     * @return <code>true</code> if the graph is a primary graph
     */
    public boolean isPrimary();

    /**
     * Return whether the graph has a price for range xRange  
     * Is trivially true for all graphs apart from the PointAndFigure graph 
     * as P&F is the only graph type where price data does not map one to one  
     * to date.
     *
     * @param   xRange Range of X values
     * @return  True if data is available for range xRange.     
    */

    public boolean dataAvailable(Vector xRange);



}


