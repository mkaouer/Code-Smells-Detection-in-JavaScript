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

import org.mov.analyser.ann.ArtificialNeuralNetwork;
import org.mov.main.CommandManager;
import org.mov.main.Module;
import org.mov.main.ModuleFrame;
import org.mov.parser.Expression;
import org.mov.parser.EvaluationException;
import org.mov.parser.Variable;
import org.mov.parser.Variables;
import org.mov.portfolio.Portfolio;
import org.mov.quote.EODQuoteBundle;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;

public class ANNModule extends JPanel implements Module {

    private PropertyChangeSupport propertySupport;
    private JDesktopPane desktop;
    private EODQuoteBundle quoteBundle;

    // Single result table for entire application
    private static ModuleFrame resultsFrame = null;

    private JTabbedPane tabbedPane;

    // Pages
    private QuoteRangePage quoteRangePage;
    private PortfolioPage portfolioPage;
    private TradeValuePage tradeValuePage;
    private ANNPage ANNPage;
    private ANNTrainingPage ANNTrainingPage;
    private ANNNetworkTypePage ANNNetworkTypePage;
    
    /**
     * Create a new paper trade module.
     *
     * @param	desktop	the current desktop
     */
    public ANNModule(JDesktopPane desktop) {

	this.desktop = desktop;

	propertySupport = new PropertyChangeSupport(this);

	layoutPaperTrade();

	// Load GUI settings from preferences
	load();
    }

