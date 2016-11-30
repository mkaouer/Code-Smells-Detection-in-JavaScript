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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import org.mov.analyser.gp.GeneticProgramme;
import org.mov.analyser.gp.Individual;
import org.mov.analyser.gp.GPQuoteBundle;
import org.mov.main.CommandManager;
import org.mov.main.Module;
import org.mov.main.ModuleFrame;
import org.mov.parser.ExpressionFactory;
import org.mov.quote.EODQuoteBundle;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;

public class GPModule extends JPanel implements Module {
    
    private PropertyChangeSupport propertySupport;
    private JDesktopPane desktop;
    private JTabbedPane tabbedPane;
    
    // Single result table for entire application
    private static ModuleFrame resultsFrame = null;
    
    // Pages
    private QuoteRangePage quoteRangePage;
    private PortfolioPage portfolioPage;
    private GPPage GPPage;
    private GPPageInitialPopulation GPPageInitialPopulation;
    private GPGondolaSelection GPGondolaSelection;
    private TradeValuePage tradeValuePage;
    
   
    public GPModule(JDesktopPane desktop) {
        this.desktop = desktop;
        
        propertySupport = new PropertyChangeSupport(this);
        
        layoutGeneticProgramme();
        
        // Load GUI settings from preferences
        load();
    }
    
    private void layoutGeneticProgramme() {
        
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
        
        GPPage = new GPPage(desktop);
        tabbedPane.addTab(GPPage.getTitle(), GPPage.getComponent());
        
        // Get the max height
        if (GPPage.getPreferredSize().getHeight()>maxHeight)
            maxHeight = GPPage.getPreferredSize().getHeight();
        
        GPPageInitialPopulation = new GPPageInitialPopulation(desktop, maxHeight);
        tabbedPane.addTab(GPPageInitialPopulation.getTitle(), GPPageInitialPopulation.getComponent());
        
        // Get the max height
        if (GPPageInitialPopulation.getPreferredSize().getHeight()>maxHeight)
            maxHeight = GPPageInitialPopulation.getPreferredSize().getHeight();
        
        GPGondolaSelection = new GPGondolaSelection(desktop, maxHeight);
        tabbedPane.addTab(GPGondolaSelection.getTitle(), GPGondolaSelection.getComponent());
        

        // Run, close buttons
        JPanel buttonPanel = new JPanel();
        JButton runButton = new JButton(Locale.getString("RUN"));
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Run GP
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
    
    public void load() {
        quoteRangePage.load(getClass().getName());
        portfolioPage.load(getClass().getName());
        tradeValuePage.load(getClass().getName());
        GPPage.load(getClass().getName());
        GPPageInitialPopulation.load(getClass().getName());
        GPGondolaSelection.load(getClass().getName());
    }
    
    public void save() {
        quoteRangePage.save(getClass().getName());
        portfolioPage.save(getClass().getName());
        tradeValuePage.save(getClass().getName());
        GPPage.save(getClass().getName());
        GPPageInitialPopulation.save(getClass().getName());
        GPGondolaSelection.save(getClass().getName());
    }
    
    public String getTitle() {
        return Locale.getString("GENETIC_PROGRAMMING_TITLE");
    }
    
    public void addModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removeModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public ImageIcon getFrameIcon() {
        return null;
    }
    
    public JComponent getComponent() {
        return this;
    }
    
    public JMenuBar getJMenuBar() {
        return null;
    }
    
    public boolean encloseInScrollPane() {
        return true;
    }
    
    private void run() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Thread thread = Thread.currentThread();
                
                // Before we run the GP, save our interface results
                // so if the programme crashes etc our stuff is still there
                save();
                
                // Read data from GUI and perform GP!
                if(parse())
                    geneticProgramme();
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
        else if(!GPPage.parse()) {
            tabbedPane.setSelectedComponent(GPPage.getComponent());
            return false;
        }
        else if(!GPPageInitialPopulation.parse()) {
            tabbedPane.setSelectedComponent(GPPageInitialPopulation.getComponent());
            return false;
        }
        else if(!GPGondolaSelection.parse()) {
            tabbedPane.setSelectedComponent(GPGondolaSelection.getComponent());
            return false;
        }
        else
            return true;
    }
    
