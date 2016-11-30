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

package org.mov.chart;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

import org.mov.chart.graph.*;
import org.mov.util.Locale;

/**
 * New swing component that allows creation of charts. This charting
 * component supports graphing levels and also highlighting of a graph
 * region.
 *
 * @see ChartModule
 */
public class Chart extends JComponent implements MouseListener {

    // The first element of this vector contains a vector of the
    // graphs for the primary graph, the second for the secondary graph etc
    // I.e. its a vector of vector of graphs.
    private Vector levels = new Vector();

    private BasicChartUI gui;
    private boolean toolTipBeenUp = false;

    // Will graph between the start and end X coordinates (inclusive)
    private Comparable startX;
    private Comparable endX;
    private Vector xRange;

    // When highlighting the graph keep track of the start and end x
    private Comparable startHighlightedX;
    private Comparable endHighlightedX;

    private ChartDrawingModel drawnElements;

    // Are we in a zoomed in view?
    private boolean zoomedIn = false;

    // Map of graphs to display annotations
    private HashMap annotatedGraphs = new HashMap();

    /**
     * Create a new Chart component.
     */
    public Chart() {
	setBackground(Color.white);
	setForeground(Color.gray);
	addMouseListener(this);
	updateUI();

	drawnElements = new ChartDrawingModel();
	
    }

    // Create a vector of iterators containing the x range iterators
    // for each graph.
    private Vector getGraphXRangeIterators() {

	Vector graphXRangeIterators = new Vector();
	Iterator levelsIterator = levels.iterator();
	
	while(levelsIterator.hasNext()) {

	    Iterator graphIterator =
		((Vector)levelsIterator.next()).iterator();

	    while(graphIterator.hasNext()) {	
		
		Graph graph = (Graph)graphIterator.next();
		Set xRange = graph.getXRange();
		graphXRangeIterators.add(xRange.iterator());
	    }
	}

	return graphXRangeIterators;
    }

    // Create a set of X values containing every X value between startX
    // and endX (inclusive) that occurs in any one of the graphs we
    // are charting.
    private void setXRange(Comparable startX, Comparable endX) {

	// Algorithm work as follows:
	//
	// get x range iterators for all graphs
	//
	// while x range iterator for all graphs
	//	get graph x range iterator
	//
	//	while graph x range iterator
	//		get x value
	//		if below starting x
	//		        next
	//		if above starting x
	//			last
	//		if not already in set of x values
	//			add

	this.startX = startX;
	this.endX = endX;

	// Use a tree set to give us a means of sorting the x range
	TreeSet xRangeSet = new TreeSet();

	// Get vector of iterators over x range for each graph
	Vector graphXRangeiterators = getGraphXRangeIterators();

	// Add each x within range
	Iterator graphXRangeIterator = graphXRangeiterators.iterator();

	while(graphXRangeIterator.hasNext()) {

	    // Get next graph's iterator over its x range
	    Iterator xRangeIterator = (Iterator)graphXRangeIterator.next();

	    while(xRangeIterator.hasNext()) {
		Comparable x = (Comparable)xRangeIterator.next();

		if(x.compareTo(startX) < 0)
		    continue;
		else if(x.compareTo(endX) > 0)
		    break;
		else if(!xRangeSet.contains(x))
		    xRangeSet.add(x);
	    }
	}

	// Load x range into a vector as we need to access
	// values at arbitary offsets
	xRange = new Vector(xRangeSet);
    }

    /**
     * Get the range of X values which appear in the chart.
     *
     * @return	<code>Vector</code> of <code>Comparables</code>
     */
    public Vector getXRange() {
	return xRange;
    }
    
    /**
     * Get the first X value that appears in the chart.
     */
    public Comparable getStartX() {
	return startX;
    }

    /**
     * Get the last X value that appears in the chart.
     */
    public Comparable getEndX() {
	return endX;
    }

