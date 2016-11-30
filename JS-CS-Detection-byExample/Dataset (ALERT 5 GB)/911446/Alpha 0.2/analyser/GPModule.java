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
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

import org.mov.analyser.gp.GeneticProgramme;
import org.mov.analyser.gp.Individual;
import org.mov.analyser.gp.GPQuoteBundle;
import org.mov.main.CommandManager;
import org.mov.main.Module;
import org.mov.main.ModuleFrame;
import org.mov.quote.QuoteBundle;
import org.mov.quote.ScriptQuoteBundle;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
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
        tabbedPane.addTab("Range", quoteRangePage.getComponent());

        portfolioPage = new PortfolioPage(desktop);
        tabbedPane.addTab("Portfolio", portfolioPage.getComponent());

        GPPage = new GPPage(desktop);
        tabbedPane.addTab("GP", GPPage.getComponent());

	// Run, close buttons
	JPanel buttonPanel = new JPanel();
	JButton runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Run GP
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

    public void load() {
        quoteRangePage.load(getClass().getName());
        portfolioPage.load(getClass().getName());
        GPPage.load(getClass().getName());
    }

    public void save() {
        quoteRangePage.save(getClass().getName());
        portfolioPage.save(getClass().getName());
        GPPage.save(getClass().getName());
    }

    public String getTitle() {
	return "Genetic Programming";
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
        else if(!GPPage.parse()) {
            tabbedPane.setSelectedComponent(GPPage.getComponent());
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
        progress.show("Genetic Programme");

        // Get a copy of the values in the GUI, so that if the user changes
        // them, it won't screw up the GP.
        TradingDate startDate = quoteRangePage.getStartDate();
        TradingDate endDate = quoteRangePage.getEndDate();
        float initialCapital = portfolioPage.getInitialCapital();
        float tradeCost = portfolioPage.getTradeCost();
        int breedingPopulation = GPPage.getBreedingPopulation();
        int displayPopulation = GPPage.getDisplayPopulation();
        float stockValue = portfolioPage.getStockValue();
        int numberStocks = portfolioPage.getNumberStocks();

        // quote bundle needs to load 30 days before quote range.
        GPQuoteBundle quoteBundle =
            new GPQuoteBundle(new ScriptQuoteBundle(quoteRangePage.getQuoteRange()), 30);

        if(!thread.isInterrupted()) {
            int numberGenerations = GPPage.getGenerations();
            int population = GPPage.getPopulation();

            progress.setIndeterminate(false);
            progress.setMaximum(numberGenerations * population);
            progress.setProgress(0);
            progress.setMaster(true);

            GeneticProgramme geneticProgramme =
                new GeneticProgramme(quoteBundle,
                                     quoteRangePage.getOrderComparator(quoteBundle),
                                     startDate,
                                     endDate,
                                     initialCapital,
                                     stockValue,
                                     numberStocks,
                                     tradeCost,
                                     breedingPopulation,
                                     1);

            for(int generation = 1; generation <= numberGenerations; generation++) {
                if(thread.isInterrupted())
                    break;

                for(int individual = 1; individual < population; individual++) {
                    if(thread.isInterrupted())
                        break;

                    progress.setNote("Generation " + generation + " of " +
                                     numberGenerations);
                    geneticProgramme.nextIndividual();
                    progress.increment();
                }

                // needs to stop the GP and put up a dialog if the breeding population == 0.
                geneticProgramme.nextGeneration();
                display(getResults(geneticProgramme, displayPopulation, quoteBundle, 
                                   startDate, endDate,
                                   initialCapital, tradeCost, generation));
            }
        }

        ProgressDialogManager.closeProgressDialog(progress);
    }

    private List getResults(GeneticProgramme geneticProgramme,
                            int displayPopulation,
                            QuoteBundle quoteBundle,
                            TradingDate startDate,
                            TradingDate endDate,
                            float initialCapital,
                            float tradeCost,
                            int generation) {
        // Create a list of results from the top breeding individuals
        List results = new ArrayList();
        int numberResults = Math.min(geneticProgramme.getActualBreedingPopulation(),
                                     displayPopulation);

        for(int i = 0; i < numberResults; i++) {
            int offset = geneticProgramme.getActualBreedingPopulation() - 1 - i;

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
			    CommandManager.getInstance().newGPResultTable();
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
