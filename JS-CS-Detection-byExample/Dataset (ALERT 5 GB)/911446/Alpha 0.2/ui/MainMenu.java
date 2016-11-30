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

package org.mov.ui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import org.mov.chart.*;
import org.mov.main.*;
import org.mov.util.*;
import org.mov.portfolio.*;
import org.mov.prefs.*;
import org.mov.quote.*;
import org.mov.table.*;
import org.mov.ui.*;

/**
 * The main menu of the application.
 */
public class MainMenu 
    implements ActionListener, ModuleListener
{

    // All the menu items
    private JMenuItem fileImportQuotesMenuItem;
    private JMenuItem filePortfolioNewMenuItem;
    private JMenuItem filePreferencesMenuItem;
    private JMenuItem fileExitMenuItem;

    private JMenuItem graphCommodityCodeMenuItem;
    private JMenuItem graphCommodityNameMenuItem;
    private JMenuItem graphMarketAdvanceDeclineMenuItem;

    private JMenuItem quoteCompanyListAllMenuItem;
    private JMenuItem quoteCompanyListRuleMenuItem;
    private JMenuItem quoteCompanyListDateMenuItem;
    private JMenuItem quoteIndicesListAllMenuItem;
    private JMenuItem quoteIndicesListRuleMenuItem;
    private JMenuItem quoteIndicesListDateMenuItem;
    private JMenuItem quoteCommoditiesListAllMenuItem;
    private JMenuItem quoteCommoditiesListRuleMenuItem;
    private JMenuItem quoteCommoditiesListDateMenuItem;
    private JMenuItem quoteStocksListSymbolsMenuItem;

    private JMenuItem analysisPaperTradeMenuItem;
    private JMenuItem analysisGPMenuItem;

    private JMenuItem windowTileHorizontalMenuItem;
    private JMenuItem windowTileVerticalMenuItem;
    private JMenuItem windowCascadeMenuItem;
    private JMenuItem windowGridMenuItem;

    private JMenuItem helpContentsMenuItem;
    private JMenuItem helpAboutMenuItem;

    private JMenu helpMenu;
    private JMenu windowMenu;
    private JMenu filePortfolioMenu;
    private JMenu graphPortfolioMenu;

    private org.mov.ui.DesktopManager desktopManager;
    private JDesktopPane desktop;
    private JFrame frame;

    private HashMap portfolioHash = new HashMap();
    private HashMap portfolioGraphHash = new HashMap();

    // Used for window menu - keeps track of modules + menu items
    private Hashtable moduleToMenuItemHash = new Hashtable();
    private Hashtable menuItemToModuleHash = new Hashtable();

    // Singleton instance of this class
    private static MainMenu instance = null;

    /**
     * Construct a new main menu and attach it to the given frame.
     *
     * @param	frame	the window frame
     * @param	desktop	the desktop to lunch internal frames on
     */
    public static MainMenu getInstance(JFrame frame, 
				       org.mov.ui.DesktopManager desktopManager, 
				       JDesktopPane desktop) {
	if(instance == null) {
	    instance = new MainMenu(frame, desktopManager, desktop);
	}
	return instance;
    }

    /**
     * Return the instance of the main menu. Will return null if not
     * yet created.
     * 
     * @return	the main menu instance
     */
    public static MainMenu getInstance() {
	return instance;
    }

    private MainMenu(JFrame frame, 
		     org.mov.ui.DesktopManager desktopManager, 
		     JDesktopPane desktop) {

	this.frame = frame;
	this.desktop = desktop;
	this.desktopManager = desktopManager;

	// Listens for modules being added, delete or having their names
	// changed
	desktopManager.addModuleListener(this);
	
	JMenuBar menuBar = new JMenuBar();
	// File 
	{	   
	    JMenu fileMenu = MenuHelper.addMenu(menuBar, "File", 'F');

	    // File -> Portfolio
	    filePortfolioMenu = MenuHelper.addMenu(fileMenu, "Portfolio", 'P');

	    fileMenu.addSeparator();

	    // File -> Import
	    fileImportQuotesMenuItem = MenuHelper.addMenuItem(this, fileMenu, 
							      "Import Quotes", 
							      'I');
	    // File -> Preferences
	    filePreferencesMenuItem = MenuHelper.addMenuItem(this, fileMenu, 
							     "Preferences", 
							     'R');
	    fileMenu.addSeparator();

	    // File -> Exit
	    fileExitMenuItem = MenuHelper.addMenuItem(this, fileMenu, "Exit", 'Q');
	}

	// Table
	{
	    JMenu quoteMenu = MenuHelper.addMenu(menuBar, "Table", 'T');
	    
	    // Table -> Companies + Funds
	    JMenu quoteMenuCompany = MenuHelper.addMenu(quoteMenu, 
							"All Ordinaries",
					     'C');

	    // Table -> Companies + Funds -> List all
	    quoteCompanyListAllMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuCompany, "List All");

	    // Table -> Companies + Funds -> List by rule
	    quoteCompanyListRuleMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuCompany, "List by Rule");

	    // Table -> Companies + Funds -> List by date
	    quoteCompanyListDateMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuCompany, "List by Date");
	    
	    // Table -> Indices
	    JMenu quoteMenuIndices = MenuHelper.addMenu(quoteMenu, 
							"Market Indices", 'I');

	    // Table -> Indices -> List All
	    quoteIndicesListAllMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuIndices, "List All");

	    // Table -> Indices -> List by Rule
	    quoteIndicesListRuleMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuIndices, "List by Rule");

	    // Table -> Indices -> List by Date
	    quoteIndicesListDateMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuIndices, "List by Date");
	    
	    // Table -> All Stocks
	    JMenu quoteMenuCommodities = MenuHelper.addMenu(quoteMenu, 
							    "All Stocks",
							    'A');

	    // Table -> All Stocks -> List All
	    quoteCommoditiesListAllMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuCommodities, 
				       "List All", 'L');

	    // Table -> All Stocks -> List by Rule
	    quoteCommoditiesListRuleMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuCommodities, 
				       "List by Rule",'B');

	    // Table -> All Stocks -> List by Date
	    quoteCommoditiesListDateMenuItem = 
		MenuHelper.addMenuItem(this, quoteMenuCommodities, 
				       "List by Date",'D');

            quoteMenu.addSeparator();

            // Table -> Stocks -> List by Symbols
            JMenu quoteMenuStocks = MenuHelper.addMenu(quoteMenu,
                                                       "Stocks", 'S');

            quoteStocksListSymbolsMenuItem =
                MenuHelper.addMenuItem(this, quoteMenuStocks, "List by Symbols", 'B');
	}
	
	// Graph        
	{
	    JMenu graphMenu = MenuHelper.addMenu(menuBar, "Graph", 'G');
	    
	    // Graph -> Commodities
	    JMenu graphCommodityMenu = MenuHelper.addMenu(graphMenu, 
							  "Stock");
	    
	    // Graph -> Commodities -> By Codes
	    graphCommodityCodeMenuItem = 
		MenuHelper.addMenuItem(this, graphCommodityMenu, 
				       "Graph by Symbols", 'G');
	    
	    // Graph -> Commodities -> By Name
            //	    graphCommodityNameMenuItem = 
            //		MenuHelper.addMenuItem(this, graphCommodityMenu, 
            //			       "By Name",'N');

	    // Graph -> Market Indicator
	    JMenu graphMarketIndicator = 
		MenuHelper.addMenu(graphMenu, "Market Indicator");

	    // Graph -> Market Indicator -> Advance/Decline
	    graphMarketAdvanceDeclineMenuItem =
		MenuHelper.addMenuItem(this, graphMarketIndicator,
				       "Advance/Decline");

	    // Graph -> Portfolio
	    graphPortfolioMenu = MenuHelper.addMenu(graphMenu, "Portfolio");
	}

	// Analysis menu
	{
	    JMenu analysisMenu = 
		MenuHelper.addMenu(menuBar, "Analysis", 'A');

	    analysisPaperTradeMenuItem = 
		MenuHelper.addMenuItem(this, analysisMenu,
				       "Paper Trade");

	    analysisGPMenuItem = 
		MenuHelper.addMenuItem(this, analysisMenu,
				       "Genetic Programming");
	}

	// Window menu
	{
	    windowMenu = MenuHelper.addMenu(menuBar, "Window", 'W');
	    windowTileHorizontalMenuItem = 
		MenuHelper.addMenuItem(this, windowMenu, "Tile Horizontally");
	    windowTileHorizontalMenuItem.setEnabled(false);
	    windowTileVerticalMenuItem = 
		MenuHelper.addMenuItem(this, windowMenu, "Tile Vertically");
	    windowTileVerticalMenuItem.setEnabled(false);
	    windowCascadeMenuItem = 
		MenuHelper.addMenuItem(this, windowMenu, "Cascade");
	    windowCascadeMenuItem.setEnabled(false);
	    windowGridMenuItem = 
		MenuHelper.addMenuItem(this, windowMenu, "Arrange all");
	    windowGridMenuItem.setEnabled(false);
	}

        // Help menu
        {
            helpMenu = MenuHelper.addMenu(menuBar, "Help", 'H');
            helpContentsMenuItem = MenuHelper.addMenuItem(this, helpMenu, "Contents");
            helpAboutMenuItem = MenuHelper.addMenuItem(this, helpMenu, "About");
        }

	// Build portfolio menus
	updatePortfolioMenu();

	frame.setJMenuBar(menuBar);
    }

    /**
     * Called when a menu item is selected.
     *
     * @param	e	an action
     */
    public void actionPerformed(final ActionEvent e) {

	// Handle all menu actions in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread menuAction = new Thread() {

		public void run() {
		    // They should all be menu actions
		    JMenuItem menu = (JMenuItem)e.getSource();

		    // Perhaps user selected a module from the window menu?
		    Component c = (Component)menuItemToModuleHash.get(menu);
		    
		    // File Menu
		    if(menu == fileImportQuotesMenuItem) {
			CommandManager.getInstance().importQuotes();
		    }
		    else if(menu == filePortfolioNewMenuItem)
			CommandManager.getInstance().newPortfolio();

		    // Maybe its a portfolio?
		    else if(portfolioHash.get(menu) != null) {
			String portfolioName =
			    (String)portfolioHash.get(menu);
			CommandManager.getInstance().openPortfolio(portfolioName);
		    }

		    else if(menu == fileExitMenuItem) {
			// This exits the application
			frame.dispose();
			return;
		    }
		    else if(menu == filePreferencesMenuItem) {
			// Display preferences
			((DesktopManager)(desktop.getDesktopManager()))
			    .newFrame(new PreferencesModule(desktop), true, true);
		    }
		    
		    // Table Menu
		    else if(menu == quoteCommoditiesListAllMenuItem)
			CommandManager.getInstance().tableStocks(QuoteRange.ALL_SYMBOLS);
		    else if (menu == quoteCommoditiesListRuleMenuItem)
			CommandManager.getInstance().tableStocksByRule(QuoteRange.ALL_SYMBOLS);
		    else if (menu == quoteCommoditiesListDateMenuItem)
			CommandManager.getInstance().tableStocksByDate(QuoteRange.ALL_SYMBOLS);

		    else if(menu == quoteCompanyListAllMenuItem)
                        CommandManager.getInstance().tableStocks(QuoteRange.ALL_ORDINARIES);
		    else if (menu == quoteCompanyListRuleMenuItem)
			CommandManager.getInstance().tableStocksByRule(QuoteRange.ALL_ORDINARIES);
		    else if (menu == quoteCompanyListDateMenuItem)
			CommandManager.getInstance().tableStocksByDate(QuoteRange.ALL_ORDINARIES);

		    else if (menu == quoteIndicesListAllMenuItem)
			CommandManager.getInstance().tableStocks(QuoteRange.MARKET_INDICES);
		    else if (menu == quoteIndicesListRuleMenuItem)
			CommandManager.getInstance().tableStocksByRule(QuoteRange.MARKET_INDICES);
		    else if (menu == quoteIndicesListDateMenuItem)
			CommandManager.getInstance().tableStocksByDate(QuoteRange.MARKET_INDICES);

                    else if (menu == quoteStocksListSymbolsMenuItem)
			CommandManager.getInstance().tableStocks(null);
		    
		    // Graph Menu
		    else if (menu == graphCommodityCodeMenuItem) 
			CommandManager.getInstance().graphStockBySymbol(null);
                    //		    else if (menu == graphCommodityNameMenuItem)
                    //	CommandManager.getInstance().graphStockByName();
		    else if (menu == graphMarketAdvanceDeclineMenuItem)
			CommandManager.getInstance().graphAdvanceDecline();

		    // Maybe its a portfolio?
		    else if(portfolioGraphHash.get(menu) != null) {
			String portfolioName =
			    (String)portfolioGraphHash.get(menu);

			Portfolio portfolio = 
			    PreferencesManager.loadPortfolio(portfolioName);

			CommandManager.getInstance().graphPortfolio(portfolio);
		    }

		    // Analysis Menu
		    else if (menu == analysisPaperTradeMenuItem)
			CommandManager.getInstance().paperTrade();
		    else if (menu == analysisGPMenuItem)
			CommandManager.getInstance().gp();

		    // Window Menu
		    else if (menu == windowTileHorizontalMenuItem)
			CommandManager.getInstance().tileFramesHorizontal();
		    else if (menu == windowTileVerticalMenuItem)
			CommandManager.getInstance().tileFramesVertical();
		    else if (menu == windowCascadeMenuItem)
			CommandManager.getInstance().tileFramesCascade();
		    else if (menu == windowGridMenuItem)
			CommandManager.getInstance().tileFramesArrange();

                    // Help Menu
                    else if (menu == helpContentsMenuItem)
                        CommandManager.getInstance().openHelp();
                    else if (menu == helpAboutMenuItem)
                        CommandManager.getInstance().openAboutDialog();

                    // If the user selected a window from the Window menu, then
                    // bring that window to the front
		    else if(c != null) {

                        Module module;

                        // Get module
                        try {
                            module = (Module)c;
                        } 
                        catch(ClassCastException e) {
                            assert false;
                            return;
                        }
                            
                        // Get frame from module
                        Component component =  module.getComponent();
                        while(!(component instanceof JInternalFrame)) {
                            component = component.getParent();
                        }
			
                        JInternalFrame frame = (JInternalFrame)component;

			try {
			    frame.setIcon(false);
			    desktop.setSelectedFrame(frame);
			    frame.setSelected(true);
			    frame.toFront();
			} 
                        catch (PropertyVetoException exception) {
                            assert false;
                        }			
		    }
		    else 
			assert false;
		}
	    };
	
	menuAction.start();
    }

    /**
     * Called by the desktop manager to notify us when a new module is
     * added
     *
     * @param	moduleEvent	Module Event
     */
    public void moduleAdded(ModuleEvent moduleEvent) {
	Module module = (Module)moduleEvent.getSource();
	String title = module.getTitle();

	// First window? Then enable window arrange menu items and
	// add separator
	if (moduleToMenuItemHash.size() == 0) {
	    windowTileHorizontalMenuItem.setEnabled(true);
	    windowTileVerticalMenuItem.setEnabled(true);
	    windowCascadeMenuItem.setEnabled(true);
	    windowGridMenuItem.setEnabled(true);
	    
	    windowMenu.addSeparator();
	}
	
	// Store the menu item in a hash referenced by the window's name
	JMenuItem menuItem = MenuHelper.addMenuItem(this, windowMenu, title);
	moduleToMenuItemHash.put(module, menuItem);
	menuItemToModuleHash.put(menuItem, module);
    }

    /**
     * Called by the desktop manager to notify us when a module is removed
     *
     * @param	moduleEvent	Module Event
     */
    public void moduleRemoved(ModuleEvent moduleEvent) {
	Module module = (Module)moduleEvent.getSource();

	windowMenu.remove((JMenuItem)moduleToMenuItemHash.get(module));
	menuItemToModuleHash.remove(moduleToMenuItemHash.get(module));
	moduleToMenuItemHash.remove(module);

	// No more menu items? Then disable window arrange menu items
	// and remove separator
	if (moduleToMenuItemHash.size() == 0) {
	    windowTileHorizontalMenuItem.setEnabled(false);
	    windowTileVerticalMenuItem.setEnabled(false);
	    windowCascadeMenuItem.setEnabled(false);
	    windowGridMenuItem.setEnabled(false);
	    
	    // Window separator is the last menu item
	    windowMenu.remove(windowMenu.getItemCount() - 1);
	}
    }

    /**
     * Called by the desktop manager to notify us when a module is renamed
     *
     * @param	moduleEvent	Module Event
     */
    public void moduleRenamed(ModuleEvent moduleEvent) {
	// Same as removing and then adding a totally new module
	moduleRemoved(moduleEvent);
	moduleAdded(moduleEvent);
    }

    /**
     * Inform menu that the list of portfolios has changed and that
     * its menus should be redrawn
     */
    public void updatePortfolioMenu() {
	// Remove old menu items from portfolio menus (if there were any)
	filePortfolioMenu.removeAll();
	graphPortfolioMenu.removeAll();

	// Portfolio menu off of file has the ability to create a new
	// portfolio
	filePortfolioNewMenuItem = 
	    MenuHelper.addMenuItem(this, filePortfolioMenu, "New Portfolio");

	if(PreferencesManager.getPortfolioNames().length > 0) {
	    filePortfolioMenu.addSeparator();
	}

	// Build both portfolio menus
	portfolioHash = buildPortfolioMenu(filePortfolioMenu);
	portfolioGraphHash = buildPortfolioMenu(graphPortfolioMenu);
    }

    // Build menu with names of all portfolios. Create hashmap which
    // maps menu items back to the portfolio listed.
    private HashMap buildPortfolioMenu(JMenu portfolioMenu)
    {
	HashMap menuPortfolioMap = new HashMap();
	
	String[] portfolioNames = PreferencesManager.getPortfolioNames();
	if(portfolioNames.length > 0) {
	    for(int i = 0; i < portfolioNames.length; i++) {
		JMenuItem menu = MenuHelper.addMenuItem(this, 
							portfolioMenu,
							portfolioNames[i]);
		menuPortfolioMap.put(menu, portfolioNames[i]);
	    }
	}

	return menuPortfolioMap;
    }
}



