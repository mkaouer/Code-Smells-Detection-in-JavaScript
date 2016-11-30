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

package nz.org.venice.chart;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import nz.org.venice.chart.graph.*;
import nz.org.venice.chart.source.*;
import nz.org.venice.util.Locale;
import nz.org.venice.portfolio.*;
import nz.org.venice.quote.*;
import nz.org.venice.prefs.settings.MenuSettings;

/**
 * Provides a menu which is associated with a stock symbol being graphed.
 * The menu provides a series of options which allow the user
 * to graph related charts and indicators.
 */
public class PortfolioChartMenu extends JMenu implements ActionListener {

    // Graphs
    private static final String CASH_VALUE = Locale.getString("CASH_VALUE");
    private static final String SHARE_VALUE = Locale.getString("SHARE_VALUE");
    private static final String RETURN_VALUE = Locale.getString("RETURN_VALUE");
    private static final String STOCKS_HELD = Locale.getString("STOCKS_HELD");

    JMenu graphMenu;
    JMenu annotateMenu;

    private JMenuItem removeMenu;

    private EODQuoteBundle quoteBundle;
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
    public PortfolioChartMenu(ChartModule listener, EODQuoteBundle quoteBundle,
			      Portfolio portfolio, Graph graph) {

	super(graph.getSourceName());
	
	this.quoteBundle = quoteBundle;
	this.graph = graph;
	this.listener = listener;
	this.portfolio = portfolio;
	
	init();
       
    }

    /**
     * Create a new menu allowing the user to graph related graphs
     * for the given graph. The symbol we are associated with will be
     * extracted from the graph.
     *
     * @param	listener	the chart module associated with the menu
     * @param	graph		the graph we are associated with
     * @param	menuSettings	the menuSettings for the menu
     */
    public PortfolioChartMenu(ChartModule listener, EODQuoteBundle quoteBundle,
			      Portfolio portfolio, Graph graph, MenuSettings menuSettings) {

	super(menuSettings.getTitle());
	
	this.quoteBundle = quoteBundle;
	this.graph = graph;
	this.listener = listener;
	this.portfolio = portfolio;
	
	init();	
	map = menuSettings.getMap();

	//Select all the graphs in the menu
	Iterator iterator = map.keySet().iterator();
	while (iterator.hasNext()) {
	    String key = (String)iterator.next();
	    Graph value = (Graph)map.get(key);
	    selectMenuItem(value.getName());
	}

    }

    private void init() {
	// Create graph + annotation menus
	graphMenu = new JMenu(Locale.getString("GRAPH"));
	this.add(graphMenu);

	// Add graph menu items
	addMenuItem(CASH_VALUE);
	addMenuItem(SHARE_VALUE);
	addMenuItem(RETURN_VALUE);
	addMenuItem(STOCKS_HELD);

        // Add account menu items
	graphMenu.addSeparator();

        List accounts = portfolio.getAccounts();
        for(Iterator iterator = accounts.iterator(); iterator.hasNext();) {
            Account account = (Account)iterator.next();
            addMenuItem(account.getName());
        }

        // Add remove option
	this.addSeparator();
	removeMenu = new JMenuItem(Locale.getString("REMOVE"));
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
        if(graph != null)
            return graph.getSourceName();
        else
            return "";
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
	    else if(text == RETURN_VALUE)
		addGraph(new LineGraph(new PortfolioGraphSource(portfolio,
								quoteBundle,
								PortfolioGraphSource.RETURN_VALUE),
                                       RETURN_VALUE, true),
			 RETURN_VALUE, 0);
	    else if(text == CASH_VALUE)
		addGraph(new LineGraph(new PortfolioGraphSource(portfolio,
								quoteBundle,
								PortfolioGraphSource.CASH_VALUE),
                                       CASH_VALUE, true),
			 CASH_VALUE, 0);
	    else if(text == SHARE_VALUE)
		addGraph(new LineGraph(new PortfolioGraphSource(portfolio,
								quoteBundle,
								PortfolioGraphSource.SHARE_VALUE),
                                       SHARE_VALUE, true),
			 SHARE_VALUE, 0);

	    else if(text == STOCKS_HELD)
		addGraph(new BarGraph(new PortfolioGraphSource(portfolio,
                                                               quoteBundle,
                                                               PortfolioGraphSource.STOCKS_HELD),
                                      STOCKS_HELD, false),
			 STOCKS_HELD);

            // Otherwise it's an account in the portfolio
            else {
                String accountName = text;
                addGraph(new LineGraph(new PortfolioGraphSource(portfolio,
                                                                quoteBundle,
                                                                accountName),
                                       accountName, true),
                         accountName, 0);
            }
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

    //Select the menuItem for graphName.
    private void selectMenuItem(String graphName) {
	
	for (int i = 0; i < graphMenu.getItemCount(); i++) {
	    JMenuItem item = graphMenu.getItem(i);
	    if (item != null) {

		if (graphName.compareTo(item.getText()) == 0) {
		    item.setSelected(true);
		    return;
		}
	    }
	}	
    }

}
