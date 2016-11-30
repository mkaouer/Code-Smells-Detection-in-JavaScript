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

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

import org.mov.prefs.PreferencesManager;
import org.mov.ui.ConfirmDialog;
import org.mov.util.Locale;

public class GPGondolaSelection extends JPanel implements AnalyserPage {

    private final int maxPanels = 9;
    
    private JDesktopPane desktop;
    private Random random;
    
    // Panel inside the section (Titled Panels)
    GPGondolaSelectionPanel[] GPGondolaSelectionPanel = new GPGondolaSelectionPanel[maxPanels];

    public GPGondolaSelection(JDesktopPane desktop, double maxHeight) {
        this.desktop = desktop;
        random = new Random(System.currentTimeMillis());

        Dimension preferredSize = new Dimension();
        preferredSize.setSize(this.getPreferredSize().getWidth(), maxHeight/20);
        
        // Float or Integer
        int[] defaultValuesFloatInteger = {5000, 5000};
        String[] defaultTextFieldValuesFloatInteger = {Locale.getString("PERCENTAGE_FUNCTIONS", "float"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "integer")
        };
        GPGondolaSelectionPanel[0] = new GPGondolaSelectionPanel(defaultValuesFloatInteger.length,
                                desktop,
                                defaultValuesFloatInteger,
                                defaultTextFieldValuesFloatInteger);

        // Terminal Integers
        int[] defaultValuesTerminalIntegers = {5000,    /*Ordinary number has 50% probabilty as default
                                                * As default we don't generate DayOfYearExpression() or MonthExpression()
                                                * because it would make it easy for the GP to hook onto specific dates
                                                * where the market is low. By removing these it forces the GP
                                                * to use the stock data to generate buy/sell decisions.
                                                 */
                               0, 0, 1250, 1250, 750, 750, 500, 500};
        String[] defaultTextFieldValuesTerminalIntegers = {Locale.getString("PERCENTAGE_ORDINARY_NUMBER"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "dayofyear()"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "month()"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "day()"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "dayofweek()"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "daysfromstart"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "transactions"),
                                           /* order and held must be the last ones, because they can be skipped
                                            * according to GP parameters.
                                            * See getRandom in GPGondolaSelectionPanel for further details.
                                            * {@link GPGondolaSelectionPanel#getRandom}
                                           */
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "held"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "order")
        };
        GPGondolaSelectionPanel[1] = new GPGondolaSelectionPanel(defaultValuesTerminalIntegers.length,
                                desktop,
                                defaultValuesTerminalIntegers,
                                defaultTextFieldValuesTerminalIntegers);
        GPGondolaSelectionPanel[1].setLastButOneNotEnough();
        GPGondolaSelectionPanel[1].setLastNotEnough();