    // Find the lowest X value in all the graphs we are going to chart
    private Comparable calculateStartX() {
	Iterator iterator = levels.iterator();
	Comparable x;
	Comparable startX = null;
	
	while(iterator.hasNext()) {

	    Iterator innerIterator = ((Vector)iterator.next()).iterator();
	
	    while(innerIterator.hasNext()) {	
		x =
		    ((Graph)innerIterator.next()).getStartX();
		if(startX == null || x.compareTo(startX) < 0)
		    startX = x;
	    }
	}

	return startX;
    }

    // Find the highest X value in all the graphs we are going to chart
    private Comparable calculateEndX() {
	Iterator iterator = levels.iterator();
	Comparable x;
	Comparable endX = null;
	
	while(iterator.hasNext()) {

	    Iterator innerIterator = ((Vector)iterator.next()).iterator();
	
	    while(innerIterator.hasNext()) {	

		x =
		    ((Graph)innerIterator.next()).getEndX();

		if(endX == null || x.compareTo(endX) > 0)
		    endX = x;
	    }
	}
	
	return endX;
    }

    /**
     * Set whether the given graph should display its annotations.
     *
     * @param	graph	the graph to modify
     * @param	enabled	<code>true</code> to turn on graph annotations;
     *			<code>false</code> to turn them off
     */
    public void handleAnnotation(Graph graph, boolean enabled) {
	if(enabled)
	    annotatedGraphs.put(graph, new Boolean(true));
	else
	    annotatedGraphs.remove(graph);
    }

    /**
     * Query whether the given graph is displaying its annotations.
     *
     * @param	graph	the graph to query
     * @return	<code>true</code> if the graph is displaying its annotations;
     *		<code>false</code> otherwise
     */
    public boolean isAnnotated(Graph graph) {
	if(annotatedGraphs.get(graph) != null)
	    return true;
	else
	    return false;
    }

    /**
     * Chart a new graph at the given level. If the chart does not contain
     * the level, create a new one.
     *
     * @param	graph	the new graph to chart
     * @param	index	the level index
     */
    public void add(Graph graph, int index) {

        // Make sure it has at least one value
        assert graph.getXRange().size() > 0;

	// Do we already have any graphs at this index?
	if(index >= levels.size()) {
	    Vector newGraphs = new Vector();
	    newGraphs.add(graph);
	    levels.add(newGraphs);
	}

	// Otherwise add it to an existing graph vector
	else {
	    Vector oldGraphs = (Vector)levels.elementAt(index);
	    oldGraphs.add(graph);
	}

	// If we are not zoomed in, make sure we are displaying the
	// x range of the new graph
	if(!zoomedIn)
	    setXRange(calculateStartX(), calculateEndX());
    }

    /**
     * Return the number of graphs in the chart.
     *
     * @return the number of graphs in the chart
     */
    public int count() {
        Iterator iterator = levels.iterator();
        int count = 0;

        while(iterator.hasNext()) {
            Vector innerVector = (Vector)iterator.next();
            count += innerVector.size();
        }

        return count;
    }

    /**
     * Remove the graph from the chart. If the graph was an only member of
     * its level, then the level will also be removed.
     *
     * @param	graph	the graph to remove
     */
    public void remove(Graph graph) {

	// Find and remove first (only) matching graph
	boolean found = false;
	Graph traverse;
	Iterator iterator = levels.iterator();
	Vector innerVector;
	Iterator innerIterator;

	while(iterator.hasNext()) {
	    innerVector = (Vector)iterator.next();
	    innerIterator = innerVector.iterator();

	    while(innerIterator.hasNext()) {
		traverse = (Graph)innerIterator.next();

		// Found one to remove?
		if(traverse == graph) {
		    // Remove it
		    innerVector.removeElement((Object)graph);

		    // Is the vector now empty?
		    if(innerVector.size() == 0) {
			// Yes so remove it from the outer vector
			levels.remove(innerVector);

			// If we are not zoomed in a graph has disappeared,
			// make sure we arent displaying x range for a graph
			// that doesnt exist yet - but dont do this if
			// we are zoomed in.
			if(!zoomedIn)
			    setXRange(calculateStartX(), calculateEndX());

		    }
		    return;
		}
	    }
	}

	// If we got here we couldnt find it
    }

