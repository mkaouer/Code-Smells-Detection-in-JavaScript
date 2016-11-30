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
import java.lang.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import org.mov.main.*;
import org.mov.util.*;
import org.mov.table.*;
import org.mov.quote.*;
import org.mov.ui.*;

/**
 * A dialog for letting the user add a new Transaction.
 * <pre>
 * TransactionDialog dialog = new TransactionDialog(desktop, portfolio);
 *
 * // Let user create a new transaction and add it to the portfolio
 * dialog.newTransaction()
 * </pre>
 *
 * @see Transaction
 */
public class TransactionDialog extends JInternalFrame 
    implements ActionListener {

    private JDesktopPane desktop;

    private JButton okButton;
    private JButton cancelButton;

    private JPanel mainPanel;
    private JPanel transactionPanel;

    // Fields of a transaction
    private JComboBox typeComboBox;
    private JTextField dateTextField;
    private JTextField amountTextField;
    private JTextField symbolTextField;
    private JTextField sharesTextField;
    private JTextField tradeCostTextField;
    private JComboBox cashAccountComboBox;
    private JComboBox cashAccountComboBox2;
    private JComboBox shareAccountComboBox;
    private PortfolioSymbolComboBox symbolComboBox;

    private Portfolio portfolio;

    // Results of dialog. Has it finished. What button was pressed. What is the transaction
    // on the screen.
    private boolean isDone = false;
    private boolean okButtonPressed = false;
    private Transaction transaction;

    /**
     * Create a new transaction dialog.
     *
     * @param	desktop	the current desktop
     * @param	portfolio	portfolio to add new transaction
     */
    public TransactionDialog(JDesktopPane desktop, Portfolio portfolio) {
	super();

	this.desktop = desktop;
	this.portfolio = portfolio;

	// Make sure we can't be hidden behind other windows
	setLayer(JLayeredPane.MODAL_LAYER);

	getContentPane().setLayout(new BorderLayout());

	mainPanel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	mainPanel.setLayout(gridbag);

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	JLabel typeLabel = new JLabel("Transaction Type");
	c.gridwidth = 1;
	gridbag.setConstraints(typeLabel, c);
	mainPanel.add(typeLabel);

	typeComboBox = new JComboBox();

	// If the portfolio only has a cash account then dont display
	// the share transactions
	if(portfolio.countAccounts(Account.SHARE_ACCOUNT) > 0) {
            boolean haveShares = (portfolio.getSymbolsTraded().size() > 0);

            typeComboBox.addItem(Transaction.typeToString(Transaction.ACCUMULATE));
	    typeComboBox.addItem(Transaction.typeToString(Transaction.DEPOSIT));

            if(haveShares) {
                typeComboBox.addItem(Transaction.typeToString(Transaction.DIVIDEND));
                typeComboBox.addItem(Transaction.typeToString(Transaction.DIVIDEND_DRP));
            }

	    typeComboBox.addItem(Transaction.typeToString(Transaction.FEE));
	    typeComboBox.addItem(Transaction.typeToString(Transaction.INTEREST));

            if(haveShares)
                typeComboBox.addItem(Transaction.typeToString(Transaction.REDUCE));

            if(portfolio.countAccounts(Account.CASH_ACCOUNT) >= 2) 
                typeComboBox.addItem(Transaction.typeToString(Transaction.TRANSFER));

	    typeComboBox.addItem(Transaction.typeToString(Transaction.WITHDRAWAL));
	}
	else {
	    typeComboBox.addItem(Transaction.typeToString(Transaction.DEPOSIT));
	    typeComboBox.addItem(Transaction.typeToString(Transaction.FEE));
	    typeComboBox.addItem(Transaction.typeToString(Transaction.INTEREST));

            if(portfolio.countAccounts(Account.CASH_ACCOUNT) >= 2) 
                typeComboBox.addItem(Transaction.typeToString(Transaction.TRANSFER));

	    typeComboBox.addItem(Transaction.typeToString(Transaction.WITHDRAWAL));
	}

	typeComboBox.addActionListener(this);

	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(typeComboBox, c);
	mainPanel.add(typeComboBox);

	TradingDate today = new TradingDate();

	dateTextField = 
	    GridBagHelper.addTextRow(mainPanel, "Date", today.toString("dd/mm/yyyy"), 
                                     gridbag, c, 10);

	JPanel buttonPanel = new JPanel();
	okButton = new JButton("OK");
	okButton.addActionListener(this);
	cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(this);
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);
  
	getContentPane().add(mainPanel, BorderLayout.NORTH);
	getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	setFrameSize();

	// Some panels use a combo box to get a symbol, others use a text field.
	// We need to reset this here so that when we try to retrieve the symbol
	// we try from the combo box first - if its null we try the text field instead
	symbolComboBox = null;
	symbolTextField = null;

	// Work out starting panel
	if(portfolio.countAccounts(Account.SHARE_ACCOUNT) > 0)
	    transactionPanel = getAccumulatePanel();
	else
	    transactionPanel = getCashPanel();

	getContentPane().add(transactionPanel, BorderLayout.CENTER);
    }

    // Get combo box listing cash accounts
    private JComboBox getCashAccountComboBox() {
	List accounts = portfolio.getAccounts();
	Iterator iterator = accounts.iterator();

	cashAccountComboBox = new JComboBox();
	
	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();

	    if(account.getType() == Account.CASH_ACCOUNT) 
		cashAccountComboBox.addItem(account.getName());

	}

	return cashAccountComboBox; 
    }


    // Get second combo box listing cash accounts
    private JComboBox getCashAccountComboBox2() {
	List accounts = portfolio.getAccounts();
	Iterator iterator = accounts.iterator();

	cashAccountComboBox2 = new JComboBox();
	
	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();

	    if(account.getType() == Account.CASH_ACCOUNT) 
		cashAccountComboBox2.addItem(account.getName());

	}

	return cashAccountComboBox2; 
    }

    // Get combo box listing symbols traded in the portfolio
    private JComboBox getSymbolComboBox() {
	symbolComboBox = new PortfolioSymbolComboBox(portfolio, null);
	
	return symbolComboBox;
    }

    // Get combo box listing share accounts
    private JComboBox getShareAccountComboBox() {
	List accounts = portfolio.getAccounts();
	Iterator iterator = accounts.iterator();

	shareAccountComboBox = new JComboBox();

	while(iterator.hasNext()) {
	    Account account = (Account)iterator.next();

	    if(account.getType() == Account.SHARE_ACCOUNT) 
		shareAccountComboBox.addItem(account.getName());
	}

	return shareAccountComboBox; 
    }

    // Get panel displayed when user enters a dividend transaction
    private JPanel getDividendPanel() {
	JPanel borderPanel = new JPanel();
	TitledBorder titled = new TitledBorder("Dividend");
	borderPanel.setBorder(titled);
	borderPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout(gridbag);

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	// Cash Account Line
	JLabel cashAccountLabel = new JLabel("Cash Account");
	c.gridwidth = 1;
	gridbag.setConstraints(cashAccountLabel, c);
	panel.add(cashAccountLabel);
	JComboBox cashAccountComboBox = getCashAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cashAccountComboBox, c);
	panel.add(cashAccountComboBox);

	// Share Account Line
	JLabel shareAccountLabel = new JLabel("Share Account");
	c.gridwidth = 1;
	gridbag.setConstraints(shareAccountLabel, c);
	panel.add(shareAccountLabel);
	JComboBox shareAccountComboBox = getShareAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(shareAccountComboBox, c);
	panel.add(shareAccountComboBox);

	// Symbol Line
	JLabel symbolLabel = new JLabel("Symbol");
	c.gridwidth = 1;
	gridbag.setConstraints(symbolLabel, c);
	panel.add(symbolLabel);
	JComboBox symbolComboBox = getSymbolComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(symbolComboBox, c);
	panel.add(symbolComboBox);

	// Amount Line
	amountTextField = 
	    GridBagHelper.addTextRow(panel, "Total Dividend Value", "", gridbag, c, 15);

	borderPanel.add(panel, BorderLayout.NORTH);

	return borderPanel;
    }

    // Get panel displayed when user enters a dividend DRP transaction
    private JPanel getDividendDRPPanel() {
	JPanel borderPanel = new JPanel();
	TitledBorder titled = new TitledBorder("Dividend (DRP)");
	borderPanel.setBorder(titled);
	borderPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout(gridbag);

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	// Share Account Line
	JLabel shareAccountLabel = new JLabel("Share Account");
	c.gridwidth = 1;
	gridbag.setConstraints(shareAccountLabel, c);
	panel.add(shareAccountLabel);
	JComboBox shareAccountComboBox = getShareAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(shareAccountComboBox, c);
	panel.add(shareAccountComboBox);

	// Symbol Line
	JLabel symbolLabel = new JLabel("Symbol");
	c.gridwidth = 1;
	gridbag.setConstraints(symbolLabel, c);
	panel.add(symbolLabel);
	JComboBox symbolComboBox = getSymbolComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(symbolComboBox, c);
	panel.add(symbolComboBox);

	sharesTextField = 
	    GridBagHelper.addTextRow(panel, "Shares", "", gridbag, c, 15);

	borderPanel.add(panel, BorderLayout.NORTH);

	return borderPanel;
    }

    // Get panel displayed when user enters an accumulate transaction
    private JPanel getAccumulatePanel() {
	JPanel borderPanel = new JPanel();
	TitledBorder titled = new TitledBorder("Accumulate Transaction");
	borderPanel.setBorder(titled);
	borderPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout(gridbag);

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	// Cash Account Line
	JLabel cashAccountLabel = new JLabel("Cash Account");
	c.gridwidth = 1;
	gridbag.setConstraints(cashAccountLabel, c);
	panel.add(cashAccountLabel);
	JComboBox cashAccountComboBox = getCashAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cashAccountComboBox, c);
	panel.add(cashAccountComboBox);

	// Share Account Line
	JLabel shareAccountLabel = new JLabel("Share Account");
	c.gridwidth = 1;
	gridbag.setConstraints(shareAccountLabel, c);
	panel.add(shareAccountLabel);
	JComboBox shareAccountComboBox = getShareAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(shareAccountComboBox, c);
	panel.add(shareAccountComboBox);

	// Symbol Line
	symbolTextField = 
	    GridBagHelper.addTextRow(panel, "Symbol", "", gridbag, c, 5);

	// Number Shares Line
	sharesTextField = 
	    GridBagHelper.addTextRow(panel, "Shares", "", gridbag, c, 15);

	// Share Value Line
	amountTextField = 
	    GridBagHelper.addTextRow(panel, "Total Share Value", "", gridbag, c, 15);

	tradeCostTextField = 
	    GridBagHelper.addTextRow(panel, "Trade Cost", "", gridbag, c, 15);

	borderPanel.add(panel, BorderLayout.NORTH);

	return borderPanel;
    }

    // Get panel displayed when user enters a reduce transaction
    private JPanel getReducePanel() {
	JPanel borderPanel = new JPanel();
	TitledBorder titled = new TitledBorder("Reduce Transaction");
	borderPanel.setBorder(titled);
	borderPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout(gridbag);

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	// Cash Account Line
	JLabel cashAccountLabel = new JLabel("Cash Account");
	c.gridwidth = 1;
	gridbag.setConstraints(cashAccountLabel, c);
	panel.add(cashAccountLabel);
	JComboBox cashAccountComboBox = getCashAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cashAccountComboBox, c);
	panel.add(cashAccountComboBox);

	// Share Account Line
	JLabel shareAccountLabel = new JLabel("Share Account");
	c.gridwidth = 1;
	gridbag.setConstraints(shareAccountLabel, c);
	panel.add(shareAccountLabel);
	JComboBox shareAccountComboBox = getShareAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(shareAccountComboBox, c);
	panel.add(shareAccountComboBox);

	// Symbol Line
	JLabel symbolLabel = new JLabel("Symbol");
	c.gridwidth = 1;
	gridbag.setConstraints(symbolLabel, c);
	panel.add(symbolLabel);
	JComboBox symbolComboBox = getSymbolComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(symbolComboBox, c);
	panel.add(symbolComboBox);

	// Number Shares Line
	sharesTextField = 
	    GridBagHelper.addTextRow(panel, "Shares", "", gridbag, c, 15);

	// Share Value Line
	amountTextField = 
	    GridBagHelper.addTextRow(panel, "Total Share Value", "", gridbag, c, 15);

	tradeCostTextField = 
	    GridBagHelper.addTextRow(panel, "Trade Cost", "", gridbag, c, 15);

	borderPanel.add(panel, BorderLayout.NORTH);

	return borderPanel;
    }

    // Get panel displayed when user enters a cash transaction
    private JPanel getCashPanel() {
	JPanel borderPanel = new JPanel();
	TitledBorder titled = new TitledBorder("Cash Transaction");
	borderPanel.setBorder(titled);
	borderPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout(gridbag);

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	JLabel cashAccountLabel = new JLabel("Cash Account");
	c.gridwidth = 1;
	gridbag.setConstraints(cashAccountLabel, c);
	panel.add(cashAccountLabel);

	JComboBox cashAccountComboBox = getCashAccountComboBox();

	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cashAccountComboBox, c);
	panel.add(cashAccountComboBox);
	
	amountTextField =
	    GridBagHelper.addTextRow(panel, "Amount", "", gridbag, c,
                                     15);

	borderPanel.add(panel, BorderLayout.NORTH);

	return borderPanel;
    }

    // Get panel displayed when user enters a cash transfer
    private JPanel getTransferPanel() {
	JPanel borderPanel = new JPanel();
	TitledBorder titled = new TitledBorder("Transfer");
	borderPanel.setBorder(titled);
	borderPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout(gridbag);

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	JLabel cashAccountLabel = new JLabel("Source Cash Account");
	c.gridwidth = 1;
	gridbag.setConstraints(cashAccountLabel, c);
	panel.add(cashAccountLabel);

	JComboBox cashAccountComboBox = getCashAccountComboBox();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cashAccountComboBox, c);
	panel.add(cashAccountComboBox);

	JLabel cashAccountLabel2 = new JLabel("Destination Cash Account");
	c.gridwidth = 1;
	gridbag.setConstraints(cashAccountLabel2, c);
	panel.add(cashAccountLabel2);

	JComboBox cashAccountComboBox2 = getCashAccountComboBox2();
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cashAccountComboBox2, c);
	panel.add(cashAccountComboBox2);
	
	amountTextField =
	    GridBagHelper.addTextRow(panel, "Amount", "", gridbag, c,
                                     15);

	borderPanel.add(panel, BorderLayout.NORTH);

	return borderPanel;
    }

    private Dimension getPreferredSizeWithPanel(JPanel panel) {

	if(transactionPanel != null) 
	    getContentPane().remove(transactionPanel);
	getContentPane().add(panel, BorderLayout.CENTER);
	transactionPanel = panel;

	return getPreferredSize();
    }

    private void setFrameSize() {
	Dimension preferred; 
	int width = 0;
	int height = 0;

	// Go through all panels and get the largest width and
	// height needed by all panels
	preferred = getPreferredSizeWithPanel(getDividendDRPPanel());
	width = preferred.width;
	height = preferred.height;

	preferred = getPreferredSizeWithPanel(getCashPanel());
	width = Math.max(width, preferred.width);
	height = Math.max(height, preferred.height);

	preferred = getPreferredSizeWithPanel(getDividendPanel());
	width = Math.max(width, preferred.width);
	height = Math.max(height, preferred.height);

	preferred = getPreferredSizeWithPanel(getAccumulatePanel());
	width = Math.max(width, preferred.width);
	height = Math.max(height, preferred.height);

	preferred = getPreferredSizeWithPanel(getReducePanel());
	width = Math.max(width, preferred.width);
	height = Math.max(height, preferred.height);

	preferred = getPreferredSizeWithPanel(getTransferPanel());
	width = Math.max(width, preferred.width);
	height = Math.max(height, preferred.height);

	int x = (desktop.getWidth() - width) / 2;
	int y = (desktop.getHeight() - height) / 2;

	setBounds(x, y, width, height);
    }

    /**
     * Display a dialog letting the user enter a new transaction.
     * Add the transaction to the portfolio.
     *
     * @return	whether the OK button was pressed
     */
    public boolean newTransaction() {

	setTitle("New Transaction");

	desktop.add(this);
	show();

	try {
	    while(isDone == false) {
		Thread.sleep(10);
	    }
	}
	catch(Exception e) {
	    // ignore
	}

	// If the user pressed the OK button then add the transaction to the
	// portfolio
	if(okButtonPressed)
	    portfolio.addTransaction(transaction);
		

	return okButtonPressed;
    }

    /**
     * Display a dialog letting the user edit an existing transaction.
     *
     * @param	oldTransaction	transaction to edit
     * @return	whether the OK button was pressed
     */
    public boolean editTransaction(Transaction oldTransaction) {

	setTitle("Edit Transaction");
	displayTransaction(oldTransaction);

	desktop.add(this);
	show();

	try {
	    while(isDone == false) {
		Thread.sleep(10);
	    }
	}
	catch(Exception e) {
	    // ignore
	}

	// To the user pressed the OK button then add the transaction. To do this remove all 
	// the old transactions and then re-add them with the new one.
	if(okButtonPressed) {
	    List transactions = new ArrayList(portfolio.getTransactions());

	    // Remove old transaction from list
	    Iterator iterator = transactions.iterator();
	    while(iterator.hasNext()) {
		Transaction traverseTransaction = (Transaction)iterator.next();

		if(traverseTransaction == oldTransaction) {
		    iterator.remove();
		    break;
		}
	    }

	    // Replace it with the new transaction
	    transactions.add(transaction);

	    // Remove and add transactions
	    portfolio.removeAllTransactions();
	    portfolio.addTransactions(transactions);
	}
	
	return okButtonPressed;
    }

    // Display the given transaction's details in the dialog box
    private void displayTransaction(Transaction transaction) {

	// Make sure we are displaying the right panel
	int type = transaction.getType();
	setTransactionPanel(type);

	// Set type and date fields
	typeComboBox.setSelectedItem(Transaction.typeToString(type));
	dateTextField.setText(transaction.getDate().toString("dd/mm/yyyy"));
	
	// Now fill in the fields for this panel (depending on type)
	if(type == Transaction.ACCUMULATE) {
	    cashAccountComboBox.setSelectedItem(transaction.getCashAccount().getName());
	    shareAccountComboBox.setSelectedItem(transaction.getShareAccount().getName());
	    symbolTextField.setText(transaction.getSymbol().toString());
	    sharesTextField.setText(String.valueOf(transaction.getShares()));
	    amountTextField.setText(String.valueOf(transaction.getAmount()));
	    tradeCostTextField.setText(String.valueOf(transaction.getTradeCost()));
	}
	else if(type == Transaction.REDUCE) {
	    cashAccountComboBox.setSelectedItem(transaction.getCashAccount().getName());
	    shareAccountComboBox.setSelectedItem(transaction.getShareAccount().getName());
	    symbolComboBox.setSelectedItem(transaction.getSymbol());
	    sharesTextField.setText(String.valueOf(transaction.getShares()));
	    amountTextField.setText(String.valueOf(transaction.getAmount()));
	    tradeCostTextField.setText(String.valueOf(transaction.getTradeCost()));
	}
	else if(type == Transaction.DEPOSIT ||
		type == Transaction.FEE ||
		type == Transaction.INTEREST ||
		type == Transaction.WITHDRAWAL) {

	    amountTextField.setText(String.valueOf(transaction.getAmount()));
	    cashAccountComboBox.setSelectedItem(transaction.getCashAccount().getName());
	}
	else if(type == Transaction.DIVIDEND) {
	    cashAccountComboBox.setSelectedItem(transaction.getCashAccount().getName());
	    shareAccountComboBox.setSelectedItem(transaction.getShareAccount().getName());
	    symbolComboBox.setSelectedItem(transaction.getSymbol());
	    amountTextField.setText(String.valueOf(transaction.getAmount()));
	}
	else if(type == Transaction.DIVIDEND_DRP) {
	    shareAccountComboBox.setSelectedItem(transaction.getShareAccount().getName());
	    symbolComboBox.setSelectedItem(transaction.getSymbol());
	    sharesTextField.setText(String.valueOf(transaction.getShares()));
	}       
	else {
	    assert type == Transaction.TRANSFER;

	    cashAccountComboBox.setSelectedItem(transaction.getCashAccount().getName());
	    cashAccountComboBox2.setSelectedItem(transaction.getCashAccount2().getName());
	    amountTextField.setText(String.valueOf(transaction.getAmount()));	    
	}
    }

    // Changes the transaction panel we are displaying depending on the type
    private void setTransactionPanel(int type) {
	getContentPane().remove(transactionPanel);
	
	// Some panels use a combo box to get a symbol, others use a text field.
	// We need to reset this here so that when we try to retrieve the symbol
	// we try from the combo box first - if its null we try the text field instead
	symbolComboBox = null;
	symbolTextField = null;

	if(type == Transaction.ACCUMULATE) {
	    transactionPanel = getAccumulatePanel();
	}
	else if(type == Transaction.REDUCE) {
	    transactionPanel = getReducePanel();
	}
	else if(type == Transaction.DEPOSIT ||
		type == Transaction.FEE ||
		type == Transaction.INTEREST ||
		type == Transaction.WITHDRAWAL) {
	    transactionPanel = getCashPanel();
	}
	else if(type == Transaction.DIVIDEND) {
	    transactionPanel = getDividendPanel();
	}
	else if(type == Transaction.DIVIDEND_DRP) {
	    transactionPanel = getDividendDRPPanel();
	}
	else {
	    assert type == Transaction.TRANSFER;
	    transactionPanel = getTransferPanel();
	}
	
	getContentPane().add(transactionPanel, BorderLayout.CENTER);	
    }

    public void actionPerformed(ActionEvent e) {

	if(e.getSource() == okButton) {
	    transaction = buildTransaction();
	    
	    // Add it to portfolio and exit if we managed to
	    // build a complete transaction
	    if(transaction != null) {
		dispose();
		isDone = true;
		okButtonPressed = true;
	    }
	}
	else if(e.getSource() == cancelButton) {
	    transaction = null;
	    dispose();
	    isDone = true;
	    okButtonPressed = false;
	}
	else if(e.getSource() == typeComboBox) {
	    // Change panel depending on transaction type
	    String selected = (String)typeComboBox.getSelectedItem();
	    int type = Transaction.stringToType(selected);

	    setTransactionPanel(type);

	    validate();
	    repaint();
	}

    }	

    // Take transaction details from GUI, verify and create a
    // Transaction
    private Transaction buildTransaction() {
	Transaction transaction = null;
        String symbolParseError = null;

	//
	// First extract data from GUI
	//

	int type = 
	    Transaction.stringToType((String)typeComboBox.getSelectedItem());
	TradingDate date = new TradingDate(dateTextField.getText(),
					   TradingDate.BRITISH);

	// Get symbol - try the combo box first. If it doesn't exist then try the
	// text field.
	Symbol symbol = null;

	if(symbolComboBox != null) {
	    symbol = (Symbol)symbolComboBox.getSelectedItem();
	}
	else if(symbolTextField != null) {
            try {
                symbol = Symbol.toSymbol(symbolTextField.getText());
            }
            catch(SymbolFormatException e) {
                symbolParseError = e.getReason();
            }
	}

	CashAccount cashAccount = null;
	if(cashAccountComboBox != null) {
	    String accountName = 
		(String)cashAccountComboBox.getSelectedItem();
	    cashAccount = 
		(CashAccount)portfolio.findAccountByName(accountName);
	}

	CashAccount cashAccount2 = null;
	if(cashAccountComboBox2 != null) {
	    String accountName = 
		(String)cashAccountComboBox2.getSelectedItem();
	    cashAccount2 = 
		(CashAccount)portfolio.findAccountByName(accountName);
	}

	ShareAccount shareAccount = null;
	if(shareAccountComboBox != null) {
	    String accountName = 
		(String)shareAccountComboBox.getSelectedItem();
	    shareAccount = 
		(ShareAccount)portfolio.findAccountByName(accountName);
	}

	float amount = 0.0F;
	int shares = 0;
	float tradeCost = 0.0F;

	try {
	    if(amountTextField != null) 
		if(!amountTextField.getText().equals(""))
		    amount = Float.parseFloat(amountTextField.getText());

	    if(sharesTextField != null) 
		if(!sharesTextField.getText().equals(""))
		    shares = Integer.parseInt(sharesTextField.getText());
	    
	    if(tradeCostTextField != null) 
		if(!tradeCostTextField.getText().equals(""))
		    tradeCost = Float.parseFloat(tradeCostTextField.getText());
	}

	//
	// Validate fields
	//

	// Can't parse numeric field?
	catch(NumberFormatException e) {
	    String message = new String("Can't parse number '" +
					e.getMessage() + "'");

	    JOptionPane.showInternalMessageDialog(desktop, 
						  message,
						  "Error building transaction",
						  JOptionPane.ERROR_MESSAGE);
	    return null;
	}

	// Can't parse date? (all fields will be 0)
	if(date.getYear() == 0) {
	    String message = new String("Can't parse date '" + 
					dateTextField.getText() + "', " +
					"please enter in form DD/MM/YYYY");

	    JOptionPane.showInternalMessageDialog(desktop, 
						  message,
						  "Error building transaction",
						  JOptionPane.ERROR_MESSAGE);
	    return null;
	}

	// When transferring money - it must be between two different accounts
	if(type == Transaction.TRANSFER && cashAccount == cashAccount2) {
	    JOptionPane.showInternalMessageDialog(desktop, 
						  "Source and destination accounts are the same",
						  "Error building transaction",
						  JOptionPane.ERROR_MESSAGE);
	    return null;
	}
	    
	// If we are using the stock symbol check that its valid
	if((type == Transaction.ACCUMULATE || type == Transaction.REDUCE ||
	    type == Transaction.DIVIDEND_DRP) && symbolParseError != null) {

            JOptionPane.showInternalMessageDialog(desktop, 
                                                  symbolParseError,
                                                  "Error building transaction",
                                                  JOptionPane.ERROR_MESSAGE);
            return null;
        }

	//
	// Build transaction
	//

	if(type == Transaction.WITHDRAWAL) {
	    transaction = Transaction.newWithdrawal(date, amount, cashAccount);
	}
	else if(type == Transaction.DEPOSIT) {
	    transaction = Transaction.newDeposit(date, amount,
						 cashAccount);
	}
	else if(type == Transaction.INTEREST) {
	    transaction = Transaction.newInterest(date, amount,
						  cashAccount);
	}
	else if(type == Transaction.FEE) {
	    transaction = Transaction.newFee(date, amount,
					     cashAccount);
	}
	else if(type == Transaction.ACCUMULATE) {
	    transaction = Transaction.newAccumulate(date, amount,
						    symbol, shares,
						    tradeCost,
						    cashAccount,
						    shareAccount);
	}
	else if(type == Transaction.REDUCE) {
	    transaction = Transaction.newReduce(date, amount,
						symbol, shares,
						tradeCost,
						cashAccount,
						shareAccount);
	}
	else if(type == Transaction.DIVIDEND) {
	    transaction = Transaction.newDividend(date, amount,
						  symbol, 
						  cashAccount,
						  shareAccount);
	}
	else if(type == Transaction.DIVIDEND_DRP) {
	    transaction = Transaction.newDividendDRP(date, amount,
						     symbol, shares,
						     shareAccount);
	}

	else {
	    assert type == Transaction.TRANSFER;
	    transaction = Transaction.newTransfer(date, amount,
						  cashAccount,
						  cashAccount2);
	}

	return transaction;
    }
}
