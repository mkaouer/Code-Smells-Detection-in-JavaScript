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

package org.mov.analyser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.mov.main.CommandManager;
import org.mov.main.Module;
import org.mov.main.ModuleFrame;
import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.Variables;
import org.mov.portfolio.Portfolio;
import org.mov.prefs.PreferencesManager;
import org.mov.quote.ScriptQuoteBundle;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.util.TradingDate;

public class PaperTradeModule extends JPanel implements Module {

    private PropertyChangeSupport propertySupport;
    private JDesktopPane desktop;
    private ScriptQuoteBundle quoteBundle;

    // Single result table for entire application
    private static ModuleFrame resultsFrame = null;

    private JTabbedPane tabbedPane;

    // Pages
    private QuoteRangePage quoteRangePage;
    private RulesPage rulesPage;
    private PortfolioPage portfolioPage;

    /**
     * Create a new paper trade module.
     *
     * @param	desktop	the current desktop
     */
    public PaperTradeModule(JDesktopPane desktop) {

	this.desktop = desktop;

	propertySupport = new PropertyChangeSupport(this);

	layoutPaperTrade();

	// Load GUI settings from preferences
	load();
    }

    private void layoutPaperTrade() {

        tabbedPane = new JTabbedPane();
        quoteRangePage = new QuoteRangePage(desktop);
        tabbedPane.addTab("Range", quoteRangePage.getComponent());

        rulesPage = new RulesPage(desktop);
        tabbedPane.addTab("Rules", rulesPage.getComponent());

        portfolioPage = new PortfolioPage(desktop);
        tabbedPane.addTab("Portfolio", portfolioPage.getComponent());

	// Run, close buttons
	JPanel buttonPanel = new JPanel();
	JButton runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Run paper trade
                    run();
                }
            });
	buttonPanel.add(runButton);

	JButton closeButton = new JButton("Close");
	closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Tell frame we want to close
                    propertySupport.
                        firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
                }
            });
	buttonPanel.add(closeButton);

        // Now layout components
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);
    }

    // Load GUI settings from preferences
    private void load() {
        quoteRangePage.load(getClass().getName());
        rulesPage.load(getClass().getName());
        portfolioPage.load(getClass().getName());
    }

    // Save GUI settings to preferences
    public void save() {
        quoteRangePage.save(getClass().getName());
        rulesPage.save(getClass().getName());
        portfolioPage.save(getClass().getName());
    }

    public String getTitle() {
	return "Paper Trade";
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
	return null;
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
	return null;
    }

    /**
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane() {
	return true;
    }

    private void run() {
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    Thread thread = Thread.currentThread();

                    // Before we paper trade, save our interface results
                    // so if the programme crashes etc our stuff is still there
                    save();

                    // Read data from GUI and load quote data
                    if(parse()) {
                        List paperTradeResults = getPaperTradeResults();

                        if(paperTradeResults != null && !thread.isInterrupted())
                            display(paperTradeResults);
                    }
                }
            });

        thread.start();
    }

    // Read data from interface and display if there are any errors.
    // Return true if the data is OK
    private boolean parse() {
        if(!quoteRangePage.parse()) {
            tabbedPane.setSelectedComponent(quoteRangePage.getComponent());
            return false;
        }
        else if(!rulesPage.parse()) {
            tabbedPane.setSelectedComponent(rulesPage.getComponent());
            return false;
        }
        else if(!portfolioPage.parse()) {
            tabbedPane.setSelectedComponent(portfolioPage.getComponent());
            return false;
        }
        else
            return true;
    }

    private PaperTradeResult paperTrade(ProgressDialog progress,
                                        ScriptQuoteBundle quoteBundle,
                                        String quoteRangeDescription,
                                        OrderComparator orderComparator,
                                        TradingDate startDate,
                                        TradingDate endDate,
                                        Expression buyRule,
                                        Expression sellRule,
                                        float initialCapital,
                                        int mode,
                                        float stockValue,
                                        int numberStocks,
                                        float tradeCost,
                                        Variables variables,
                                        int a, int b, int c)
        throws EvaluationException {

        Portfolio portfolio;

        if(mode == PortfolioPage.STOCK_VALUE_MODE) {
            portfolio = PaperTrade.paperTrade("Paper Trade of " +
                                              quoteRangeDescription,
                                              quoteBundle,
                                              variables,
                                              orderComparator,
                                              startDate,
                                              endDate,
                                              buyRule,
                                              sellRule,
                                              initialCapital,
                                              stockValue,
                                              tradeCost);
        }
        else {
            assert portfolioPage.getMode() == PortfolioPage.NUMBER_STOCKS_MODE;
            portfolio = PaperTrade.paperTrade("Paper Trade of " +
                                              quoteRangeDescription,
                                              quoteBundle,
                                              variables,
                                              orderComparator,
                                              startDate,
                                              endDate,
                                              buyRule,
                                              sellRule,
                                              initialCapital,
                                              numberStocks,
                                              tradeCost);
        }

        // Running the equation means we might need to load in
        // more quotes so the note may have changed...
        progress.setNote("Paper Trading...");
        progress.increment();

        return new PaperTradeResult(portfolio,
                                    quoteBundle,
                                    initialCapital,
                                    tradeCost,
                                    buyRule.toString(),
                                    sellRule.toString(),
                                    a, b, c,
                                    startDate,
                                    endDate);
    }

    private List getPaperTradeResults() {
        ProgressDialog progress =
            ProgressDialogManager.getProgressDialog();

        Thread thread = Thread.currentThread();
        progress.setIndeterminate(true);
        progress.show("Paper Trade");

        // Get a copy of the values in the GUI, so that if the user changes
        // them, it won't screw up the paper trade.
        boolean isFamilyEnabled = rulesPage.isFamilyEnabled();
        int aRange = rulesPage.getARange();
        int bRange = rulesPage.getBRange();
        int cRange = rulesPage.getCRange();
        TradingDate startDate = quoteRangePage.getQuoteRange().getFirstDate();
        TradingDate endDate = quoteRangePage.getQuoteRange().getLastDate();
        Expression buyRule = rulesPage.getBuyRule();
        Expression sellRule = rulesPage.getSellRule();
        float initialCapital = portfolioPage.getInitialCapital();
        int mode = portfolioPage.getMode();
        float stockValue = portfolioPage.getStockValue();
        int numberStocks = portfolioPage.getNumberStocks();
        float tradeCost = portfolioPage.getTradeCost();

        quoteBundle = new ScriptQuoteBundle(quoteRangePage.getQuoteRange());

        OrderComparator orderComparator = quoteRangePage.getOrderComparator(quoteBundle);
        String quoteRangeDescription = quoteBundle.getQuoteRange().getDescription();

        // If we are using a rule family, how many equations are in the family?
        // Otherwise it's just a single equation.
        int numberEquations = (isFamilyEnabled ? aRange * bRange * cRange : 1);

        progress.setIndeterminate(false);
        progress.setMaximum(numberEquations);
        progress.setProgress(0);
        progress.setNote("Paper Trading...");
        progress.setMaster(true);

        // Iterate through all possible paper trade equations
        List paperTradeResults = new ArrayList(numberEquations);

        try {
            Variables variables = new Variables();

            // If the user has selected rule family, then iterate through
            // each combination of a, b, c
            if(isFamilyEnabled) {
                variables.add("a", Expression.INTEGER_TYPE);
                variables.add("b", Expression.INTEGER_TYPE);
                variables.add("c", Expression.INTEGER_TYPE);

                for(int a = 1; a <= aRange; a++) {
                    if(thread.isInterrupted())
                        break;

                    variables.setValue("a", a);

                    for(int b = 1; b <= bRange; b++) {
                        if(thread.isInterrupted())
                            break;

                        variables.setValue("b", b);

                        for(int c = 1; c <= cRange; c++) {
                            if(thread.isInterrupted())
                                break;

                            variables.setValue("c", c);
                            paperTradeResults.add(paperTrade(progress,
                                                             quoteBundle,
                                                             quoteRangeDescription,
                                                             orderComparator,
                                                             startDate,
                                                             endDate,
                                                             buyRule,
                                                             sellRule,
                                                             initialCapital,
                                                             mode,
                                                             stockValue,
                                                             numberStocks,
                                                             tradeCost,
                                                             variables,
                                                             a, b, c));
                        }
                    }
                }
            }

            // Otherwise there is only one equation and one result.
            else if (!thread.isInterrupted())
                paperTradeResults.add(paperTrade(progress,
                                                 quoteBundle,
                                                 quoteRangeDescription,
                                                 orderComparator,
                                                 startDate,
                                                 endDate,
                                                 buyRule,
                                                 sellRule,
                                                 initialCapital,
                                                 mode,
                                                 stockValue,
                                                 numberStocks,
                                                 tradeCost,
                                                 variables,
                                                 0, 0, 0));

        } catch(EvaluationException e) {
            ProgressDialogManager.closeProgressDialog(progress);
            progress = null;

            JOptionPane.showInternalMessageDialog(desktop,
                                                  e.getReason() + ".",
                                                  "Error executing paper trade",
                                                  JOptionPane.ERROR_MESSAGE);

            return null;
        }

        ProgressDialogManager.closeProgressDialog(progress);
	return paperTradeResults;
    }

    private void display(final List paperTradeResults) {

	// Invokes on dispatch thread
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {

		    // Dispaly results table if its not already up (or if it
		    // was closed we need to create a new one)
		    if(resultsFrame == null || resultsFrame.isClosed()) {
			resultsFrame =
			    CommandManager.getInstance().newPaperTradeResultTable();
		    }
		    else {
			resultsFrame.toFront();
			
			try {
			    resultsFrame.setIcon(false);
			    resultsFrame.setSelected(true);
			}
			catch(PropertyVetoException e) {
			    assert false;
			}
		    }

		    // Send result to result table for display
		    PaperTradeResultModule resultsModule =
			(PaperTradeResultModule)resultsFrame.getModule();
		
                    resultsModule.addResults(paperTradeResults);
		}});
    }
}