    /**
     * Return all the graphs for each level.
     *
     * @return	A <code>Vector</code> where each element represents a
     *		a graph level. Each element in the <code>Vector</code>
     *		is another <code>Vector</code> containing the graphs at that
     *		level.
     */
    public Vector getLevels() {
	return levels;
    }

    /**
     * Return the colour that the given graph will be drawn. Note that the
     * graph may choose to override this colour.
     *
     * @param	graph	the graph to query
     * @return	the colour the graph may be drawn as
     */
    public Color getGraphColour(Graph graph) {
	return gui.getGraphColour(graph, this);
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param	ui	the ButtonUI L&F object
     */
    public void setUI(BasicChartUI ui) {
	super.setUI(ui);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     */
    public void updateUI() {
	gui = new BasicChartUI();

	setUI(gui);
	resetBuffer();
    }

    /**
     * Return the X value at the given x coordinate.
     *
     * @param	x	an x coordinate on the screen
     * @return	the X value at the x coordinate
     */
    public Comparable getXAtPoint(int x) {
	return gui.getXAtPoint(this, x);
    }

    /**
     * Return the Y value at the given y coordinate.
     *
     * @param	y	an x coordinate on the screen
     * @return	the Y value at the y coordinate
     */
    public Double getYAtPoint(int y) {
	return gui.getYAtPoint(this, y);
    
    }


    /**
     * Return whether the x,y coordinate is within this component.
     *
     * @param	x	the x coordinate
     * @param	y	the y coordinate
     * @return	<code>true</code> if the point is within this component;
     *		<code>false</code> otherwise.
     */
    public boolean contains(int x, int y) {

	String text = gui.getToolTipText(this, x, y);

	// If the user leaves the cursor over the chart but not near
	// the graph (i.e. the line) we'll tell them how to get a day
	// quote. This is really to fix a problem where tool tips dont
	// pop up IFF another gadget is listening for mouse events AND
	// the first few tool tips set are NULL when the cursor enters
	// the gadget.
	if(text == null && !toolTipBeenUp)
	    text = Locale.getString("HOVER_CURSOR_NEAR_GRAPH");
	else if(!toolTipBeenUp)
	    toolTipBeenUp = true;

	setToolTipText(text);

	return super.contains(x, y);
    }

    // Set the size of this component
    private void setSize() {
	setMinimumSize(new Dimension(BasicChartUI.getMinimumWidth(this),
				     BasicChartUI.getMinimumHeight(this)));
	setPreferredSize(new Dimension(BasicChartUI.getMinimumWidth(this),
				       BasicChartUI.getMinimumHeight(this)));
    }

    /**
     * Returns a string that specifies the name of the l&f class that
     * renders this component.
     *
     * @return String "ChartUI"
     */
    public String getUIClassID() {
	return "ChartUI";
    }

    /**
     * Sets the start X point of the highlighted region. Also resets the
     * end point of the highlighted region to the start point. This
     * creates a highlighted region 1 pixel thick.
     *
     * @param	x	the starting x value
     */
    public void setHighlightedStart(Comparable x) {
	startHighlightedX = x;
	endHighlightedX = x;
	repaint();
    }

    /**
     * Sets the end X point of the highlighted region. 
     *      
     * @param	x	the starting x value
     *
     */
    public void setHighlightedEnd(Comparable x) {
	endHighlightedX = x;
	repaint();
    }

    /**
     * Sets the end point of a drawn line. The line
     * will now stretch from the start to the end point.
     *
     * @param	dataX	the x data point corresponding to the start of the line
     * @param   dataY   the y data point corresponding to the start of the line.
     * @param   absY    the absolute y coordinate of the start of the line     
     
     */

    public void setDrawnLineStart(Comparable dataX, Double  dataY, Integer absY) {

	Vector lines;
	int level;

	level = gui.getLevelAtPoint(absY.intValue());
		
	drawnElements.createNewLine(new Coordinate(dataX, 
						   dataY, 
						   absY,
						   level));   
	repaint();
    }

    /**
     * Sets the end point of a drawn line. The line
     * will now stretch from the start to the end point.
     *
     * @param	dataX	the x data point corresponding to the end of the line
     * @param   dataY   the y data point corresponding to the end of the line.
     * @param   absY    the absolute y coordinate of the end of the line     
     * @param   newline a flag indicating if the line drawn is new, or being
                continued.
     */
    
    public void setDrawnLineEnd(Comparable dataX, Double dataY, 
				Integer absY) {
	
	Vector lines;
	DrawnLine line;
	int level;

	lines = drawnElements.getDrawnLines();
	line = drawnElements.getLastLine();
	level = gui.getLevelAtPoint(absY.intValue());
	
	line.setEnd(new Coordinate(dataX, dataY, absY,level));

	repaint();
    }

    /**
     * Puts a point on a chart.
     *
     * @param	coord the coordinate 
     */
    
    public void setPoint(Coordinate coord) {
		
	drawnElements.setPoint(coord);
	repaint();
    }


    /**
     * Puts a point on a chart.
     *
     * @param	coord the coordinate 
     */
    
    public void setPoint(Comparable dataX, Double dataY, Integer absY) {
	
	int level = gui.getLevelAtPoint(absY.intValue());
	drawnElements.setPoint(new Coordinate(dataX, dataY, absY, level));
	repaint();
    }

    
    /** 
     * Generate the equation of the line passing through points specified by 
     * index, and return the difference between those points and x,y
     *
     * @param x        the x coordinate
     * @param dataY    the y coordinate
     * @param index    the index a drawn line 
     */

    public double getDifference(Integer x, Integer y, int index) {
	DrawnLine line = drawnElements.getDrawnLine(index);
	
	Coordinate start = line.getStart();
	Coordinate end = line.getEnd();

	return gui.getDifference(this, x, y, start, end);
    }


    /** 
     * Find the graph element intersecting at x,y and delete it
     *
     * @param dataX    the x data corresponding to the selected point
     * @param dataY    the y data corresponding to  the selected point 
     * @param absY     the y coordinate of the selected point 
     */
    
    public void setErase(Comparable dataX, Double dataY, Integer absY) {
	int i;
	int delta = 20;
	Coordinate c1;
	Vector lines, points;
	HashMap text;
	DrawnLine line;
	Iterator iterator;
	

	points = drawnElements.getDrawnPoints();
	lines = drawnElements.getDrawnLines();
	
	text = drawnElements.getText();
	iterator = text.keySet().iterator();
	c1 = new Coordinate(dataX, dataY, absY);
		
	while (iterator.hasNext()) {
	    Coordinate c2 = (Coordinate)iterator.next();
	    if (gui.intersect(this, c1, c2, delta)) {
		drawnElements.removeText(c2);
	    }
	}

	iterator = points.iterator();
	while (iterator.hasNext()) {	    
	    Coordinate c2 = (Coordinate)iterator.next();
	    if (gui.intersect(this, c1, c2, delta)) {
		drawnElements.removePoint(c2);
		
		break;
	    }
	}
		
	iterator = lines.iterator();
	while (iterator.hasNext()) {	    
	    line = (DrawnLine)iterator.next();
	    Coordinate start = line.getStart();
	    Coordinate end = line.getEnd();
	    double diff = gui.getDifference(this, c1, start, end);

	    if (Math.abs(diff) <= 3.0) {
		drawnElements.removeLine(start, end);
		break;		
	    }
	}	
    }
    
    /**
     * Find the line intersecting at x,y.
     * Return the coordinate of the end point which the user has chosen.
     
     @param x         The X Coordinate of the selected point.
     @param y         The Y Coordinate of the selected point.

    */

    public Coordinate move(Integer x, Integer y) {
	int i;
	Coordinate rv = null;
	DrawnLine line;
	Vector lines = drawnElements.getDrawnLines();
	
	for (i = 0; i < lines.size(); i++) {
	    double diff = getDifference(x,y,i);	    
	    if (Math.abs(diff) <= 3.0) {
		line = drawnElements.getDrawnLine(i);
		Coordinate start = line.getStart();
		Coordinate end = line.getEnd();
		rv = gui.getMoved(this, x, y, start, end);
		drawnElements.removeLine(start, end);
		
		break;		
	    }
	}
	return rv;
    }

    /** 
     * Put text at x,y.
     *
     * @param x        the x coordinate of the selected point
     * @param y        the y coordinate of the selected point 
     */

    public void setText(String text, Comparable dataX, Double dataY, Integer absY) {
	
	int level = gui.getLevelAtPoint(absY.intValue());
	drawnElements.setText( new Coordinate(dataX, dataY, absY, level), text);
	
	repaint();
    }

    /**
     * Get the start X of the highlighted region.
     *
     * @return	the start X
     */
    public Comparable getHighlightedStart() {
	return startHighlightedX;
    }

    /**
     * Get the end X of the highlighted region.
     *
     * @return	the end X
     */
    public Comparable getHighlightedEnd() {
	return endHighlightedX;
    }

    public ChartDrawingModel getChartDrawingModel() {
	return drawnElements;
    }

    /**
     * Clear the highlighted region.
     */
    public void clearHighlightedRegion() {
	startHighlightedX = null;
	endHighlightedX = null;
	repaint();
    }

    /**
     * Draw the graph zoomed into the given highlighted region.
     */
    public void zoomToHighlightedRegion() {
	// Order [startHighlightedX, endHighlightedX]
	if(startHighlightedX.compareTo(endHighlightedX) > 0) {
	    Comparable temp = startHighlightedX;
	    startHighlightedX = endHighlightedX;
	    endHighlightedX = temp;
	}

	// Recalculate x range
	setXRange(startHighlightedX, endHighlightedX);
	clearHighlightedRegion();
	zoomedIn = true;
	resetBuffer();
    }

    /**
     * Draw the graph back at its default zoom.
     */
    public void zoomToDefaultRegion() {

	// Recalculate x range
	setXRange(calculateStartX(), calculateEndX());
	zoomedIn = false;
	resetBuffer();
    }

    /**
     * Reset the double buffer, forcing the graph to redraw.
     */
    public void resetBuffer() {
	if(gui != null)
	    gui.resetBuffer();

	setSize();
    }

    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle() {

	Vector symbols = getSymbols();
	String symbolListString = "";
	boolean firstSymbol = true;

	// Sort array of chart symbols
	TreeSet sortedSet = new TreeSet(Collator.getInstance());
	sortedSet.addAll(symbols);

	Iterator symbolIterator = sortedSet.iterator();

	while (symbolIterator.hasNext()) {

	    String symbol = (String)symbolIterator.next();
	    if (firstSymbol) {
		symbolListString = symbolListString.concat(" " + symbol);
		firstSymbol = false;
	    } else {
		symbolListString = symbolListString.concat(", " + symbol);
	    }
	}

	// Now construct the title "Graph of XXX, BBB, YYY"
	return Locale.getString("CHART_TITLE", symbolListString);	
    }

    /**
     * Return the window symbols.
     *
     * @return	the window symbols
     */

    public Vector getSymbols() {
	Iterator levelIterator = levels.iterator();

	// First create a vector of all the chart symbols
	Vector symbols = new Vector();

	while(levelIterator.hasNext()) {
	    Vector graphs = (Vector)levelIterator.next();
	    Iterator graphIterator = graphs.iterator();

	    while(graphIterator.hasNext()) {
		Graph graph = (Graph)graphIterator.next();

		symbols.addElement(graph.getSourceName());
	    }
	}


	return symbols;
    }
    
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {
	toolTipBeenUp = false;
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}


    public BufferedImage getImage() {
	return gui.getImage();
    }

}




