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
import java.beans.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

import org.mov.chart.graph.*;
import org.mov.chart.source.*;
import org.mov.main.*;
import org.mov.util.Locale;
import org.mov.portfolio.*;
import org.mov.quote.*;
import org.mov.ui.*;

/**
 * The charting module for venice. This class provides the user interface
 * used to draw any of the required charts.
 * Example:
 * <pre>
 *	EODQuoteBundle quoteBundle = new EODQuoteBundle(new EODQuoteRange(symbol));
 *	GraphSource dayClose = new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_CLOSE);
 *	Graph graph = new LineGraph(dayClose, Locale.getString("LINE_CHART"), true);
 *
 *	ChartModule chart = new ChartModule(desktop);
 *	chart.add(graph, 0);
 *	chart.redraw();
 *
 *	// Create a frame around the module and add to the desktop
 *	ModuleFrame frame = new ModuleFrame(chart, 0, 0, 400, 300);
 *	desktop.add(frame);
 * </pre>
 *
 * <h2>Structure</h2>
 *
 * The chart module is made up of three core classes. These core classes
 * are: <code>ChartModule</chart>, <code>Chart</code> &
 * <code>BasicChartUI</code>.
 *
 * <p>
 * <ul>
 *
 * <li>
 * <code>BasicChartUI</chart>
 * <p>
 * This class provides the sizing and drawing code to draw graphs.
 * Given a set of graph levels it will arrange the graphs in the display,
 * calculate the size of each graph and for each graph level draw each
 * graph at that level. It will also create and manage the graph axes.
 * </li>

 * <li>
 * <code>Chart</chart>
 * <p>
 * This class is a new Swing widget which allows charting of graphs.
 * The actual code in this class is responsible for extending java swing's
 * <code>JComponent</code> class. It provides the code to allow the
 * user to select and unselect a portion of the chart and also
 * manages graph annotations via tooltips.
 * </li>
 *
 * <li>
 * <code>ChartModule</chart>
 * <p>
 * This class handles the integration of the chart module with <i>Venice</i>.
 * It is the container class of the actual chart, and is responsible for
 * laying out the chart widget and the toolbar in a frame. It also
 * provides the menu.
 * </li>
 *
 * </ul>
 *
 * <p>
 *
 * <h2>Glossary</h2>
 *
 * The charting module uses a variety of phrases which have special meaning:
 * <p>
 * <dl compact>
 *
 * <dt><i>Annotations</i>
 * <dd>These appear on the graph as little yellow notes indicating
 * <i>Graph</i> specific information to the user. This information may
 * include buy/sell recommendations or any other data.
 *
 * <dt><i>Chart</i>
 * <dd>A chart represents the entire graphable area. The chart can
 * consist of several <i>Graph Levels</i>, each graph level may contain
 * several <i>Graphs</i>.
 *
 * <dt><i>Graph</i>
 * <dd>A graph represents a specific type of graph to display, for example
 * a <i>Line Graph</i>, a <i>Bar Graph</i>, a <i>Moving Average Graph</i>
 * etc. These graphs can then be used to display different things to the
 * user, for example the <i>Bar Graph</i> can be used to graph a stock's
 * volume. A <i>Line Graph</i> can be used to graph a stock's day close.
 *
 * <dt><i>Graph Level</i>
 * <dd>For each chart there can be several <i>levels</i> of graph, these
 * levels are displayed vertical one on top of the other. The top level
 * may contain several stock's day close graphs. The bottom levels typically
 * contain indicators such as volume or RSI.
 *
 * <dt><i>Graph Source</i>
 * <dd>Contains useful information that <i>Graphs</i> need to know so they
 * can graph particular data (such as quote data). This information includes
 * the values to be graphed, a title, the axis types to use and any
 * <i>Annotations</i> to display for the graph.
 * </dl>
 *
 * @see Graph
 */

