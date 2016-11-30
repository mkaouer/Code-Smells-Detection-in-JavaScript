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
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import org.mov.main.*;
import org.mov.util.*;
import org.mov.prefs.*;
import org.mov.table.*;
import org.mov.quote.*;
import org.mov.ui.*;

/**
 * Venice module for displaying a portfolio to the user. This module
 * allows a user to view a portfolio and, manage the accounts and transactions
 * in that portfolio.
 */
public class PortfolioModule extends JPanel implements Module,
						       ActionListener {

    private PropertyChangeSupport propertySupport;

    private JDesktopPane desktop;
    private Portfolio portfolio;
    private QuoteBundle quoteBundle;

    private JMenuBar menuBar;
    private JMenuItem accountNewCashAccount;
    private JMenuItem accountNewShareAccount;
    private JMenuItem portfolioGraph;
    private JMenuItem portfolioExport;
    private JMenuItem portfolioImport;
    private JMenuItem portfolioDelete;
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
			   QuoteBundle quoteBundle) {

	this.desktop = desktop;
	this.portfolio = portfolio;
	this.quoteBundle = quoteBundle;

	propertySupport = new PropertyChangeSupport(this);

	createMenu();
	redraw();
    }

    // create new menu for this module
    private void createMenu() {
	menuBar = new JMenuBar();

	JMenu portfolioMenu = MenuHelper.addMenu(menuBar, "Portfolio", 'P');
	{
	    portfolioGraph =
		MenuHelper.addMenuItem(this, portfolioMenu,
				       "Graph");

	    portfolioMenu.addSeparator();

	    portfolioExport = 
		MenuHelper.addMenuItem(this, portfolioMenu,
				       "Export");

	    portfolioImport = 
		MenuHelper.addMenuItem(this, portfolioMenu,
				       "Import");

	    portfolioMenu.addSeparator();

	    portfolioDelete = MenuHelper.addMenuItem(this, portfolioMenu, 
						     "Delete");
	    portfolioMenu.addSeparator();


	    portfolioClose = MenuHelper.addMenuItem(this, portfolioMenu, 
						    "Close");
	}

	JMenu accountMenu = MenuHelper.addMenu(menuBar, "Account", 'A');
	{
	    accountNewCashAccount = 
		MenuHelper.addMenuItem(this, accountMenu,
				       "New Cash Account");
	    accountNewShareAccount = 
		MenuHelper.addMenuItem(this, accountMenu,
				       "New Share Account");
	}

	JMenu transactionMenu = 
	    MenuHelper.addMenu(menuBar, "Transaction", 'T');
	{
	    transactionNew = 
		MenuHelper.addMenuItem(this, transactionMenu,
				       "New");
	    transactionShowHistory = 
		MenuHelper.addMenuItem(this, transactionMenu,
				       "Show History");
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

	Vector accounts = portfolio.getAccounts();

	// If the portfolio is empty display a label saying "Empty"
	// otherwise display the portfolio
	if(accounts.size() == 0) {
	    addLabel("Empty");
	}
	else {
	    
	    Iterator iterator = accounts.iterator();
	    
	    // First list share accounts
	    while(iterator.hasNext()) {
		Account account = (Account)iterator.next();
		if(account instanceof ShareAccount) {
		    
		    addLabel(account.getName());
		    
		    // Add table of stock holdings for the portfolio
		    ShareAccount shareAccount = (ShareAccount)account;
		    table =
			new StockHoldingTable(shareAccount.getStockHoldings(),
					      quoteBundle);
		    
		    scrolledTable = new JScrollPane(table);
		    add(scrolledTable);
		    
		    restrictTableHeight(scrolledTable, table);
		}
	    }
	    
	    // Now add summary containing all accounts including total
	    addLabel("Summary");
	    
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
	// Dont save it if the user just deleted it otherwise save
	// the portfolio state
	if(!isDeleted) {
	    PreferencesManager.savePortfolio(portfolio);
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
			CommandManager.getInstance().graphPortfolio(portfolio);
		    }
		    else if(e.getSource() == portfolioDelete) {
			deletePortfolio();
		    }
		    else if(e.getSource() == portfolioImport) {
			importPortfolio();
		    }
		    else if(e.getSource() == portfolioExport) {
			exportPortfolio();
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
	    historyModule = 
		new TransactionModule(this, portfolio);
	
	    historyFrame = 
                ((org.mov.ui.DesktopManager)(desktop.getDesktopManager())).newFrame(historyModule);
	}
    }

    // Delete this portfolio
    private void deletePortfolio() {
	JDesktopPane desktop =
	    org.mov.ui.DesktopManager.getDesktop();

	int option = 
	    JOptionPane.showInternalConfirmDialog(desktop,
						  "Are you sure you wish to delete this portfolio?",
						  "Delete Portfolio",
						  JOptionPane.YES_NO_OPTION,
						  JOptionPane.WARNING_MESSAGE);
	if(option == JOptionPane.YES_OPTION) {
	    PreferencesManager.deletePortfolio(portfolio.getName());

	    MainMenu.getInstance().updatePortfolioMenu();

	    // Prevent save() function resurrecting portfolio
	    isDeleted = true;

	    // Close window
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}
    }

    // Export this portfolio to a CSV file
    private void exportPortfolio() {
	// Select file to export to
	JFileChooser chooser = new JFileChooser();
	int action = chooser.showSaveDialog(desktop);

	if(action == JFileChooser.APPROVE_OPTION) {
	    File file = chooser.getSelectedFile();
	    String fileName = file.getName();
		
	    try {
		FileWriter fileOut = new FileWriter(file);
		PrintWriter out = new PrintWriter(new BufferedWriter(fileOut));
		
		// Iterate through transactions printing one each on every line
		Vector transactions = portfolio.getTransactions();
		Iterator iterator = transactions.iterator();

		while(iterator.hasNext()) {
		    Transaction transaction = (Transaction)iterator.next();

		    // Save in CVS format
		    out.println(transaction);
		}
		
		out.close();
	
	    }
	    catch(java.io.IOException e) {
		org.mov.ui.DesktopManager.
		    showErrorMessage("Error writing to file: " +
				     fileName);
	    }
	}
    }

    // Import from a CSV file into this portfolio
    private void importPortfolio() {
	// Select file to import from
	JFileChooser chooser = new JFileChooser();
	chooser.setMultiSelectionEnabled(false);
	int action = chooser.showOpenDialog(desktop);

	if(action == JFileChooser.APPROVE_OPTION) {
	    File file = chooser.getSelectedFile();
	    String fileName = file.getName();

	    try {
		// Read file
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);		
		String line = br.readLine();

		// ... one line at a time
		while(line != null) {
		    // Uncomma separate
		    String[] parts = line.split(",");

		    int i = 0;
		    TradingDate date = new TradingDate(parts[i++],
						       TradingDate.BRITISH);

		    int type = Transaction.stringToType(parts[i++]);
		    float amount = Float.valueOf(parts[i++]).floatValue();
		    String symbol = parts[i++];
		    int shares = Integer.valueOf(parts[i++]).intValue();
		    float tradeCost = Float.valueOf(parts[i++]).floatValue();
		    String cashAccountName = parts[i++];
		    String cashAccountName2 = "";
		    String shareAccountName = "";

                    // When the line ends in ",," the split doesn't take the
                    // last values. So be prepared for a ArrayIndexOutOfBounds
                    // which is OK.
                    try {
                        cashAccountName2 = parts[i++];
                        shareAccountName = parts[i++];
                    }
                    catch(ArrayIndexOutOfBoundsException e) {
                        // OK
                    }

		    // Convert the cash/share accounts to a string - if
		    // we don't have the account in the portfolio, create it
		    CashAccount cashAccount = null;
		    CashAccount cashAccount2 = null;
		    ShareAccount shareAccount = null;

		    if(!cashAccountName.equals("")) {
			cashAccount = (CashAccount)
			    portfolio.findAccountByName(cashAccountName);

			// If its not found then create it
			if(cashAccount == null) { 
			    cashAccount = new CashAccount(cashAccountName);
			    portfolio.addAccount(cashAccount);
			}
		    }


		    if(!cashAccountName2.equals("")) {
			cashAccount2 = (CashAccount)
			    portfolio.findAccountByName(cashAccountName2);

			// If its not found then create it
			if(cashAccount2 == null) { 
			    cashAccount2 = new CashAccount(cashAccountName2);
			    portfolio.addAccount(cashAccount2);
			}
		    }

		    if(!shareAccountName.equals("")) {
			shareAccount = (ShareAccount)
			    portfolio.findAccountByName(shareAccountName);

			// If its not found then create it
			if(shareAccount == null) { 
			    shareAccount = new ShareAccount(shareAccountName);
			    portfolio.addAccount(shareAccount);
			}
		    }
		    
		    Transaction transaction = 
			new Transaction(type, date, amount, symbol, shares,
					tradeCost, cashAccount, cashAccount2, shareAccount);
		    portfolio.addTransaction(transaction);

		    line = br.readLine();
		}
	    }
	    catch(java.io.IOException e) {
		org.mov.ui.DesktopManager.
		    showErrorMessage("Error reading from file: " +
				     fileName);
	    }
	}

	redraw();
	checkMenuDisabledStatus(); // enable transaction menu
    }

    // Create a new cash account
    private void newCashAccount() {
	TextDialog dialog = 
	    new TextDialog(desktop, "Enter account name",
			   "New Cash Account");

	String accountName = dialog.showDialog();

	if(accountName != null && accountName.length() > 0) {
	    Account account = new CashAccount(accountName);
	    portfolio.addAccount(account);
	}

	redraw();
	checkMenuDisabledStatus(); // enable transaction menu
    }

    // Create a new share account
    private void newShareAccount() {
	TextDialog dialog = 
	    new TextDialog(desktop, "Enter account name",
			   "New Share Account");
	String accountName = dialog.showDialog();
	
	if(accountName != null && accountName.length() > 0) {
	    Account account = new ShareAccount(accountName);
	    portfolio.addAccount(account);
	}
	
	redraw();
	checkMenuDisabledStatus(); // enable transaction menu
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
	dialog.newTransaction();

	// Layout this portfolio and also the transaction history table (if its open)
	redraw();

	if(historyFrame != null && !historyFrame.isClosed())
	    historyModule.redraw();
    }
}
