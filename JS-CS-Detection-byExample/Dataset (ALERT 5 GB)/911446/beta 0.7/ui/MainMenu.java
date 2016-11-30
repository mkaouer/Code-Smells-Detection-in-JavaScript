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

import java.awt.Component;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import org.mov.macro.MacroManager;
import org.mov.macro.StoredMacro;
import org.mov.main.*;
import org.mov.util.Locale;
import org.mov.portfolio.*;
import org.mov.prefs.*;
import org.mov.quote.*;

/**
 * The main menu of the application.
 *
 * @author Daniel Makovec
 */
public class MainMenu implements ActionListener, ModuleListener
{
    // Application's main menu
    private JMenuBar menuBar;

    // All the menu items
    private JMenuItem filePortfolioNewMenuItem;
    private JMenuItem fileImportPreferencesMenuItem;
    private JMenuItem fileImportQuotesMenuItem;
    private JMenuItem fileSyncIDQuotesMenuItem;
    private JMenuItem fileExportPreferencesMenuItem;
    private JMenuItem fileExportQuotesMenuItem;
    private JMenuItem filePreferencesMenuItem;
    private JMenuItem fileExitMenuItem;
    
    private JMenuItem graphCommodityCodeMenuItem;
    private JMenuItem graphCommodityNameMenuItem;
    private JMenuItem graphMarketAdvanceDeclineMenuItem;
    
    private JMenuItem quoteWatchScreenNewMenuItem;
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
    private JMenuItem analysisGAMenuItem;
    private JMenuItem analysisANNMenuItem;
    
    private JMenuItem windowTileHorizontalMenuItem;
    private JMenuItem windowTileVerticalMenuItem;
    private JMenuItem windowCascadeMenuItem;
    private JMenuItem windowGridMenuItem;
    
    private JMenuItem macroManageMenuItem;

    private JMenuItem helpContentsMenuItem;
    private JMenuItem helpAboutMenuItem;
    private JMenuItem helpViewLicenseMenuItem;

    private JMenu helpMenu;
    private JMenu windowMenu;
    private JMenu filePortfolioMenu;
    private JMenu fileImportMenu;
    private JMenu fileSyncMenu;
    private JMenu fileExportMenu;
    private JMenu graphPortfolioMenu;
    private JMenu tablePortfolioMenu;
    private JMenu quoteWatchScreenMenu;
    private JMenu macroMenu;
    
    private org.mov.ui.DesktopManager desktopManager;
    private JDesktopPane desktop;
    private JFrame frame;
    
    // Mappings between menus and portfolios
    private HashMap portfolioHash = new HashMap();
    private HashMap portfolioGraphHash = new HashMap();
    private HashMap portfolioTableHash = new HashMap();
    
    // Mapping between menus and watch screens
    private HashMap watchScreenHash = new HashMap();
    
    // Used for window menu - keeps track of modules + menu items
    private Hashtable moduleToMenuItemHash = new Hashtable();
    private Hashtable menuItemToModuleHash = new Hashtable();
    private Hashtable menuItemToMacroHash  = new Hashtable();
    
    // Singleton instance of this class
    private static MainMenu instance = null;
    
