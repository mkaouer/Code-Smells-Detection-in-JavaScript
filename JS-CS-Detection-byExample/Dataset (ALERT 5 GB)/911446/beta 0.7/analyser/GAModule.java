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

/**
 *
 * @author  Alberto Nacher
 */

package org.mov.analyser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.mov.analyser.ga.*;
import org.mov.main.CommandManager;
import org.mov.main.Module;
import org.mov.main.ModuleFrame;
import org.mov.parser.Expression;
import org.mov.parser.Variable;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.EODQuoteBundle;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;

public class GAModule extends JPanel implements Module {

    private PropertyChangeSupport propertySupport;
    private JDesktopPane desktop;
    private EODQuoteBundle quoteBundle;

    // Single result table for entire application
    private static ModuleFrame resultsFrame = null;

    private JTabbedPane tabbedPane;

    // Pages
    private QuoteRangePage quoteRangePage;
    private GARulesPage GARulesPage;   
    private PortfolioPage portfolioPage;
    private TradeValuePage tradeValuePage;
    private GAPage GAPage;
    
    /**
     * Create a new paper trade module.
     *
     * @param	desktop	the current desktop
     */
    public GAModule(JDesktopPane desktop) {

	this.desktop = desktop;

	propertySupport = new PropertyChangeSupport(this);

	layoutGeneticAlgorithm();

	// Load GUI settings from preferences
	load();
    }