    private void layoutPaperTrade() {

        tabbedPane = new JTabbedPane();
        quoteRangePage = new QuoteRangePage(desktop);
        tabbedPane.addTab(quoteRangePage.getTitle(), quoteRangePage.getComponent());

        portfolioPage = new PortfolioPage(desktop);
        tabbedPane.addTab(portfolioPage.getTitle(), portfolioPage.getComponent());
        
        tradeValuePage = new TradeValuePage(desktop);
        tabbedPane.addTab(tradeValuePage.getTitle(), tradeValuePage.getComponent());

        // Get the max height
        double maxHeight = quoteRangePage.getPreferredSize().getHeight();
        if (portfolioPage.getPreferredSize().getHeight()>maxHeight)
            maxHeight = portfolioPage.getPreferredSize().getHeight();
        if (tradeValuePage.getPreferredSize().getHeight()>maxHeight)
            maxHeight = tradeValuePage.getPreferredSize().getHeight();
        
        ANNPage = new ANNPage(desktop, maxHeight);
        tabbedPane.addTab(ANNPage.getTitle(), ANNPage.getComponent());

        ANNTrainingPage = new ANNTrainingPage(desktop);
        tabbedPane.addTab(ANNTrainingPage.getTitle(), ANNTrainingPage.getComponent());

        ANNNetworkTypePage = new ANNNetworkTypePage(desktop);
        tabbedPane.addTab(ANNNetworkTypePage.getTitle(), ANNNetworkTypePage.getComponent());

	// Run, training, close buttons
	JPanel buttonPanel = new JPanel();
	JButton runButton = new JButton(Locale.getString("RUN"));
        runButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Run ANN without changing its weights
                    run();
                }
            });
	buttonPanel.add(runButton);

	// Training of artificial neural network
	JButton trainingButton = new JButton(Locale.getString("TRAINING"));
        trainingButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Train ANN, so weights will be changed
                    train();
                }
            });
	buttonPanel.add(trainingButton);

	JButton closeButton = new JButton(Locale.getString("CLOSE"));
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
        portfolioPage.load(getClass().getName());
        tradeValuePage.load(getClass().getName());
        ANNPage.load(getClass().getName());
        ANNTrainingPage.load(getClass().getName());
        ANNNetworkTypePage.load(getClass().getName());
    }

    // Save GUI settings to preferences
    public void save() {
        quoteRangePage.save(getClass().getName());
        portfolioPage.save(getClass().getName());
        tradeValuePage.save(getClass().getName());
        ANNPage.save(getClass().getName());
        ANNTrainingPage.save(getClass().getName());
        ANNNetworkTypePage.save(getClass().getName());
    }

    public String getTitle() {
	return Locale.getString("ARTIFICIAL_NEURAL_NETWORK_TITLE");
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

                    // Read data from GUI and load quote data
                    if(parse()) {
                        List ANNResults = getANNResults();

                        if(ANNResults != null && !thread.isInterrupted())
                            display(ANNResults);
                    }
                }
            });

        thread.start();
    }

    private void train() {
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    Thread thread = Thread.currentThread();

                    // Before we train, save our interface results
                    // so if the programme crashes etc our stuff is still there
                    save();

                    // Read data from GUI and load quote data
                    if(parse()) {
                        trainANN();
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
        else if(!portfolioPage.parse()) {
            tabbedPane.setSelectedComponent(portfolioPage.getComponent());
            return false;
        }
        else if(!tradeValuePage.parse()) {
            tabbedPane.setSelectedComponent(tradeValuePage.getComponent());
            return false;
        }
        else if(!ANNPage.parse()) {
            tabbedPane.setSelectedComponent(ANNPage.getComponent());
            return false;
        }
        else if(!ANNTrainingPage.parse()) {
            tabbedPane.setSelectedComponent(ANNTrainingPage.getComponent());
            return false;
        }
        else if(!ANNNetworkTypePage.parse(ANNPage.getInputExpressions().length)) {
            tabbedPane.setSelectedComponent(ANNNetworkTypePage.getComponent());
            return false;
        }
        else {
            return true;
        }
    }

    /*
     * Run the artificial neural network
     */
    private ANNResult paperTrade(EODQuoteBundle quoteBundle,
            String quoteRangeDescription,
            OrderCache orderCache,
            TradingDate startDate,
            TradingDate endDate,
            Money initialCapital,
            int mode,
            Money stockValue,
            int numberStocks,
            Money tradeCost,
            Variables variables,
            String tradeValueBuy,
            String tradeValueSell,
            ProgressDialog progress,
            Expression[] inputExpressions,
            ArtificialNeuralNetwork artificialNeuralNetwork)
        throws EvaluationException {

        Portfolio portfolio;

        if(mode == PortfolioPage.STOCK_VALUE_MODE) {
            portfolio = ANNPaperTrade.paperTrade(
                    Locale.getString("ANN_OF", quoteRangeDescription),
                    quoteBundle,
                    variables,
                    orderCache,
                    startDate,
                    endDate,
                    initialCapital,
                    stockValue,
                    tradeCost,
                    tradeValueBuy,
                    tradeValueSell,
                    progress,
                    inputExpressions,
                    artificialNeuralNetwork);
        } else {
            assert portfolioPage.getMode() == PortfolioPage.NUMBER_STOCKS_MODE;
            portfolio = ANNPaperTrade.paperTrade(
                    Locale.getString("ANN_OF", quoteRangeDescription),
                    quoteBundle,
                    variables,
                    orderCache,
                    startDate,
                    endDate,
                    initialCapital,
                    numberStocks,
                    tradeCost,
                    tradeValueBuy,
                    tradeValueSell,
                    progress,
                    inputExpressions,
                    artificialNeuralNetwork);
        }
        
        return new ANNResult(portfolio,
                                quoteBundle,
                                initialCapital,
                                tradeCost,
                                startDate,
                                endDate,
                                ANNPaperTrade.getTip());
    }

    /*
     * Do the training of artificial neural network
     */
    private void paperTraining(ProgressDialog progress,
            EODQuoteBundle quoteBundle,
            String quoteRangeDescription,
            OrderCache orderCache,
            TradingDate startDate,
            TradingDate endDate,
            Money initialCapital,
            int mode,
            Money stockValue,
            int numberStocks,
            Money tradeCost,
            Variables variables,
            String tradeValueBuy,
            String tradeValueSell,
            ANNTrainingPage ANNTrainingPage,
            Expression[] inputExpressions,
            ArtificialNeuralNetwork artificialNeuralNetwork)
        throws EvaluationException {

        Portfolio portfolio;

        if(mode == PortfolioPage.STOCK_VALUE_MODE) {
            ANNPaperTrade.paperTraining(Locale.getString("ANN_OF",
                                                           quoteRangeDescription),
                quoteBundle,
                variables,
                orderCache,
                startDate,
                endDate,
                initialCapital,
                stockValue,
                tradeCost,
                tradeValueBuy,
                tradeValueSell,
                progress,
                ANNTrainingPage,
                inputExpressions,
                artificialNeuralNetwork);
        } else {
            assert portfolioPage.getMode() == PortfolioPage.NUMBER_STOCKS_MODE;
            ANNPaperTrade.paperTraining(Locale.getString("ANN_OF",
                                                               quoteRangeDescription),
                    quoteBundle,
                    variables,
                    orderCache,
                    startDate,
                    endDate,
                    initialCapital,
                    numberStocks,
                    tradeCost,
                    tradeValueBuy,
                    tradeValueSell,
                    progress,
                    ANNTrainingPage,
                    inputExpressions,
                    artificialNeuralNetwork);
        }
    }

    /*
     * Return a list with a ANNResult object.
     * It is used to know all about paper trade got with current artificial neural network.
     */
    private List getANNResults() {
        ProgressDialog progress =
            ProgressDialogManager.getProgressDialog(false);

        Thread thread = Thread.currentThread();
        progress.setIndeterminate(true);
        progress.show(Locale.getString("RUNNING"));

        // Get a copy of the values in the GUI, so that if the user changes
        // them, it won't screw up the paper trade.
        TradingDate startDate = quoteRangePage.getQuoteRange().getFirstDate();
        TradingDate endDate = quoteRangePage.getQuoteRange().getLastDate();
        Money initialCapital = portfolioPage.getInitialCapital();
        int mode = portfolioPage.getMode();
        Money stockValue = portfolioPage.getStockValue();
        int numberStocks = portfolioPage.getNumberStocks();
        Money tradeCost = portfolioPage.getTradeCost();
        

        quoteBundle = new EODQuoteBundle(quoteRangePage.getQuoteRange());

        OrderComparator orderComparator = quoteRangePage.getOrderComparator(quoteBundle);
        OrderCache orderCache = new OrderCache(quoteBundle, orderComparator);
        String quoteRangeDescription = quoteBundle.getQuoteRange().getDescription();

        // We get the formulas that rule at which price the stock is sold or bought
        String tradeValueBuy = tradeValuePage.getTradeValueBuy();
        String tradeValueSell = tradeValuePage.getTradeValueSell();

        // We get the ANN and its input expressions
        Expression[] inputExpressions = ANNPage.getInputExpressions();
        ArtificialNeuralNetwork artificialNeuralNetwork = ANNNetworkTypePage.getANN();
        artificialNeuralNetwork.setBuyThreshold(ANNPage.getBuyThreshold());
        artificialNeuralNetwork.setSellThreshold(ANNPage.getSellThreshold());

        // We set the progress bar, it will be incremented one by one for each trading date
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setNote(Locale.getString("ARTIFICIAL_NEURAL_NETWORK_TITLE"));
        progress.setMaster(true);

        List ANNResults = new ArrayList();
        
        try {
            Variables variables = new Variables();
            
            ANNResults.add(paperTrade(quoteBundle,
                    quoteRangeDescription,
                    orderCache,
                    startDate,
                    endDate,
                    initialCapital,
                    mode,
                    stockValue,
                    numberStocks,
                    tradeCost,
                    variables,
                    tradeValueBuy,
                    tradeValueSell,
                    progress,
                    inputExpressions,
                    artificialNeuralNetwork));

        } catch(EvaluationException e) {
            ProgressDialogManager.closeProgressDialog(progress);
            progress = null;

            JOptionPane.showInternalMessageDialog(desktop,
                                                  e.getReason(),
                                                  Locale.getString("ERROR_EVALUATING_EQUATION"),
                                                  JOptionPane.ERROR_MESSAGE);

            return null;
        }

        ProgressDialogManager.closeProgressDialog(progress);
	return ANNResults;
    }

    /*
     * Train the neural network according to the input from ANN Page GUI
     */
    private void trainANN() {
        ProgressDialog progress =
            ProgressDialogManager.getProgressDialog();
        // We set the progress bar so that the ANN can manage it independently
        // We update it each ANN training cycle.
        ANNNetworkTypePage.setProgressBar(progress);

        Thread thread = Thread.currentThread();
        progress.setIndeterminate(true);
        progress.show(Locale.getString("TRAINING"));

        // Get a copy of the values in the GUI, so that if the user changes
        // them, it won't screw up the paper trade.
        TradingDate startDate = quoteRangePage.getQuoteRange().getFirstDate();
        TradingDate endDate = quoteRangePage.getQuoteRange().getLastDate();
        Money initialCapital = portfolioPage.getInitialCapital();
        int mode = portfolioPage.getMode();
        Money stockValue = portfolioPage.getStockValue();
        int numberStocks = portfolioPage.getNumberStocks();
        Money tradeCost = portfolioPage.getTradeCost();
        

        quoteBundle = new EODQuoteBundle(quoteRangePage.getQuoteRange());

        OrderComparator orderComparator = quoteRangePage.getOrderComparator(quoteBundle);
        OrderCache orderCache = new OrderCache(quoteBundle, orderComparator);
        String quoteRangeDescription = quoteBundle.getQuoteRange().getDescription();

        // We get the formulas that rule at which price the stock is sold or bought
        String tradeValueBuy = tradeValuePage.getTradeValueBuy();
        String tradeValueSell = tradeValuePage.getTradeValueSell();

        // We get the ANN and its input expressions
        Expression[] inputExpressions = ANNPage.getInputExpressions();
        ArtificialNeuralNetwork artificialNeuralNetwork = ANNNetworkTypePage.getANN();
        artificialNeuralNetwork.setBuyThreshold(ANNPage.getBuyThreshold());
        artificialNeuralNetwork.setSellThreshold(ANNPage.getSellThreshold());

        // Initialize the progress bar
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setNote(Locale.getString("ARTIFICIAL_NEURAL_NETWORK_TITLE"));
        progress.setMaster(true);

        try {
            Variables variables = new Variables();
            
            paperTraining(progress,
                    quoteBundle,
                    quoteRangeDescription,
                    orderCache,
                    startDate,
                    endDate,
                    initialCapital,
                    mode,
                    stockValue,
                    numberStocks,
                    tradeCost,
                    variables,
                    tradeValueBuy,
                    tradeValueSell,
                    ANNTrainingPage,
                    inputExpressions,
                    artificialNeuralNetwork);

        } catch(EvaluationException e) {
            ProgressDialogManager.closeProgressDialog(progress);
            progress = null;

            JOptionPane.showInternalMessageDialog(desktop,
                                                  e.getReason(),
                                                  Locale.getString("ERROR_EVALUATING_EQUATION"),
                                                  JOptionPane.ERROR_MESSAGE);

        }
    }

    /*
     * Display the results of ANN running, as paper trade analysis.
     */
    private void display(final List ANNResults) {

	// Invokes on dispatch thread
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {

		    // Dispaly results table if its not already up (or if it
		    // was closed we need to create a new one)
		    if(resultsFrame == null || resultsFrame.isClosed()) {
			resultsFrame =
			    CommandManager.getInstance().newANNResultTable();
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
		    ANNResultModule resultsModule =
			(ANNResultModule)resultsFrame.getModule();
		
                    resultsModule.setDesktop(desktop);
                    resultsModule.addResults(ANNResults);
		}});
    }
}