    /**
     * Construct a new main menu and attach it to the given frame.
     *
     * @param	frame	the window frame
     * @param	desktopManager	the desktop to lunch internal frames on
     */
    public static MainMenu getInstance(JFrame frame,
            org.mov.ui.DesktopManager desktopManager) {
        if(instance == null)
            instance = new MainMenu(frame, desktopManager);
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
            org.mov.ui.DesktopManager desktopManager) {
        this.frame = frame;
        this.desktopManager = desktopManager;
        this.desktop = DesktopManager.getDesktop();
        
        // Listens for modules being added, delete or having their names
        // changed
        desktopManager.addModuleListener(this);
        
        menuBar = new JMenuBar();

        /**********************************************
         * File
         **********************************************/
        JMenu fileMenu = MenuHelper.addMenu(menuBar, Locale.getString("FILE"), 'F');
        
        // File -> Portfolio
        filePortfolioMenu = MenuHelper.addMenu(fileMenu, Locale.getString("PORTFOLIO"), 'P');
        
        fileMenu.addSeparator();
        
        // File -> Import
        fileImportMenu = MenuHelper.addMenu(fileMenu, Locale.getString("IMPORT"), 'I');
        // File -> Import -> Preferences
        fileImportPreferencesMenuItem =
            MenuHelper.addMenuItem(this, fileImportMenu, Locale.getString("PREFERENCES"));
        // File -> Import -> End of Day Quotes
        fileImportQuotesMenuItem =
            MenuHelper.addMenuItem(this, fileImportMenu, Locale.getString("END_OF_DAY_QUOTES"));
        
        // File -> Sync
        fileSyncMenu = MenuHelper.addMenu(fileMenu, Locale.getString("SYNC"));
        // File -> Import -> Sync Intra-day Quotes
        fileSyncIDQuotesMenuItem =
            MenuHelper.addMenuItem(this, fileSyncMenu, Locale.getString("INTRA_DAY_QUOTES"));
        //        fileSyncIDQuotesMenuItem.setEnabled(false);

        // File -> Export
        fileExportMenu = MenuHelper.addMenu(fileMenu, Locale.getString("EXPORT"), 'E');
        // File -> Export -> Preferences
        fileExportPreferencesMenuItem =
            MenuHelper.addMenuItem(this, fileExportMenu, Locale.getString("PREFERENCES"));
        // File -> Export -> End of Day Quotes
        fileExportQuotesMenuItem =
            MenuHelper.addMenuItem(this, fileExportMenu, Locale.getString("END_OF_DAY_QUOTES"));
        // File -> Preferences
        filePreferencesMenuItem = MenuHelper.addMenuItem(this, fileMenu,
                Locale.getString("PREFERENCES"), 'R');
        fileMenu.addSeparator();
        
        // File -> Exit
        fileExitMenuItem = MenuHelper.addMenuItem(this, fileMenu, Locale.getString("EXIT"), 'Q');
        
        /**********************************************
         * Table
         **********************************************/
        JMenu quoteMenu = MenuHelper.addMenu(menuBar, Locale.getString("TABLE"), 'T');
        
        // Table -> Watch screens
        quoteWatchScreenMenu = MenuHelper.addMenu(quoteMenu, Locale.getString("WATCH_SCREEN"), 'W');
        
        quoteMenu.addSeparator();
        
        // Table -> Companies + Funds
        JMenu quoteMenuCompany = MenuHelper.addMenu(quoteMenu,
                Locale.getString("ALL_ORDINARIES"), 'C');
        
        // Table -> Companies + Funds -> List all
        quoteCompanyListAllMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuCompany, Locale.getString("LIST_ALL"));
        