    private void geneticProgramme() {
        ProgressDialog progress =
            ProgressDialogManager.getProgressDialog();
        
        Thread thread = Thread.currentThread();
        progress.setIndeterminate(true);
        progress.show(Locale.getString("GENETIC_PROGRAMME"));
        
        // Get a copy of the values in the GUI, so that if the user changes
        // them, it won't screw up the GP.
        TradingDate startDate = quoteRangePage.getStartDate();
        TradingDate endDate = quoteRangePage.getEndDate();
        Money initialCapital = portfolioPage.getInitialCapital();
        Money tradeCost = portfolioPage.getTradeCost();
        int breedingPopulation = GPPage.getBreedingPopulation();
        int displayPopulation = GPPage.getDisplayPopulation();
        int window = GPPage.getWindow();
        Money stockValue = portfolioPage.getStockValue();
        int numberStocks = portfolioPage.getNumberStocks();
        // number of mutations to be applied to the rules defined in Initial Population Section
        int mutations = GPPageInitialPopulation.getMutations();
        String tradeValueBuy = tradeValuePage.getTradeValueBuy();
        String tradeValueSell = tradeValuePage.getTradeValueSell();

        // quote bundle should load window days before quote range...
        GPQuoteBundle quoteBundle =
            new GPQuoteBundle(new EODQuoteBundle(quoteRangePage.getQuoteRange()), window);
        OrderComparator orderComparator = quoteRangePage.getOrderComparator(quoteBundle);
        OrderCache orderCache = new OrderCache(quoteBundle, orderComparator);
        
        if(!thread.isInterrupted()) {
            int numberGenerations = GPPage.getGenerations();
            int population = GPPage.getPopulation();
            
            progress.setIndeterminate(false);
            progress.setMaximum(numberGenerations * population);
            progress.setProgress(0);
            progress.setMaster(true);
            
            GeneticProgramme geneticProgramme =
                new GeneticProgramme(quoteBundle,
                                     GPGondolaSelection,
                                     orderCache,
                                     startDate,
                                     endDate,
                                     initialCapital,
                                     stockValue,
                                     numberStocks,
                                     tradeCost,
                                     breedingPopulation,
                                     tradeValueBuy,
                                     tradeValueSell);
            
            for(int generation = 1; generation <= numberGenerations; generation++) {
                if(thread.isInterrupted())
                    break;
                
                int individual = 1;
                
                // Keep generating more individuals until we've created the
                // breeding population size or if the breeding population size
                // is too small. The breeding population size can only be too
                // small for the first generation.
                int actualBreedingPopulation = geneticProgramme.getNextBreedingPopulationSize();
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
                    
                    // GPPageInitialPopulation.getIfRandom()==0 is true
                    // if we must create a random individual.
                    // GPPageInitialPopulation.getIfRandom()==0 is false
                    // if we must create an individual according to
                    // the user defined buy/sell rules
                    // (defined in the Initial Population Section).
                    // All that is written above is applied only
                    // for the first generation, the other generations
                    // get new inidividuals from their parents,
                    // so it can be applied geneticProgramme.nextIndividual(null, null);
                    // with no input rules.
                    if ((GPPageInitialPopulation.getIfRandom()==0) || (generation!=1)) {
                        geneticProgramme.nextIndividual(null, null, mutations);
                    } else {
                        // Get a random buy/sell rules from initial population
                        int randomRow = GPPageInitialPopulation.getIfRandom();
                        String buyRuleString = GPPageInitialPopulation.getBuyRule(randomRow);
                        String sellRuleString = GPPageInitialPopulation.getSellRule(randomRow);
                        // Call the nextIndividual method to put
                        geneticProgramme.nextIndividual(
                            ExpressionFactory.newExpression(buyRuleString),
                            ExpressionFactory.newExpression(sellRuleString),
                            mutations);
                    }
                    
                    individual++;
                    actualBreedingPopulation = geneticProgramme.getNextBreedingPopulationSize();
                }
                
                geneticProgramme.nextGeneration();
                
                // The actual breeding population size and the breeding population
                // may be different iff the operation was cancelled
                if(geneticProgramme.getBreedingPopulationSize() > 0)
                    display(getResults(geneticProgramme,
                        geneticProgramme.getBreedingPopulationSize(),
                        displayPopulation,
                        quoteBundle, startDate, endDate,
                        initialCapital, tradeCost, generation));
            }
        }
        
        ProgressDialogManager.closeProgressDialog(progress);
    }
    
    private List getResults(GeneticProgramme geneticProgramme,
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
            Individual individual = geneticProgramme.getBreedingIndividual(offset);
            results.add(new GPResult(individual,
                                    quoteBundle,
                                    initialCapital,
                                    tradeCost,
                                    generation,
                                    startDate,
                                    endDate));
        }
        return results;
    }
    
    private void display(final List GPResults) {
        // Invokes on dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // Dispaly results table if its not already up (or if it
                // was closed we need to create a new one)
                if(resultsFrame == null || resultsFrame.isClosed()) {
                    resultsFrame =
                    CommandManager.getInstance().newGPResultTable(GPPageInitialPopulation);
                }
                
                // If it is already created, don't try to move it to
                // the front like the PaperTradeResults. For small
                // generations we might have a lot of data slowly streaming
                // in, and having the window pop up every 30s can get
                // very annoying.
                
                // Send result to result table for display
                GPResultModule resultsModule =
                (GPResultModule)resultsFrame.getModule();
                
                resultsModule.addResults(GPResults);
            }});
    }
}
