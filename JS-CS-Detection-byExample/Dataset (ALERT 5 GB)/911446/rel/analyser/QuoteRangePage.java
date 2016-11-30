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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.String;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.ExpressionException;
import nz.org.venice.parser.Parser;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.EODQuoteCache;
import nz.org.venice.quote.EODQuoteRange;
import nz.org.venice.quote.QuoteSourceManager;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.quote.WeekendDateException;
import nz.org.venice.ui.ExpressionComboBox;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.ui.SymbolListComboBox;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;

/**
 * An analysis tool page that lets the user enter data about which quotes
 * to work with (a quote range). This page is used by both the
 * {@link PaperTradeModule} and {@link GPModule}. The page contains the
 * following fields:
 *
 * <ul><li>Date Range
 *     <ul><li>Start Date</li>
 *         <li>End Date</li></ul></li>
 * <li>Period (Optional)</li>
 * <li>Symbols</li>
 * <li>Order</li></ul>
 *
 * The date range denotes the date range of the quotes to work with.
 * The optional period parameter allows this page to let the user
 * specify multiple date ranges, the date ranges aren't specified
 * explicitly here but are created from the given period option.
 * The symbols denotes the symbols to work with. The order denotes
 * the preferred order in working with the symbols.
 *
 * @author Andrew Leppard
 */
public class QuoteRangePage extends Page implements AnalyserPage {

    // Period types

    /** No period. */
    public final static int NO_PERIOD = 0;

    /** One week period. */
    public final static int ONE_WEEK = 1;
    
    /** Two week period. */
    public final static int TWO_WEEKS = 2;

    /** One month period. */
    public final static int ONE_MONTH = 3;

    /** Two month period. */
    public final static int TWO_MONTHS = 4;

    /** Three month period. */
    public final static int THREE_MONTHS = 5;

    /** Four month period. */
    public final static int FOUR_MONTHS = 6;

    /** Six month period. */
    public final static int SIX_MONTHS = 7;

    /** One year period. */
    public final static int ONE_YEAR = 8;

    /** Two year period. */
    public final static int TWO_YEARS = 9;

    /** Three year period. */
    public final static int THREE_YEARS = 10;

    /** Four year period. */
    public final static int FOUR_YEARS = 11;

    private boolean allowMultipleDateRanges;

    // Swing items
    private JTextField startDateTextField;
    private JTextField endDateTextField;
    private SymbolListComboBox symbolListComboBox;
    private JRadioButton orderByKeyButton;
    private JRadioButton orderByExpressionButton;
    private JComboBox orderByKeyComboBox;
    private ExpressionComboBox orderByExpressionComboBox;
    private JCheckBox multipleDateRangesCheckBox;
    private JComboBox dateRangePeriodComboBox;

    // Parsed data
    private EODQuoteRange quoteRange;
    private Expression orderByExpression;
    private TradingDate startDate;
    private TradingDate endDate;
    private int dateRangePeriod;

    /**
     * Construct a new quote range page. The period setting will not be shown.
     *
     * @param desktop the desktop.
     */
    public QuoteRangePage(JDesktopPane desktop) {
        this(desktop, false);
    }

    /**
     * Construct a new quote range page.
     *
     * @param desktop the desktop.
     * @param allowMultipleDateRanges if set the period setting will be available.
     */
    public QuoteRangePage(JDesktopPane desktop, boolean allowMultipleDateRanges) {
        this.desktop = desktop;
	this.allowMultipleDateRanges = allowMultipleDateRanges;

        layoutPage();
    }

    public void load(String key) {

        // Load last GUI settings from preferences
	HashMap settings =
            PreferencesManager.getAnalyserPageSettings(key + getClass().getName());

	Iterator iterator = settings.keySet().iterator();

	while(iterator.hasNext()) {
	    String setting = (String)iterator.next();
	    String value = (String)settings.get(setting);

            if(setting.equals("start_date"))
                startDateTextField.setText(value);
            else if(setting.equals("end_date"))
                endDateTextField.setText(value);
            else if(setting.equals("symbols"))
                symbolListComboBox.setText(value);
            else if(setting.equals("by")) {
                if(value.equals("orderByKey"))
                    orderByKeyButton.setSelected(true);
                else
                    orderByExpressionButton.setSelected(true);
            }
            else if(setting.equals("by_key"))
                orderByKeyComboBox.setSelectedItem(value);
            else if(setting.equals("by_equation"))
                orderByExpressionComboBox.setExpressionText(value);
	    else if(setting.equals("is_multiple_date_ranges")) {
		if(allowMultipleDateRanges)
		    multipleDateRangesCheckBox.setSelected(value.equals("1"));
	    }
	    else if(setting.equals("period")) {
		if(allowMultipleDateRanges)
		    dateRangePeriodComboBox.setSelectedItem(value);
	    }
            else
                assert false;
        }

        checkDisabledStatus();
    }

