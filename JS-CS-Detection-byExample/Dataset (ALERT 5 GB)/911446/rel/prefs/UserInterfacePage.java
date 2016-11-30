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

package nz.org.venice.prefs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import nz.org.venice.quote.EODQuoteCache;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;

/**
 * Provides a preferences page to let the user set user interface
 * parameters.
 */
public class UserInterfacePage extends JPanel implements PreferencesPage
{
    private JDesktopPane desktop;
    private JTextField minDecimalDigitsTextField;
    private JTextField maxDecimalDigitsTextField;
    private JTextField tabSizeTextField;
    private JCheckBox scrollToLatestDataChart;
    private JCheckBox scrollToLatestDataTable; 
    private JCheckBox restoreWindowsCheckBox;
    private JCheckBox confirmExitCheckBox;
    
    
   
    /**
     * Create a new user interface preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public UserInterfacePage(JDesktopPane desktop) {
	this.desktop = desktop;

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
        add(createQuotesPanel());
    }
    
    private JPanel createQuotesPanel() {
        JPanel quotesPanel = new JPanel();
        quotesPanel.setLayout(new BorderLayout());
        JPanel borderPanel = new JPanel();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        borderPanel.setLayout(gridbag);
        
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        int minDecimalDigits = PreferencesManager.getMinDecimalDigits();
        int maxDecimalDigits = PreferencesManager.getMaxDecimalDigits();

        minDecimalDigitsTextField = 
            GridBagHelper.addTextRow(borderPanel, 
                                     Locale.getString("MIN_DECIMAL_DIGITS"), 
                                     Integer.toString(minDecimalDigits),
                                     gridbag, c, 10);
        maxDecimalDigitsTextField = 
            GridBagHelper.addTextRow(borderPanel, 
                                     Locale.getString("MAX_DECIMAL_DIGITS"), 
                                     Integer.toString(maxDecimalDigits),
                                     gridbag, c, 10);

	minDecimalDigitsTextField.setToolTipText(Locale.getString("DECIMAL_DIGITS_FIELD_TOOLTIP"));
	maxDecimalDigitsTextField.setToolTipText(Locale.getString("DECIMAL_DIGITS_FIELD_TOOLTIP"));

	int tabSizeDigits = PreferencesManager.getEditTabSize();
	tabSizeTextField = 
            GridBagHelper.addTextRow(borderPanel, 
                                     Locale.getString("EDIT_TAB_SIZE"), 
                                     Integer.toString(tabSizeDigits),
                                     gridbag, c, 10);

	tabSizeTextField.setToolTipText(Locale.getString("TAB_LENGTH_FIELD_TOOLTIP"));

	scrollToLatestDataChart = 
	    GridBagHelper.addCheckBoxRow(borderPanel,
					 Locale.getString("CHART_SHOW_LATEST_LABEL"),
					 PreferencesManager.getDefaultChartScrollToEnd(),
					 gridbag, c);

	scrollToLatestDataChart.setToolTipText(Locale.getString("SCROLL_CHART_TO_END_TOOLTIP"));

	scrollToLatestDataTable = 
	    GridBagHelper.addCheckBoxRow(borderPanel,
					 Locale.getString("TABLE_SHOW_LATEST_LABEL"),
					 PreferencesManager.getDefaultTableScrollToEnd(),
					 gridbag, c);
	
	scrollToLatestDataTable.setToolTipText(Locale.getString("SCROLL_TABLE_TO_END_TOOLTIP"));
	
	restoreWindowsCheckBox = 
	    GridBagHelper.addCheckBoxRow(borderPanel,
					 Locale.getString("RESTORE_SAVED_WINDOWS"),
					 PreferencesManager.getRestoreSavedWindowsSetting(),
					 gridbag, c);
	
	
	restoreWindowsCheckBox.setToolTipText(Locale.getString("RESTORE_SAVED_WINDOWS_CHECKBOX_TOOLTIP"));

	confirmExitCheckBox = 
	    GridBagHelper.addCheckBoxRow(borderPanel,
					 Locale.getString("CONFIRM_VENICE_EXIT_TITLE"),
					 PreferencesManager.getConfirmExitSetting(),
					 gridbag, c);

	confirmExitCheckBox.setToolTipText(Locale.getString("CONFIRM_EXIT_CHECKBOX_TOOLTIP"));

        quotesPanel.add(borderPanel, BorderLayout.NORTH);
	
        return quotesPanel;
    }

    public JComponent getComponent() {
	return this;
    }

    public String getTitle() {
	return Locale.getString("USER_INTERFACE_PAGE_TITLE");
    }

    public void save() {
        int minDecimalDigits = 0;
        int maxDecimalDigits = 0;
	int editTabSize      = 0;
	boolean scrollToChartEnd;
	boolean scrollToTableEnd;
	boolean restoreSavedWindows;
	boolean confirmExit;

        try {
            minDecimalDigits = Integer.parseInt(minDecimalDigitsTextField.getText());
            maxDecimalDigits = Integer.parseInt(maxDecimalDigitsTextField.getText());

	    editTabSize = Integer.parseInt(tabSizeTextField.getText());
        }
        catch(NumberFormatException e) {
            // ignore
        }

        if(minDecimalDigits > 0) {
            PreferencesManager.putMinDecimalDigits(minDecimalDigitsTextField.getText());
        }
        if(maxDecimalDigits > 0) {
            PreferencesManager.putMaxDecimalDigits(maxDecimalDigitsTextField.getText());
        }

	if (editTabSize > 0) {
	    PreferencesManager.putEditTabSize(tabSizeTextField.getText());
	}

	scrollToChartEnd =  scrollToLatestDataChart.isSelected(); 
	PreferencesManager.putDefaultChartScrollToEnd(scrollToChartEnd);

	scrollToTableEnd =  scrollToLatestDataTable.isSelected(); 
	PreferencesManager.putDefaultTableScrollToEnd(scrollToTableEnd);

	restoreSavedWindows = restoreWindowsCheckBox.isSelected();
	PreferencesManager.putRestoreSavedWindowsSetting(restoreSavedWindows);

	confirmExit = confirmExitCheckBox.isSelected();
	PreferencesManager.putConfirmExitSetting(confirmExit);

    }
}
