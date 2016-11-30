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


/**
 * This manages the interface between ChartModule and TrackedQuoteModule, 
 * ensuring that the location of the cursor matches the selected row in
 * TrackedQuoteModule.
 * 
 * @see ChartModule
 * @see TrackedQuoteModule
 * @author Mark Hummel
 */

package nz.org.venice.chart;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.quote.Symbol;
import nz.org.venice.ui.ModuleListener;
import nz.org.venice.ui.ModuleEvent;
import nz.org.venice.table.TrackedQuoteModule;
import nz.org.venice.chart.graph.Graph;

public class ChartTracking implements ModuleListener {
    
    boolean active;
    ArrayList xrangeList;
    Comparable position; //x value where the cursor is on the chart
    Coordinate coordinate; //Coordinate of the cursor on the chart
    ListIterator positions; 

    Symbol symbol;
    Graph primaryGraph = null;
    ChartModule chartModule;
    Chart chart;
    TrackedQuoteModule table;

    /**
     * Construct a new ChartTracking object to manage the interface between the
     * chartmodule and trackedquote module.
     * @param chartModule The chartModule which contains the chart.
     * @param chart The chart which displays the cursor
     * @param table The TrackedQuoteModule which displays the quote 
     * under the cursor.
     * @param symbol The symbol being graphed.
     */
    public ChartTracking(ChartModule chartModule, Chart chart, TrackedQuoteModule table, Symbol symbol) {
	active = true;
	this.chartModule = chartModule;
	this.chart = chart;
	this.table = table;	
	this.symbol = symbol;

	
	List graphs = (List)chart.getLevels().get(0);
	Iterator graphIterator = graphs.iterator();
	while (graphIterator.hasNext()) {
	    Graph graph = (Graph)graphIterator.next();
	    if (graph.getSourceType() != GraphSource.SYMBOL) {
		continue;
	    }
	    if (graph.getSourceName().equals(symbol.toString())) {
		primaryGraph = graph;
		break;
	    }	    
	}
	assert primaryGraph != null;
	
	xrangeList = new ArrayList(primaryGraph.getXRange());
	positions = xrangeList.listIterator();
	position = (Comparable)xrangeList.get(0);
    }

    /**
     * Activate or deactivate the tracker.
     * 
     * @param active Set to true to activate the tracker
     * 
     */
    public void setActive(boolean active) {
	this.active = active;
    }

    /**
     * @return true if the tracker is activated.
     * 
     */
    public boolean isActive() {
	return active;
    }

    /** 
     * When a new row in the table is selected, move the tracker to the 
     * quote at the selected position.
     * @param position The selected row in the TrackedQuoteModule       
     */
    public void setPosition(int position) {
	int diff = position - table.getPosition();
	int direction = (diff >= 0) ? 1 : -1;

	movePosition(Math.abs(diff), direction);
    }


    /**
     * Record the cursor's new coordinate value.
     * @param x The data value 
     * @param y The y value relative to the level 
     * @param absY The absolute value of the coordinate on the screen
     * @param keyInput Whether the coordinate was passed by keyboard or mouse
     */
    public void setCursorCoord(Comparable x, Double y, Integer absY, boolean keyInput) {
	int level = 0;
	setCoordinate(new Coordinate(x, y, absY, level));
	chart.updateCursor();
	
	//If the position is set by the mouse
	//move the list iterator until position is the same as this coordinate 
	if (!keyInput) {
	    //New position is x
	    //Determine where in the list the new position is.
	    int newIndex = xrangeList.indexOf(x);
	    if (newIndex != -1) {
		int currentIndex = xrangeList.indexOf(position);
		int diff = newIndex - currentIndex;
		
		int direction = (diff >= 0) ? 1 : -1;
		//Update the position, positions iterator and table index
		movePosition(Math.abs(diff), direction);
	    }
	}
    }

    /**
     * Record the cursor's new coordinate value.
     * @param x The data value 
     * @param y The y value relative to the level 
     * @param absY The absolute value of the coordinate on the screen
     */
    public void setCursorCoord(Comparable x, Double y, Integer absY) {
	setCursorCoord(x,y, absY, false);
    }

    private void movePosition(int numPositions, int direction) {	
	for (int i = numPositions; i > 0; i--) {
	    if (direction > 0) {
		if (positions.hasNext()) {
		    position = (Comparable)positions.next();
		}
	    } else {
		if (positions.hasPrevious()) {
		    position = (Comparable)positions.previous();
		}
	    }
	}
	//Update the selected row of the table 
	table.setPosition( table.getPosition() + numPositions * direction);
	update();
    }

    
    /**
     * Set the cursor coordinate.
     * @param coordinate The coordinate value of the cursor
     */
    public void setCoordinate(Coordinate coordinate) {
	this.coordinate = coordinate;
    }

    /**
     * @return The cursor coordinate.
     */
    public Coordinate getCoordinate() {
	return coordinate;
    }
    

    //Causes the cursor to be redrawn in the new position on the chart
    //when a quote is selected on the trackedquotemodel
    private void update() {
	Double y = primaryGraph.getY(position);
	Integer absY = getAbsY();
	setCursorCoord(position, y, absY, true);
    }


    public void moduleAdded(ModuleEvent moduleEvent) {

    }

    public void moduleRenamed(ModuleEvent moduleEvent) {

    }
    
    /**
     * Listen for when the quotemodule or chart module are closed
     * so that either the tracker can be removed or the quote module closed.
     * @param moduleEvent Contains the module which was removed  
     */
    public void moduleRemoved(ModuleEvent moduleEvent) {
	Object module = moduleEvent.getSource();
	
	if (module instanceof TrackedQuoteModule) {
	    if (module == table) {
		remove();
	    }
	}
	if (module instanceof ChartModule) {
	    if (module == chartModule) {
		table.close();
	    }
	}
    }

    /**
     * Remove the tracker from the chart.  
     */
    public void remove() {
	chartModule.removeTracker();
    }

    //Find an absolute y coordinate which is in the first level
    private Integer getAbsY() {
	int testPoint = BasicChartUI.getMinimumHeight(chart);
	int level = chart.getLevelAtPoint(testPoint);
	while (level != 0) {
	    testPoint = (int)(testPoint / 2);
	    level = chart.getLevelAtPoint(testPoint);
	}
	return new Integer(testPoint);
    }
   
}