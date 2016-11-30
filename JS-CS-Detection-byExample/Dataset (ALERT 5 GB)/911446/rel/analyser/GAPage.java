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

package nz.org.venice.analyser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 * An analysis tool page that lets the user enter basic Genetic
 * Algorithm configuration parameters. This page is used only
 * by the {@link GAModule}. The page contains the following user fields:
 *
 * <ul><li>Generations</li>
 *     <li>Population</li>
 *     <li>Breeding Population</li>
 *     <li>Display Population</li>
 * </ul>
 *
 * The generations field describes the number of generations the
 * GA will run for. The population field describes the number of
 * individuals that will be randomly generated (or breed) for
 * that generation. The first generation may generate more random
 * individuals than this value, it will generate enough individuals
 * so that the minimum breeding population has been reached.
 * <p>
 * The breeding population field describes the number of individuals
 * that will breed, i.e. will contribute parts of their buy/sell
 * rules to the next generation. The display population field describes
 * the number of top individuals that should be displayed in the results
 * table.
 *
 * @author Alberto Nacher
 */
public class GAPage extends Page implements AnalyserPage {

    // Swing components
    private JTextField generationsTextField;
    private JTextField populationTextField;
    private JTextField breedingPopulationTextField;
    private JTextField displayPopulationTextField;
    private JTextField randomPercentageTextField;

    // Parsed input
    private int generations;
    private int population;
    private int breedingPopulation;
    private int displayPopulation;
    private int randomPercentage;

    /** Minimum number of quote days an equation can see. */
    private final static int MINIMUM_WINDOW_SIZE = 3;

    /**
     * Construct a new genetic programming parameters page.
     *
     * @param desktop the desktop
     */
    public GAPage(JDesktopPane desktop) {
        this.desktop = desktop;
        layoutPage();
    }

