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
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import java.io.File;

import org.mov.chart.graph.*;
import org.mov.chart.source.GraphSource;
import org.mov.chart.source.OHLCVQuoteGraphSource;
import org.mov.chart.source.OHLCVIndexQuoteGraphSource;
import org.mov.ui.DesktopManager;
import org.mov.util.Locale;
import org.mov.quote.Quote;
import org.mov.quote.EODQuoteBundle;
import org.mov.quote.Symbol;
import org.mov.util.ImageFilter;
import org.mov.util.BMPFile;

/**
 * Provides a menu which is associated with a stock symbol being graphed.
 * This menu provides a series of options which allow the user to graph related
 * charts and indicators.
 *
 * <p>If you have added a view or indicator graph to Venice, you'll need to
 * add it to this menu. To add the graph, you'll need to make two changes.
 * First add your graph to the <code>JMenu</code> which is created in
 * <code>buildMenu</code>. Then add your graph to the factory method
 * <code>newGraph</code>.
 *
 * @author Andrew Leppard
 */

public class EODQuoteChartMenu extends JMenu {

    // Graph's quotes
    private EODQuoteBundle quoteBundle;

    // Charting module
    private ChartModule listener;

    // Map of graph name to graph
    private HashMap map = new HashMap();

    // Current view graph displayed
    private Graph currentViewGraph = null;
    private JRadioButtonMenuItem currentViewMenuItem = null;

    // Name of menu / graph source
    private String menuName = null;

    // Symbol being graphed
    private Symbol symbol = null;

    // Cached graph sources shared between indicators
    private GraphSource dayOpenGraphSource = null;
    private GraphSource dayHighGraphSource = null;
    private GraphSource dayLowGraphSource = null;
    private GraphSource dayCloseGraphSource = null;
    private GraphSource dayVolumeGraphSource = null;

    /*
      Is the data constitute and index?
      Indices data is many symbols data averaged and thus has have their 
      own source.      
    */
    private boolean indexChart = false;

    /**
     * Create a new menu allowing the user to graph related graphs
     * for the given graph. The symbol we are associated with will be
     * extracted from the graph.
     *
     * @param	listener	the chart module associated with the menu
     * @param   quoteBundle     graph's quote data
     * @param   symbol          the symbol being graphed
     * @param	graph		the graph we are associated with
     */
    public EODQuoteChartMenu(final ChartModule listener, EODQuoteBundle quoteBundle,
                             Symbol symbol, Graph graph, boolean indexChart) {
	super(graph.getSourceName());
	menuName = graph.getSourceName();

	this.quoteBundle = quoteBundle;
        this.symbol = symbol;
	this.listener = listener;
        this.currentViewGraph = graph;
	this.indexChart = indexChart;

        buildMenu();
    }