        // Terminal Floats
        int[] defaultValuesTerminalFloats = {7500, 1250, 1250};
        String[] defaultTextFieldValuesTerminalFloats = {Locale.getString("PERCENTAGE_ORDINARY_NUMBER"),
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "capital"),
                                           /* stockcapital must be the last one, because it can be skipped
                                            * according to GP parameters.
                                            * See getRandom in GPGondolaSelectionPanel for further details.
                                            * {@link GPGondolaSelectionPanel#getRandom}
                                           */
                                           Locale.getString("PERCENTAGE_FUNCTIONS", "stockcapital"),
        };
        GPGondolaSelectionPanel[2] = new GPGondolaSelectionPanel(defaultValuesTerminalFloats.length,
                                desktop,
                                defaultValuesTerminalFloats,
                                defaultTextFieldValuesTerminalFloats);
        GPGondolaSelectionPanel[2].setLastNotEnough();

        // Float Quote
        int[] defaultValuesFloatQuote = {2500, 2500, 2500, 2500};
        String[] defaultTextFieldValuesFloatQuote = {Locale.getString("PERCENTAGE_FUNCTIONS", "open"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "low"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "high"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "close")
        };
        GPGondolaSelectionPanel[3] = new GPGondolaSelectionPanel(defaultValuesFloatQuote.length,
                                desktop,
                                defaultValuesFloatQuote,
                                defaultTextFieldValuesFloatQuote);

        // Boolean
        int[] defaultValuesBoolean = {1112, 1111, 1111, 1111, 1111, 1111, 1111, 1111, 1111};
        String[] defaultTextFieldValuesBoolean = {Locale.getString("PERCENTAGE_FUNCTIONS", "not"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "="),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", ">="),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", ">"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "<="),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "<"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "!="),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "and"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "or")
        };
        GPGondolaSelectionPanel[4] = new GPGondolaSelectionPanel(defaultValuesBoolean.length,
                                desktop,
                                defaultValuesBoolean,
                                defaultTextFieldValuesBoolean);
        
        // Float Expression
        int[] defaultValuesFloatExpression = {400, 400, 400, 400, 400,
                                            400, 400, 400, 400, 400,
                                            400, 400, 400, 400, 400,
                                            400, 400, 400, 400, 400,
                                            400, 400, 400, 400, 400};
        String[] defaultTextFieldValuesFloatExpression = {Locale.getString("PERCENTAGE_TERMINAL"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "+"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "-"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "*"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "/"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "percent()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "if(){}else{}"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "lag()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "min()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "max()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sum()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sqrt()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "abs()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "cos()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sin()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "log()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "exp()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "avg()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "ema()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "macd()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "momentum()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "rsi()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sd()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "bol_lower()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "bol_upper()")
        };
        GPGondolaSelectionPanel[5] = new GPGondolaSelectionPanel(defaultValuesFloatExpression.length,
                                desktop,
                                defaultValuesFloatExpression,
                                defaultTextFieldValuesFloatExpression);
        
        // Integer Expression
        int[] defaultValuesIntegerExpression = {480,
                                            476, 476, 476, 476, 476,
                                            476, 476, 476, 476, 476,
                                            476, 476, 476, 476, 476,
                                            476, 476, 476, 476, 476};
        String[] defaultTextFieldValuesIntegerExpression = {Locale.getString("PERCENTAGE_TERMINAL"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "+"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "-"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "*"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "/"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "percent()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "if(){}else{}"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "lag()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "min()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "max()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sum()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sqrt()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "abs()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "avg()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "ema()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "macd()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "momentum()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sd()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "bol_lower()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "bol_upper()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "obv()")
        };
        GPGondolaSelectionPanel[6] = new GPGondolaSelectionPanel(defaultValuesIntegerExpression.length,
                                desktop,
                                defaultValuesIntegerExpression,
                                defaultTextFieldValuesIntegerExpression);
        
        // Positive Short Integer Expression
        int[] defaultValuesPositiveShortIntegerExpression = {
                                            /* We put as default to zero the two following experssions:
                                             * '*' and '(+1)*float'.
                                             * That's done, because them both may increase the integer number too much
                                             * and we don't want that because we want a small positive integer.
                                             * Anyway user interface permits the use of the above expressions.
                                             */
                                            1250, 1250, 1250, 0, 1250,
                                            1250, 1250, 1250, 1250, 0, 0};
        String[] defaultTextFieldValuesPositiveShortIntegerExpression = {Locale.getString("PERCENTAGE_TERMINAL"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "+"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "-"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "*"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "/"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "percent()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "if(){}else{}"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "sqrt()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "abs()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "(+1)*(float expression)"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "integer expression")
        };
        GPGondolaSelectionPanel[7] = new GPGondolaSelectionPanel(defaultValuesPositiveShortIntegerExpression.length,
                                desktop,
                                defaultValuesPositiveShortIntegerExpression,
                                defaultTextFieldValuesPositiveShortIntegerExpression);
        
        // Positive Short Integer Expression
        int[] defaultValuesNegativeShortIntegerExpression = {
                                            /* We put as default to zero the two following experssions:
                                             * '*' and '(-1)*float'.
                                             * That's done, because them both may increase the integer number too much
                                             * and we don't want that because we want a small positive integer.
                                             * Anyway user interface permits the use of the above expressions.
                                             */
                                            1750, 1650, 1650, 0, 1650,
                                            1650, 1650, 0, 0};
        String[] defaultTextFieldValuesNegativeShortIntegerExpression = {Locale.getString("PERCENTAGE_TERMINAL"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "+"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "-"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "*"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "/"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "percent()"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "if(){}else{}"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "(-1)*(float expression)"),
                                  Locale.getString("PERCENTAGE_FUNCTIONS", "(-1)*(integer expression)")
        };
        GPGondolaSelectionPanel[8] = new GPGondolaSelectionPanel(defaultValuesNegativeShortIntegerExpression.length,
                                desktop,
                                defaultValuesNegativeShortIntegerExpression,
                                defaultTextFieldValuesNegativeShortIntegerExpression);

        
        setGraphic(preferredSize);
        
        setDefaults();
    }
    
    public int getRandomToGenerateFloatInteger() {
        return GPGondolaSelectionPanel[0].getRandom();
    }

    public int getRandomToGenerateTerminalInteger(boolean isOkLastButOne, boolean isOkLast) {
        return GPGondolaSelectionPanel[1].getRandom(isOkLastButOne, isOkLast);
    }

    public int getRandomToGenerateTerminalFloat(boolean isOkLast) {
        return GPGondolaSelectionPanel[2].getRandom(isOkLast);
    }

    public int getRandomToGenerateFloatQuote() {
        return GPGondolaSelectionPanel[3].getRandom();
    }

    public int getRandomToGenerateBoolean() {
        return GPGondolaSelectionPanel[4].getRandom();
    }

    public int getRandomToGenerateFloat() {
        return GPGondolaSelectionPanel[5].getRandom();
    }

    public int getRandomToGenerateInteger() {
        return GPGondolaSelectionPanel[6].getRandom();
    }

    public int getRandomToGeneratePositiveShortInteger() {
        return GPGondolaSelectionPanel[7].getRandom();
    }

    public int getRandomToGenerateNegativeShortInteger() {
        return GPGondolaSelectionPanel[8].getRandom();
    }

    public void load(String key) {
        // Load last GUI settings from preferences
	HashMap settings =
            PreferencesManager.loadAnalyserPageSettings(key + getClass().getName());

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

            GPGondolaSelectionPanel[0].load(setting, "gp_float_integer", value);
            GPGondolaSelectionPanel[1].load(setting, "gp_terminal_integer", value);
            GPGondolaSelectionPanel[2].load(setting, "gp_terminal_float", value);
            GPGondolaSelectionPanel[3].load(setting, "gp_float_quote", value);
            GPGondolaSelectionPanel[4].load(setting, "gp_boolean", value);
            GPGondolaSelectionPanel[5].load(setting, "gp_float_expression", value);
            GPGondolaSelectionPanel[6].load(setting, "gp_integer_expression", value);
            GPGondolaSelectionPanel[7].load(setting, "gp_pos_integer_expression", value);
            GPGondolaSelectionPanel[8].load(setting, "gp_neg_integer_expression", value);
        }
    }

    public void save(String key) {
        
        HashMap settings = new HashMap();

	GPGondolaSelectionPanel[0].save(settings, "gp_float_integer");
	GPGondolaSelectionPanel[1].save(settings, "gp_terminal_integer");
	GPGondolaSelectionPanel[2].save(settings, "gp_terminal_float");
	GPGondolaSelectionPanel[3].save(settings, "gp_float_quote");
	GPGondolaSelectionPanel[4].save(settings, "gp_boolean");
	GPGondolaSelectionPanel[5].save(settings, "gp_float_expression");
	GPGondolaSelectionPanel[6].save(settings, "gp_integer_expression");
	GPGondolaSelectionPanel[7].save(settings, "gp_pos_integer_expression");
	GPGondolaSelectionPanel[8].save(settings, "gp_neg_integer_expression");

        PreferencesManager.saveAnalyserPageSettings(key + getClass().getName(),
                                                    settings);
    }

    public boolean parse() {
        boolean retValue = true;
        if(!isAllValuesAcceptable()) {
            retValue = false;
        } else {
            if(!isFitAll()) {
                ConfirmDialog dialog = new ConfirmDialog(desktop,
                                                         Locale.getString("GP_FIT"),
                                                         Locale.getString("GP_FIT_TITLE"));
                boolean returnConfirm = dialog.showDialog();
                if (returnConfirm) {
                    fitAll();
                    retValue = true;
                } else {
                    retValue = false;
                }
            }
        }
        return retValue;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("GP_GONDOLA_SELECTION_SHORT_TITLE");
    }

    private void setDefaults() {
        for (int ii=0; ii<maxPanels; ii++) {
            GPGondolaSelectionPanel[ii].setDefaults();
        }
    }
    
    private void fitAll() {
        // Fit all values one after another (only if previous one was fitted without error)
        for (int ii=0; ii<maxPanels; ii++) {
            GPGondolaSelectionPanel[ii].fit();
        }
    }

    private boolean isFitAll() {
        boolean retValue = true;
        // Fit all values one after another and exit when false is found
        for (int ii=0; retValue && (ii<maxPanels); ii++) {
            retValue = GPGondolaSelectionPanel[ii].isFit();
        }
        return retValue;
    }

    private boolean isAllValuesAcceptable() {
        boolean retValue = true;
        // Fit all values one after another and exit when false is found
        for (int ii=0; retValue && (ii<maxPanels); ii++) {
            retValue = GPGondolaSelectionPanel[ii].isAllValuesAcceptable();
        }
        return retValue;
    }

    private void setGraphic(Dimension preferredSize) {

        TitledBorder titledBorder = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE"));
        
        this.setBorder(titledBorder);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(preferredSize);

        JPanel upDownPanel = new JPanel();
        upDownPanel.setLayout(new BoxLayout(upDownPanel, BoxLayout.Y_AXIS));
        
        JScrollPane upDownScrollPane = new JScrollPane(upDownPanel);
        upDownScrollPane.setLayout(new ScrollPaneLayout());
        
        TitledBorder[] titledBorderSections = new TitledBorder[maxPanels];
        titledBorderSections[0] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_FLOAT_INTEGER"));
        titledBorderSections[1] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_TERMINAL_INTEGER"));
        titledBorderSections[2] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_TERMINAL_FLOAT"));
        titledBorderSections[3] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_FLOAT_QUOTE"));
        titledBorderSections[4] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_BOOLEAN"));
        titledBorderSections[5] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_FLOAT"));
        titledBorderSections[6] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_INTEGER"));
        titledBorderSections[7] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_POSITIVE_SHORT_INTEGER"));
        titledBorderSections[8] = new TitledBorder(Locale.getString("GP_GONDOLA_SELECTION_TITLE_NEGATIVE_SHORT_INTEGER"));
        for (int ii=0; ii<maxPanels; ii++) {
            GPGondolaSelectionPanel[ii].setBorder(titledBorderSections[ii]);
            upDownPanel.add(GPGondolaSelectionPanel[ii]);
        }
        
        this.add(upDownScrollPane);
    }
}
