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

package org.mov.main;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import org.mov.analyser.ANNModule;
import org.mov.analyser.ANNResultModule;
import org.mov.analyser.GAModule;
import org.mov.analyser.GAResultModule;
import org.mov.analyser.GPPageInitialPopulation;
import org.mov.analyser.GPModule;
import org.mov.analyser.GPResultModule;
import org.mov.analyser.PaperTradeModule;
import org.mov.analyser.PaperTradeResultModule;
import org.mov.chart.*;
import org.mov.chart.graph.*;
import org.mov.chart.source.*;
import org.mov.help.HelpModule;
import org.mov.util.Currency;
import org.mov.util.Locale;
import org.mov.util.TradingDate;
import org.mov.portfolio.AccountDialog;
import org.mov.portfolio.Portfolio;
import org.mov.portfolio.PortfolioModule;
import org.mov.prefs.PreferencesException;
import org.mov.prefs.PreferencesModule;
import org.mov.prefs.PreferencesManager;
import org.mov.quote.ImportQuoteModule;
import org.mov.quote.ExportQuoteModule;
import org.mov.quote.Quote;
import org.mov.quote.EODQuoteBundle;
import org.mov.quote.EODQuoteRange;
import org.mov.quote.MixedQuoteBundle;
import org.mov.quote.QuoteSourceManager;
import org.mov.quote.Symbol;
import org.mov.quote.IDQuoteSyncModule;
import org.mov.table.PortfolioTableModule;
import org.mov.table.QuoteModule;
import org.mov.table.WatchScreen;
import org.mov.table.WatchScreenModule;
import org.mov.importer.PreferencesXML;
import org.mov.ui.DesktopManager;
import org.mov.ui.ExpressionQuery;
import org.mov.ui.GPLViewDialog;
import org.mov.ui.MainMenu;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.ui.SymbolListDialog;
import org.mov.ui.TextDialog;
import org.mov.ui.TradingDateDialog;

/**
 * This class manages the tasks that can be initiated from menus and toolbars. Each
 * task is launched in a separate thread.
 *
 * @author Dan Makovec
 */
public class CommandManager {

    // Singleton instance of this class
    private static CommandManager instance = null;

    private DesktopManager desktopManager;
    private JDesktopPane desktop;

    // Keep track of dialogs/modules to make sure the user doesn't open
    // two about dialogs, two preferences etc.
    private boolean isAboutDialogUp = false;
    private boolean isLicenseDialogUp = false;
    private JInternalFrame importQuoteModuleFrame = null;
    private JInternalFrame syncIDQuoteModuleFrame = null;
    private JInternalFrame exportQuoteModuleFrame = null;
    private JInternalFrame preferencesModuleFrame = null;

    // Locales for about box translation credits
    private java.util.Locale catalan = new java.util.Locale("CA");
    private java.util.Locale french = new java.util.Locale("FR");
    private java.util.Locale italian = new java.util.Locale("IT");
    private java.util.Locale polish = new java.util.Locale("PL");
    private java.util.Locale swedish = new java.util.Locale("SV");
    private java.util.Locale simplifiedChinese = new java.util.Locale("ZH");    

    // Class should only be constructed once by this class
    private CommandManager() {
        // nothing to do
    }

    /**
     * Return the static CommandManager for this application
     */
    public static CommandManager getInstance() {
	if (instance == null)
	    instance = new CommandManager();

	return instance;
    }

    public DesktopManager getDesktopManager() {
        return desktopManager;
    }

    public void setDesktopManager(DesktopManager desktopManager) {
	this.desktopManager = desktopManager;
	this.desktop = DesktopManager.getDesktop();
    }

    /**
     * Tile all the open internal frames horizontally
     */
    public void tileFramesHorizontal() {
	DesktopManager.tileFrames(DesktopManager.HORIZONTAL);
    }

    /**
     * Tile all the open internal frames vertically
     */
    public void tileFramesVertical() {
	DesktopManager.tileFrames(DesktopManager.VERTICAL);
    }

    /**
     * Arrange all open internal frames in a cascading fashion
     */
    public void tileFramesCascade() {
	DesktopManager.tileFrames(DesktopManager.CASCADE);
    }

    /**
     * Allocate as square a shape as possible to open infternal frames
     */
    public void tileFramesArrange() {
	DesktopManager.tileFrames(DesktopManager.ARRANGE);
    }