public class ChartModule extends JPanel implements Module,
						   MouseListener,
						   MouseMotionListener,
						   ActionListener
{
    // Constants
    private static int TOOLBAR_GRAPHIC_SIZE = 12;

    private PropertyChangeSupport propertySupport;
    private Chart chart;
    private JScrollPane scrollPane;
    private JMenuBar menuBar = new JMenuBar();

    // Function Toolbar
    private JButton defaultZoom = null;
    private JButton zoomIn = null;
    private JButton cloneChart = null;
    private JButton paintOnChart = null;
    private JButton eraseOnChart = null;
    private JButton moveOnChart = null;
    private JButton scribbleOnChart = null;    
    private JButton editOnChart = null;

    // Modes
    private static final int SELECTING = 0;
    private static final int DRAWING = 1;
    private static final int MOVING = 2;
    private static final int ERASING = 3;
    private static final int SCRIBBLING = 4;
    private static final int EDITING = 5;

    // Menus
    private JMenuItem addMenuItem = null;
    private JMenuItem closeMenuItem = null;

    // Enabled?
    private boolean defaultZoomEnabled = false;
    private boolean previousDefaultZoomState = false;
    private boolean zoomInEnabled = false;

    private int viewMode = SELECTING;
        
    private JDesktopPane desktop;

    // Frame Icon
    private String frameIcon = "org/mov/images/TableIcon.gif";

    // ToolBar Images - these are from jlfgr-1.0.jar
    // Editing images (paint, erase, text scribble, move ) aren't the
    // best we could have but at least there are unique icons for
    // different functions.
    private String defaultZoomImage = "toolbarButtonGraphics/general/Zoom24.gif";
    private String zoomInImage = "toolbarButtonGraphics/general/ZoomIn24.gif";
    private String paintInImage = "toolbarButtonGraphics/general/Edit24.gif";
    private String eraseInImage = "toolbarButtonGraphics/general/Remove24.gif";
    private String moveInImage = "toolbarButtonGraphics/general/Move24.gif";
    private String scribbleInImage = "toolbarButtonGraphics/general/Scribble24.gif";
    private String editInImage = "toolbarButtonGraphics/general/Text24.gif";
    private String cloneChartImage = "toolbarButtonGraphics/general/Copy24.gif";
    
    //Index chart indicator - Index charts have aggregate graph sources
    private boolean indexChart = false;

    /**
     * Create a new Chart.
     *
     * @param	desktop	the parent desktop.
     */
    public ChartModule(JDesktopPane desktop) {

	this.desktop = desktop;
	indexChart = false;

	propertySupport = new PropertyChangeSupport(this);

	chart = new Chart();
	chart.addMouseListener(this);
	chart.addMouseMotionListener(this);

	setLayout(new BorderLayout());

	addFunctionToolBar();
	
	// Add non-company specific menu for graph
	JMenu menu = new JMenu(Locale.getString("GRAPH"));
	addMenuItem = new JMenuItem(Locale.getString("ADD"));
	addMenuItem.setAccelerator(KeyStroke.getKeyStroke('A',
				   java.awt.Event.CTRL_MASK, false));
	addMenuItem.addActionListener(this);
	menu.add(addMenuItem);
	menu.addSeparator();

	closeMenuItem = new JMenuItem(Locale.getString("CLOSE"));
	closeMenuItem.setAccelerator(KeyStroke.getKeyStroke('C',
		  		     java.awt.Event.CTRL_MASK, false));
	closeMenuItem.addActionListener(this);
	menu.add(closeMenuItem);

	menuBar.add(menu);

	scrollPane = new JScrollPane(chart);
	
	add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Create a new Chart.
     *
     * @param	desktop	the parent desktop.
     */
    public ChartModule(JDesktopPane desktop, boolean indexChart) {

	this.desktop = desktop;
	this.indexChart = indexChart;

	propertySupport = new PropertyChangeSupport(this);

	chart = new Chart();
	chart.addMouseListener(this);
	chart.addMouseMotionListener(this);

	setLayout(new BorderLayout());

	addFunctionToolBar();
	
	// Add non-company specific menu for graph
	JMenu menu = new JMenu(Locale.getString("GRAPH"));
	addMenuItem = new JMenuItem(Locale.getString("ADD"));
	addMenuItem.setAccelerator(KeyStroke.getKeyStroke('A',
				   java.awt.Event.CTRL_MASK, false));
	addMenuItem.addActionListener(this);
	menu.add(addMenuItem);
	menu.addSeparator();

	closeMenuItem = new JMenuItem(Locale.getString("CLOSE"));
	closeMenuItem.setAccelerator(KeyStroke.getKeyStroke('C',
		  		     java.awt.Event.CTRL_MASK, false));
	closeMenuItem.addActionListener(this);
	menu.add(closeMenuItem);

	menuBar.add(menu);

	scrollPane = new JScrollPane(chart);
	
	add(scrollPane, BorderLayout.CENTER);
    }   


    // Adds the toolbar that gives the user the options to zoom in and out
    // of the chart
    private void addFunctionToolBar() {

	// Create image on toolbar to zoom to default zoom level
        URL defaultZoomURL = ClassLoader.getSystemResource(defaultZoomImage);
        URL zoomInImageURL = ClassLoader.getSystemResource(zoomInImage);
	URL paintInImageURL = ClassLoader.getSystemResource(paintInImage);
	URL eraseInImageURL = ClassLoader.getSystemResource(eraseInImage);
	URL moveInImageURL = ClassLoader.getSystemResource(moveInImage);
	URL scribbleInImageURL = ClassLoader.getSystemResource(scribbleInImage);	URL editInImageURL = ClassLoader.getSystemResource(editInImage);
	URL cloneChartURL = ClassLoader.getSystemResource(cloneChartImage);

        // If either image is not available, then do not create the
        // toolbar
        if(defaultZoomURL != null && zoomInImageURL != null) {
            JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);

            // Create image on toolbar to return to default zoom
            ImageIcon defaultZoomImageIcon = new ImageIcon(defaultZoomURL);
            defaultZoom = new JButton(defaultZoomImageIcon);
            defaultZoom.addActionListener(this);
            defaultZoom.setEnabled(defaultZoomEnabled);
            toolBar.add(defaultZoom);

            // Create image on toolbar to zoom in to highlighted region
            ImageIcon zoomInImageIcon = new ImageIcon(zoomInImageURL);
            zoomIn = new JButton(zoomInImageIcon);
            zoomIn.addActionListener(this);
            zoomIn.setEnabled(zoomInEnabled);
            toolBar.add(zoomIn);

	    // Create image on toolbar to clone the graph
	    ImageIcon cloneChartIcon = new ImageIcon(cloneChartURL);
	    cloneChart = new JButton(cloneChartIcon);
	    cloneChart.addActionListener(this);
	    cloneChart.setEnabled(true);
	    toolBar.add(cloneChart);

	    // Create image on toolbar to paint lines on graph
	    ImageIcon paintOnChartIcon = new ImageIcon(paintInImageURL);
	    paintOnChart = new JButton(paintOnChartIcon);
	    paintOnChart.addActionListener(this);
	    paintOnChart.setEnabled(true);
	    toolBar.add(paintOnChart);

	    // Create image on toolbar to move lines on graph
	    ImageIcon moveOnChartIcon = new ImageIcon(moveInImageURL);
	    moveOnChart = new JButton(moveOnChartIcon);
	    moveOnChart.addActionListener(this);
	    moveOnChart.setEnabled(true);
	    toolBar.add(moveOnChart);

	    // Create image on toolbar to scribble on graph
	    ImageIcon scribbleOnChartIcon = new ImageIcon(scribbleInImageURL);
	    scribbleOnChart = new JButton(scribbleOnChartIcon);
	    scribbleOnChart.addActionListener(this);
	    scribbleOnChart.setEnabled(true);
	    toolBar.add(scribbleOnChart);

	    // Create image on toolbar to put text on graph
	    ImageIcon editOnChartIcon = new ImageIcon(editInImageURL);
	    editOnChart = new JButton(editOnChartIcon);
	    editOnChart.addActionListener(this);
	    editOnChart.setEnabled(true);
	    toolBar.add(editOnChart);	    

	    // Create image on toolbar to delete lines on graph
	    ImageIcon eraseOnChartIcon = new ImageIcon(eraseInImageURL);
	    eraseOnChart = new JButton(eraseOnChartIcon);
	    eraseOnChart.addActionListener(this);
	    eraseOnChart.setEnabled(true);
	    toolBar.add(eraseOnChart);
	    

	    
	    
            add(toolBar, BorderLayout.WEST);
        }
    }

    /**
     * Redraw the current display.
     */
    public void redraw() {
	chart.resetBuffer();
	chart.validate();
	chart.repaint();
    }

    // Inserts the menu such that all the menus are in alphabetical
    // order
    private void addMenu(JMenu menu) {

	int menus = menuBar.getMenuCount();
	boolean menuBarInserted = false;

	for(int i = 1; i < menus; i++) {
	    JMenu currentMenu = menuBar.getMenu(i);
	    // Should it go before this menu item? If so insert
	    if(menu.getText().compareTo(currentMenu.getText()) <= 0) {
		menuBar.add(menu, i);
		menuBarInserted = true;
		break;
	    }
	}

	// If we haven't inserted the menu bar yet then append it
	if(menuBarInserted == false)
	    menuBar.add(menu);	

	// Send signal that our frame name has changed
	propertySupport.
	    firePropertyChange(ModuleFrame.TITLEBAR_CHANGED_PROPERTY, 0, 1);
    }

    /**
     * Add a new graph to the specified level. Add new menu for graph.
     *
     * @param	graph	the new graph to add
     * @param	level	graph level to add the new graph
     */
    public void add(Graph graph, Symbol symbol, EODQuoteBundle quoteBundle, int level) {

	// Make sure it has at least one value
        assert graph.getXRange().size() > 0;

	// Add graph to chart
	chart.add(graph, level);

	// Add menu for this quote
	EODQuoteChartMenu menu = new EODQuoteChartMenu(this, quoteBundle, symbol,
                                                       graph,indexChart);

	addMenu(menu);
    }

    // Add the given symbols to the graph - should be run in a separate
    // thread from the graphics dispatch thread
    public void add(SortedSet symbols) {
	Iterator iterator = symbols.iterator();

        final Thread thread = Thread.currentThread();
        ProgressDialog progress = ProgressDialogManager.getProgressDialog();
	EODQuoteBundle quoteBundle = null;
	Graph graph = null;
	GraphSource dayClose = null;

	while(iterator.hasNext()) {
	    final Symbol symbol = (Symbol)iterator.next();
	    progress.show(Locale.getString("LOADING_QUOTES_FOR", symbol.toString()));
	
	    if (!thread.isInterrupted())
		quoteBundle = new EODQuoteBundle(new EODQuoteRange(symbol));
	
	    if (!thread.isInterrupted())
		dayClose =
		    new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_CLOSE);
	
	    if (!thread.isInterrupted())
		graph = new LineGraph(dayClose, Locale.getString("DAY_CLOSE"), true);
	
	    if (!thread.isInterrupted()) {

		final Graph finalGraph = graph;
		final EODQuoteBundle finalQuoteBundle = quoteBundle;

		// Invokes on dispatch thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		
			    add(finalGraph, symbol, finalQuoteBundle, 0);

			    // This makes sure the menu updates OK
			    getTopLevelAncestor().validate();
			    getTopLevelAncestor().repaint();

			    redraw();		

			    // Only way I can seem to get the scrollpane
			    // to handle if the viewport size has changed!
			    scrollPane.setViewportView(chart);
			}});
	    }
	}

        ProgressDialogManager.closeProgressDialog(progress);
    }

    /**
     * Return if we graphing the given symbol/portfolio.
     *
     * @param	name	Name of symbol/portfolio
     * @return	whether we are graphing the symbol/portfolio or not
     */
    public boolean isGraphing(String name) {
	Vector levels = chart.getLevels();
	Iterator levelsIterator = levels.iterator();

	name = name.toUpperCase();

	while(levelsIterator.hasNext()) {
	    Vector graphs = (Vector)levelsIterator.next();
	    Iterator graphIterator = graphs.iterator();

	    while(graphIterator.hasNext()) {
		Graph graph = (Graph)graphIterator.next();

		if(name.equals(graph.getSourceName()))
		    return true;
	    }
	}

	// If we got here it wasnt found
	return false;
    }

    /**
     * Add a new portfolio graph to the specified level. Add new menu
     * for graph.
     *
     * @param	graph	     the portfolio graph
     * @param	portfolio    the portfolio
     * @param	quoteBundle  the quote bundle
     * @param	level	     specified level
     */
    public void add(Graph graph, Portfolio portfolio, EODQuoteBundle quoteBundle, int level) {

	// Add graph to chart
	chart.add(graph, level);

	// Add menu for this portfolio
	PortfolioChartMenu menu = new PortfolioChartMenu(this, quoteBundle,
							 portfolio, graph);
	addMenu(menu);
    }

    public void addMarketIndicator(Graph graph) {
        chart.add(graph, 0);
        addMenu(new MarketIndicatorChartMenu(this, graph));
    }

    /**
     * Add the graph to the specified level. This is identical to
     * the add method except that it does not add a new menu for the
     * graph.
     *
     * @param	graph	The new graph to add
     * @param	level	graph level to add the new graph
     * @see	#add
     */
    public void append(Graph graph, int level) {
	// Add graph to chart to given level, redraw chart but dont add it
	// to menu as it is already there
	chart.add(graph, level);
    }

    /**
     * Create a new level and add the graph. This is identical to the
     * add method except that it does not add a new menu for the
     * graph.
     *
     * @param	graph	the new graph to add.
     * @see	#add
     */
    public void append(Graph graph) {
	// Add graph at a new graph level, redraw chart but dont add graph to
	// menu as it is already there
	append(graph, chart.getLevels().size());
    }

    /**
     * Remove the graph from the chart. Currently does not remove the
     * menu for the appropriate symbol. Probably should.
     *
     * @param	graph	the graph to remove.
     */
    public void remove(Graph graph) {
	// Remove graph from chart, redraw chart and dont remove any
	// menus
	chart.remove(graph);
    }

    /**
     * Return the number of graphs in the chart.
     *
     * @return the number of graphs in the chart
     */
    public int count() {
        return chart.count();
    }

    /**
     * Remove all graphs with the given symbol from the chart.
     *
     * @param name	The name of the graphs to remove
     */
    public void removeAll(String name) {

	// Construct vector of all graphs with the given name, then
	// remove them one by one
	Vector graphsToRemove = new Vector();

	Vector levels = chart.getLevels();
	Iterator levelsIterator = levels.iterator();

	while(levelsIterator.hasNext()) {
	    Vector graphs = (Vector)levelsIterator.next();
	    Iterator graphIterator = graphs.iterator();

	    while(graphIterator.hasNext()) {
		Graph graph = (Graph)graphIterator.next();

		if(name.equals(graph.getSourceName()))
		    graphsToRemove.add(graph);
	    }
	}

	Iterator graphToRemoveIterator = graphsToRemove.iterator();
	while(graphToRemoveIterator.hasNext()) {
	    chart.remove((Graph)graphToRemoveIterator.next());
	}

	// Remove from menu bar
	int menus = menuBar.getMenuCount();
	
	for(int i = 1; i < menus; i++) {
	    JMenu currentMenu = menuBar.getMenu(i);

	    // Is this the menu to remove?
	    if(name.equals(currentMenu.getText())) {
		menuBar.remove(currentMenu);
		break;
	    }
	}

	// Send signal that our frame name has changed
	propertySupport.
	    firePropertyChange(ModuleFrame.TITLEBAR_CHANGED_PROPERTY, 0, 1);

        // If there are no graphs left then close the window
        if(count() == 0)
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
    }

    /**
     * Record whether the given graph should have its annotations
     * displayed or not. Annotations are little popup text notes that
     * contain information about the graph, such as buy/sell suggestions.
     *
     * @param	graph	the graph to change annotations for.
     * @param	enabled	set to true if the graph should handle annotations
     *			false otherwise.	
     */
    public void handleAnnotation(Graph graph, boolean enabled) {
	chart.handleAnnotation(graph, enabled);
    }

    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle() {
	return chart.getTitle();
    }

    /**
     * Add a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void addModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void removeModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * Return displayed component for this module.
     *
     * @return the component to display.
     */
    public JComponent getComponent() {
	return this;
    }

    public void mouseClicked(MouseEvent e) {
	chart.clearHighlightedRegion();

        if(zoomIn != null) {
            zoomIn.setEnabled(zoomInEnabled = false);
	}
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
	Comparable x = chart.getXAtPoint(e.getX());
	Double y = chart.getYAtPoint(e.getY());
	Integer mouseX = new Integer(e.getX());
	Integer mouseY = new Integer(e.getY());
	
	if(x != null &&
	   viewMode == SELECTING) {
	    chart.setHighlightedStart(x);
	}

	if (x != null && viewMode == DRAWING) {
	    chart.setDrawnLineStart(x, y, mouseY);
	}

	if (x != null && viewMode == MOVING) {
	    Coordinate start;
 	    
	    if ( (start = chart.move(mouseX,mouseY)) != null) {
		
		chart.setDrawnLineStart(start.getXData(), 
					start.getYData(),
					start.getYCoord());
	    }
	}
	
    }
    public void mouseReleased(MouseEvent e) { 
 	Comparable x = chart.getXAtPoint(e.getX());
	Double y = chart.getYAtPoint(e.getY());
	Integer mouseY = new Integer(e.getY());

	if (x != null && (viewMode == DRAWING || viewMode == MOVING)) {
	    chart.setDrawnLineEnd(x, y, mouseY);
	}

	if (x != null && viewMode == EDITING) {
	    String val = 
		JOptionPane.showInputDialog(this,
					    Locale.getString("ENTER_TEXT"), 
					    "Text", 
					    JOptionPane.OK_CANCEL_OPTION);
	   
	    if (val != null) {
		chart.setText(val,x, y, mouseY);
	    }
	}

	if (x != null && viewMode == SCRIBBLING) {
	    Coordinate c = new Coordinate();
	    chart.setPoint(c);

	}

    }
    public void mouseDragged(MouseEvent e) {

	Comparable x = chart.getXAtPoint(e.getX());
	Double y = chart.getYAtPoint(e.getY());
	Integer mouseY = new Integer(e.getY());

	// can now zoom in!
	// As long as we're not in an editing mode.
        if(zoomIn != null && viewMode == SELECTING) {
            zoomIn.setEnabled(zoomInEnabled = true);
	}

	if(x != null && viewMode != DRAWING && viewMode != MOVING)
	    chart.setHighlightedEnd(x);
	
	if (x != null && (viewMode == DRAWING || viewMode == MOVING)) {
	    chart.setDrawnLineEnd(x, y, mouseY);
	    
	}
       	
	if (x != null && viewMode == ERASING) {
	    chart.setErase(x, y, mouseY);
	}

	if (x != null && viewMode == SCRIBBLING) {
	    chart.setPoint(x, y, mouseY);
	}

    }
    public void mouseMoved(MouseEvent e) {}

    /**
     * Handle widget events.
     *
     * @param	e	action event
     */
    public void actionPerformed(ActionEvent e) {

	if(zoomIn != null && e.getSource() == zoomIn) {
	    chart.zoomToHighlightedRegion();
            zoomIn.setEnabled(zoomInEnabled = false);
            defaultZoom.setEnabled(defaultZoomEnabled = true);
	    // This tells the scrollpane to re-asses whether it needs
	    // the horizontal scrollbar now
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    repaint();
	}
	else if(defaultZoom != null && e.getSource() == defaultZoom) {
	    chart.zoomToDefaultRegion();
            defaultZoom.setEnabled(defaultZoomEnabled = false);
	    // This tells the scrollpane to re-asses whether it needs
	    // the horizontal scrollbar now
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    repaint();
	}
	/* paintOnChart and eraseOnChart are "toggle switches" - they toggle
	   the editing and zoom functions. Ie While in editing mode, 
	   a user won't be able to select portions of the chart.
	*/
	
	/*
	   FIXME: Change interface to reflect this.	    
	   Either identify the mode or make the buttons depressed or 
	   something. 

	   Graph painting buttons now stay depressed. Is this enough?
	*/

	else if(paintOnChart != null && e.getSource() == paintOnChart) {
	    if (viewMode != DRAWING) {	
		previousDefaultZoomState = defaultZoomEnabled;
		defaultZoom.setEnabled(defaultZoomEnabled = false);
		viewMode = DRAWING;		
		activateButton(paintOnChart);

	    } else {		
		defaultZoom.setEnabled(defaultZoomEnabled = previousDefaultZoomState);
		activateButton(null);
		viewMode = SELECTING;
	    }
	}
	else if(eraseOnChart != null && e.getSource() == eraseOnChart) {  
	    if (viewMode != ERASING) {	
		previousDefaultZoomState = defaultZoomEnabled;
		defaultZoom.setEnabled(defaultZoomEnabled = false);
		viewMode = ERASING;

		activateButton(eraseOnChart);
	    } else {		
		defaultZoom.setEnabled(defaultZoomEnabled = previousDefaultZoomState);

		activateButton(null);
		
		viewMode = SELECTING;
	    }
	}
	else if(scribbleOnChart != null && e.getSource() == scribbleOnChart) {
	    if (viewMode != SCRIBBLING) {	
		previousDefaultZoomState = defaultZoomEnabled;
		defaultZoom.setEnabled(defaultZoomEnabled = false);

		viewMode = SCRIBBLING;
		activateButton(scribbleOnChart);
	    } else {		
		defaultZoom.setEnabled(defaultZoomEnabled = previousDefaultZoomState);
		activateButton(null);
		viewMode = SELECTING;
	    }
	}

	else if(moveOnChart != null && e.getSource() == moveOnChart) {
	    if (viewMode != MOVING) {	
		previousDefaultZoomState = defaultZoomEnabled;
		defaultZoom.setEnabled(defaultZoomEnabled = false);

		viewMode = MOVING;
		activateButton(moveOnChart);
	    } else {		
		defaultZoom.setEnabled(defaultZoomEnabled = previousDefaultZoomState);
		activateButton(null);
		viewMode = SELECTING;
	    }
	}

	else if (editOnChart != null && e.getSource() == editOnChart) {
	    if (viewMode != EDITING) {
		previousDefaultZoomState = defaultZoomEnabled;
		defaultZoom.setEnabled(defaultZoomEnabled = false);

		viewMode = EDITING;
		activateButton(editOnChart);
	    } else {
		defaultZoom.setEnabled(defaultZoomEnabled = previousDefaultZoomState);
		activateButton(null);
		viewMode = SELECTING;
	    }
	} 

	else if (cloneChart != null && e.getSource() == cloneChart) {
	    Vector symbols = chart.getSymbols(); 
	    Vector outputList = new Vector();

	    Iterator i = symbols.iterator();
	    while (i.hasNext()) {
		try {
		    Symbol s = Symbol.find( (String)i.next());
		    outputList.add(s);
		} catch (SymbolFormatException sfe) {
		    //Shouldn't happen because the chart
		    //has been instantiated which implies
		    //a valid symbol has been found.
		}		
	    }

	    CommandManager.getInstance().graphStockBySymbol(outputList);
	}

	else if(e.getSource() == closeMenuItem) {
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}
	else if(e.getSource() == addMenuItem) {

	    Thread menuAction = new Thread() {
		    public void run() {
			SortedSet symbols =
			    SymbolListDialog.getSymbols(desktop, Locale.getString("ADD_GRAPH"));
			// Did the user select anything?
			if(symbols != null) {

			    // Remove any symbols that we are already
			    // displaying
			    SortedSet newSymbols = new TreeSet();

			    Iterator iterator = symbols.iterator();
			    while(iterator.hasNext()) {
				Symbol symbol = (Symbol)iterator.next();

				if(!isGraphing(symbol.toString()))
				    newSymbols.add(symbol);
			    }

			    if(newSymbols.size() > 0)
				add(newSymbols);
			}
		    }
		};
	
	    menuAction.start();
	}
    }


    /**
     *
     * Activate chart button b, and deactivate all the rest of the
     * buttons. 
     *
     * @param b      Chart Drawing button to activate
     *
    */

    public void activateButton(JButton b) {
	
	paintOnChart.setSelected(false);
	eraseOnChart.setSelected(false);
	moveOnChart.setSelected(false);
	scribbleOnChart.setSelected(false);
	editOnChart.setSelected(false);

	if (b != null) {
	    b.setSelected(true);	    
	}
    }

    public JDesktopPane getDesktop() {
	return desktop;
    }

    /**
     * Return menu bar for chart module.
     *
     * @return	the menu bar.
     */
    public JMenuBar getJMenuBar() {
	return menuBar;
    }

    /**
     * Return frame icon for chart module.
     *
     * @return	the frame icon
     */
    public ImageIcon getFrameIcon() {
	return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(frameIcon));
    }

    /**
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane() {
	return false;
    }

    /**
     * Tell module to save any current state data / preferences data because
     * the window is being closed.
     */
    public void save() { }

    public BufferedImage getImage() { 
	return chart.getImage();
    }

}