    /**
     * Builds the menu.
     */
    private void buildMenu() {
        // Graph main menus
	JMenu graphMenu = new JMenu(Locale.getString("GRAPH"));

        // Add the view menu items. Usually the user will only want to display
        // one of these at a time. So if they do, unselect the other members of
        // the group.
        ButtonGroup group = new ButtonGroup();
        currentViewMenuItem =
            addViewMenuItem(graphMenu, group, Locale.getString("LINE_CHART"), true); // selected
        addViewMenuItem(graphMenu, group, Locale.getString("BAR_CHART"), false);
	addViewMenuItem(graphMenu, group, Locale.getString("CANDLE_STICK"), false);
	addViewMenuItem(graphMenu, group, Locale.getString("HIGH_LOW_BAR"), false);
        addViewMenuItem(graphMenu, group, Locale.getString("POINT_AND_FIGURE"), false);

        graphMenu.addSeparator();

	// Add the indicator menu items. These indicators can be "stacked" anyway
        // the user wishes. For example, the user can display bollinger bands and
        // Moving Average in the same graph.
        addMenuItem(graphMenu, Locale.getString("BOLLINGER_BANDS"));
        addMenuItem(graphMenu, Locale.getString("CUSTOM"));
        addMenuItem(graphMenu, Locale.getString("DAY_OPEN"));
        addMenuItem(graphMenu, Locale.getString("DAY_HIGH"));
	addMenuItem(graphMenu, Locale.getString("DAY_LOW"));
	addMenuItem(graphMenu, Locale.getString("VOLUME"));
	addMenuItem(graphMenu, Locale.getString("MOMENTUM"));
	addMenuItem(graphMenu, Locale.getString("MACD"));
	addMenuItem(graphMenu, Locale.getString("SIMPLE_MOVING_AVERAGE"));
	addMenuItem(graphMenu, Locale.getString("EXP_MOVING_AVERAGE"));
	addMenuItem(graphMenu, Locale.getString("OBV"));
        addMenuItem(graphMenu, Locale.getString("RSI"));
	addMenuItem(graphMenu, Locale.getString("STANDARD_DEVIATION"));
	addMenuItem(graphMenu, Locale.getString("SUPPORT_AND_RESISTENCE"));

	// Add all static menus
	JMenuItem removeMenu = new JMenuItem(Locale.getString("REMOVE"));
	removeMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listener.removeAll(getName());

                    // Redraw chart if there are other graphs left
                    if(listener.count() > 0)
                        listener.redraw();
                }});

	JMenuItem annotateMenu = new JMenuItem(Locale.getString("GRAPH_ANNOTATE"));
	annotateMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 

		    UserNotes notes = new UserNotes(menuName);
		    
                }});

	JMenuItem exportMenu = new JMenuItem(Locale.getString("GRAPH_EXPORT"));

	exportMenu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    File f;
		    String filename = "";
		    String userDir = System.getProperty("user.home");
		    JFileChooser chooser = new JFileChooser();
                    chooser.setMultiSelectionEnabled(false);

		    ImageFilter filter = new ImageFilter();
		    chooser.setFileFilter(filter);

		    int rv = chooser.showSaveDialog(DesktopManager.getDesktop());

		    if (rv == JFileChooser.APPROVE_OPTION) {
			
			BufferedImage bi = listener.getImage();
			f = chooser.getSelectedFile();		
			filename = f.getAbsolutePath();

			BMPFile bmpwrite = new BMPFile(true);
			bmpwrite.saveBitmap(filename, bi, bi.getWidth(), bi.getHeight());

		    }
		}
	    });

        // Build menu items
	this.add(graphMenu);
	this.add(annotateMenu);
	this.add(exportMenu);
	this.add(removeMenu);	
    }

    /**
     * Adds the view graph to the given menu. A view graph is a way of viewing
     * the graph, a graph can be viewed as a high-low bar, or a line of the day
     * close, or a candlestick graph etc. Typically the user wants to choose
     * only one of these views at anyone time.
     *
     * @param menu the menu
     * @param group menu buttom group
     * @param label name of graph
     * @param isSelected is this the initial view?
     * @return menu item
     */
    private JRadioButtonMenuItem addViewMenuItem(JMenu menu, ButtonGroup group, String label,
                                                 boolean isSelected) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(label);
        group.add(item);
        item.setSelected(isSelected);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem)e.getSource();
                    final String text = menuItem.getText();

                    // NOTE: the menu will be auto-checked which might mess things up!!!

                    // Handle all action in a separate thread so we dont
                    // hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
                    Thread thread = new Thread() {
                            public void run() {
                                Graph graph = getGraph(text);
                                if (graph != null) {

                                    // Remove last graph first
                                    if(currentViewGraph != null)
                                        listener.remove(currentViewGraph);

                                    addGraph(graph);
                                    currentViewGraph = graph;
                                    currentViewMenuItem = menuItem;
                                }

                                // Reset last selected menu item if the user cancelled
                                else if(currentViewMenuItem != null)
                                    currentViewMenuItem.setSelected(true);
                            }};

                    thread.start();
                }});

        menu.add(item);

        return item;
    }

    /**
     * Adds the indicator graph to the given menu. An indicator graph is
     * a graph that can be stacked with other indicators.
     *
     * @param menu the menu
     * @param label name of graph
     */
    private void addMenuItem(JMenu menu, String label) {
	// Add graph menu
	JMenuItem item = new JCheckBoxMenuItem(label);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)e.getSource();
                    final String text = menuItem.getText();

                    // If the menu is already selected then unselecting removes the
                    // graph...
                    if(!menuItem.getState())
                        removeGraph(text);

                    // ... otherwise it adds the graph
                    else {
                        // NOTE: the menu will be auto-checked which might mess things up!!!

                        // Handle all action in a separate thread so we dont
                        // hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
                        Thread thread = new Thread() {
                                public void run() {
                                    Graph graph = getGraph(text);
                                    if (graph != null)
                                        addGraph(graph);
                                    else
                                        menuItem.setSelected(false);
                                }};

                        thread.start();
                    }}});

	menu.add(item);
    }

    /**
     * Return the menu name / name of the object we are graphing
     *
     * @return	the menu name
     */
    public String getName() {
        return menuName;
    }

    /**
     * Returns day open graph source for input to graph.
     *
     * @return graph souce
     */
    private GraphSource getDayOpen() {
        if(dayOpenGraphSource == null)
	    if (indexChart) {
		dayOpenGraphSource = new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_OPEN);
	    } else {
		dayOpenGraphSource = new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_OPEN);
	    }
        return dayOpenGraphSource;
    }

    /**
     * Returns day high graph source for input to graph.
     *
     * @return graph souce
     */
    private GraphSource getDayHigh() {
        if(dayHighGraphSource == null)
	    if (indexChart) {
		dayHighGraphSource = new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_HIGH); 
	    } else {		
		dayHighGraphSource = new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_HIGH); 
	    }
	
        return dayHighGraphSource;
    }

    /**
     * Returns day low graph source for input to graph.
     *
     * @return graph souce
     */
    private GraphSource getDayLow() {
        if(dayLowGraphSource == null)
	    if (indexChart) {
		dayLowGraphSource = new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_LOW); 
	    } else {	    
		dayLowGraphSource = new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_LOW); 
	    }
        return dayLowGraphSource;
    }

    /**
     * Returns day close graph source for input to graph.
     *
     * @return graph souce
     */
    private GraphSource getDayClose() {
        if(dayCloseGraphSource == null)
	    if (indexChart) {
		dayCloseGraphSource = new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_CLOSE);
	    } else {
		dayCloseGraphSource = new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_CLOSE);
	    }
        return dayCloseGraphSource;
    }

    /**
     * Returns day volume graph source for input to graph.
     *
     * @return graph souce
     */
    private GraphSource getDayVolume() {
        if(dayVolumeGraphSource == null) {
	    dayVolumeGraphSource = new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_VOLUME);
	} else {
            dayVolumeGraphSource = new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_VOLUME);
	}
        return dayVolumeGraphSource;
    }

    /**
     * Creates an instance of the graph that has the given localised name.
     * Raises the graph's parameter user interface if it has one. Returns
     * the graph or <code>null</code> if the user cancelled the operation.
     *
     * @param text localised name of graph
     * @return the instance of that graph or <code>null</code> if the
     *         operation was cancelled
     */
    private Graph getGraph(final String text) {
        Graph graph = newGraph(text);
        GraphUI graphUI = graph.getUI(new HashMap());

        if(graphUI != null) {
            GraphSettingsDialog dialog =
                new GraphSettingsDialog(graphUI, graph.getName());

            int buttonPressed = dialog.showDialog();

            if (buttonPressed == GraphSettingsDialog.ADD)
                graph.setSettings(dialog.getSettings());
            else
                graph = null;
        }

        return graph;
    }

    /**
     * Adds graph to chart.
     *
     * @param graph the graph
     */
    private void addGraph(Graph graph) {
        String mapIdentifier = graph.getName();
	map.put(mapIdentifier, graph);

        if(graph.isPrimary())
            listener.append(graph, 0);
        else
            listener.append(graph);
	listener.redraw();
    }

    /**
     * Removes a graph from the chart.
     *
     * @param mapIdentifier name of graph
     */
    private void removeGraph(String mapIdentifier) {
	Graph graph = (Graph)map.get(mapIdentifier);
	map.remove(mapIdentifier);
	
	// Remove graph
	listener.remove(graph);
	listener.redraw();
    }

    /**
     * This method creates an instance of the graph that has the given
     * localised name.
     *
     * @param text localised name of graph
     * @return the instace of that graph
     */
    private Graph newGraph(String text) {
        Graph graph;

        if(text == Locale.getString("BAR_CHART"))
            graph = new BarChartGraph(getDayOpen(), getDayLow(), getDayHigh(), getDayClose());

        else if(text == Locale.getString("BOLLINGER_BANDS"))
            graph = new BollingerBandsGraph(getDayClose());

        else if(text == Locale.getString("CANDLE_STICK"))
            graph = new CandleStickGraph(getDayOpen(), getDayLow(), getDayHigh(), getDayClose());
        
        else if(text == Locale.getString("CUSTOM"))
            graph = new CustomGraph(getDayClose(), symbol, quoteBundle);

        else if(text == Locale.getString("DAY_HIGH"))
            graph = new LineGraph(getDayHigh(), text, true);
	
        else if(text == Locale.getString("DAY_LOW"))
            graph = new LineGraph(getDayLow(), text, true);
	
        else if(text == Locale.getString("DAY_OPEN"))
            graph = new LineGraph(getDayOpen(), text, true);

        else if(text == Locale.getString("VOLUME"))
            graph = new BarGraph(getDayVolume(), text, false);
	
        else if(text == Locale.getString("EXP_MOVING_AVERAGE"))
            graph = new ExpMovingAverageGraph(getDayClose());

        else if(text == Locale.getString("HIGH_LOW_BAR"))
            graph = new HighLowBarGraph(getDayLow(), getDayHigh(), getDayClose());

        else if(text == Locale.getString("MACD"))
            graph = new MACDGraph(getDayClose());
	
        else if(text == Locale.getString("MOMENTUM"))
            graph = new MomentumGraph(getDayClose());
	
        else if(text == Locale.getString("OBV"))
            graph = new OBVGraph(getDayOpen(), getDayClose(), getDayVolume());

        else if(text == Locale.getString("POINT_AND_FIGURE")) 
            graph = new PointAndFigureGraph(getDayClose());

	else if (text == Locale.getString("SUPPORT_AND_RESISTENCE"))
	    graph = new SupportAndResistenceGraph(getDayClose());

        else if(text == Locale.getString("RSI"))
            graph = new RSIGraph(getDayClose());

        else if(text == Locale.getString("SIMPLE_MOVING_AVERAGE"))
            graph = new MovingAverageGraph(getDayClose());

        else if(text == Locale.getString("STANDARD_DEVIATION"))
            graph = new StandardDeviationGraph(getDayClose());

        else {
            assert(text == Locale.getString("LINE_CHART"));
            graph = new LineGraph(getDayClose(), text, true);
        }

        // Make sure we did the right text -> graph mapping.
        assert text == graph.getName();

        return graph;
    }
}