    public void save(String key) {
        HashMap settings = new HashMap();

        settings.put("start_date", startDateTextField.getText());
        settings.put("end_date", endDateTextField.getText());
        settings.put("symbols", symbolListComboBox.getText());
        settings.put("by", orderByKeyButton.isSelected()? "orderByKey" : "orderByEquation");
        settings.put("by_key", orderByKeyComboBox.getSelectedItem());
        settings.put("by_equation", orderByExpressionComboBox.getExpressionText());
	if(allowMultipleDateRanges) {
	    settings.put("is_multiple_date_ranges",
			 multipleDateRangesCheckBox.isSelected()? "1" : "0");
	    settings.put("period", dateRangePeriodComboBox.getSelectedItem());
	}

        PreferencesManager.putAnalyserPageSettings(key + getClass().getName(),
                                                   settings);
    }

    public boolean parse() {
        quoteRange = null;

        try {
            startDate = new TradingDate(startDateTextField.getText(),
                                        TradingDate.BRITISH);
            endDate = new TradingDate(endDateTextField.getText(),
                                      TradingDate.BRITISH);
        }
        catch(TradingDateFormatException e) {
        	showErrorMessage(
        			Locale.getString("ERROR_PARSING_DATE",e.getDate()),
        			Locale.getString("INVALID_QUOTE_RANGE_ERROR"));
	    return false;
	}

	dateRangePeriod = NO_PERIOD;
	if(allowMultipleDateRanges) {
	    if(multipleDateRangesCheckBox.isSelected())
		dateRangePeriod = dateRangePeriodComboBox.getSelectedIndex();
	}

        if(startDate.after(endDate)) {
        	showErrorMessage(
        			Locale.getString("DATE_RANGE_ERROR"),
        			Locale.getString("INVALID_QUOTE_RANGE_ERROR"));
	    return false;
        }

        if(!QuoteSourceManager.getSource().containsDate(startDate)) {
        	showErrorMessage(
        			Locale.getString("NO_QUOTES_DATE",startDateTextField.getText()),
        			Locale.getString("INVALID_QUOTE_RANGE_ERROR"));
            return false;
        }

        if(!QuoteSourceManager.getSource().containsDate(endDate)) {
        	showErrorMessage(
        			Locale.getString("NO_QUOTES_DATE",endDateTextField.getText()),
        			Locale.getString("INVALID_QUOTE_RANGE_ERROR"));
            return false;
        }

        try {
            int offset = EODQuoteCache.getInstance().dateToOffset(startDate);
        }
        catch(WeekendDateException e) {
        	showErrorMessage(
        			Locale.getString("DATE_ON_WEEKEND",startDateTextField.getText()),
                    Locale.getString("INVALID_QUOTE_RANGE_ERROR"));
	    return false;
        }

        try {
            int offset = EODQuoteCache.getInstance().dateToOffset(endDate);
        }
        catch(WeekendDateException e) {
        	showErrorMessage(
        			Locale.getString("DATE_ON_WEEKEND",endDateTextField.getText()),
                    Locale.getString("INVALID_QUOTE_RANGE_ERROR"));
	    return false;
        }

        try {
            quoteRange = symbolListComboBox.getQuoteRange();
        }
        catch(SymbolFormatException e) {
        	showErrorMessage(
        			e.getMessage(),
                    Locale.getString("INVALID_QUOTE_RANGE_ERROR"));
            return false;
        }

        if(orderByExpressionButton.isSelected()) {
            try {
		if (orderByExpressionComboBox.getExpressionText().equals("")) {
		    throw new ExpressionException(Locale.getString("MISSING_EQUATION_NAME"));
		}
                orderByExpression = Parser.parse(orderByExpressionComboBox.getExpressionText());
            }
            catch(ExpressionException e) {
            	showErrorMessage(
            			e.getReason(),
                        Locale.getString("ERROR_PARSING_EQUATION"));

                return false;
            }
        }

        quoteRange.setFirstDate(startDate);
        quoteRange.setLastDate(endDate);

        return true;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("QUOTE_RANGE_PAGE_TITLE");
    }