    /**
     * Display the transactions to the user, opening portfolio window
     *
     * @param portfolio the portfolio
     * @param quoteBundle fully loaded quote bundle
     */
    public void tableTransactions(final Portfolio portfolio,
                                  final EODQuoteBundle quoteBundle) {
        PortfolioModule porfolioModule = new PortfolioModule(desktop, portfolio, quoteBundle);
        desktopManager.newFrame(porfolioModule);
        porfolioModule.tablePortfolio();
    }

    public void tableStocks(final int type) {
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    String title =
                        new String(Locale.getString("LIST_IT",
						    EODQuoteRange.getDescription(type)));
                    tableStocks(title, type, null, null, null);
                }
            });
        thread.start();
    }

    public void tableStocks(final List symbols) {
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    SortedSet symbolsCopy;
		    String description = EODQuoteRange.getDescription(EODQuoteRange.GIVEN_SYMBOLS);
                    String title =
                        new String(Locale.getString("LIST_IT", description));

                    if(symbols == null)
                        symbolsCopy = SymbolListDialog.getSymbols(desktop, title);

                    else {
                        symbolsCopy = new TreeSet(symbols);

                        for(Iterator iterator = symbolsCopy.iterator(); iterator.hasNext();) {
                            Symbol symbol = (Symbol)iterator.next();

                            if(!QuoteSourceManager.getSource().symbolExists(symbol)) {
                                JOptionPane.showInternalMessageDialog(desktop,
                                                                      Locale.getString("NO_QUOTES_SYMBOL",
                                                                                       symbol.toString()),
                                                                      Locale.getString("INVALID_SYMBOL_LIST"),
                                                                      JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }

                    if(symbolsCopy != null && symbolsCopy.size() > 0)
                        tableStocks(title, EODQuoteRange.GIVEN_SYMBOLS, null, symbolsCopy, null);
                }
            });
        thread.start();
    }

    public void tableStocksByDate(final int type) {
        Thread thread = new Thread(new Runnable() {
                public void run() {
		
                    String title = new String(Locale.getString("LIST_IT_BY_DATE",
							       EODQuoteRange.getDescription(type)));
                    TradingDate date = TradingDateDialog.getDate(desktop,
                                                                 title,
                                                                 Locale.getString("DATE"));
                    if(date != null)
                        tableStocks(title, type, null, null, date);
                }
            });
        thread.start();
    }

    public void tableStocksByRule(final int type) {
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    String title = new String(Locale.getString("LIST_IT_BY_RULE",
							       EODQuoteRange.getDescription(type)));
                    String rule = ExpressionQuery.getExpression(desktop,
                                                                title,
                                                                Locale.getString("RULE"));
                    if(rule != null)
                        tableStocks(title, type, rule, null, null);
                }
            });
        thread.start();
    }

    private void tableStocks(String title, int type, String rule, SortedSet symbols,
                             TradingDate date) {
	Thread thread = Thread.currentThread();
        EODQuoteBundle quoteBundle = null;
        EODQuoteRange quoteRange = null;
        QuoteModule table = null;
        ProgressDialog progressDialog = ProgressDialogManager.getProgressDialog();
        progressDialog.show(title);
        boolean singleDate = false;

        if (!thread.isInterrupted()) {

            if(type == EODQuoteRange.GIVEN_SYMBOLS) {
                quoteRange =
                    new EODQuoteRange(new ArrayList(symbols));
                singleDate = false;
            }
            else {
                // If this fails it'll throw a thread interupted to cancel the operation
                // If we were given a date use that, otherwise use the latest date
                // available. Load the last two dates - we need yesterday's quotes to
                // calculate each stocks percent change.
                if(date == null)
                    date = QuoteSourceManager.getSource().getLastDate();

                if(!thread.isInterrupted()) {
                    // If we couldn't load a date, the quote source will have interrupted
                    // the thead. So this shouldn't be null here.
                    assert date != null;

                    quoteRange = new EODQuoteRange(type, date.previous(1), date);
                }

                singleDate = true;
            }
        }

        if (!thread.isInterrupted())
            quoteBundle = new EODQuoteBundle(quoteRange);

        if (!thread.isInterrupted()) {
            table = new QuoteModule(quoteBundle, rule, singleDate);
            desktopManager.newFrame(table);
        }

        ProgressDialogManager.closeProgressDialog(progressDialog);
    }

    /**
     * Display the portfolio with the given name to the user.
     *
     * @param	portfolioName	name of portfolio to display
     */
    public void openPortfolio(final String portfolioName) {
        final Thread thread = new Thread(new Runnable() {

            public void run() {
                Thread thread = Thread.currentThread();
                ProgressDialog progress = ProgressDialogManager.getProgressDialog();

                progress.show(Locale.getString("OPEN_PORTFOLIO", portfolioName));

                try {
                    Portfolio portfolio = PreferencesManager.getPortfolio(portfolioName);
                    EODQuoteBundle quoteBundle = null;
                    TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();
                    
                    if(lastDate != null) {
                        if(!thread.isInterrupted()) {
                            EODQuoteRange quoteRange =
                                new EODQuoteRange(portfolio.getStocksHeld(), lastDate.previous(1),
                                                  lastDate);
                            
                            quoteBundle = new EODQuoteBundle(quoteRange);
                        }
                        
                        if(!thread.isInterrupted())
                            openPortfolio(portfolio, quoteBundle);
                    }
                }
                catch(PreferencesException e) {
                    DesktopManager.showErrorMessage(Locale.getString("ERROR_LOADING_PORTFOLIO_TITLE"),
                                                    e.getMessage());
                }

                ProgressDialogManager.closeProgressDialog(progress);
            }
            });

        thread.start();
    }

    /**
     * Display the portfolio to the user
     *
     * @param portfolio the portfolio
     */
    public void openPortfolio(final Portfolio portfolio) {
        final Thread thread = new Thread(new Runnable() {

            public void run() {
                Thread thread = Thread.currentThread();
                ProgressDialog progress = ProgressDialogManager.getProgressDialog();

                progress.show(Locale.getString("OPEN_PORTFOLIO", portfolio.getName()));

                EODQuoteBundle quoteBundle = null;
                TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();
                
                if(lastDate != null) {
                    if(!thread.isInterrupted()) {
                        EODQuoteRange quoteRange =
                            new EODQuoteRange(portfolio.getStocksHeld(), lastDate.previous(1),
                                              lastDate);

                        quoteBundle = new EODQuoteBundle(quoteRange);
                    }

                    if(!thread.isInterrupted())
                        openPortfolio(portfolio, quoteBundle);
                }

                ProgressDialogManager.closeProgressDialog(progress);
            }
            });

        thread.start();
    }

    /**
     * Display the portfolio to the user
     *
     * @param portfolio the portfolio
     * @param quoteBundle fully loaded quote bundle
     */
    public void openPortfolio(final Portfolio portfolio,
                              final EODQuoteBundle quoteBundle) {
        desktopManager.newFrame(new PortfolioModule(desktop,
                                                    portfolio, quoteBundle));
    }

    /**
     * Open up a new paper trade module.
     */
    public void paperTrade() {
	PaperTradeModule module = new PaperTradeModule(desktop);
	desktopManager.newFrame(module, true, true, true);
    }

    /**
     * Open up a new genetic programming module.
     */
    public void gp() {
	GPModule module = new GPModule(desktop);
	desktopManager.newFrame(module, true, true, true);
    }

    /**
     * Open up a new genetic algorithm module.
     */
    public void ga() {
	GAModule module = new GAModule(desktop);
	desktopManager.newFrame(module, true, true, true);
    }

    /**
     * Open up a new artificial neural network module.
     */
    public void ann() {
	ANNModule module = new ANNModule(desktop);
	desktopManager.newFrame(module, true, true, true);
    }

    /**
     * Open up a result table that will display a summary of results
     * from paper trading.
     *
     * @return	frame containing paper trade result module
     */
    public ModuleFrame newPaperTradeResultTable() {	
	PaperTradeResultModule results = new PaperTradeResultModule();
	return desktopManager.newFrame(results);	
    }

    /**
     * Open up a result table that will display a summary of results
     * from genetic programming.
     *
     * @return	frame containing genetic programmes
     */
    public ModuleFrame newGPResultTable(GPPageInitialPopulation  GPPageInitialPopulation) {
	GPResultModule results = new GPResultModule(GPPageInitialPopulation);
	return desktopManager.newFrame(results);
    }

    /**
     * Open up a result table that will display a summary of results
     * from genetic algorithm.
     *
     * @return	frame containing genetic algorithm results
     */
    public ModuleFrame newGAResultTable() {
	GAResultModule results = new GAResultModule();
	return desktopManager.newFrame(results);
    }

    /**
     * Open up a result table that will display a summary of results
     * from artificial neural network.
     *
     * @return	frame containing artificial neural network results
     */
    public ModuleFrame newANNResultTable() {
	ANNResultModule results = new ANNResultModule();
	return desktopManager.newFrame(results);
    }

    /**
     * Open up a dialog to create and then display a new watch screen.
     */
    public void newWatchScreen() {
	// Get name for watch screen
	TextDialog dialog = new TextDialog(desktop,
					   Locale.getString("ENTER_WATCH_SCREEN_NAME"),
					   Locale.getString("NEW_WATCH_SCREEN"));
	String watchScreenName = dialog.showDialog();
	
        if(watchScreenName != null && watchScreenName.length() > 0) {
            WatchScreen watchScreen = new WatchScreen(watchScreenName);

	    // Save watch screen so we can update the menu
	    PreferencesManager.putWatchScreen(watchScreen);
	    MainMenu.getInstance().updateWatchScreenMenu();
	
	    // Open as normal
            openWatchScreen(watchScreen);
	}
    }

    /**
     * Display the watch screen to the user
     *
     * @param watchScreenName the name of the watch screen
     */
    public void openWatchScreen(String watchScreenName) {
        WatchScreen watchScreen = PreferencesManager.getWatchScreen(watchScreenName);
        openWatchScreen(watchScreen);
    }

    /**
     * Display the watch screen to the user
     *
     * @param watchScreen the watch screen
     */
    public void openWatchScreen(final WatchScreen watchScreen) {
        final Thread thread = new Thread(new Runnable() {

            public void run() {
                Thread thread = Thread.currentThread();
                ProgressDialog progress = ProgressDialogManager.getProgressDialog();

                progress.show(Locale.getString("OPEN_WATCH_SCREEN", watchScreen.getName()));

                MixedQuoteBundle quoteBundle = null;
                TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();

                if(lastDate != null) {
                    if(!thread.isInterrupted())
                        quoteBundle = new MixedQuoteBundle(watchScreen.getSymbols(),
                                                           lastDate.previous(1),
                                                           lastDate);

                    if(!thread.isInterrupted())
                        desktopManager.newFrame(new WatchScreenModule(watchScreen,
                                                                      quoteBundle));
                }

                ProgressDialogManager.closeProgressDialog(progress);
            }
            });

        thread.start();
    }

    /**
     * Opens up an instance of the preferences module at the last visited page.
     */
    public void openPreferences() {
	// Only allow one copy of the preferences module to be displayed
	synchronized(this) {
	    if(!wakeIfPresent(preferencesModuleFrame)) {
		PreferencesModule preferencesModule = new PreferencesModule(desktop);

		preferencesModuleFrame =
                   desktopManager.newFrame(preferencesModule, true, false, true);
	    }
	}
    }

    /**
     * Opens up an instance of the preferences module at the given page.
     *
     * @param page the preference page to view.
     */
    public void openPreferences(int page ) {
	// Only allow one copy of the preferences module to be displayed
	synchronized(this) {
	    if(!wakeIfPresent(preferencesModuleFrame)) {
		PreferencesModule preferencesModule = new PreferencesModule(desktop, page);

		preferencesModuleFrame =
                   desktopManager.newFrame(preferencesModule, true, false, true);
	    }
	}
    }

    /**
     * Open up a dialog to create and then display a new portfolio.
     */
    public void newPortfolio() {
	// Get name for portfolio
	AccountDialog dialog = new AccountDialog(desktop,
                                                 Locale.getString("ENTER_PORTFOLIO_NAME"),
                                                 Locale.getString("NEW_PORTFOLIO"));

        if(dialog.showDialog()) {
            String portfolioName = dialog.getAccountName();
            Currency portfolioCurrency = dialog.getAccountCurrency();
	    Portfolio portfolio = new Portfolio(portfolioName, portfolioCurrency);
	
	    // Save portfolio so we can update the menu
            try {
                PreferencesManager.putPortfolio(portfolio);
                MainMenu.getInstance().updatePortfolioMenu();
                openPortfolio(portfolio);
            }
            catch(PreferencesException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_SAVING_PORTFOLIO_TITLE"),
                                                e.getMessage());
            }
	}
    }

    /**
     * Graph the given portfolio.
     *
     * @param portfolioName the name of the portfolio to graph
     */
    public void graphPortfolio(String portfolioName) {

        try {
            Portfolio portfolio =
                PreferencesManager.getPortfolio(portfolioName);

            graphPortfolio(portfolio);
        }
        catch(PreferencesException e) {
            DesktopManager.showErrorMessage(Locale.getString("ERROR_LOADING_PORTFOLIO_TITLE"),
                                            e.getMessage());
        }
    }

    /**
     * Graph the given portfolio.
     *
     * @param	portfolio	the portfolio to graph
     */
    public void graphPortfolio(Portfolio portfolio) {

	// Set the start and end dates to null - the other graph
	// function will determine appropriate start and end dates
	graphPortfolio(portfolio, null, null, null);
    }

    /**
     * Graph the given portfolio.
     *
     * @param portfolio the portfolio
     * @param quoteBundle fully loaded quote bundle
     */
    public void graphPortfolio(Portfolio portfolio,
                               EODQuoteBundle quoteBundle) {
        graphPortfolio(portfolio, quoteBundle, null, null);
    }

    /**
     * Graph the given portfolio inbetween the given dates.
     *
     * @param	portfolio	the portfolio to graph
     * @param	quoteBundle		quote bundle
     * @param	startDate	date to graph from
     * @param	endDate		date to graph to
     */
    public void graphPortfolio(Portfolio portfolio,
			       EODQuoteBundle quoteBundle,
			       TradingDate startDate,
			       TradingDate endDate) {

        ChartModule chart = new ChartModule(desktop);

	Thread thread = Thread.currentThread();
	ProgressDialog progress = ProgressDialogManager.getProgressDialog();

        progress.show(Locale.getString("GRAPH_PORTFOLIO", portfolio.getName()));

        List symbolsTraded = portfolio.getSymbolsTraded();
        PortfolioGraphSource portfolioGraphSource = null;
        Graph graph = null;

        // If the portfolio has traded symbols and the caller has not supplied a
        // quote bundle, then load one now.
        if (symbolsTraded.size() > 0 && quoteBundle == null) {
            // Get default start and end date if not supplied
            if(startDate == null)
                startDate = portfolio.getStartDate();

            if(endDate == null) {
                endDate = QuoteSourceManager.getSource().getLastDate();		

                // Make sure the end date is after the start date! Otherwise the code
                // will assert later.
                if (endDate.before(startDate))
                    endDate = startDate;
            }

            quoteBundle = new EODQuoteBundle(new EODQuoteRange(symbolsTraded, startDate,
                                                               endDate));
        }

        // If the portfolio hasn't traded symbols then there is nothing to
        // graph
        if (symbolsTraded.size() == 0)
            DesktopManager.showErrorMessage(Locale.getString("NOTHING_TO_GRAPH"));

        else if (!thread.isInterrupted()) {
            portfolioGraphSource =
                new PortfolioGraphSource(portfolio, quoteBundle,
                                         PortfolioGraphSource.MARKET_VALUE);
	    graph = new LineGraph(portfolioGraphSource,
                                  Locale.getString("MARKET_VALUE"),
                                  true);
	    
            chart.add(graph, portfolio, quoteBundle, 0);
            chart.redraw();
            desktopManager.newFrame(chart);
        }

        ProgressDialogManager.closeProgressDialog(progress);
    }

    public void tablePortfolio(String portfolioName) {
        try {
            Portfolio portfolio =
                PreferencesManager.getPortfolio(portfolioName);

            tablePortfolio(portfolio);
        }
        catch(PreferencesException e) {
            DesktopManager.showErrorMessage(Locale.getString("ERROR_LOADING_PORTFOLIO_TITLE"),
                                            e.getMessage());
        }
    }

    public void tablePortfolio(Portfolio portfolio) {
        tablePortfolio(portfolio, null, null, null);
    }

    public void tablePortfolio(Portfolio portfolio, EODQuoteBundle quoteBundle) {
        tablePortfolio(portfolio, quoteBundle, null, null);
    }

    public void tablePortfolio(Portfolio portfolio,
                               EODQuoteBundle quoteBundle,
                               TradingDate startDate,
                               TradingDate endDate) {

	Thread thread = Thread.currentThread();
	ProgressDialog progress = ProgressDialogManager.getProgressDialog();

        progress.show(Locale.getString("TABLE_PORTFOLIO", portfolio.getName()));

        List symbolsTraded = portfolio.getSymbolsTraded();

        // If the portfolio has traded symbols and the caller has not supplied a
        // quote bundle, then load one now.
        if (symbolsTraded.size() > 0 && quoteBundle == null) {
            // Get default start and end date if not supplied
            if(startDate == null)
                startDate = portfolio.getStartDate();

            if(endDate == null) {
                endDate = QuoteSourceManager.getSource().getLastDate();		

                // Make sure the end date is after the start date! Otherwise the code
                // will assert later.
                if (endDate.before(startDate))
                    endDate = startDate;
            }

            quoteBundle = new EODQuoteBundle(new EODQuoteRange(symbolsTraded, startDate,
                                                               endDate));
        }

        if (!thread.isInterrupted()) {
            PortfolioTableModule table = new PortfolioTableModule(portfolio, quoteBundle);
            desktopManager.newFrame(table);
        }

        ProgressDialogManager.closeProgressDialog(progress);
    }

    /**
     * Graph the advance/decline market indicator
     */
    public void graphAdvanceDecline() {

        final Thread thread = new Thread(new Runnable() {

            public void run() {
                Thread thread = Thread.currentThread();
                Graph graph = new AdvanceDeclineGraph();

                if (!thread.isInterrupted()) {
                    ChartModule chart = new ChartModule(desktop);
                    chart.addMarketIndicator(graph);
                    chart.redraw();

                    desktopManager.newFrame(chart);
                }
	    }
	    });

	thread.start();
    }

    /**
     * Displays a graph closing prices for stock(s), based on their code.
     * The stock(s) is/are determined by a user prompt if a set of symbols
     * is not supplied.
     *
     * @param	symbols	Optional. Set of symbols to graph.
     */
    public void graphStockBySymbol(final java.util.List symbols) {

        final Thread thread = new Thread(new Runnable() {
                public void run() {
                    SortedSet symbolsCopy;

                    if(symbols == null)
                        symbolsCopy =
			    SymbolListDialog.getSymbols(desktop,
							Locale.getString("GRAPH_BY_SYMBOLS"));
                    else {
                        // If we were given the list of symbols - then check each one exists
                        // before trying to graph it. Abort if any are not found.
                        symbolsCopy = new TreeSet(symbols);

                        for(Iterator iterator = symbolsCopy.iterator(); iterator.hasNext();) {
                            Symbol symbol = (Symbol)iterator.next();

                            if(!QuoteSourceManager.getSource().symbolExists(symbol)) {
                                JOptionPane.showInternalMessageDialog(desktop,
                                                                      Locale.getString("NO_QUOTES_SYMBOL",
                                                                                       symbol.toString()),
                                                                      Locale.getString("INVALID_SYMBOL_LIST"),
                                                                      JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }

                    graphStock(symbolsCopy);
                }
            });
        thread.start();
    }

    /**
     * Displays a graph closing prices for stock(s), based on their name.
     *  The stock(s) is/are determined by a user prompt
     */
    /*
    public void graphStockByName() {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                SortedSet s = SymbolListDialog.getSymbolByName(desktop,
							       "Graph by name");
                graphStock(s);
            }
        });
        thread.start();
        }*/


    /**
     * Displays a graph of closing prices for an index, based on a list of symbols.
     * The stock(s) is/are determined by a user prompt if a set of symbols
     * is not supplied.
     *
     * @param	symbols	Optional. Set of symbols to graph.
     */
    public void graphIndexBySymbol(final java.util.List symbols) {

        final Thread thread = new Thread(new Runnable() {
                public void run() {
                    SortedSet symbolsCopy;

                    if(symbols == null)
                        symbolsCopy =
			    SymbolListDialog.getSymbols(desktop,
							Locale.getString("GRAPH_BY_SYMBOLS"));
                    else {
                        // If we were given the list of symbols - then check each one exists
                        // before trying to graph it. Abort if any are not found.
                        symbolsCopy = new TreeSet(symbols);

                        for(Iterator iterator = symbolsCopy.iterator(); iterator.hasNext();) {
                            Symbol symbol = (Symbol)iterator.next();

                            if(!QuoteSourceManager.getSource().symbolExists(symbol)) {
                                JOptionPane.showInternalMessageDialog(desktop,
                                                                      Locale.getString("NO_QUOTES_SYMBOL",
                                                                                       symbol.toString()),
                                                                      Locale.getString("INVALID_SYMBOL_LIST"),
                                                                      JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }

                    graphIndex(symbolsCopy);
                }
            });
        thread.start();
    }

    /**
     * Internal function for generic setup of graph modules
     *
     * @param companySet the list of stock symbols to graph
     */
    private void graphStock(SortedSet symbols) {

        if(symbols != null) {
            ChartModule chart = new ChartModule(desktop);
            Thread thread = Thread.currentThread();
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();

            Iterator iterator = symbols.iterator();
            EODQuoteBundle quoteBundle = null;
            
	    
            Graph graph = null;

            String title = symbols.toString();
            title = title.substring(1, title.length() - 1);

            int progressValue = 0;

            if(symbols.size() > 1) {
                progress.setIndeterminate(false);
                progress.setMaximum(symbols.size());
                progress.setMaster(true);
            }
            else
                progress.setIndeterminate(true);

            progress.show(Locale.getString("GRAPH_SYMBOLS", title));

            while(iterator.hasNext() && !thread.isInterrupted()) {
                Symbol symbol = (Symbol)iterator.next();

                quoteBundle = new EODQuoteBundle(new EODQuoteRange(symbol));

                if(thread.isInterrupted())
                    break;

		graph = getNewGraph(quoteBundle, false);
		
                chart.add(graph, symbol, quoteBundle, 0);
                chart.redraw();

                if(symbols.size() > 1)
                    progress.increment();
            }

            if (!thread.isInterrupted())
                desktopManager.newFrame(chart);

            ProgressDialogManager.closeProgressDialog(progress);
        }
    }

    /**
     * Internal function for generic setup of index graph modules
     *
     * @param companySet the list of stock symbols to graph
     */
    private void graphIndex(SortedSet symbols) {

        if(symbols != null) {
            ChartModule chart = new ChartModule(desktop,true);
            Thread thread = Thread.currentThread();
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();
            Iterator iterator = symbols.iterator();
            EODQuoteBundle quoteBundle = null;
            GraphSource dayClose = null;
            Graph graph = null;

            String title = symbols.toString();
            title = title.substring(1, title.length() - 1);

            int progressValue = 0;

            if(symbols.size() > 1) {
                progress.setIndeterminate(false);
                progress.setMaximum(symbols.size());
                progress.setMaster(true);
            }
            else
                progress.setIndeterminate(true);

            progress.show(Locale.getString("GRAPH_SYMBOLS", title));

	    quoteBundle = new EODQuoteBundle(new EODQuoteRange(symbols));

	    graph = getNewGraph(quoteBundle, true);

	    /* Has data is aggregate of all symbols, the actual
	       symbol used to mark the chart is irrelevant. 
	    */
	    Symbol symbol = (Symbol)iterator.next();
	    chart.add(graph, symbol, quoteBundle, 0);
	    
	    chart.redraw();
	
	    if(symbols.size() > 1)
		progress.increment();
	

	    if (!thread.isInterrupted())
		desktopManager.newFrame(chart);
	
	
	    ProgressDialogManager.closeProgressDialog(progress);
	}

    }

    /**
     * Opens the about dialog box.
     */
    public void openAboutDialog() {
        if(!isAboutDialogUp) {
            isAboutDialogUp = true;
            String aboutMessage = (Locale.getString("VENICE_LONG") + ", " +
				   Main.LONG_VERSION + " / " +
                                   Main.RELEASE_DATE + "\n" +

				   Locale.getString("COPYRIGHT",
                                                    Main.COPYRIGHT_DATE_RANGE) + ", " +
				   "Andrew Leppard\n\n" +

                                   "Andrew Leppard (aleppard@picknowl.com.au)\n\n" +
				
				   Locale.getString("ADDITIONAL_CODE") + "\n" +
                                   "Daniel Makovec, Quentin Bossard, Peter Fradley, Mark Hummel,\n" +
                                   "Bryan Lin, Alberto Nacher, Matthias St\366ckel &\n" +
                                   "Dennis van den Berg.\n\n" +

                                   Locale.getString("TRANSLATORS") + "\n" +
                                   "Benedict P. Barszcz (" + polish.getDisplayName() + "), " +
                                   "Quentin Bossard (" + french.getDisplayName() + "),\n" +
                                   "Bryan Lin (" + simplifiedChinese.getDisplayName() + "), " +
                                   "Alberto Nacher (" + italian.getDisplayName() + "),\n" +
                                   "Jordi Pujol (" + catalan.getDisplayName() + ") & " +
				   "Pontus Str\366mdahl (" + swedish.getDisplayName() + ")"
				   );

	    String aboutVenice = Locale.getString("ABOUT_VENICE",
						  Locale.getString("VENICE_SHORT"));
	    JOptionPane.showInternalMessageDialog(desktop, aboutMessage, aboutVenice,
                                                  JOptionPane.PLAIN_MESSAGE);
            isAboutDialogUp = false;
        }
    }

    /**
     * Display a dialog to the user showing Venice's license.
     */
    public void openLicenseDialog() {
        if(!isLicenseDialogUp) {
            isLicenseDialogUp = true;
            GPLViewDialog.showGPLViewDialog();
            isLicenseDialogUp = false;
        }
    }

    /**
     * Opens the help module at the default page.
     */
    public void openHelp() {
	// Let the user open multiple instances of help if they wish. This
	// enables them to have multiple pages open and doesn't affect
	// correctness.
        HelpModule helpModule = new HelpModule(desktop);

        desktopManager.newFrame(helpModule, false, false, true);
    }

    /**
     * Displays an import file dialog that allows the user to import
     * preferences into the application from an XML file.
     */
    public void importPreferences() {
	PreferencesXML prefs = new PreferencesXML(desktop);
        prefs.importPreferences();
    }

    /**
     * Displays an import file dialog that allows the user to export
     * preferences from the application into an XML file.
     */
    public void exportPreferences() {
	PreferencesXML prefs = new PreferencesXML(desktop);
        prefs.exportPreferences();
    }

    /**
     * Displays the import quotes modules that allows the user to import
     * quotes into the application.
     */
    public void importQuotes() {
	// Only allow one copy of the import module to be displayed.
	synchronized(this) {
	    if(!wakeIfPresent(importQuoteModuleFrame)) {
                ImportQuoteModule importQuoteModule = new ImportQuoteModule(desktop);
		importQuoteModuleFrame = desktopManager.newFrame(importQuoteModule, true, true, false);
	    }
	}
    }
		
    /**
     * Displays the intra-day quote quote sync module that allows the user
     * to automatically download new intra-day quotes into the application.
     */
    public void syncIDQuotes() {
        // Only allow one copy of the sync module to be displayed
        synchronized(this) {
            if(!wakeIfPresent(syncIDQuoteModuleFrame)) {
                IDQuoteSyncModule module = new IDQuoteSyncModule(desktop);
                syncIDQuoteModuleFrame = desktopManager.newFrame(module, true, true, false);
            }
        }
    }

    /**
     * Displays the export quotes modules that allows the user to export
     * quotes from the application.
     */
    public void exportQuotes() {
	// Only allow one copy of the export module to be displayed.
	synchronized(this) {
	    if(!wakeIfPresent(exportQuoteModuleFrame)) {
                ExportQuoteModule exportQuoteModule = new ExportQuoteModule(desktop);
		exportQuoteModuleFrame = desktopManager.newFrame(exportQuoteModule, true, true, false);
	    }
	}
    }

    /**
     * Checks to see if the current frame is open. If so it will make sure the
     * frame is visible and move it to the front of the screen. This function
     * has too purposes: (1) To re-use previously created frames (2) To prevent
     * multiple instances of the frames being displayed.
     *
     * @param frame the frame to check (may be null)
     * @returns <code>TRUE</code> if the frame is now displayed; <code>FALSE</code> otherwise
     */
    private boolean wakeIfPresent(JInternalFrame frame) {
	// If we have already opened the frame, and it hasn't been closed
	// then move it to the front, deiconify it and select it.
	if(frame != null && !frame.isClosed()) {
	    frame.toFront();

	    try {
		frame.setIcon(false);
		frame.setSelected(true);
	    }
	    catch(PropertyVetoException e) {
		// No frame should veto this action.
		assert false;
	    }

	    return true;
	}	

	return false;
    }

    /*
      Return a new graph according to the default. 
    */
    private Graph getNewGraph(EODQuoteBundle quoteBundle, boolean index) {
	GraphSource dayOpen = null, dayClose = null, dayHigh = null, dayLow = null;
	
	String defaultChart = PreferencesManager.getDefaultChart();
	Graph graph;	
	

	// This would be nicer as a set of Ternary ops	   	 
	if (index) {	
	    dayOpen =
		new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_OPEN);
	    dayClose =
		new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_CLOSE);
	    dayHigh =
		new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_HIGH);
	    dayLow =
		new OHLCVIndexQuoteGraphSource(quoteBundle, Quote.DAY_LOW);
	} else {
	    dayOpen =
		new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_OPEN);
	    dayClose =
		new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_CLOSE);
	    dayHigh =
		new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_HIGH);
	    dayLow =
		new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_LOW);
	}
	

	if (defaultChart.compareTo("BAR_CHART") == 0) {		    
	    graph = new BarChartGraph(dayOpen, dayLow, 
				      dayHigh, dayClose);	    
	} else if (defaultChart.compareTo("CANDLE_STICK") == 0) {
	    graph = new CandleStickGraph(dayOpen, dayLow,
					 dayHigh, dayClose);
	} else if (defaultChart.compareTo("HIGH_LOW_BAR") == 0) {
	    graph = new HighLowBarGraph(dayLow, dayHigh, dayClose);
	} else if (defaultChart.compareTo("POINT_AND_FIGURE") == 0) {
	    graph = new PointAndFigureGraph(dayClose);
	} else {
	    graph = new LineGraph(dayClose, Locale.getString("DAY_CLOSE"), true);
	}
	return graph;
    }        
    
}
