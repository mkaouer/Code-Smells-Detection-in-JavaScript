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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mov.prefs.PreferencesManager;
import org.mov.ui.GridBagHelper;
import org.mov.util.Locale;

/**
 * An analysis tool page that lets the user enter basic Genetic
 * Programming configuration parameters. This page is used only
 * by the {@link GPModule}. The page contains the following user fields:
 *
 * <ul><li>Generations</li>
 *     <li>Population</li>
 *     <li>Breeding Population</li>
 *     <li>Display Population</li>
 *     <li>Window Size</li>
 * </ul>
 *
 * The generations field describes the number of generations the
 * GP will run for. The population field describes the number of
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
 * <p>
 * The window size field describes the number of quote days that
 * the buy/sell rules can access. When equations are evaluating buy or
 * sell decisions, they can only access this many quote days into the
 * past.
 *
 * @author Andrew Leppard
 */
public class GPPage extends JPanel implements AnalyserPage {

    private JDesktopPane desktop;

    // Swing components
    private JTextField generationsTextField;
    private JTextField windowTextField;
    private JTextField populationTextField;
    private JTextField breedingPopulationTextField;
    private JTextField displayPopulationTextField;

    // Parsed input
    private int generations;
    private int window;
    private int population;
    private int breedingPopulation;
    private int displayPopulation;

    /** Minimum number of quote days an equation can see. */
    private final static int MINIMUM_WINDOW_SIZE = 3;

    /**
     * Construct a new genetic programming parameters page.
     *
     * @param desktop the desktop
     */
    public GPPage(JDesktopPane desktop) {
        this.desktop = desktop;
        layoutPage();
    }

    public void load(String key) {
        // Load last GUI settings from preferences
	HashMap settings =
            PreferencesManager.loadAnalyserPageSettings(key + getClass().getName());

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

            if(setting.equals("generations"))
                generationsTextField.setText(value);
            else if(setting.equals("window"))
                windowTextField.setText(value);
            else if(setting.equals("population"))
                populationTextField.setText(value);
            else if(setting.equals("breeding_population"))
                breedingPopulationTextField.setText(value);
            else if(setting.equals("display_population"))
                displayPopulationTextField.setText(value);
        }
    }

    public void save(String key) {
        HashMap settings = new HashMap();

	settings.put("generations", generationsTextField.getText());
	settings.put("window", windowTextField.getText());
	settings.put("population", populationTextField.getText());
	settings.put("breeding_population", breedingPopulationTextField.getText());
	settings.put("display_population", displayPopulationTextField.getText());

        PreferencesManager.saveAnalyserPageSettings(key + getClass().getName(),
                                                    settings);
    }

    public boolean parse() {
        generations = 0;
        population = 0;
        breedingPopulation = 0;
        displayPopulation = 0;
        window = 0;

        try {
	    if(!generationsTextField.getText().equals(""))
		generations =
		    Integer.parseInt(generationsTextField.getText());

	    if(!windowTextField.getText().equals(""))
		window =
		    Integer.parseInt(windowTextField.getText());

	    if(!populationTextField.getText().equals(""))
		population =
		    Integer.parseInt(populationTextField.getText());
	    	
	    if(!breedingPopulationTextField.getText().equals(""))
		breedingPopulation =
		    Integer.parseInt(breedingPopulationTextField.getText());

	    if(!displayPopulationTextField.getText().equals(""))
		displayPopulation =
		    Integer.parseInt(displayPopulationTextField.getText());
	}
	catch(NumberFormatException e) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("ERROR_PARSING_NUMBER",
                                                                   e.getMessage()),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
	}

        if(displayPopulation > breedingPopulation) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("DISPLAY_POPULATION_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        if(generations <= 0) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("NO_GENERATION_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        if(window < MINIMUM_WINDOW_SIZE) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("WINDOW_SIZE_ERROR", MINIMUM_WINDOW_SIZE),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(population <= 0) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("NO_INDIVIDUAL_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        if(breedingPopulation <= 0) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("NO_BREEDING_INDIVIDUAL_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        if(displayPopulation <= 0) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("NO_DISPLAY_INDIVIDUAL_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    return false;
        }

        return true;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("GP_PAGE_PARAMETERS_SHORT");
    }

    /**
     * Return the number of generations in the genetic programme.
     *
     * @return the number of generations
     */
    public int getGenerations() {
        return generations;
    }

    /**
     * Return the number of quote days that any indivdual's buy or sell rules
     * can access at anyone time. The buy or sell rule will only be able to
     * see this many quote days into the past.
     *
     * @return the window size in days
     */
    public int getWindow() {
        return window;
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

    private void layoutPage() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TitledBorder titledBorder = new TitledBorder(Locale.getString("GP_PAGE_PARAMETERS_LONG"));
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
        windowTextField =
            GridBagHelper.addTextRow(innerPanel, Locale.getString("WINDOW_SIZE"), "",
                                     gridbag, c,
                                     5);

        panel.add(innerPanel, BorderLayout.NORTH);
        add(panel);
    }
    
}
