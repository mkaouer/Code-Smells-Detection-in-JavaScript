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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JOptionPane;

import nz.org.venice.chart.graph.BarChartGraph;
import nz.org.venice.chart.graph.BarGraph;
import nz.org.venice.chart.graph.BollingerBandsGraph;
import nz.org.venice.chart.graph.CandleStickGraph;
import nz.org.venice.chart.graph.CountbackLineGraph;
import nz.org.venice.chart.graph.CustomGraph;
import nz.org.venice.chart.graph.ExpMovingAverageGraph;
import nz.org.venice.chart.graph.FiboGraph;
import nz.org.venice.chart.graph.Graph;
import nz.org.venice.chart.graph.GraphUI;
import nz.org.venice.chart.graph.HighLowBarGraph;
import nz.org.venice.chart.graph.KDGraph;
import nz.org.venice.chart.graph.LineGraph;
import nz.org.venice.chart.graph.MACDGraph;
import nz.org.venice.chart.graph.MomentumGraph;
import nz.org.venice.chart.graph.MovingAverageGraph;
import nz.org.venice.chart.graph.MultipleMovingAverageGraph;
import nz.org.venice.chart.graph.OBVGraph;
import nz.org.venice.chart.graph.PointAndFigureGraph;
import nz.org.venice.chart.graph.RSIGraph;
import nz.org.venice.chart.graph.StandardDeviationGraph;
import nz.org.venice.chart.graph.SupportAndResistenceGraph;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.chart.source.OHLCVIndexQuoteGraphSource;
import nz.org.venice.chart.source.OHLCVQuoteGraphSource;
import nz.org.venice.chart.source.Adjustment;
import nz.org.venice.main.CommandManager;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.settings.MenuSettings;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.Symbol;
import nz.org.venice.ui.ConfirmDialog;
import nz.org.venice.ui.AdjustPriceDataDialog;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.util.IImageExporter;
import nz.org.venice.util.ImageExporterFactory;
import nz.org.venice.util.ImageFilter;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.Locale;

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

    private JMenu graphMenu = null;

    /*
      Does the data constitute an index?
      Indices data is many symbols data averaged and thus has their 
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
      
    public EODQuoteChartMenu(final ChartModule listener, 
			     EODQuoteBundle quoteBundle, 
			     Symbol symbol,
			     Graph graph,
			     boolean indexChart,
			     MenuSettings settings) {
	super(settings.getTitle());
	menuName = settings.getTitle();

	this.quoteBundle = quoteBundle;
	this.symbol = symbol;
	this.listener = listener;
	this.currentViewGraph = graph;
	this.indexChart = indexChart;

	map = settings.getMap();	
	buildMenu();

	//Select all the graphs in the menu	
	Iterator iterator = map.keySet().iterator();
	while (iterator.hasNext()) {
	    String key = (String)iterator.next();
	    Graph value = (Graph)map.get(key); 
	    
	    selectMenuItem(value.getName());
	}

    }
    

    /**
     * Builds the menu.
     */
    private void buildMenu() {
        // Graph main menus
	graphMenu = new JMenu(Locale.getString("GRAPH"));


	/* Determine which chart type to be selected based
	   on the default chart chosen in the preferences */
	String defaultChart = PreferencesManager.getDefaultChart();
	boolean lineSel = false, barSel = false, candleSel = false, 
	    highLowSel = false, pfSel = false;

	if (defaultChart.compareTo("LINE_CHART") == 0) {
	    lineSel = true;	    
	}
	if (defaultChart.compareTo("BAR_CHART") == 0) {
	    barSel = true;	    
	}
	if (defaultChart.compareTo("HIGH_LOW_BAAR") == 0) {
	    highLowSel = true;	    
	}
	if (defaultChart.compareTo("CANDLE_STICK") == 0) {
	    candleSel = true;	    
	}
	if (defaultChart.compareTo("POINT_AND_FIGURE") == 0) {
	    pfSel = true;	    
	}

        // Add the view menu items. Usually the user will only want to display
        // one of these at a time. So if they do, unselect the other members of
        // the group.
        ButtonGroup group = new ButtonGroup();
        currentViewMenuItem =
            addViewMenuItem(graphMenu, group, Locale.getString("LINE_CHART"), lineSel); 
        addViewMenuItem(graphMenu, group, Locale.getString("BAR_CHART"), barSel);
	addViewMenuItem(graphMenu, group, Locale.getString("CANDLE_STICK"), candleSel);
	addViewMenuItem(graphMenu, group, Locale.getString("HIGH_LOW_BAR"), highLowSel);
        addViewMenuItem(graphMenu, group, Locale.getString("POINT_AND_FIGURE"), pfSel);

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
	addMenuItem(graphMenu, Locale.getString("KD"));
 	addMenuItem(graphMenu, Locale.getString("SIMPLE_MOVING_AVERAGE"));
	addMenuItem(graphMenu, Locale.getString("EXP_MOVING_AVERAGE"));
	addMenuItem(graphMenu, Locale.getString("MULT_MOVING_AVERAGE"));
	addMenuItem(graphMenu, Locale.getString("OBV"));
        addMenuItem(graphMenu, Locale.getString("RSI"));
	addMenuItem(graphMenu, Locale.getString("STANDARD_DEVIATION"));
	addMenuItem(graphMenu, Locale.getString("COUNTBACK_LINE"));
	addMenuItem(graphMenu, Locale.getString("SUPPORT_AND_RESISTENCE"));
	addMenuItem(graphMenu, Locale.getString("FIBO_CHART"));       	

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
		    notes.moveToFront();
                }});

	JMenuItem alertMenu = new JMenuItem(Locale.getString("ALERT_ADD"));
	alertMenu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    CommandManager.getInstance().newAlert(symbol);
		}});
    
	JMenuItem alertListMenu = new JMenuItem(Locale.getString("ALERT_LIST"));
	alertListMenu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    
		    Vector symbolList = new Vector();
		    symbolList.add(symbol);
		    CommandManager.getInstance().tableAlertsBySymbol(symbolList);
		}
	    });

	//TODO - get this menuitem to select/deselect
	JMenuItem trackerMenu = new JMenuItem(Locale.getString("CHART_TRACKED_TOGGLE"));
	trackerMenu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (listener.getTracker() == null) {
			listener.addTracker(symbol);
		    } else if (listener.getTracker().isActive()) {
			listener.disableTracker();
		    } else {
			listener.enableTracker();
		    }
		}});
	
	JMenuItem exportMenu = new JMenuItem(Locale.getString("GRAPH_EXPORT"));

	exportMenu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

		    Thread thread = new Thread() {
			    public void run() {

				boolean okToWrite = true;
				File f;
				String filename = "";
				String userDir 
				    = PreferencesManager.getDirectoryLocation("imageExporter");

				if (userDir == null) {
				    userDir = System.getProperty("user.home");
				}
				    
				JFileChooser chooser = new JFileChooser(userDir);
				chooser.setMultiSelectionEnabled(false);
				
				ImageFilter filter = new ImageFilter();
				chooser.setFileFilter(filter);
				
				
				int rv = chooser.showSaveDialog(DesktopManager.getDesktop());
				
				if (rv == JFileChooser.APPROVE_OPTION) {
				    
				    BufferedImage bi = listener.getImage();
				    f = chooser.getSelectedFile();		
				    filename = f.getAbsolutePath();

				    //Check for extension
				    if (ImageExporterFactory.getExtension(filename) == null) {
					JOptionPane.
					    showInternalMessageDialog(DesktopManager.getDesktop(),
								      Locale.getString("FILENAME_EXTENSION_MISSING"),
								      Locale.getString("FILENAME"),
								      JOptionPane.ERROR_MESSAGE);
					okToWrite = false;
				    }

				    //Check existence, confirm overwrite
				    if (f.exists()) {
					ConfirmDialog confirmDialog = 
					    new ConfirmDialog(DesktopManager.getDesktop(), 
							      Locale.getString("GRAPH_SURE_OVERWRITE"), 
							      Locale.getString("GRAPH_OVERWRITE"));
						
					
					boolean choice = confirmDialog.showDialog();
					if (!choice) {
					    okToWrite = false;
					}
				    }

				    //Save parent directory for next time.
				    PreferencesManager.
					putDirectoryLocation("imageExporter", f.getParent());
				    
				    if (okToWrite) {
				    	IImageExporter exporter = ImageExporterFactory.get(filename);
				    	exporter.export(filename, bi);										
				    }
				}
			    }
			};
		    thread.start();
		}});

	JMenuItem adjustMenu = new JMenuItem(Locale.getString("SPLIT_DIV_ADJUST"));
	adjustMenu.addActionListener(new ActionListener() {
		Adjustment adjustment = null;

		public void actionPerformed(ActionEvent e) {
		    //Show Adjust dialog

		    Thread getAdjustment = new Thread() {
			    public void run() {

				AdjustPriceDataDialog adjustDialog = 
				    new AdjustPriceDataDialog(DesktopManager.getDesktop());
				
				adjustment = adjustDialog.showDialog();

				HashMap replacementMap = new HashMap();
				
				Graph newGraph = GraphFactory.newGraph(
								       currentViewGraph.getName(),
								       indexChart,
								       quoteBundle, symbol,
								       adjustment);
				
				replacementMap.put(currentViewGraph, newGraph);
				
				Iterator graphIterator = map.keySet().iterator();
				while (graphIterator.hasNext()) {
				    String name = (String)graphIterator.next();
				    Graph oldGraph = (Graph)map.get(name);
				    
				    newGraph = GraphFactory.newGraph(name, 
								     indexChart,
								     quoteBundle,
								     symbol,
								     adjustment);
				    replacementMap.put(oldGraph, newGraph);		
				}		    
				
				
				graphIterator = replacementMap.keySet().iterator();
				while (graphIterator.hasNext()) {
				    Graph oldGraph = (Graph)graphIterator.next();
				    newGraph = (Graph)replacementMap.get(oldGraph);
				    listener.replaceGraph(oldGraph, newGraph);
				}
				listener.redraw();				
			    }
			};
		    getAdjustment.start();		    
		}
	    });
	
	// Build menu items
	this.add(graphMenu);
	this.add(annotateMenu);
	this.add(exportMenu);
	this.add(alertMenu);
	this.add(alertListMenu);
	this.add(trackerMenu);
	this.add(adjustMenu);
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
				HashMap settings = (map.get(text) != null) 
					? ((Graph)map.get(text)).getSettings()
					: new HashMap();
                                Graph graph = getGraph(text, settings);
                                if (graph != null) {

                                    // Remove last graph first
                                    if(currentViewGraph != null) {
                                        listener.remove(currentViewGraph);
				    }

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
                    if(!menuItem.getState() && menuItem.getState()) {
                        removeGraph(text);
		    } 

                    // ... otherwise it adds the graph
                    else {
                        // NOTE: the menu will be auto-checked which might mess things up!!!

                        // Handle all action in a separate thread so we dont
                        // hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
                        Thread thread = new Thread() {
                                public void run() {

				    //This menu is setSelected automatically
				    //unless it's specifically modified.
				    menuItem.setSelected(true);
				    
				    /* graph can be null or not null. No side effects.
				       Scenarios:
				       1. Graph opened, removed => remove graph, unselect menuitem
				       2. Graph not opened, cancelled => unselect menuitem.
				       3. Graph opened, cancalled => do nothing
				       4. Graph opened, updated => redraw.

				       getGraph == null, when:
				       button is cancelled, but no graph exists,
				       button is delete.
				    */

				    HashMap settings = (map.get(text) != null) 
					? ((Graph)map.get(text)).getSettings()
					: new HashMap();

                                    Graph graph = getGraph(text, settings);
                                    if (graph != null) {  
					//Graph not in the map, means it's 
					//being added for the first time.
					if (map.get(text) == null) {
					    addGraph(graph);
					} else {
					    updateGraph(graph);
					}					
				    } else {
					if (map.get(text) != null) {
					    removeGraph(text);
					}
					menuItem.setSelected(false);
				    }
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
	    if (indexChart) {
		dayVolumeGraphSource = new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_VOLUME);
	    } else {
		dayVolumeGraphSource = new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_VOLUME);
	    }
	}
        return dayVolumeGraphSource;
    }

    /**
     * Create an instance of the graph that has the given localised name.
     * Raise the graph's parameter user interface if it has one. Return
     * the graph or <code>null</code> if the user cancels the operation.
     *
     * @param text localised name of graph
     * @return the instance of the graph or <code>null</code> if the
     *         operation is cancelled
     */
    private Graph getGraph(final String text, final HashMap settings) {
	Graph graph = GraphFactory.newGraph(text, indexChart, quoteBundle, symbol);

	GraphUI graphUI = null;
	if (graph != null) {
	    graphUI = graph.getUI(settings);
	}

	//Remove the graph if it exists and it has no settings 
	if (graphUI == null && map.get(text) != null) {
	    graph = null;
	}
	
        if(graphUI != null) {
            GraphSettingsDialog dialog =
                new GraphSettingsDialog(graphUI, graph.getName(), map.get(text) == null);

            int buttonPressed = dialog.showDialog();

	    if (buttonPressed == GraphSettingsDialog.ADD || 
		buttonPressed == GraphSettingsDialog.EDIT) {
		graph.setSettings(dialog.getSettings());	
	    } else if (buttonPressed == GraphSettingsDialog.DELETE) {
		graph = null;
	    }  else if (buttonPressed == GraphSettingsDialog.CANCEL) {
		graph = (map.get(text) == null) ? null : graph;	
		//If the cancel button is pressed, reset the settings
		//so the graph settings persist.
		if (graph != null) {
		    if (graphUI.checkSettings(dialog.getSettings()) == null) {
			graph.setSettings(dialog.getSettings());	
		    }
		}
		
	    }
        }
	
	if (graph != null && !listener.isDataAvailable(graph)) {
	    DesktopManager.showWarningMessage(Locale.getString("CHART_NO_DATA_AVAILABLE_WARNING"));
	    graph = null;
	}
	
        return graph;
    }

    /**
     * Adds graph to chart.
     *
     * @param graph the graph
     */
    private synchronized void addGraph(Graph graph) {

        String mapIdentifier = graph.getName();
	map.put(mapIdentifier, graph);

        if(graph.isPrimary())
            listener.append(graph, 0);
        else
            listener.append(graph);
	listener.redraw();
    }

    private synchronized void updateGraph(Graph graph) {
	/*
	  There's a race here somewhere	
	  Steps to reproduce:
	  1. Open a graph
	  2. Select an indicator
	  3. Change the type of graph (e.g. from line to bar)
	  4. Update the indicator	

	  The problem appears with the addGraph - comment out or add delay and the problem disappears.
  	  
	  UPDATE: Appears to have been resolved by adding synchronized calls
	  too BasicChartUI.resetBuffer and BasicChartUI.bufferedPaint - 
	  currently testing

	*/
	removeGraph(graph.getName());	
	addGraph(graph);

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