    public void load(String key) {
        // Load last GUI settings from preferences
	HashMap settings =
            PreferencesManager.getAnalyserPageSettings(key + getClass().getName());

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

            if(setting.equals("generations"))
                generationsTextField.setText(value);
            else if(setting.equals("population"))
                populationTextField.setText(value);
            else if(setting.equals("breeding_population"))
                breedingPopulationTextField.setText(value);
            else if(setting.equals("display_population"))
                displayPopulationTextField.setText(value);
            else if(setting.equals("random_percentage"))
                randomPercentageTextField.setText(value);
        }
    }

    public void save(String key) {
        HashMap settings = new HashMap();

	settings.put("generations", generationsTextField.getText());
	settings.put("population", populationTextField.getText());
	settings.put("breeding_population", breedingPopulationTextField.getText());
	settings.put("display_population", displayPopulationTextField.getText());
	settings.put("random_percentage", randomPercentageTextField.getText());

        PreferencesManager.putAnalyserPageSettings(key + getClass().getName(),
                                                   settings);
    }

    public boolean parse() {
        generations = 0;
        population = 0;
        breedingPopulation = 0;
        displayPopulation = 0;
        randomPercentage = 20;

        try {
	    if(!generationsTextField.getText().equals(""))
		generations =
		    Integer.parseInt(generationsTextField.getText());

	    if(!populationTextField.getText().equals(""))
		population =
		    Integer.parseInt(populationTextField.getText());
	    	
	    if(!breedingPopulationTextField.getText().equals(""))
		breedingPopulation =
		    Integer.parseInt(breedingPopulationTextField.getText());

	    if(!displayPopulationTextField.getText().equals(""))
		displayPopulation =
		    Integer.parseInt(displayPopulationTextField.getText());

	    if(!randomPercentageTextField.getText().equals(""))
		randomPercentage =
		    Integer.parseInt(randomPercentageTextField.getText());
	}
	catch(NumberFormatException e) {
		showErrorMessage(
        		Locale.getString("ERROR_PARSING_NUMBER",e.getMessage()),
                Locale.getString("INVALID_GA_ERROR"));
	    return false;
	}

        if(displayPopulation > breedingPopulation) {
        	showErrorMessage(
            		Locale.getString("DISPLAY_POPULATION_ERROR"),
                    Locale.getString("INVALID_GA_ERROR"));
	    return false;
        }

        if(generations <= 0) {
        	showErrorMessage(
            		Locale.getString("NO_GENERATION_ERROR"),
                    Locale.getString("INVALID_GA_ERROR"));
	    return false;
        }

        if(population <= 0) {
        	showErrorMessage(
            		Locale.getString("NO_INDIVIDUAL_ERROR"),
                    Locale.getString("INVALID_GA_ERROR"));
	    return false;
        }

        if(breedingPopulation <= 0) {
        	showErrorMessage(
            		Locale.getString("NO_BREEDING_INDIVIDUAL_ERROR"),
                    Locale.getString("INVALID_GA_ERROR"));
	    return false;
        }

        if(displayPopulation <= 0) {
        	showErrorMessage(
            		Locale.getString("NO_DISPLAY_INDIVIDUAL_ERROR"),
                    Locale.getString("INVALID_GA_ERROR"));
	    return false;
        }

        if(randomPercentage < 0 || randomPercentage > 100) {
        	showErrorMessage(
            		Locale.getString("ERROR_GA_RANDOM_PERCENTAGE_ERROR"),
                    Locale.getString("INVALID_GA_ERROR"));
	    return false;
        }

        return true;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("GA_PAGE_PARAMETERS_SHORT");
    }

    /**
     * Return the number of generations in the genetic algorithm.
     *
     * @return the number of generations
     */
    public int getGenerations() {
        return generations;
    }

    /**
     * Return the number of individuals to generate for each generation.
     * The first generation may generate more than this number of
     * individuals, if the breeding population size has not been reached.
     *
     * @return generation's population size
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Return the number of individuals, for each generation, that can
     * contribute their buy/sell rules to the next generation.
     *
     * @return the breeding population size
     */
    public int getBreedingPopulation() {
        return breedingPopulation;
    }

    /**
     * Return the number of top performing individuals that are displayed
     * in the results table.
     *
     * @return the display population size
     */
    public int getDisplayPopulation() {
        return displayPopulation;
    }

    /**
     * Return the percentage of the likelihood of changing a GA parameter in a random manner
     *  instead of a combination of mother and father rules.
     *
     * @return the percentage
     */
    public int getRandomPercentage() {
        return randomPercentage;
    }
    
    private void layoutPage() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TitledBorder titledBorder = new TitledBorder(Locale.getString("GA_PAGE_PARAMETERS_LONG"));
        JPanel panel = new JPanel();
        panel.setBorder(titledBorder);
        panel.setLayout(new BorderLayout());

        JPanel innerPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        innerPanel.setLayout(gridbag);

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        generationsTextField =
            GridBagHelper.addTextRow(innerPanel, Locale.getString("GENERATIONS"), "",
                                     gridbag, c,
                                     5);

        populationTextField =
            GridBagHelper.addTextRow(innerPanel,
                                     Locale.getString("POPULATION"), "",
                                     gridbag, c,
                                     10);
        breedingPopulationTextField =
            GridBagHelper.addTextRow(innerPanel,
                                     Locale.getString("BREEDING_POPULATION"), "",
                                     gridbag, c, 7);

        displayPopulationTextField =
            GridBagHelper.addTextRow(innerPanel,
                                     Locale.getString("DISPLAY_POPULATION"), "",
                                     gridbag, c, 7);

        randomPercentageTextField =
            GridBagHelper.addTextRow(innerPanel,
                                     Locale.getString("RANDOM_PERCENTAGE"), "",
                                     gridbag, c, 7);

        panel.add(innerPanel, BorderLayout.NORTH);
        add(panel);
    }
    
}
