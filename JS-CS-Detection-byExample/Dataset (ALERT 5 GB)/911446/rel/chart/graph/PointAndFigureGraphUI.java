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

package nz.org.venice.chart.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 * The Point and Figure graph user interface.
 *
 * @author Andrew Leppard
 * @see PointAndFigureGraph
 */
public class PointAndFigureGraphUI implements GraphUI {

    // The graph's user interface
    private JPanel panel;
    private JTextField priceReversalTextField;
    private JTextField boxPriceTextField;

    // String name of settings
    protected final static String PRICE_REVERSAL_SCALE = "price_reversal_scale";
    protected final static String BOX_PRICE_SCALE = "box_price";

    // Limits
    private final static double MINIMUM_PRICE_SCALE = 0.0001D;

    // Default values - these are not constants because the default
    // value will be set depending on the graph
    private double defaultPriceReversalScale;
    private double defaultBoxPriceScale;

    /**
     * Create a new Point and Figure user interface with the initial settings.
     *
     * @param settings the initial settings
     * @param defaultPriceReversalScale default price scale based on data
     */
    public PointAndFigureGraphUI(HashMap settings, 
				 double defaultPriceReversalScale,
				 double defaultBoxPriceScale) {
        this.defaultPriceReversalScale = defaultPriceReversalScale;
	this.defaultBoxPriceScale = defaultBoxPriceScale;
        buildPanel();
        setSettings(settings);
    }

    /**
     * Build the user interface JPanel.
     */
    private void buildPanel() {
        panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(layout);

        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        priceReversalTextField =
            GridBagHelper.addTextRow(panel, Locale.getString("PRICE_REVERSAL_SCALE"), "",
                                     layout, c, 8);

	boxPriceTextField =
            GridBagHelper.addTextRow(panel, Locale.getString("BOX_PRICE_SCALE"), "",
                                     layout, c, 8);
		
    }

    public String checkSettings() {
	return checkSettings(getSettings());
    }

    public String checkSettings(HashMap settings) {
        // Check price scale
        String priceReversalScaleString = (String)settings.get(PRICE_REVERSAL_SCALE);
	String boxPriceScaleString = (String)settings.get(BOX_PRICE_SCALE);
        double priceReversalScale;
	double boxPriceScale;
	

        try {
            priceReversalScale = Double.parseDouble(priceReversalScaleString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", priceReversalScaleString);
        }

        try {
            boxPriceScale = Double.parseDouble(boxPriceScaleString);
        }
        catch(NumberFormatException e) {
            return Locale.getString("ERROR_PARSING_NUMBER", boxPriceScaleString);
        }

        if (priceReversalScale < MINIMUM_PRICE_SCALE)
            return Locale.getString("ERROR_PRICE_SCALE_TOO_SMALL");

        if (boxPriceScale < MINIMUM_PRICE_SCALE)
            return Locale.getString("ERROR_PRICE_SCALE_TOO_SMALL");

        // Settings are OK
        return null;
    }

    public HashMap getSettings() {
        HashMap settings = new HashMap();
        settings.put(PRICE_REVERSAL_SCALE, priceReversalTextField.getText());
	settings.put(BOX_PRICE_SCALE, boxPriceTextField.getText());
        return settings;
    }

    public void setSettings(HashMap settings) {
        priceReversalTextField.setText(Double.toString(getPriceReversalScale(settings,
									     defaultPriceReversalScale)));
        boxPriceTextField.setText(Double.toString(getBoxPriceScale(settings,
								   defaultBoxPriceScale)));
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Retrieve the price scale from the settings hashmap. If the hashmap
     * is empty, then return the default price scale.
     *
     * @param settings the settings
     * @param defaultPriceReversalScale the default price scale
     * @return the price scale
     */
    public static double getPriceReversalScale(HashMap settings, double defaultPriceReversalScale) {
	
	double priceReversalScale = defaultPriceReversalScale;

        String priceReversalScaleString = (String)settings.get(PRICE_REVERSAL_SCALE);

        if(priceReversalScaleString != null) {
            try {
                priceReversalScale = Double.parseDouble(priceReversalScaleString);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return priceReversalScale;
    }

    /**
     * Retrieve the box price from the settings hashmap. If the hashmap
     * is empty, then return the default box scale.
     *
     * @param settings the settings
     * @param defaultBoxPrice the default price scale
     * @return the box price
     */
    public static double getBoxPriceScale(HashMap settings, double defaultBoxPrice) {
	
	double boxPriceScale = defaultBoxPrice;
	
        String boxPriceScaleString = (String)settings.get(BOX_PRICE_SCALE);


        if(boxPriceScaleString != null) {
            try {
                boxPriceScale = Double.parseDouble(boxPriceScaleString);
            }
            catch(NumberFormatException e) {
                // Value should already be checked
                assert false;
            }
        }

        return boxPriceScale;
    }


}