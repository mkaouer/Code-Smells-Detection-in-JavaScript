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

package nz.org.venice.table;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JDesktopPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nz.org.venice.main.CommandManager;
import nz.org.venice.main.Module;
import nz.org.venice.main.ModuleFrame;
import nz.org.venice.prefs.PreferencesException;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.quote.IDQuote;
import nz.org.venice.quote.IDQuoteCache;
import nz.org.venice.quote.IDQuoteSync;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.MixedQuoteBundle;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.QuoteEvent;
import nz.org.venice.quote.QuoteListener;
import nz.org.venice.quote.Symbol;
import nz.org.venice.alert.AlertDialog;
import nz.org.venice.alert.AlertWriter;
import nz.org.venice.alert.AlertManager;
import nz.org.venice.ui.AbstractTable;
import nz.org.venice.ui.Column;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.ui.MainMenu;
import nz.org.venice.ui.MenuHelper;
import nz.org.venice.ui.MixedQuoteModel;
import nz.org.venice.ui.SymbolListDialog;
import nz.org.venice.ui.TextDialog;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingTime;
import nz.org.venice.prefs.settings.WatchScreenSettings;

/**
 * Venice module for displaying a watch screen to the user. This module allows a
 * user to build and modify a watch screen which can be used to monior a group
 * of stocks.
 *
 * @author Andrew Leppard
 * @see WatchScreen
 */
public class WatchScreenModule extends AbstractTable implements Module, ActionListener {

    // Main menu items
    private JMenuBar menuBar;
    private JMenuItem addSymbols;
    private JMenuItem removeSymbols;
    private JMenuItem graphSymbols;
    private JMenuItem graphIndexSymbols;
    private JMenuItem tableSymbols;
    private JMenuItem tableClose;
    private JMenuItem renameWatchScreen;
    private JMenuItem deleteWatchScreen;
    private JMenuItem applyExpressionsMenuItem;
    private JMenuItem addAlert;

    // Poup menu items
    private JMenuItem popupRemoveSymbols = null;
    private JMenuItem popupGraphSymbols = null;
    private JMenuItem popupTableSymbols = null;

    private PropertyChangeSupport propertySupport;
    private MixedQuoteBundle quoteBundle;

    private WatchScreen watchScreen;
    private MixedQuoteModel model;
    private WatchScreenSettings settings;

    // Frame Icon
    private String frameIcon = "nz/org/venice/images/TableIcon.gif";

    // Set to true if weve deleted this watch screen and shouldn't try
    // to save it when we exit
    private boolean isDeleted = false;

