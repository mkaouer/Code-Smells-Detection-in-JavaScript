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

import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import org.mov.chart.graph.*;
import org.mov.chart.source.*;
import org.mov.util.*;
import org.mov.portfolio.*;
import org.mov.quote.*;

/**
 * Provides a menu which is associated with a stock symbol being graphed. 
 * The menu provides a series of options which allow the user
 * to graph related charts and indicators. 
 */
public class PortfolioChartMenu extends JMenu implements ActionListener {

    // Graphs
    private static final String PROFIT_LOSS	= "Profit Loss";

    JMenu graphMenu;
    JMenu annotateMenu;

    private JMenuItem removeMenu;
    
    private QuoteBundle quoteBundle;
    private Graph graph;
    private ChartModule listener;
    private HashMap map = new HashMap();
  
    private Portfolio portfolio;

    /**
     * Create a new menu allowing the user to graph related graphs
     * for the given graph. The symbol we are associated with will be
     * extracted from the graph.
     *
     * @param	listener	the chart module associated with the menu
     * @param	graph		the graph we are associated with
     */
    public PortfolioChartMenu(ChartModule listener, QuoteBundle quoteBundle,
			      Portfolio portfolio, Graph graph) {

	super(graph.getName());
	
	this.quoteBundle = quoteBundle; 
	this.graph = graph;
	this.listener = listener;
	this.portfolio = portfolio;
	
	// Create graph + annotation menus
	graphMenu = new JMenu("Graph");
	this.add(graphMenu);

	// Add graph menu items
	addMenuItem(PROFIT_LOSS);

	// Add all static menus
	this.addSeparator();
	removeMenu = new JMenuItem("Remove");
	removeMenu.addActionListener(this);
	this.add(removeMenu);	    
    }

    // Add a graph menu item, e.g. "Market Value"
    private void addMenuItem(String label) {
	// Add graph menu
	JMenuItem item = new JCheckBoxMenuItem(label);
	item.addActionListener(this);
	graphMenu.add(item);
    }

    /**
     * Return the graph name we are associated with
     *
     * @return	the graph name
     */
    public String getName() {
	return graph.getName();
    }

    /**
     * This function is called when the user selects one of the menu
     * items.
     *
     * @param	e	the action performed
     */
    public void actionPerformed(ActionEvent e) {
	
	// Check static menus first
	if(e.getSource() == removeMenu) {
	    listener.removeAll(getName());
	    listener.redraw();
	}
	
	// Otherwise check dynamic menus
	else {
	    JCheckBoxMenuItem menu = (JCheckBoxMenuItem)e.getSource();
	    String text = menu.getText();
	    
	    // Handle removing graphs next
	    if(!menu.getState())
		removeGraph(text);
	    
	    // Ok looks like its adding a graph
	    else if(text == PROFIT_LOSS) 
		addGraph(new LineGraph(new PortfolioGraphSource(portfolio,
								quoteBundle, 
								PortfolioGraphSource.PROFIT_LOSS)), 
			 PROFIT_LOSS, 0);
	}
    }
    
    // Adds graph to chart
    private void addGraph(Graph graph, String mapIdentifier) {
	map.put(mapIdentifier, graph); 
	listener.append(graph);
	listener.redraw();
    }
    
    // Same as above but add at specific index
    private void addGraph(Graph graph, String mapIdentifier, int index) {
	map.put(mapIdentifier, graph); 
	listener.append(graph, index);
	listener.redraw();
    }

    // Removes graph from chart
    private void removeGraph(String mapIdentifier) {
	Graph graph = (Graph)map.get(mapIdentifier);
	map.remove(mapIdentifier);
	
	// Remove graph and annotation
	listener.remove(graph);
	listener.handleAnnotation(graph, false);
	listener.redraw();
    }
}
