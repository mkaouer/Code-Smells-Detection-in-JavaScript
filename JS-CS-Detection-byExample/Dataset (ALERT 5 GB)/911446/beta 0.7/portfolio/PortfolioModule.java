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

package org.mov.portfolio;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.*;

import org.mov.main.*;
import org.mov.util.Currency;
import org.mov.util.ExchangeRateCache;
import org.mov.util.Locale;
import org.mov.prefs.*;
import org.mov.quote.*;
import org.mov.ui.*;

/**
 * Venice module for displaying a portfolio to the user. This module
 * allows a user to view a portfolio and, manage the accounts and transactions
 * in that portfolio.
 *
 * @author Andrew Leppard
 */
public class PortfolioModule extends JPanel implements Module,
						       ActionListener {

    private PropertyChangeSupport propertySupport;

    private JDesktopPane desktop;
    private Portfolio portfolio;
    private EODQuoteBundle quoteBundle;

    private JMenuBar menuBar;
    private JMenuItem accountNewCashAccount;
    private JMenuItem accountNewShareAccount;
    private JMenuItem portfolioGraph;
    private JMenuItem portfolioTable;
    private JMenuItem portfolioDelete;
    private JMenuItem portfolioRename;
    private JMenuItem portfolioClose;

    private JMenuItem transactionNew;
    private JMenuItem transactionShowHistory;

    // Set to true if weve deleted this portfolio and shouldn't try
    // to save it when we exit
    private boolean isDeleted = false;

    // Keep a single copy of the transaction history table
    JInternalFrame historyFrame = null;
    TransactionModule historyModule = null;

    /**
     * Create a new portfolio module.
     *
     * @param	desktop	the current desktop
     * @param	portfolio	the portfolio to display
     * @param	quoteBundle	quote bundle
     */
    public PortfolioModule(JDesktopPane desktop, Portfolio portfolio,
			   EODQuoteBundle quoteBundle) {

	this.desktop = desktop;
	this.portfolio = portfolio;
	this.quoteBundle = quoteBundle;

	propertySupport = new PropertyChangeSupport(this);

	createMenu();
	redraw();

        // Calculate the value of the portfolio before we create and
        // display the table. This way we don't pop up a dialog
        // asking the user to enter an exchange rate half-way through
        // rendering.
        try {
            portfolio.getValue(quoteBundle, portfolio.getLastDate());
        } catch (MissingQuoteException e) {
            // We don't actually care about the result
        }
    }

    // create new menu for this module
    private void createMenu() {
	menuBar = new JMenuBar();
	JMenu portfolioMenu = MenuHelper.addMenu(menuBar, Locale.getString("PORTFOLIO"), 'P');
	{
	    portfolioGraph =
		MenuHelper.addMenuItem(this, portfolioMenu,
				       Locale.getString("GRAPH"));
	    portfolioTable =
		MenuHelper.addMenuItem(this, portfolioMenu,
				       Locale.getString("TABLE"));

            // If the portfolio is transient it won't be saved anyway - so
            // you can't delete or rename it.
            if(!portfolio.isTransient()) {
                portfolioMenu.addSeparator();

                portfolioDelete = MenuHelper.addMenuItem(this, portfolioMenu,
                                                         Locale.getString("DELETE"));

                portfolioRename = MenuHelper.addMenuItem(this, portfolioMenu,
                                                         Locale.getString("RENAME"));
            }

            portfolioMenu.addSeparator();

	    portfolioClose = MenuHelper.addMenuItem(this, portfolioMenu,
						    Locale.getString("CLOSE"));
	}

	JMenu accountMenu = MenuHelper.addMenu(menuBar, Locale.getString("ACCOUNT"), 'A');
	{
	    accountNewCashAccount =
		MenuHelper.addMenuItem(this, accountMenu,
				       Locale.getString("NEW_CASH_ACCOUNT"));
	    accountNewShareAccount =
		MenuHelper.addMenuItem(this, accountMenu,
				       Locale.getString("NEW_SHARE_ACCOUNT"));
	}

	JMenu transactionMenu =
	    MenuHelper.addMenu(menuBar, Locale.getString("TRANSACTION"), 'T');
	{
	    transactionNew =
		MenuHelper.addMenuItem(this, transactionMenu,
				       Locale.getString("NEW"));
	    transactionShowHistory =
		MenuHelper.addMenuItem(this, transactionMenu,
				       Locale.getString("SHOW_HISTORY"));
	}

	// Make sure appropriate menus are enabled or disabled
	checkMenuDisabledStatus();
    }

    // You can only create transactions if youve got a cash account
    private void checkMenuDisabledStatus() {
	int hasCashAccount = portfolio.countAccounts(Account.CASH_ACCOUNT);

	transactionNew.setEnabled(hasCashAccount > 0);
	transactionShowHistory.setEnabled(hasCashAccount > 0);
    }

    /**
     * Layout and redraw portfolio module.
     */
    public void redraw() {

	StockHoldingTable table = null;
	JScrollPane scrolledTable = null;
	AccountTable accountTable = null;

	// Remove old portfolio layout if there was one
	removeAll();

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        List accounts = portfolio.getAccounts();

	// If the portfolio is empty display a label saying "Empty"
	// otherwise display the portfolio
	if(accounts.size() == 0) {
	    addLabel(Locale.getString("EMPTY") + " (" + portfolio.getCurrency() + ")");
	}
	else {
            Iterator iterator = accounts.iterator();
	
	    // First list share accounts
	    while(iterator.hasNext()) {
		Account account = (Account)iterator.next();
		if(account instanceof ShareAccount) {
                    addLabel(account.getName() + " (" + account.getCurrency() + ")");
		
		    // Add table of stock holdings for the portfolio
		    ShareAccount shareAccount = (ShareAccount)account;
		    table = new StockHoldingTable(shareAccount, quoteBundle);
		    scrolledTable = new JScrollPane(table);
		    add(scrolledTable);
		
		    restrictTableHeight(scrolledTable, table);
		}
	    }
	
	    // Now add summary containing all accounts including total
	    addLabel(Locale.getString("SUMMARY") + " (" + portfolio.getCurrency() + ")");
	
	    accountTable =
		new AccountTable(portfolio, quoteBundle);

	    scrolledTable = new JScrollPane(accountTable);
	    add(scrolledTable);

	    restrictTableHeight(scrolledTable, accountTable);
	}	

	add(Box.createVerticalGlue());

	validate();
	repaint();
    }

    // Hack to get swing tables using the minimum amount of room needed
    // instead of gobbling up extra room at the bottom
    private void restrictTableHeight(JScrollPane scrollPane, JTable table) {

	int rows = table.getRowCount();

	// If we don't do this empty tables look ugly
	if(rows == 0)
	    rows = 1;

	// Calculate minimum height required to display table without
	// scrollbars - this will become the minimum, preferred and
	// maximum height of the table
	double maximumHeight =
	    table.getTableHeader().getPreferredSize().getHeight() +
	    table.getRowHeight() * rows + 4;
	// +3 for swing metal, +4 for XP, Aqua themes
	
	Dimension maximumSize = new Dimension();
	maximumSize.setSize(table.getMaximumSize().getWidth(),
			    maximumHeight);
	
	Dimension preferredSize = new Dimension();
	preferredSize.setSize(table.getPreferredSize().getWidth(),
			      maximumHeight);
	
	Dimension minimumSize = new Dimension();
	minimumSize.setSize(table.getMinimumSize().getWidth(),
			    maximumHeight);
	
	scrollPane.setPreferredSize(preferredSize);
	scrollPane.setMaximumSize(maximumSize);
	scrollPane.setMinimumSize(minimumSize);
    }

    // Add label in large letters to indicate either the share account
    // name or "Summary"
    private void addLabel(String text) {
	JLabel label = new JLabel(text);
	label.setForeground(Color.BLACK);
	label.setFont(new Font(label.getFont().getName(), Font.BOLD, 20));
	label.setBorder(new EmptyBorder(0, 5, 0, 0));

	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	panel.add(label, BorderLayout.NORTH);
	add(panel);

	Dimension preferredSize = new Dimension();
	preferredSize.setSize(panel.getPreferredSize().getWidth(),
			  label.getPreferredSize().getHeight());
	Dimension maximumSize = new Dimension();
	maximumSize.setSize(panel.getMaximumSize().getWidth(),
			  label.getPreferredSize().getHeight());

	panel.setPreferredSize(preferredSize);
	panel.setMaximumSize(maximumSize);
    }

    public void save() {
        // Don't save if the portfolio is transient or the user just
        // deleted it.
	if(!portfolio.isTransient() && !isDeleted) {
            try {
                PreferencesManager.putPortfolio(portfolio);
            }
            catch(PreferencesException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_SAVING_PORTFOLIO_TITLE"),
                                                e.getMessage());
            }

	    MainMenu.getInstance().updatePortfolioMenu();
	}
    }

    public String getTitle() {
	return portfolio.getName();
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
	return menuBar;
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

	// Handle all menu actions in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread menuAction = new Thread() {

		public void run() {

		    if(e.getSource() == portfolioClose) {
			propertySupport.
			    firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
		    }
		    else if(e.getSource() == portfolioGraph) {
                        graphPortfolio();
		    }
		    else if(e.getSource() == portfolioTable) {
                        tablePortfolio();
		    }
		    else if(e.getSource() == portfolioDelete) {
			deletePortfolio();
		    }
		    else if(e.getSource() == portfolioRename) {
			renamePortfolio();
		    }
		    else if(e.getSource() == accountNewCashAccount) {
			newCashAccount();
		    }
		    else if(e.getSource() == accountNewShareAccount) {
			newShareAccount();
		    }
		    else if(e.getSource() == transactionNew) {
			newTransaction();
		    }
		    else if(e.getSource() == transactionShowHistory) {
			showTransactionHistory();
		    }
		    else {
			assert false;
		    }
		}
	    };

	menuAction.start();

    }

    // Show the transaction history table
    private void showTransactionHistory() {

	// If we have already created it - then just open it
	if(historyFrame != null && !historyFrame.isClosed()) {
	    historyFrame.toFront();

	    try {
		historyFrame.setIcon(false);
		historyFrame.setSelected(true);
	    }
	    catch(PropertyVetoException e) {
		assert false;
	    }
	}
	else {
            DesktopManager desktopManager = CommandManager.getInstance().getDesktopManager();
            historyModule = new TransactionModule(this, portfolio);
	    historyFrame = desktopManager.newFrame(historyModule);
	}
    }

    // Graph this portfolio
    private void graphPortfolio() {
        // Can only graph if:
        // (1) There are shares in the portfolio...
        // (2) We don't have new enough quotes.
        if(portfolio.getStartDate() == null ||
           portfolio.getStartDate().after(QuoteSourceManager.getSource().getLastDate()))
            DesktopManager.showErrorMessage(Locale.getString("NOTHING_TO_GRAPH"));
        else {
            // If this portfolio has been given to us from a paper trade we will
            // also get a fully loaded quote bundle ready for graphing.
            // We do a simple check to see if we can graph using that quote bundle:
            // If the quote bundle has quotes before or at the first trade in the
            // portfolio then we will use the quote bundle given.
            if(quoteBundle.getFirstDate().compareTo(portfolio.getStartDate()) <= 0)
                CommandManager.getInstance().graphPortfolio(portfolio, quoteBundle);
            else
                CommandManager.getInstance().graphPortfolio(portfolio);
        }
    }

    /**
     * Display a table of the value of the portfolio over time.
     */
    public void tablePortfolio() {
        // If this portfolio has been given to us from a paper trade we will
        // also get a fully loaded quote bundle ready for tabling.
        // We do a simple check to see if we can table using that quote bundle:
        // If the quote bundle has quotes before or at the first trade in the
        // portfolio then we will use the quote bundle given.
        if(portfolio.getStartDate() == null ||
           quoteBundle.getFirstDate().compareTo(portfolio.getStartDate()) <= 0)
            CommandManager.getInstance().tablePortfolio(portfolio, quoteBundle);
        else
            CommandManager.getInstance().tablePortfolio(portfolio);
    }

    // Delete this portfolio
    private void deletePortfolio() {
	JDesktopPane desktop =
	    org.mov.ui.DesktopManager.getDesktop();

	ConfirmDialog dialog = new ConfirmDialog(desktop,
				   Locale.getString("SURE_DELETE_PORTFOLIO"),
				   Locale.getString("DELETE_PORTFOLIO"));
	boolean deletePortfolio = dialog.showDialog();

	if(deletePortfolio) {
	    PreferencesManager.deletePortfolio(portfolio.getName());

	    MainMenu.getInstance().updatePortfolioMenu();

	    // Prevent save() function resurrecting portfolio
	    isDeleted = true;

	    // Close window
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}
    }

    // Rename the portfolio
    private void renamePortfolio() {
        String oldPortfolioName = portfolio.getName();
	JDesktopPane desktop =
	    org.mov.ui.DesktopManager.getDesktop();

        // Get new name for portfolio
	TextDialog dialog = new TextDialog(desktop,
					   Locale.getString("ENTER_NEW_PORTFOLIO_NAME"),
					   Locale.getString("RENAME_PORTFOLIO"),
                                           oldPortfolioName);
	String newPortfolioName = dialog.showDialog();

        if(newPortfolioName != null && newPortfolioName.length() > 0 &&
           !newPortfolioName.equals(oldPortfolioName)) {

            // Save the portfolio under the new name
            portfolio.setName(newPortfolioName);

            try {
                // Add new portfolio
                PreferencesManager.putPortfolio(portfolio);

                // Delete the old portfolio. Don't do this if the above failed!
                PreferencesManager.deletePortfolio(oldPortfolioName);
            }
            catch(PreferencesException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_SAVING_PORTFOLIO_TITLE"),
                                                e.getMessage());
            }

            // Update GUI
	    MainMenu.getInstance().updatePortfolioMenu();
            propertySupport.firePropertyChange(ModuleFrame.TITLEBAR_CHANGED_PROPERTY, 0, 1);
        }
    }

    // Create a new cash account
    private void newCashAccount() {
	AccountDialog dialog =
	    new AccountDialog(desktop,
                              Locale.getString("ENTER_ACCOUNT_NAME"),
                              Locale.getString("NEW_CASH_ACCOUNT"),
                              portfolio.getCurrency());

        if(dialog.showDialog()) {
            String accountName = dialog.getAccountName();
            Currency accountCurrency = dialog.getAccountCurrency();
	    Account account = new CashAccount(accountName, accountCurrency);

            // Get exchange rate before we add it to the table. Otherwise the
            // table will initiate the request during rendering which is ugly.
            ExchangeRateCache.getInstance().getRate(portfolio.getLastDate(),
                                                    accountCurrency,
                                                    portfolio.getCurrency());

	    portfolio.addAccount(account);
            redraw();
            checkMenuDisabledStatus(); // enable transaction menu
	}
    }

    // Create a new share account
    private void newShareAccount() {
	AccountDialog dialog =
	    new AccountDialog(desktop,
                              Locale.getString("ENTER_ACCOUNT_NAME"),
                              Locale.getString("NEW_SHARE_ACCOUNT"),
                              portfolio.getCurrency());
        
        if(dialog.showDialog()) {
            String accountName = dialog.getAccountName();
            Currency accountCurrency = dialog.getAccountCurrency();
	    Account account = new ShareAccount(accountName, accountCurrency);
            
            // Get exchange rate before we add it to the table. Otherwise the
            // table will initiate the request during rendering which is ugly.
            ExchangeRateCache.getInstance().getRate(portfolio.getLastDate(),
                                                    accountCurrency,
                                                    portfolio.getCurrency());

	    portfolio.addAccount(account);
            redraw();
            checkMenuDisabledStatus(); // enable transaction menu
	}
    }

    /**
     * Open a new transaction dialog to allow the user to enter a new
     * transaction. When the dialog is closed it will call redraw() on
     * both the portfolio module window and the transaction module window
     * (if its open) to ensure everything is kept in-sync.
     */
    public void newTransaction() {
	JDesktopPane desktop =
	    org.mov.ui.DesktopManager.getDesktop();
	TransactionDialog dialog = new TransactionDialog(desktop, portfolio);

        // Update portfolio displayed if the user entered a new transaction
        if(dialog.newTransaction()) {
            redraw();

            if(historyFrame != null && !historyFrame.isClosed())
                historyModule.redraw();
        }
    }
}