    /**
     * Create a watch screen module.
     *
     * @param watchScreen the watch screen object
     * @param quoteBundle watch screen quotes
     */
    public WatchScreenModule(WatchScreen watchScreen,
                             MixedQuoteBundle quoteBundle) {

        this.watchScreen = watchScreen;
	this.quoteBundle = quoteBundle;

	propertySupport = new PropertyChangeSupport(this);

        model = new MixedQuoteModel(quoteBundle, getQuotes(), Column.HIDDEN, Column.VISIBLE);
	setModel(model, MixedQuoteModel.SYMBOL_COLUMN, SORT_UP);
	showColumns(model);
	addMenu();
	model.addTableModelListener(this);
        resort();

        // If the user clicks on the table trap it.
	addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent evt) {
                    handleMouseClicked(evt);
                }
	    });

        // Update the table on new intra-day quotes
        IDQuoteCache.getInstance().addQuoteListener(new QuoteListener() {
               public void newQuotes(QuoteEvent event) {
		   updateTable();
                }
            });
    }

    // Graph menu item is only enabled when items are selected in the table.
    private void checkMenuDisabledStatus() {
	int numberOfSelectedRows = getSelectedRowCount();

        removeSymbols.setEnabled(numberOfSelectedRows > 0? true : false);
        graphSymbols.setEnabled(numberOfSelectedRows > 0? true : false);
	graphIndexSymbols.setEnabled(numberOfSelectedRows > 0? true : false);
        tableSymbols.setEnabled(numberOfSelectedRows > 0? true : false);
	addAlert.setEnabled(numberOfSelectedRows == 1? true : false);
    }

    // If the user double clicks on a stock with the LMB, graph the stock.
    // If the user right clicks over the table, open up a popup menu.
    private void handleMouseClicked(MouseEvent event) {

        Point point = event.getPoint();

        // Right click on the table - raise menu
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu menu = new JPopupMenu();

            popupGraphSymbols =
                MenuHelper.addMenuItem(this, menu,
                                       Locale.getString("GRAPH"));
            popupGraphSymbols.setEnabled(getSelectedRowCount() > 0);

            popupTableSymbols =
                MenuHelper.addMenuItem(this, menu,
                                       Locale.getString("TABLE"));
            popupTableSymbols.setEnabled(getSelectedRowCount() > 0);

            menu.addSeparator();

            popupRemoveSymbols =
                MenuHelper.addMenuItem(this, menu,
                                       Locale.getString("REMOVE"));
            popupRemoveSymbols.setEnabled(getSelectedRowCount() > 0);

            menu.show(this, point.x, point.y);
        }

        // Left double click on the table - graph stock
        else if(event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {

            // Left double click on the table - graph stock
            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();
            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol
                    = (Symbol)model.getValueAt(selectedRows[i],
                                               MixedQuoteModel.SYMBOL_COLUMN);
                symbols.add(symbol);
            }
            // Graph the highlighted symbols
            CommandManager.getInstance().graphStockBySymbol(symbols);
        }
    }

    // Create a menu
    private void addMenu() {
	menuBar = new JMenuBar();

        // Watch Screen Menu
        {
            JMenu tableMenu = MenuHelper.addMenu(menuBar, Locale.getString("WATCH_SCREEN"));

            // Show columns menu
            tableMenu.add(createShowColumnMenu(model));

            tableMenu.addSeparator();

            applyExpressionsMenuItem = MenuHelper.addMenuItem(this, tableMenu,
                                                              Locale.getString("APPLY_EQUATIONS"));

            tableMenu.addSeparator();

            deleteWatchScreen = MenuHelper.addMenuItem(this, tableMenu,
                                                       Locale.getString("DELETE"));

            renameWatchScreen = MenuHelper.addMenuItem(this, tableMenu,
                                                       Locale.getString("RENAME"));

            tableMenu.addSeparator();

            tableClose = MenuHelper.addMenuItem(this, tableMenu,
						Locale.getString("CLOSE"));
        }

        // Symbol Menu
        {
            JMenu symbolsMenu = MenuHelper.addMenu(menuBar, Locale.getString("SYMBOLS"));

            addSymbols =
                MenuHelper.addMenuItem(this, symbolsMenu,
                                       Locale.getString("ADD"));
            removeSymbols =
                MenuHelper.addMenuItem(this, symbolsMenu,
                                       Locale.getString("REMOVE"));

            symbolsMenu.addSeparator();

            graphSymbols =
                MenuHelper.addMenuItem(this, symbolsMenu,
                                       Locale.getString("GRAPH"));

            graphIndexSymbols =
                MenuHelper.addMenuItem(this, symbolsMenu,
                                       Locale.getString("GRAPH_INDEX"));


            tableSymbols =
                MenuHelper.addMenuItem(this, symbolsMenu,
                                       Locale.getString("TABLE"));

	    addAlert = 
		MenuHelper.addMenuItem(this, symbolsMenu,
				       Locale.getString("ALERT_ADD"));
        }

        // Listen for changes in selection so we can update the menus
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    checkMenuDisabledStatus();
                }

            });

	checkMenuDisabledStatus();
    }

    /**
     * Tell module to save any current state data / preferences data because
     * the window is being closed.
     */
    public void save() {
        // Don't save the watch screen if it was just deleted.
	if(!isDeleted) {
            try {
                PreferencesManager.putWatchScreen(watchScreen);
            }
            catch(PreferencesException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_SAVING_WATCH_SCREEN_TITLE"),
                                                e.getMessage());
            }

	    settings = new WatchScreenSettings(watchScreen.getName());

        }
    }

    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle() {
        return watchScreen.getName();
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
     * Return frame icon for table module.
     *
     * @return	the frame icon.
     */
    public ImageIcon getFrameIcon() {
	return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(frameIcon));
    }

    /**
     * Return displayed component for this module.
     *
     * @return the component to display.
     */
    public JComponent getComponent() {
	return this;
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
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane() {
	return true;
    }

    /**
     * Handle widget events.
     *
     * @param	e	action event
     */
    public void actionPerformed(final ActionEvent e) {

	if(e.getSource() == tableClose) {
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}

        // Graph symbols, either by the popup menu or the main menu
        else if((popupGraphSymbols != null && e.getSource() == popupGraphSymbols) ||
                e.getSource() == graphSymbols || e.getSource() == graphIndexSymbols) {

            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();

            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol = (Symbol)model.getValueAt(selectedRows[i],
                                                         MixedQuoteModel.SYMBOL_COLUMN);

                symbols.add(symbol);
            }

            // Graph the highlighted symbols
	    if (e.getSource() == graphSymbols || e.getSource() == popupGraphSymbols) {
		CommandManager.getInstance().graphStockBySymbol(symbols);
	    }
	    else if (e.getSource() == graphIndexSymbols) {
		CommandManager.getInstance().graphIndexBySymbol(symbols);
	    }
        }

        // Remove symbols, either by the popup menu or the main menu
        else if((popupRemoveSymbols != null && e.getSource() == popupRemoveSymbols) ||
                e.getSource() == removeSymbols) {

            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();

            // Pull out symbols into separate list. We have to do this in two steps
            // because if we start to remove them in this loop the getValueAt()
            // function won't work properly.
            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol = (Symbol)model.getValueAt(selectedRows[i],
                                                         MixedQuoteModel.SYMBOL_COLUMN);
                symbols.add(symbol);
            }

            // Now delete from watch screen
            watchScreen.removeAllSymbols(symbols);

            model.setQuotes(getQuotes());
            model.fireTableDataChanged();
        }

        // Add symbols to watch screen
        else if(e.getSource() == addSymbols)
            addSymbols();

        // Apply expressions to watch screen
        else if(e.getSource() == applyExpressionsMenuItem)
            applyExpressions(model);

        // Delete watch screen
        else if(e.getSource() == deleteWatchScreen)
            deleteWatchScreen();

        // Rename watch screen
        else if(e.getSource() == renameWatchScreen)
            renameWatchScreen();

        // Table symbols, either by the popup menu or the main menu
        else if((popupTableSymbols != null && e.getSource() == popupTableSymbols) ||
                e.getSource() == tableSymbols) {

            int[] selectedRows = getSelectedRows();
            List symbols = new ArrayList();

            for(int i = 0; i < selectedRows.length; i++) {
                Symbol symbol = (Symbol)model.getValueAt(selectedRows[i],
                                                         MixedQuoteModel.SYMBOL_COLUMN);

                symbols.add(symbol);
            }

            // Table the highlighted symbols
            CommandManager.getInstance().tableStocks(symbols);
        }
	else if (e.getSource() == addAlert) {
	    int[] selectedRows = getSelectedRows();

	    assert selectedRows.length == 1;

	    Symbol symbol = (Symbol)model.getValueAt(0,
						     MixedQuoteModel.
						     SYMBOL_COLUMN);
	    addAlert(symbol);
	}
	else
            assert false;
    }

    private void addAlert(final Symbol symbol) {
	
	// Handle action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread showAddDialog = new Thread() {

		public void run() {
		    CommandManager.getInstance().newAlert(symbol);
		}
	    };
	
	showAddDialog.start();

    }

    // Delete this watch screen
    private void deleteWatchScreen() {
	int option =
	    JOptionPane.showInternalConfirmDialog(DesktopManager.getDesktop(),
						  Locale.getString("SURE_DELETE_WATCH_SCREEN"),
						  Locale.getString("DELETE_WATCH_SCREEN"),
						  JOptionPane.YES_NO_OPTION);
	if(option == JOptionPane.YES_OPTION) {
	    PreferencesManager.deleteWatchScreen(watchScreen.getName());

	    MainMenu.getInstance().updateWatchScreenMenu();

	    // Prevent save() function resurrecting watch screen
	    isDeleted = true;

	    // Close window
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}
    }

    // Rename the watch screen
    private void renameWatchScreen() {
	// Handle all action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread thread = new Thread() {

		public void run() {
                    String oldWatchScreenName = watchScreen.getName();

                    // Get new name for watch screen
                    TextDialog dialog = new TextDialog(DesktopManager.getDesktop(),
                                                       Locale.getString("ENTER_NEW_WATCH_SCREEN_NAME"),
                                                       Locale.getString("RENAME_WATCH_SCREEN"),
                                                       oldWatchScreenName);
                    String newWatchScreenName = dialog.showDialog();

                    if(newWatchScreenName != null && newWatchScreenName.length() > 0 &&
                       !newWatchScreenName.equals(oldWatchScreenName)) {

                        // Save the watch screen under the new name
                        watchScreen.setName(newWatchScreenName);
                        try {
                            PreferencesManager.putWatchScreen(watchScreen);
                        }
                        catch(PreferencesException e) {
                            DesktopManager.showErrorMessage(Locale.getString("ERROR_SAVING_WATCH_SCREEN_TITLE"),
                                                            e.getMessage());
                        }

                        // Delete the old watch screen
                        PreferencesManager.deleteWatchScreen(oldWatchScreenName);

                        // Update GUI
                        MainMenu.getInstance().updateWatchScreenMenu();
                        propertySupport.firePropertyChange(ModuleFrame.TITLEBAR_CHANGED_PROPERTY,
                                                           0, 1);
                    }
                }};

        thread.start();
    }

    // Add symbols to the watchscreen
    private void addSymbols() {
	// Handle all action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread thread = new Thread() {
		public void run() {
                    Set symbols = SymbolListDialog.getSymbols(DesktopManager.getDesktop(),
                                                              Locale.getString("ADD_SYMBOLS"));
                    if(symbols != null) {
                        // Add symbols to watch screen, quote sync and table
                        List symbolList = new ArrayList(symbols);

                        watchScreen.addSymbols(symbolList);
                        IDQuoteSync.getInstance().addSymbols(symbolList);
                        model.setQuotes(getQuotes());
                    }
                }};

        thread.start();
    }

    // Using the watch screen object, create a list of quotes that
    // we should display from the quote bundle
    private List getQuotes() {
        List quotes = new ArrayList();
        int dateOffset = quoteBundle.getLastOffset();

        for(Iterator iterator = watchScreen.getSymbols().iterator();
            iterator.hasNext();) {
            Symbol symbol = (Symbol)iterator.next();
            Quote quote;

            try {
                quote = quoteBundle.getQuote(symbol, dateOffset);
            }
            catch(MissingQuoteException e) {
                quote = new IDQuote(symbol, new TradingDate(), new TradingTime(),
                                    0, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            quotes.add(quote);
        }

        return quotes;
    }

    /**
     * This function is called when new intra-day quotes have been downloaded
     * and we should update the table.
     */
    private void updateTable() {
        model.setQuotes(getQuotes());
        model.fireTableDataChanged();
    }

    public Settings getSettings() {
	return settings;
    }
}