    private void layoutGeneticAlgorithm() {

        tabbedPane = new JTabbedPane();
        quoteRangePage = new QuoteRangePage(desktop);
        tabbedPane.addTab(quoteRangePage.getTitle(), quoteRangePage.getComponent());

        // Get the max height
        double maxHeight = quoteRangePage.getPreferredSize().getHeight();
        
        GARulesPage = new GARulesPage(desktop, maxHeight);
        tabbedPane.addTab(GARulesPage.getTitle(), GARulesPage.getComponent());

        portfolioPage = new PortfolioPage(desktop);
        tabbedPane.addTab(portfolioPage.getTitle(), portfolioPage.getComponent());
        
        tradeValuePage = new TradeValuePage(desktop);
        tabbedPane.addTab(tradeValuePage.getTitle(), tradeValuePage.getComponent());

        GAPage = new GAPage(desktop);
        tabbedPane.addTab(GAPage.getTitle(), GAPage.getComponent());
        
	// Run, close buttons
	JPanel buttonPanel = new JPanel();
	JButton runButton = new JButton(Locale.getString("RUN"));
        runButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Run paper trade
                    run();
                }
            });
	buttonPanel.add(runButton);

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
        GARulesPage.load(getClass().getName());
        portfolioPage.load(getClass().getName());
        tradeValuePage.load(getClass().getName());
        GAPage.load(getClass().getName());
    }

    // Save GUI settings to preferences
    public void save() {
        quoteRangePage.save(getClass().getName());
        GARulesPage.save(getClass().getName());
        portfolioPage.save(getClass().getName());
        tradeValuePage.save(getClass().getName());
        GAPage.save(getClass().getName());
    }

    public String getTitle() {
	return Locale.getString("GENETIC_ALGORITHM_TITLE");
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

                    // Before we run the GA, save our interface results
                    // so if the programme crashes etc our stuff is still there
                    save();

                    // Read data from GUI and perform GA!
                    if(parse())
                        geneticAlgorithm();
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
        else if(!GARulesPage.parse()) {
            tabbedPane.setSelectedComponent(GARulesPage.getComponent());
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
        else if(!GAPage.parse()) {
            tabbedPane.setSelectedComponent(GAPage.getComponent());
            return false;
        }
        else
            return true;
    }

    private void geneticAlgorithm() {
        ProgressDialog progress =
            ProgressDialogManager.getProgressDialog();
        
        Thread thread = Thread.currentThread();
        progress.setIndeterminate(true);
        progress.show(Locale.getString("GENETIC_ALGORITHM_TITLE"));
        
        // Get a copy of the values in the GUI, so that if the user changes
        // them, it won't screw up the GA.
        TradingDate startDate = quoteRangePage.getStartDate();
        TradingDate endDate = quoteRangePage.getEndDate();
        Money initialCapital = portfolioPage.getInitialCapital();
        Money tradeCost = portfolioPage.getTradeCost();
        int breedingPopulation = GAPage.getBreedingPopulation();
        int displayPopulation = GAPage.getDisplayPopulation();
        int randomPercentage = GAPage.getRandomPercentage();
        Money stockValue = portfolioPage.getStockValue();
        int numberStocks = portfolioPage.getNumberStocks();
        String tradeValueBuy = tradeValuePage.getTradeValueBuy();
        String tradeValueSell = tradeValuePage.getTradeValueSell();
        GAIndividual.setRandomPercentage(randomPercentage);
        GAIndividual lowestGAIndividual = GARulesPage.getLowestIndividual();
        GAIndividual highestGAIndividual = GARulesPage.getHighestIndividual();
        
        // Insert all the parameters in variables.
        // We use lowestGAIndividual, but highestGAIndividual should be the same.
        // All the GAIndividual have the same parameters during all GA Algorithm,
        // they just differ one from another because of the values.
        Variables variables = new Variables();
        variables.add("held", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("order", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("daysfromstart", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("transactions", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("capital", Expression.FLOAT_TYPE, Variable.CONSTANT);
        variables.add("stockcapital", Expression.FLOAT_TYPE, Variable.CONSTANT);
        for (int ii=0; ii<lowestGAIndividual.size(); ii++)
            variables.add(lowestGAIndividual.parameter(ii), lowestGAIndividual.type(ii), Variable.CONSTANT);
 
        // Get the expression for the buy and sell rules
        Expression buyRule = GARulesPage.getBuyRule();
        Expression sellRule = GARulesPage.getSellRule();

        // Get the quote bundle
        quoteBundle = new EODQuoteBundle(quoteRangePage.getQuoteRange());
        OrderComparator orderComparator = quoteRangePage.getOrderComparator(quoteBundle);
        OrderCache orderCache = new OrderCache(quoteBundle, orderComparator);
        
        if(!thread.isInterrupted()) {
            int numberGenerations = GAPage.getGenerations();
            int population = GAPage.getPopulation();
            
            progress.setIndeterminate(false);
            progress.setMaximum(numberGenerations * population);
            progress.setProgress(0);
            progress.setMaster(true);
            
            GeneticAlgorithm geneticAlgorithm =
                new GeneticAlgorithm(quoteBundle,
                            orderCache,
                            buyRule,
                            sellRule,
                            startDate,
                            endDate,
                            initialCapital,
                            stockValue,
                            numberStocks,
                            tradeCost,
                            breedingPopulation,
                            tradeValueBuy,
                            tradeValueSell,
                            lowestGAIndividual,
                            highestGAIndividual,
                            variables);
            
            for(int generation = 1; generation <= numberGenerations; generation++) {
                if(thread.isInterrupted())
                    break;
                
                int individual = 1;
                
                // Keep generating more individuals until we've created the
                // breeding population size or if the breeding population size
                // is too small. The breeding population size can only be too
                // small for the first generation.
                int actualBreedingPopulation = geneticAlgorithm.getNextBreedingPopulationSize();
                while(individual < population ||
                        actualBreedingPopulation < breedingPopulation) {
                    if(thread.isInterrupted())
                        break;
                    
                    // "Generation x of y (%)"
                    int perc = Math.min((new Double((100.0D*actualBreedingPopulation)/breedingPopulation)).intValue(),
                            (new Double((100.0D*individual)/population)).intValue());
                    progress.setNote(Locale.getString("GENERATION_OF",
                                                        perc,
                                                        generation,
                                                        numberGenerations));
                    
                    // If we are looping only to increase the breeding population size
                    // then don't update the progress counter as we didn't count this
                    // time in our estimate. Unfortunately this might look to the user
                    // like it has stalled at the end of the first generation.
                    if(individual < population)
                        progress.increment();
                    
                    geneticAlgorithm.nextIndividual();
                    
                    individual++;
                    actualBreedingPopulation = geneticAlgorithm.getNextBreedingPopulationSize();
                }
                
                geneticAlgorithm.nextGeneration();
                
                // The actual breeding population size and the breeding population
                // may be different iff the operation was cancelled
                if(geneticAlgorithm.getBreedingPopulationSize() > 0)
                    display(getResults(geneticAlgorithm,
                        geneticAlgorithm.getBreedingPopulationSize(),
                        displayPopulation,
                        quoteBundle, startDate, endDate,
                        initialCapital, tradeCost, generation));
            }
        }
        
        ProgressDialogManager.closeProgressDialog(progress);
    }
    
    private List getResults(GeneticAlgorithm geneticAlgorithm,
                            int breedingPopulation,
                            int displayPopulation,
                            EODQuoteBundle quoteBundle,
                            TradingDate startDate,
                            TradingDate endDate,
                            Money initialCapital,
                            Money tradeCost,
                            int generation) {
        // Create a list of results from the top breeding individuals
        List results = new ArrayList();
        int displayCount = Math.min(breedingPopulation, displayPopulation);
        
        for(int i = 0; i < displayCount; i++) {
            int offset = breedingPopulation - i - 1;
            GAIndividual individual = geneticAlgorithm.getBreedingIndividual(offset);
            results.add(new GAResult(individual,
                                    geneticAlgorithm.getBuyRule(),
                                    geneticAlgorithm.getSellRule(),
                                    quoteBundle,
                                    initialCapital,
                                    tradeCost,
                                    generation,
                                    startDate,
                                    endDate));
        }
        return results;
    }
    
    private void display(final List GAResults) {
        // Invokes on dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Display results table if its not already up (or if it
                // was closed we need to create a new one)
                if(resultsFrame == null || resultsFrame.isClosed()) {
                    resultsFrame =
                    CommandManager.getInstance().newGAResultTable();
                }
                
                // If it is already created, don't try to move it to
                // the front like the PaperTradeResults. For small
                // generations we might have a lot of data slowly streaming
                // in, and having the window pop up every 30s can get
                // very annoying.
                
                // Send result to result table for display
                GAResultModule resultsModule =
                (GAResultModule)resultsFrame.getModule();
                
                resultsModule.addResults(GAResults);
            }});
    }
}
