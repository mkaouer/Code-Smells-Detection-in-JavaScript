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
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import org.mov.main.*;
import org.mov.util.*;
import org.mov.parser.*;
import org.mov.prefs.*;
import org.mov.portfolio.*;
import org.mov.quote.*;
import org.mov.ui.*;

public class PaperTradeModule extends JPanel implements Module,
							ActionListener {

    private PropertyChangeSupport propertySupport;
    
    private JDesktopPane desktop;

    // Java Widgets
    private JTextField fromDateTextField;
    private JTextField toDateTextField;
    private QuoteRangeComboBox quoteRangeComboBox;
    private JTextField aRangeTextField;
    private JTextField bRangeTextField;
    private JTextField cRangeTextField;
    private EquationComboBox buyRuleEquationComboBox;
    private EquationComboBox sellRuleEquationComboBox;
    private JTextField initialCapitalTextField;
    private JTextField tradeCostTextField;

    private JButton tradeButton;
    private JButton closeButton;

    // Data parsed from Java Widgets
    private TradingDate fromDate;
    private TradingDate toDate;
    private QuoteRange quoteRange;
    private String buyRuleString;
    private String sellRuleString;
    private float initialCapital;
    private float tradeCost;
    private int aRange;
    private int bRange;
    private int cRange;

    private ScriptQuoteBundle quoteBundle;

    // Single result table for entire application
    private static ModuleFrame resultsFrame = null;

    // Group all data together needed to make a paper trade
    private class PaperTradeData {
	public Portfolio portfolio;
	public String buyRuleString;
	public String sellRuleString;
    }

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

	setLayout(new BorderLayout());

	Box paperTradeOptions = Box.createVerticalBox();

	// Date panel
	{
	    TitledBorder dateTitled = new TitledBorder("Date Range");
	    JPanel datePanel = new JPanel();
	    datePanel.setBorder(dateTitled);

	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    datePanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

	    fromDateTextField = 
	    	addTextRow(datePanel, "From Date", "", gridbag, c, 15);
	    toDateTextField = 
		addTextRow(datePanel, "To Date", "", gridbag, c, 15);

	    paperTradeOptions.add(datePanel);
	}

	// Symbols Panel
	{
	    TitledBorder symbolTitled = new TitledBorder("Symbol");
	    JPanel symbolPanel = new JPanel();
	    symbolPanel.setBorder(symbolTitled);

	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    symbolPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

            JLabel label = new JLabel("Symbols");
            c.gridwidth = 1;
            gridbag.setConstraints(label, c);
            symbolPanel.add(label);

            quoteRangeComboBox = new QuoteRangeComboBox();
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(quoteRangeComboBox, c);
            symbolPanel.add(quoteRangeComboBox);

	    paperTradeOptions.add(symbolPanel);
	}

	// Equations Panel
	{
	    TitledBorder equationTitled = new TitledBorder("Buy/Sell Rules");
	    JPanel equationPanel = new JPanel();
	    equationPanel.setBorder(equationTitled);

	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    equationPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

	    aRangeTextField = 
		addTextRow(equationPanel, "Range $A: 1 to", "", 
			   gridbag, c, 3);
	    bRangeTextField = 
		addTextRow(equationPanel, "Range $B: 1 to", "", 
			   gridbag, c, 3);
	    cRangeTextField = 
		addTextRow(equationPanel, "Range $C: 1 to", "", 
			   gridbag, c, 3);

	    buyRuleEquationComboBox = 
		addEquationRow(equationPanel, "Buy Rule", "", gridbag, c);
	    sellRuleEquationComboBox = 
		addEquationRow(equationPanel, "Sell Rule", "", gridbag, c);
	    
	    paperTradeOptions.add(equationPanel);

	}

	// Portfolio Panel
	{
	    TitledBorder portfolioTitled = new TitledBorder("Portfolio");
	    JPanel portfolioPanel = new JPanel();
	    portfolioPanel.setBorder(portfolioTitled);

	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    portfolioPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

	    initialCapitalTextField = 
		addTextRow(portfolioPanel, "Initial Capital", "", gridbag, c, 
			   15);
	    tradeCostTextField =
		addTextRow(portfolioPanel, "Trade Cost", "", gridbag, c, 15);
	    
	    paperTradeOptions.add(portfolioPanel);
	}

	add(paperTradeOptions, BorderLayout.CENTER);

	// Paper trade, close buttons
	JPanel buttonPanel = new JPanel();
	tradeButton = new JButton("Paper Trade");
	tradeButton.addActionListener(this);
	closeButton = new JButton("Close");
	closeButton.addActionListener(this);
	buttonPanel.add(tradeButton);
	buttonPanel.add(closeButton);

	add(buttonPanel, BorderLayout.SOUTH);
    }

    // Helper method which adds a new text field in a new row to the given 
    // grid bag layout.
    private JTextField addTextRow(JPanel panel, String field, String value,
				  GridBagLayout gridbag,
				  GridBagConstraints c,
				  int length) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

	JTextField text = new JTextField(value, length);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(text, c);
	panel.add(text);

	return text;
    }

    // Helper method which adds a new text field in a new row to the given 
    // grid bag layout.
    private EquationComboBox addEquationRow(JPanel panel, String field, 
					    String value,
					    GridBagLayout gridbag,
					    GridBagConstraints c) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

	EquationComboBox comboBox = new EquationComboBox(value);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(comboBox, c);
	panel.add(comboBox);

	return comboBox;
    }

    // Load GUI settings from preferences
    private void load() {
	// Load last GUI settings from preferences
	HashMap settings = PreferencesManager.loadLastPaperTradeSettings();

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get((Object)setting);

	    if(setting.equals("from_date"))
		fromDateTextField.setText(value);
	    else if(setting.equals("to_date"))
		toDateTextField.setText(value);
	    else if(setting.equals("symbols"))
		quoteRangeComboBox.setText(value);
	    else if(setting.equals("buy_rule"))
		buyRuleEquationComboBox.setEquationText(value);
	    else if(setting.equals("sell_rule"))
		sellRuleEquationComboBox.setEquationText(value);
	    else if(setting.equals("initial_capital"))
		initialCapitalTextField.setText(value);
	    else if(setting.equals("trade_cost"))
		tradeCostTextField.setText(value);
	    else if(setting.equals("arange"))
		aRangeTextField.setText(value);
	    else if(setting.equals("brange"))
		bRangeTextField.setText(value);
	    else if(setting.equals("crange"))
		cRangeTextField.setText(value);
	    else
		assert false;
	}
    }

    // Save GUI settings to preferences
    public void save() {
	HashMap settings = new HashMap();

	settings.put("from_date", fromDateTextField.getText());
	settings.put("to_date", toDateTextField.getText());
	settings.put("symbols", quoteRangeComboBox.getText());
	settings.put("buy_rule", buyRuleEquationComboBox.getEquationText());
	settings.put("sell_rule", sellRuleEquationComboBox.getEquationText());
	settings.put("initial_capital", initialCapitalTextField.getText());
	settings.put("trade_cost", tradeCostTextField.getText());
	settings.put("arange", aRangeTextField.getText());
	settings.put("brange", bRangeTextField.getText());
	settings.put("crange", cRangeTextField.getText());

	PreferencesManager.saveLastPaperTradeSettings(settings);
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

    /**
     * Handle widget events.
     *
     * @param	e	action event
     */
    public void actionPerformed(final ActionEvent e) {

	if(e.getSource() == closeButton) {
	    // Tell frame we want to close
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}
	else if(e.getSource() == tradeButton) {
	    Thread t = new Thread(new Runnable() {
		    public void run() {
                        // Before we paper trade, save our interface results
                        // so if the programme crashes etc our stuff is still there
                        save();

			// Read data from GUI and load quote data
			if(parseInterface()) 
			    // If its OK generate and display portfolios
			    displayPaperTrades(paperTrade());
		    }
		});

	    t.start();
	}

    }

    // Read data from interface and display if there are any errors.
    // Return true if the data is OK
    private boolean parseInterface() {
        return (parseDateRange() && parseSymbols() && parseBuySellRules() && 
                parsePortfolio());
    }
    
    // Read data from date ranges panel and display if there are any errors.
    // Return true if the data is OK
    private boolean parseDateRange() {

	fromDate = new TradingDate(fromDateTextField.getText(),
				   TradingDate.BRITISH);
	toDate = new TradingDate(toDateTextField.getText(),
				 TradingDate.BRITISH);

	if(fromDate.getYear() == 0 || toDate.getYear() == 0 ||
	   fromDate.after(toDate)) {
	    buildPaperTradeError("Invalid date range");
	    return false;
	}

        try {
            int offset = QuoteCache.getInstance().dateToOffset(fromDate);
        }
        catch(WeekendDateException e) {
            buildPaperTradeError("From date is on a weekend");
	    return false;
        }

        try {
            int offset = QuoteCache.getInstance().dateToOffset(toDate);
        }
        catch(WeekendDateException e) {
            buildPaperTradeError("To date is on a weekend");
	    return false;
        }

        return true;
    }

    // Read data from the symbols panel and display if there are any errors.
    // Return true if the data is OK
    private boolean parseSymbols() {
        // The date range panel must be parsed first!
        assert fromDate != null && toDate != null;

        quoteRange = quoteRangeComboBox.getQuoteRange();

        // Was there an error? (the combo box will display it if
        // there was one).
        if(quoteRange == null)
            return false;

        quoteRange.setFirstDate(fromDate);
        quoteRange.setLastDate(toDate);

        return true;
    }

    // Read data from the buy/sells panel and display if there are any errors.
    // Return true if the data is OK
    private boolean parseBuySellRules() {
	Parser parser = new Parser();
	
	buyRuleString = buyRuleEquationComboBox.getEquationText();
	sellRuleString = sellRuleEquationComboBox.getEquationText();

	// Create copies so we can remove $A, $B, $C - they aren't needed
	// for parsing. We only want to check the equation is OK so set
	// $A, $B, $C to 1, 1, 1 if present
	String buyRuleStringCopy = new String(buyRuleString);
	String sellRuleStringCopy = new String(sellRuleString);

	buyRuleStringCopy = substituteVariables(buyRuleStringCopy, 1, 1, 1);
	sellRuleStringCopy = substituteVariables(sellRuleStringCopy, 1, 1, 1);

	try {
	    Expression buyRule = parser.parse(buyRuleStringCopy);
	}
	catch(ExpressionException e) {	   
	    buildPaperTradeError("Error parsing buy rule: " +
				 e.getReason());
	    return false;
	}

	try {
	    Expression sellRule = parser.parse(sellRuleStringCopy);
	}
	catch(ExpressionException e) {
	    buildPaperTradeError("Error parsing sell rule: " +
				 e.getReason());
	    return false;
	}

        // Now try reading the ranges
	try {
	    if(!aRangeTextField.getText().equals(""))
		aRange = 
		    Integer.parseInt(aRangeTextField.getText());

	    if(!bRangeTextField.getText().equals(""))
		bRange =
		    Integer.parseInt(bRangeTextField.getText());

	    if(!cRangeTextField.getText().equals(""))
		cRange =
		    Integer.parseInt(cRangeTextField.getText());
        }
	catch(NumberFormatException e) {
	    buildPaperTradeError("Can't parse number '" +
				 e.getMessage() + "'");
	    return false;
	}

        return true;
    }

    // Read data from the portfolio panel and display if there are any errors.
    // Return true if the data is OK
    private boolean parsePortfolio() {
	initialCapital = 0.0F;
	tradeCost = 0.0F;

	try {
	    if(!initialCapitalTextField.getText().equals(""))
		initialCapital = 
		    Float.parseFloat(initialCapitalTextField.getText());
	    	   
	    if(!tradeCostTextField.getText().equals(""))
		tradeCost = 
		    Float.parseFloat(tradeCostTextField.getText());
	}
	catch(NumberFormatException e) {
	    buildPaperTradeError("Can't parse number '" +
				 e.getMessage() + "'");
	    return false;
	}

	if(initialCapital <= 0) {
	    buildPaperTradeError("Cannot trade without some initial capital");
	    return false;
	}

	return true;

    }

    private PaperTradeResult paperTradeSymbol(ProgressDialog progress, Vector symbols, 
                                              ScriptQuoteBundle quoteBundle,
                                              Expression buyRule, Expression sellRule)
    {
        String firstSymbol = (String)symbols.firstElement();
        Portfolio portfolio = 
            PaperTrade.paperTrade("Paper Trade of " + 
                                  firstSymbol.toLowerCase(),
                                  quoteBundle,
                                  firstSymbol.toLowerCase(),
                                  fromDate,
                                  toDate,
                                  buyRule,
                                  sellRule,
                                  initialCapital,
                                  tradeCost);

        // Running the equation means we might need to load in
        // more quotes so the note may have changed...
        progress.setNote("Paper Trading...");
        progress.increment();

        return new BasicPaperTradeResult(portfolio, quoteBundle, initialCapital,
                                         tradeCost, buyRule.toString(), sellRule.toString(), 
                                         fromDate, toDate);
    }
   
    private PaperTradeResult
        paperTradeForEachSymbol(ProgressDialog progress, Vector symbols, 
                                ScriptQuoteBundle quoteBundle,
                                Expression buyRule, Expression sellRule)
    {
        // These are the only values that are averaged
        int averageNumberTrades = 0;
        int averageFinalCapital = 0;
		    
        // Evaluate expression for each equation
        for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
            String symbol = (String)iterator.next();

            Portfolio portfolio = 
                PaperTrade.paperTrade("Paper Trade of " + 
                                      symbol.toLowerCase(),
                                      quoteBundle,
                                      symbol.toLowerCase(),
                                      fromDate,
                                      toDate,
                                      buyRule,
                                      sellRule,
                                      initialCapital,
                                      tradeCost);
            
            averageNumberTrades += (portfolio.countTransactions(Transaction.ACCUMULATE) +
                                    portfolio.countTransactions(Transaction.REDUCE));
            try {
                averageFinalCapital += portfolio.getValue(quoteBundle, toDate);
            }
            catch(MissingQuoteException e) {
                // Already checked...
                assert false;
            }
            
            // Running the equation means we might need to load in
            // more quotes so the note may have changed...
            progress.setNote("Paper Trading...");
            progress.increment();
        }

        if(symbols.size() > 1) {
            averageNumberTrades /= symbols.size();
            averageFinalCapital /= symbols.size();
        }

        return new AveragePaperTradeResult(quoteBundle, "Symbols",
                                           initialCapital, averageFinalCapital,
                                           tradeCost, averageNumberTrades,
                                           buyRule.toString(), sellRule.toString(), 
                                           fromDate, toDate);
    }
                                
    private Vector paperTrade() {

        ProgressDialog progress = 
            ProgressDialogManager.getProgressDialog();

        progress.setIndeterminate(true);
        progress.show("Paper Trade");

        Date date = new Date();
        long startTime = date.getTime();

        quoteBundle = new ScriptQuoteBundle(quoteRange);

        date = new Date();
        long endTime = date.getTime();
        long timeTaken = endTime - startTime;

        Vector symbols = quoteBundle.getAllSymbols();
	String symbol = (String)symbols.firstElement();
	symbol = symbol.toLowerCase();

	Parser parser = new Parser();

	// Normalise ranges
	if(aRange <= 0)
	    aRange = 1;
	if(bRange <= 0)
	    bRange = 1;
	if(cRange <= 0)
	    cRange = 1;
	
        int numberOfSymbolRuns = aRange * bRange * cRange;
	int numberOfRuns = numberOfSymbolRuns * symbols.size();

        progress.setIndeterminate(false);
        progress.setMaximum(numberOfRuns);
        progress.setProgress(0);
        progress.setNote("Paper Trading...");
        progress.setMaster(true);

	// Iterate through all possible paper trade equations
        Vector paperTradeResults = new Vector(numberOfSymbolRuns);

	for(int a = 1; a <= aRange; a++) {
	    for(int b = 1; b <= bRange; b++) {
		for(int c = 1; c <= cRange; c++) {
                    
		    // Put in $A, $B, $C substitutes
		    String buyRuleStringCopy = new String(buyRuleString);
		    String sellRuleStringCopy = new String(sellRuleString);

		    buyRuleStringCopy = 
			substituteVariables(buyRuleStringCopy, a, b, c);
		    sellRuleStringCopy = 
			substituteVariables(sellRuleStringCopy, a, b, c);

                    Expression buyRule = null;
                    Expression sellRule = null;
                    
                    // Parse Expression
                    try {
                        buyRule = parser.parse(buyRuleStringCopy);
                        sellRule = parser.parse(sellRuleStringCopy);
                    }
                    catch(ExpressionException e) {	   
                        // Should not happen - they've already been checked
                        assert false;
                    }

                    PaperTradeResult paperTradeResult;

                    // If there is only one symbol to trade - we create a
                    // basic paper trade result of just that symbol. This
                    // includes the portfolio so it can be graphed
                    if(symbols.size() == 1) 
                        paperTradeResult = 
                            paperTradeSymbol(progress, symbols, quoteBundle, buyRule, sellRule);

                    // If there are multiple symbols, we paper trade each one
                    // and create a summary paper trade result. This doesn't
                    // include portfolio information (there'd be too much data!)
                    else
                        paperTradeResult = 
                            paperTradeForEachSymbol(progress, symbols, quoteBundle, buyRule,
                                                    sellRule);

                    paperTradeResults.add(paperTradeResult);
		}
	    }
	}

        ProgressDialogManager.closeProgressDialog(progress);

	return paperTradeResults;
    }

    private void displayPaperTrades(final Vector paperTradeResults) {

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
			catch(java.beans.PropertyVetoException e) {
			    assert false;
			}
		    }

		    // Send result to result table for display
		    PaperTradeResultModule resultsModule = 
			(PaperTradeResultModule)resultsFrame.getModule();
		    
                    resultsModule.addResults(paperTradeResults);
		}});
    }

    // In the given source string replace all occurences of patternText with
    // text.
    private String replace(String source, String patternText, String text) {
	Pattern pattern = Pattern.compile(patternText);
	Matcher matcher = pattern.matcher(source);
	return matcher.replaceAll(text);
    }

    // Substitute $A, $B & $C for their given values
    private String substituteVariables(String string, int a, int b, int c) {
	string = replace(string, "\\$A", String.valueOf(a));
	string = replace(string, "\\$B", String.valueOf(b));
	string = replace(string, "\\$C", String.valueOf(c));

	return string;
    }

    // Convenience method for displaying the given error message
    private void buildPaperTradeError(String message) {
	JOptionPane.showInternalMessageDialog(desktop, 
					      message,
					      "Error building Paper Trade",
					      JOptionPane.ERROR_MESSAGE);
    }

}
