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

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.util.*;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import org.mov.analyser.*;
import org.mov.chart.*;
import org.mov.chart.graph.*;
import org.mov.chart.source.*;
import org.mov.help.*;
import org.mov.util.*;
import org.mov.parser.Expression;
import org.mov.portfolio.*;
import org.mov.prefs.*;
import org.mov.quote.*;
import org.mov.table.QuoteModule;
import org.mov.importer.ImporterModule;
import org.mov.ui.*;

/**
 * This class manages the tasks that can be initiated from menus and toolbars. Each
 * task is launched in a separate thread. */
public class CommandManager {

    // Singleton instance of this class
    private static CommandManager instance = null;

    // The desktop that any window operations will be performed on
    private JDesktopPane desktop;

    // Is the about dialog showing?
    private boolean isAboutDialogUp;

    // Class should only be constructed once by this class
    private CommandManager() {
        isAboutDialogUp = false;
    }

    /**
     * Return the static CommandManager for this application
     */
    public static CommandManager getInstance() {
	if (instance == null)
	    instance = new CommandManager();

	return instance;
    }

    /**
     * Sets the desktop that any window operations will be performed on
     *
     * @param desktop The desktop that any window operations will be performed on
     */
    public void setDesktop(JDesktopPane desktop) {
	this.desktop = desktop;
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
     *  Display an internal frame, listing all the stocks by company name.
     */
    public void quoteListCompanyNamesAll() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Thread thread = Thread.currentThread();

                ProgressDialog p = ProgressDialogManager.getProgressDialog();
                p.show("List All Ordinaries");
                displayStockList(QuoteRange.ALL_ORDINARIES, null);
                ProgressDialogManager.closeProgressDialog(p);
            }
        });
        thread.start();
    }

    /**
     * Display an internal frame, listing stocks by company name, matching a rule that is to
     * be input by the user
     */
    public void quoteListCompanyNamesByRule() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String expr = ExpressionQuery.getExpression(desktop,
                                                            "List All Ordinaries",
                                                            "By Rule");
                if(expr != null) {
                    ProgressDialog p =
                        ProgressDialogManager.getProgressDialog();
                    p.show("List All Ordinaries by rule \""+expr+"\"");

                    displayStockList(QuoteRange.ALL_ORDINARIES, expr);

                    ProgressDialogManager.closeProgressDialog(p);
                }
            }
            });
        thread.start();
    }

    /**
     * Display an internal frame, listing all the stocks by symbol.
     */
    public void quoteListCommoditiesAll() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ProgressDialog p = ProgressDialogManager.getProgressDialog();
                p.show("List All Symbols");

                displayStockList(QuoteRange.ALL_SYMBOLS, null);

                ProgressDialogManager.closeProgressDialog(p);
            }
            });
        thread.start();
    }

    /**
     * Display an internal frame, listing stocks by symbol, matching a rule that is to be
     * input by the user.
     */
    public void quoteListCommoditiesByRule() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String expr = ExpressionQuery.getExpression(desktop,
                                                            "List All Symbols",
                                                            "By Rule");
                ProgressDialog p = ProgressDialogManager.getProgressDialog();
                p.show("List All Symbols by rule \""+expr+"\"");

                displayStockList(QuoteRange.ALL_SYMBOLS,expr);
                ProgressDialogManager.closeProgressDialog(p);
            }
            });
        thread.start();
    }

    /**
     * Display an internal frame, listing all the indices by symbol.
     */
    public void quoteListIndicesAll() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ProgressDialog p = ProgressDialogManager.getProgressDialog();
                p.show("List Market Indices");

                displayStockList(QuoteRange.MARKET_INDICES, null);
                ProgressDialogManager.closeProgressDialog(p);
            }
            });
        thread.start();
    }

    /**
     * Display an internal frame, listing indices by symbol, matching a rule that is to be
     * input by the user.
     */
    public void quoteListIndicesByRule() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String expr = ExpressionQuery.getExpression(desktop,
                                                            "List Market Indices",
                                                            "By Rule");

                ProgressDialog p = ProgressDialogManager.getProgressDialog();
                p.show("List Market Indices by rule \""+expr+"\"");

                displayStockList(QuoteRange.MARKET_INDICES, expr);
            }
            });
        thread.start();
    }

    /**
     * Internal function for retrieving the required data displaying a table showing the results
     *
     * @param searchRestriction as defined by QuoteSource
     * @param expression as defined by Expression
     * @see org.mov.quote.QouteSource
     */
    private void displayStockList(int searchRestriction,
				  String expression) {

	Thread thread = Thread.currentThread();
        ScriptQuoteBundle quoteBundle = null;
        QuoteModule table = null;

        // If this fails it'll throw a thread interupted to cancel the operation
        TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();

        if (!thread.isInterrupted()) {
            QuoteRange quoteRange =
                new QuoteRange(searchRestriction, lastDate);
            quoteBundle = new ScriptQuoteBundle(quoteRange);
        }

        if (!thread.isInterrupted()) {
            table = new QuoteModule(quoteBundle, expression);
            getDesktopManager().newFrame(table);
        }
    }

    /**
     * Display the portfolio with the given name to the user.
     *
     * @param	portfolioName	name of portfolio to display
     */
    public void openPortfolio(String portfolioName) {

        // We don't run this in a new thread because we call openPortfolio(portfolio)
        // which will open a new thread for us.
        Portfolio portfolio =
            PreferencesManager.loadPortfolio(portfolioName);

        openPortfolio(portfolio);
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

                progress.show("Open " + portfolio.getName());

                QuoteBundle quoteBundle = null;

                if (!thread.isInterrupted()) {
                    QuoteRange quoteRange =
                        new QuoteRange(QuoteRange.ALL_SYMBOLS,
                                       QuoteSourceManager.getSource().getLastDate());
                    quoteBundle = new QuoteBundle(quoteRange);
                }
                if (!thread.isInterrupted()) {
                    getDesktopManager().newFrame(new PortfolioModule(desktop,
                                                                     portfolio, quoteBundle));
                }

                ProgressDialogManager.closeProgressDialog(progress);
            }
            });

        thread.start();
    }

    /**
     * Open up a new paper trade module.
     */
    public void paperTrade() {
	PaperTradeModule paperTrade = new PaperTradeModule(desktop);

	getDesktopManager().newFrame(paperTrade, true, true);
    }

    /**
     * Open up a result table that will display a summary of results
     * from paper trading.
     *
     * @return	frame containing paper trade result module
     */
    public ModuleFrame newPaperTradeResultTable() {
	
	PaperTradeResultModule results =
	    new PaperTradeResultModule();

	return getDesktopManager().newFrame(results);	
    }

    /**
     * Open up a dialog to create and then display a new portfolio.
     */
    public void newPortfolio() {
	// Get name for portfolio
	TextDialog dialog = new TextDialog(desktop,
					   "Enter portfolio name",
					   "New Portfolio");
	String portfolioName = dialog.showDialog();

	if(portfolioName != null && portfolioName.length() > 0) {
	    Portfolio portfolio = new Portfolio(portfolioName);
	
	    // Save portfolio so we can update the menu
	    PreferencesManager.savePortfolio(portfolio);
	    MainMenu.getInstance().updatePortfolioMenu();
	
	    // Open as normal
	    openPortfolio(portfolioName);
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
     * Graph the given portfolio inbetween the given dates.
     *
     * @param	portfolio	the portfolio to graph
     * @param	quoteBundle		quote bundle
     * @param	startDate	date to graph from
     * @param	endDate		date to graph to
     */
    public void graphPortfolio(Portfolio portfolio,
			       QuoteBundle quoteBundle,
			       TradingDate startDate,
			       TradingDate endDate) {

        ChartModule chart = new ChartModule(desktop);

	Thread thread = Thread.currentThread();
	ProgressDialog progress = ProgressDialogManager.getProgressDialog();

        progress.show("Graph " + portfolio.getName());

        PortfolioGraphSource portfolioGraphSource = null;
        Graph graph = null;

        // Get default start and end date if not supplied
        if(startDate == null)
            startDate = portfolio.getStartDate();

        if(endDate == null)
            endDate = QuoteSourceManager.getSource().getLastDate();		
        Vector symbols = portfolio.getSymbolsTraded();

        // Only need to load from quote bundle if there are any stocks
        // in the portfolio
        if(quoteBundle == null && symbols.size() > 0) {
            quoteBundle = new QuoteBundle(new QuoteRange(symbols, startDate, endDate));
        }

        if (!thread.isInterrupted()) {
            portfolioGraphSource =
                new PortfolioGraphSource(portfolio, quoteBundle,
                                         PortfolioGraphSource.MARKET_VALUE);
            graph = new LineGraph(portfolioGraphSource);
            chart.add(graph, portfolio, quoteBundle, 0);
            chart.redraw();
            getDesktopManager().newFrame(chart);
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
                    chart.add(graph, null, 0);
                    chart.redraw();

                    getDesktopManager().newFrame(chart);
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
    public void graphStockBySymbol(final Vector symbols) {

        final Thread thread = new Thread(new Runnable() {
            public void run() {
		SortedSet symbolsCopy;

		if(symbols == null)
		    symbolsCopy = SymbolListDialog.getSymbols(desktop,
							      "Graph by symbol(s)");
		else
		    symbolsCopy = new TreeSet(symbols);

                graphStock(symbolsCopy);
            }
        });
        thread.start();
    }

    /**
     * Displays a graph closing prices for stock(s), based on their name.
     *  The stock(s) is/are determined by a user prompt
     */
    public void graphStockByName() {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                SortedSet s = SymbolListDialog.getSymbolByName(desktop,
							       "Graph by name");
                graphStock(s);
            }
        });
        thread.start();
    }

    /**
     * Internal function for generic setup of graph modules
     *
     * @param companySet the list of stock symbols to graph
     */
    private void graphStock(SortedSet companySet) {

        if(companySet != null) {
            ChartModule chart = new ChartModule(desktop);
            Thread thread = Thread.currentThread();
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();

            Iterator iterator = companySet.iterator();
            String symbol = null;
            QuoteBundle quoteBundle = null;
            GraphSource dayClose = null;
            Graph graph = null;

            String title = companySet.toString();
            title = title.substring(1, title.length() - 1);

            int progressValue = 0;

            if(companySet.size() > 1) {
                progress.setIndeterminate(false);
                progress.setMaximum(companySet.size());
                progress.setMaster(true);
            }
            else
                progress.setIndeterminate(true);

            progress.show("Graph " + title);

            while(iterator.hasNext() && !thread.isInterrupted()) {
                symbol = (String)iterator.next();

                quoteBundle = new QuoteBundle(new QuoteRange(symbol));

                if(thread.isInterrupted())
                    break;

                dayClose =
                    new OHLCVQuoteGraphSource(quoteBundle, Quote.DAY_CLOSE);
                graph = new LineGraph(dayClose);
                chart.add(graph, quoteBundle, 0);
                chart.redraw();

                if(companySet.size() > 1)
                    progress.increment();
            }

            if (!thread.isInterrupted())
                getDesktopManager().newFrame(chart);

            ProgressDialogManager.closeProgressDialog(progress);
        }
    }

    /**
     * Opens the about dialog box.
     */
    public void openAboutDialog() {
        if(!isAboutDialogUp) {
            isAboutDialogUp = true;
            String aboutMessage = ("Merchant of Venice, " + Main.LONG_VERSION + " / " + 
                                   Main.RELEASE_DATE + "\n\n" +

                                   "Andrew Leppaprd (aleppard@picknow.com.au)\n" +
                                   "Daniel Makovec\n\n" +

                                   "Copyright (C) 2003, Andrew Leppard\n" +
                                   "See COPYING.txt for license terms.");
            
            JOptionPane.showInternalMessageDialog(desktop, aboutMessage, "About Venice",
                                                  JOptionPane.PLAIN_MESSAGE);
            isAboutDialogUp = false;
        }
    }

    /**
     * Opens the help module at the default page.
     */
    public void openHelp() {
        HelpModule helpModule = new HelpModule(desktop);
        
        getDesktopManager().newFrame(helpModule, false, false);
    }

    /** Shows a dialog and imports quotes into Venice */
    public void importQuotes() {
        getDesktopManager().newFrame(new ImporterModule(desktop), true, true);
    }

    // Returns the singleton desktop manager. This will be an instance of
    // org.mov.ui.DesktopManager. The desktop manager controls the layout of
    // the internal frames in the desktop
    private DesktopManager getDesktopManager() {
        return (DesktopManager)desktop.getDesktopManager();
    }
}