    /**
     * Return the quote range representation of the user's selection.
     *
     * @return the quote range representation.
     */
    public EODQuoteRange getQuoteRange() {
        return quoteRange;
    }

    /**
     * Return the selected start date.
     *
     * @return the start date.
     */
    public TradingDate getStartDate() {
        return startDate;
    }

    /**
     * Return the seelected end date.
     *
     * @return the end date.
     */
    public TradingDate getEndDate() {
        return endDate;
    }

    /**
     * Return the period. Only call this function if you selected multiple
     * date ranges.
     *
     * @return the period.
     */
    public int getDateRangePeriod() {
	assert allowMultipleDateRanges;

	return dateRangePeriod;
    }

    /**
     * Create a new order comparator to order the stocks in the given quote bundle
     * according to the user's selected symbol order.
     *
     * @param quoteBundle the quote bundle
     * @return a new order comparator.
     */
    public OrderComparator getOrderComparator(EODQuoteBundle quoteBundle) {
        if(orderByKeyButton.isSelected()) {
            // Set order (e.g. by volume).
            return new OrderComparator(quoteBundle, orderByKeyComboBox.getSelectedIndex());
        }
        else {
            // Order by expression
            assert orderByExpressionButton.isSelected();
            return new OrderComparator(quoteBundle, orderByExpression);
        }
    }

    private void layoutPage() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	// Date Range Panel
	{
	    TitledBorder dateTitled = new TitledBorder(Locale.getString("DATE_RANGE"));
	    JPanel panel = new JPanel();
	    panel.setBorder(dateTitled);
            panel.setLayout(new BorderLayout());

            JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

	    startDateTextField =
	    	GridBagHelper.addTextRow(innerPanel, Locale.getString("START_DATE"), "",
                                         gridbag, c, 15);
	    endDateTextField =
		GridBagHelper.addTextRow(innerPanel, Locale.getString("END_DATE"), "",
                                         gridbag, c, 15);

	    startDateTextField.setToolTipText(Locale.getString("START_DATE_FIELD_TOOLTIP"));
	    endDateTextField.setToolTipText(Locale.getString("END_DATE_FIELD_TOOLTIP"));

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
	}

	// Multiple Date Range Panel
	if(allowMultipleDateRanges) {
	    TitledBorder dateTitled = new TitledBorder(Locale.getString("MULTIPLE_DATE_RANGES"));
	    JPanel panel = new JPanel();
	    panel.setBorder(dateTitled);
            panel.setLayout(new BorderLayout());

            JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);	

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;

