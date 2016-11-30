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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.mov.chart.graph.Graph;
import org.mov.util.Locale;

/**
 * Provides a menu which is associated with a market indicator being graphed.
 * This menu enables the user to manipulate the graph in the chart. Currently
 * the only item in the menu is the option to remove the graph.
 *
 * @author Andrew Leppard
 */
public class MarketIndicatorChartMenu extends JMenu {

    private Graph graph = null;

    /**
     * Create a new menu for the given market indicator graph.
     *
     * @param listener the chart module to listen for when the graph is removed.
     */
    public MarketIndicatorChartMenu(final ChartModule listener, Graph graph) {
	super(graph.getName());

        this.graph = graph;

       	// Create menu
        JMenuItem removeMenuItem = new JMenuItem(Locale.getString("REMOVE"));
	removeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listener.removeAll(getName());

                    // Only redraw if there are only graphs left
                    if(listener.count() > 0)
                        listener.redraw();
                }});

        add(removeMenuItem);
    }

    /**
     * Return the graph name we are associated with.
     *
     * @return	the graph name
     */
    public String getName() {
        // Under Java 1.5 beta if I don't check against graph being NULL I get
        // a NULL pointer exception. I don't understand why. This whole module
        // will be upgraded soon so I am not too concerned.
        if(graph != null)
            return graph.getName();
        else
            return "";
    }
}