        // Table -> Companies + Funds -> List by rule
        quoteCompanyListRuleMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuCompany, Locale.getString("LIST_BY_RULE"));
        
        // Table -> Companies + Funds -> List by date
        quoteCompanyListDateMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuCompany, Locale.getString("LIST_BY_DATE"));
        
        // Table -> Indices
        JMenu quoteMenuIndices = MenuHelper.addMenu(quoteMenu,
                Locale.getString("MARKET_INDICES"), 'I');
        
        // Table -> Indices -> List All
        quoteIndicesListAllMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuIndices, Locale.getString("LIST_ALL"));
        
        // Table -> Indices -> List by Rule
        quoteIndicesListRuleMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuIndices, Locale.getString("LIST_BY_RULE"));
        
        // Table -> Indices -> List by Date
        quoteIndicesListDateMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuIndices, Locale.getString("LIST_BY_DATE"));
        
        // Table -> All Stocks
        JMenu quoteMenuCommodities = MenuHelper.addMenu(quoteMenu,
                Locale.getString("ALL_STOCKS"), 'A');
        
        // Table -> All Stocks -> List All
        quoteCommoditiesListAllMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuCommodities,
                    Locale.getString("LIST_ALL"), 'L');
        
        // Table -> All Stocks -> List by Rule
        quoteCommoditiesListRuleMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuCommodities,
                    Locale.getString("LIST_BY_RULE"),'B');
        
        // Table -> All Stocks -> List by Date
        quoteCommoditiesListDateMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuCommodities,
                    Locale.getString("LIST_BY_DATE"),'D');
        
        // Table -> Stocks -> List by Symbols
        JMenu quoteMenuStocks = MenuHelper.addMenu(quoteMenu,
                Locale.getString("STOCKS"), 'S');
        
        quoteStocksListSymbolsMenuItem =
            MenuHelper.addMenuItem(this, quoteMenuStocks,
                    Locale.getString("LIST_BY_SYMBOLS"), 'B');
        
        quoteMenu.addSeparator();
        
        // Table -> Portfolio
        tablePortfolioMenu = MenuHelper.addMenu(quoteMenu, Locale.getString("PORTFOLIO"));
                
        /**********************************************
         * Graph
         **********************************************/
        
        JMenu graphMenu = MenuHelper.addMenu(menuBar, Locale.getString("GRAPH"), 'G');
        
        // Graph -> Commodities
        JMenu graphCommodityMenu = MenuHelper.addMenu(graphMenu,
                Locale.getString("STOCK"));
        
        // Graph -> Commodities -> By Codes
        graphCommodityCodeMenuItem =
            MenuHelper.addMenuItem(this, graphCommodityMenu,
                    Locale.getString("GRAPH_BY_SYMBOLS"), 'G');
        
        // Graph -> Commodities -> By Name
        //	    graphCommodityNameMenuItem =
        //		MenuHelper.addMenuItem(this, graphCommodityMenu,
        //			       "By Name",'N');
        
        // Graph -> Market Indicator
        JMenu graphMarketIndicator =
            MenuHelper.addMenu(graphMenu, Locale.getString("MARKET_INDICATOR"));
        
        // Graph -> Market Indicator -> Advance/Decline
        graphMarketAdvanceDeclineMenuItem =
            MenuHelper.addMenuItem(this, graphMarketIndicator,
                    Locale.getString("ADVANCE_DECLINE"));
        
        // Graph -> Portfolio
        graphPortfolioMenu = MenuHelper.addMenu(graphMenu, Locale.getString("PORTFOLIO"));
        
        /**********************************************
         * Analysis
         **********************************************/
        JMenu analysisMenu =
            MenuHelper.addMenu(menuBar, Locale.getString("ANALYSIS"), 'A');
        
        analysisPaperTradeMenuItem =
            MenuHelper.addMenuItem(this, analysisMenu,
                    Locale.getString("PAPER_TRADE"));
        
        analysisGPMenuItem =
            MenuHelper.addMenuItem(this, analysisMenu,
                    Locale.getString("GP"));
        
        analysisGAMenuItem =
            MenuHelper.addMenuItem(this, analysisMenu,
                    Locale.getString("GA"));
        
        analysisANNMenuItem =
            MenuHelper.addMenuItem(this, analysisMenu,
                    Locale.getString("ANN"));
        
        /**********************************************
         * Window
         **********************************************/
        windowMenu = MenuHelper.addMenu(menuBar, Locale.getString("WINDOW"), 'W');
        windowTileHorizontalMenuItem =
            MenuHelper.addMenuItem(this, windowMenu, Locale.getString("TILE_HORIZONTALLY"));
        windowTileHorizontalMenuItem.setEnabled(false);
        windowTileVerticalMenuItem =
            MenuHelper.addMenuItem(this, windowMenu, Locale.getString("TILE_VERTICALLY"));
        windowTileVerticalMenuItem.setEnabled(false);
        windowCascadeMenuItem =
            MenuHelper.addMenuItem(this, windowMenu, Locale.getString("CASCADE"));
        windowCascadeMenuItem.setEnabled(false);
        windowGridMenuItem =
            MenuHelper.addMenuItem(this, windowMenu, Locale.getString("ARRANGE_ALL"));
        windowGridMenuItem.setEnabled(false);
        
        /**********************************************
         * Macro
         **********************************************/
        macroMenu = MenuHelper.addMenu(menuBar, Locale.getString("MACRO"), 'M');
        buildMacroMenu();
        
        /**********************************************
         * Help
         **********************************************/
        helpMenu = MenuHelper.addMenu(menuBar, Locale.getString("HELP"), 'H');
        helpAboutMenuItem = MenuHelper.addMenuItem(this, helpMenu,
                                                   Locale.getString("ABOUT"));
        helpContentsMenuItem = MenuHelper.addMenuItem(this, helpMenu,
                                                      Locale.getString("CONTENTS"));
        helpViewLicenseMenuItem = MenuHelper.addMenuItem(this, helpMenu,
                                                         Locale.getString("VIEW_LICENSE"));
        
        // Build portfolio and watchscreen menus
        updatePortfolioMenu();
        updateWatchScreenMenu();
        
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

                Object mo;
                if ((mo = menuItemToMacroHash.get(menu)) != null) {
                    StoredMacro stored_macro;
                    try {
                        stored_macro = (StoredMacro)mo;
                        MacroManager.execute(stored_macro);
                    }
                    catch(ClassCastException e) {
                        assert false;
                        return;
                    } catch (NoClassDefFoundError e) {
                        JOptionPane.showInternalMessageDialog(desktop, 
                                Locale.getString("NO_JYTHON_ERROR",
                                                 e.getMessage()),
                                Locale.getString("MACRO_EXECUTION_ERROR"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else if ((mo = menuItemToModuleHash.get(menu)) != null) {
                    
                    Module module;
                    
                    // Get module
                    try {
                        module = (Module)mo;
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
                else if(menu == macroManageMenuItem)
                    // Display preferences
                    CommandManager.getInstance().openPreferences(PreferencesModule.MACROS_PAGE);

                // File Menu
                else if(menu == fileImportPreferencesMenuItem) {
                    CommandManager.getInstance().importPreferences();
                }
                else if(menu == fileExportPreferencesMenuItem) {
                    CommandManager.getInstance().exportPreferences();
                }
                else if(menu == fileExportQuotesMenuItem) {
                    CommandManager.getInstance().exportQuotes();
                }
                else if(menu == fileImportQuotesMenuItem) {
                    CommandManager.getInstance().importQuotes();
                }
                else if(menu == fileSyncIDQuotesMenuItem) {
                    CommandManager.getInstance().syncIDQuotes();
                }
                else if(menu == filePortfolioNewMenuItem)
                    CommandManager.getInstance().newPortfolio();
                
                // Maybe it's a portfolio?
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
                else if(menu == filePreferencesMenuItem)
                    // Display preferences
                    CommandManager.getInstance().openPreferences();
                
                // Table Menu
                else if(menu == quoteWatchScreenNewMenuItem)
                    CommandManager.getInstance().newWatchScreen();
                
                // Maybe it's a watch screen
                else if(watchScreenHash.get(menu) != null) {
                    String watchScreenName =
                        (String)watchScreenHash.get(menu);
                    CommandManager.getInstance().openWatchScreen(watchScreenName);
                }
                
                else if(menu == quoteCommoditiesListAllMenuItem)
                    CommandManager.getInstance().tableStocks(EODQuoteRange.ALL_SYMBOLS);
                else if (menu == quoteCommoditiesListRuleMenuItem)
                    CommandManager.getInstance().tableStocksByRule(EODQuoteRange.ALL_SYMBOLS);
                else if (menu == quoteCommoditiesListDateMenuItem)
                    CommandManager.getInstance().tableStocksByDate(EODQuoteRange.ALL_SYMBOLS);
                
                else if(menu == quoteCompanyListAllMenuItem)
                    CommandManager.getInstance().tableStocks(EODQuoteRange.ALL_ORDINARIES);
                else if (menu == quoteCompanyListRuleMenuItem)
                    CommandManager.getInstance().tableStocksByRule(EODQuoteRange.ALL_ORDINARIES);
                else if (menu == quoteCompanyListDateMenuItem)
                    CommandManager.getInstance().tableStocksByDate(EODQuoteRange.ALL_ORDINARIES);
                
                else if (menu == quoteIndicesListAllMenuItem)
                    CommandManager.getInstance().tableStocks(EODQuoteRange.MARKET_INDICES);
                else if (menu == quoteIndicesListRuleMenuItem)
                    CommandManager.getInstance().tableStocksByRule(EODQuoteRange.MARKET_INDICES);
                else if (menu == quoteIndicesListDateMenuItem)
                    CommandManager.getInstance().tableStocksByDate(EODQuoteRange.MARKET_INDICES);
                
                else if (menu == quoteStocksListSymbolsMenuItem)
                    CommandManager.getInstance().tableStocks(null);
                
                // Maybe its a portfolio?
                else if(portfolioTableHash.get(menu) != null) {
                    String portfolioName =
                        (String)portfolioTableHash.get(menu);
                                        
                    CommandManager.getInstance().tablePortfolio(portfolioName);
                }
                
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
                                        
                    CommandManager.getInstance().graphPortfolio(portfolioName);
                }
                
                // Analysis Menu
                else if (menu == analysisPaperTradeMenuItem)
                    CommandManager.getInstance().paperTrade();
                else if (menu == analysisGPMenuItem)
                    CommandManager.getInstance().gp();
                else if (menu == analysisGAMenuItem)
                    CommandManager.getInstance().ga();
                else if (menu == analysisANNMenuItem)
                    CommandManager.getInstance().ann();
                
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
                else if (menu == helpViewLicenseMenuItem)
                    CommandManager.getInstance().openLicenseDialog();
                
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
        tablePortfolioMenu.removeAll();
        
        // Portfolio menu off of file has the ability to create a new
        // portfolio
        filePortfolioNewMenuItem =
            MenuHelper.addMenuItem(this, filePortfolioMenu, Locale.getString("NEW_PORTFOLIO"));
        
        // Build both portfolio menus
        List portfolioNames = PreferencesManager.getPortfolioNames();
        
        if(portfolioNames.size() > 0)
            filePortfolioMenu.addSeparator();
        else {
            JMenuItem noPortfoliosMenuItem = new JMenuItem(Locale.getString("NO_PORTFOLIOS"));
            noPortfoliosMenuItem.setEnabled(false);
            graphPortfolioMenu.add(noPortfoliosMenuItem);
            
            noPortfoliosMenuItem = new JMenuItem(Locale.getString("NO_PORTFOLIOS"));
            noPortfoliosMenuItem.setEnabled(false);
            tablePortfolioMenu.add(noPortfoliosMenuItem);
        }
        
        portfolioHash = buildMenu(filePortfolioMenu, portfolioNames);
        portfolioGraphHash = buildMenu(graphPortfolioMenu, portfolioNames);
        portfolioTableHash = buildMenu(tablePortfolioMenu, portfolioNames);
    }
    
    /**
     * Inform menu that the list of watch screens has changed and that
     * its menus should be redrawn
     */
    public void updateWatchScreenMenu() {
        // Remove old menu items from watch screen menu (if there were any)
        quoteWatchScreenMenu.removeAll();
        
        quoteWatchScreenNewMenuItem =
            MenuHelper.addMenuItem(this, quoteWatchScreenMenu,
                    Locale.getString("NEW_WATCH_SCREEN"));
        
        // Build both portfolio menus
        List watchScreenNames = PreferencesManager.getWatchScreenNames();
        
        if(watchScreenNames.size() > 0)
            quoteWatchScreenMenu.addSeparator();
        
        watchScreenHash = buildMenu(quoteWatchScreenMenu, watchScreenNames);
    }
    
    /** Take the list of menu item and add them to the given menu. Return
     *  a hashmap which maps each menu created with the given menu item.
     */
    private HashMap buildMenu(JMenu menu, List items) {
        HashMap menuMap = new HashMap();
        
        for(Iterator iterator = items.iterator(); iterator.hasNext();) {
            String item = (String)iterator.next();
            JMenuItem menuItem = MenuHelper.addMenuItem(this, menu, item);
            menuMap.put(menuItem, item);
        }
        
        return menuMap;
    }
    
    /** 
     * Take the list of menu item and add them to the given menu. Return
     *  a hashmap which maps each menu created with the given menu item.
     */
    public void buildMacroMenu()
    {
        java.util.List stored_macros = PreferencesManager.getStoredMacros();
        macroMenu.removeAll();
        menuItemToMacroHash.clear();

        macroManageMenuItem =
            MenuHelper.addMenuItem(this, macroMenu,
                    Locale.getString("MANAGE"), 'M');
        macroMenu.addSeparator();

        for (int i = 0; i < stored_macros.size(); i++) {
            StoredMacro m = (StoredMacro) stored_macros.get(i);
            if (m.isIn_menu()) {
                JMenuItem menuItem = MenuHelper.addMenuItem(this, macroMenu, m.getName());
                menuItemToMacroHash.put(menuItem, m);
            }
        }
    }

    /**
     * Disable the menus. This shadows the menus and disables user action.
     */
    public void disableMenus() {
        for(int i = 0; i < menuBar.getMenuCount(); i++)
            menuBar.getMenu(i).setEnabled(false);
    }

    /**
     * Enable the menus. This re-enables the menus and allows user actions.
     */
    public void enableMenus() {
        for(int i = 0; i < menuBar.getMenuCount(); i++)
            menuBar.getMenu(i).setEnabled(true);
    }
}