            multipleDateRangesCheckBox =
                GridBagHelper.addCheckBoxRow(innerPanel,
                                             Locale.getString("ENABLE_MULTIPLE_DATE_RANGES"),
                                             false, gridbag, c);
            multipleDateRangesCheckBox.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        checkDisabledStatus();
                    }});


	    JLabel label = new JLabel(Locale.getString("PERIOD"));
	    c.gridwidth = 1;
	    gridbag.setConstraints(label, c);
	    innerPanel.add(label);

	    dateRangePeriodComboBox = new JComboBox();
	    dateRangePeriodComboBox.addItem(Locale.getString("ONE_WEEK"));
	    dateRangePeriodComboBox.addItem(Locale.getString("TWO_WEEKS"));
	    dateRangePeriodComboBox.addItem(Locale.getString("ONE_MONTH"));
	    dateRangePeriodComboBox.addItem(Locale.getString("TWO_MONTHS"));
	    dateRangePeriodComboBox.addItem(Locale.getString("THREE_MONTHS"));
	    dateRangePeriodComboBox.addItem(Locale.getString("FOUR_MONTHS"));
	    dateRangePeriodComboBox.addItem(Locale.getString("SIX_MONTHS"));
	    dateRangePeriodComboBox.addItem(Locale.getString("ONE_YEAR"));
	    dateRangePeriodComboBox.addItem(Locale.getString("TWO_YEARS"));
	    dateRangePeriodComboBox.addItem(Locale.getString("THREE_YEARS"));
	    dateRangePeriodComboBox.addItem(Locale.getString("FOUR_YEARS"));

            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(dateRangePeriodComboBox, c);
            innerPanel.add(dateRangePeriodComboBox);

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
	}

	// Symbols Panel
	{
	    TitledBorder symbolTitled = new TitledBorder(Locale.getString("SYMBOLS"));
	    JPanel panel = new JPanel();
	    panel.setBorder(symbolTitled);
            panel.setLayout(new BorderLayout());

	    JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;

            symbolListComboBox =
                GridBagHelper.addSymbolListComboBox(innerPanel,
                                                    Locale.getString("SYMBOLS"), "",
                                                    gridbag, c);

	    symbolListComboBox.setToolTipText(Locale.getString("SYMBOL_BOX_TOOLTIP"));

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
	}

        // Symbols Order Panel
        {
            TitledBorder orderTitled = new TitledBorder(Locale.getString("ORDER_SYMBOLS"));
            JPanel panel = new JPanel();
            panel.setBorder(orderTitled);
            panel.setLayout(new BorderLayout());

	    JPanel innerPanel = new JPanel();
	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    innerPanel.setLayout(gridbag);

            ButtonGroup buttonGroup = new ButtonGroup();

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;

            orderByKeyButton = new JRadioButton(Locale.getString("BY"));
	    orderByKeyButton.setToolTipText(Locale.getString("ANALYSIS_ORDER_BY_KEY_BUTTON_TOOLTIP"));
            orderByKeyButton.setSelected(true);
            orderByKeyButton.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        checkDisabledStatus();
                    }});
            buttonGroup.add(orderByKeyButton);

            c.gridwidth = 1;
            gridbag.setConstraints(orderByKeyButton, c);
            innerPanel.add(orderByKeyButton);

            orderByKeyComboBox = new JComboBox();
            orderByKeyComboBox.addItem(Locale.getString("UNORDERED"));
            orderByKeyComboBox.addItem(Locale.getString("SYMBOL"));
            orderByKeyComboBox.addItem(Locale.getString("VOLUME_DECREASING"));
            orderByKeyComboBox.addItem(Locale.getString("VOLUME_INCREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_LOW_DECREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_LOW_INCREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_HIGH_DECREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_HIGH_INCREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_OPEN_DECREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_OPEN_INCREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_CLOSE_DECREASING"));
            orderByKeyComboBox.addItem(Locale.getString("DAY_CLOSE_INCREASING"));
            orderByKeyComboBox.addItem(Locale.getString("CHANGE_DECREASING"));
            orderByKeyComboBox.addItem(Locale.getString("CHANGE_INCREASING"));


	    orderByKeyComboBox.setToolTipText(Locale.getString("ANALYSIS_ORDER_BOX_TOOLTIP"));



            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(orderByKeyComboBox, c);
            innerPanel.add(orderByKeyComboBox);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

            orderByExpressionButton = new JRadioButton(Locale.getString("BY_EQUATION"));
	    orderByExpressionButton.setToolTipText(Locale.getString("ANALYSIS_ORDER_BY_EQN_BUTTON_TOOLTIP"));
            orderByExpressionButton.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        checkDisabledStatus();
                    }});

            buttonGroup.add(orderByExpressionButton);

            c.gridwidth = 1;
            gridbag.setConstraints(orderByExpressionButton, c);
            innerPanel.add(orderByExpressionButton);

            orderByExpressionComboBox = new ExpressionComboBox();
	    orderByExpressionComboBox.setToolTipText(Locale.getString("ANALYSIS_ORDER_EQN_TOOLTIP"));
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(orderByExpressionComboBox, c);
            innerPanel.add(orderByExpressionComboBox);

            panel.add(innerPanel, BorderLayout.NORTH);
	    add(panel);
        }
    }

    private void checkDisabledStatus() {
        orderByKeyComboBox.setEnabled(orderByKeyButton.isSelected());
        orderByExpressionComboBox.setEnabled(orderByExpressionButton.isSelected());

	if(allowMultipleDateRanges)
	    dateRangePeriodComboBox.setEnabled(multipleDateRangesCheckBox.isSelected());
    }
}
