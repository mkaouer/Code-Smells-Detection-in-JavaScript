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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mov.ui.CurrencyComboBox;
import org.mov.util.Currency;
import org.mov.util.Locale;

/**
 * A dialog that allows the user to enter an account name and currency. The account
 * could be a portfolio or a portfolio's account.
 *
 * Example:
 * <pre>
 *      AccountDialog dialog = new AccountDialog(desktop,
 *                                               Locale.getString("ENTER_PORTFOLIO_NAME"),
 *                                               Locale.getString("NEW_PORTFOLIO"));
 *      if(dialog.showDialog()) {
 *          String accountName = dialog.getAccountName();
 *          Currency accountCurrency = dialog.getAccountCurrency();
 *      }
 * </pre>
 *
 * @author Quentin Bossard, Andrew Leppard
 */
public class AccountDialog {

    JComponent parent = null;
    String accountName = null;
    Currency accountCurrency = null;
    JButton OKButton, cancelButton;
    JTextField textField;
    CurrencyComboBox comboBox;
    JDialog textDialog;
    JInternalFrame textFrame;
    JPanel optionPanel;

    boolean isDone;

    /**
     * Create new account dialog.
     *
     * @param parent  The parent component to tie the dialog to
     * @param message The prompt text
     * @param title   The title to place on the dialog
     */
    public AccountDialog(JComponent parent, String message, String title) {
	newDialog(parent, message, title, Currency.getDefaultCurrency());
    }

    /**
     * Create new account dialog.
     *
     * @param parent   The parent component to tie the dialog to
     * @param message  The prompt text
     * @param title    The title to place on the dialog
     * @param currency The default currency for the account.
     */
    public AccountDialog(JComponent parent, String message, String title, Currency currency) {
	newDialog(parent, message, title, currency);
    }

    /**
     * Create new account dialog.
     *
     * @param parent   The parent component to tie the dialog to
     * @param message  The prompt text
     * @param title    The title to place on the dialog
     * @param currency The default currency for the account.
     */
    private void newDialog(JComponent parent, String message, String title,  Currency currency) {
        this.parent = parent;

	OKButton = new JButton(Locale.getString("OK"));
	cancelButton = new JButton(Locale.getString("CANCEL"));
	textField = new JTextField();
        comboBox = new CurrencyComboBox(currency);

	JLabel label = new JLabel(message);
        JLabel currencyLabel = new JLabel(Locale.getString("CURRENCY"));

        // Make sure the label and text field are aligned
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);        
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        Box box = Box.createVerticalBox();
        box.add(label);
        box.add(Box.createVerticalStrut(5));
        box.add(textField);
        box.add(currencyLabel);        
        box.add(comboBox);

	OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    accountName = textField.getText();
                    accountCurrency = comboBox.getSelectedCurrency();
                    isDone = true;
                    textFrame.dispose();
                }
            });

	cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    accountName = null;
                    accountCurrency = null;
                    isDone = true;
                    textFrame.dispose();
                }
            });

	Object options[] = {OKButton, cancelButton};
	JOptionPane optionPane = new JOptionPane(box,
						 JOptionPane.QUESTION_MESSAGE,
						 JOptionPane.OK_CANCEL_OPTION,
						 null, options, null);

	textFrame = optionPane.createInternalFrame(parent,
						   title);
	optionPane.getRootPane().setDefaultButton(OKButton);
    }
    
    /*
     * Pops up the dialog and waits for feedback
     *
     * @return the string value the user has typed in
     */
    public boolean showDialog() {
        boolean invalidResponse;

        do {
            isDone = false;

            textFrame.show();
        
            try {
                while(!isDone) 
                    Thread.sleep(10);
                
            } catch (InterruptedException e) {
                // Ignore
            }

            invalidResponse = false;

            // Make sure the account only contains [a-zA-Z -_0-9]. Other letters
            // may trouble the XML format or cause problems in file names.
            if (accountName != null && accountName.length() > 0) {
                Pattern pattern = Pattern.compile("[^a-zA-Z0-9 _-]");
                Matcher matcher = pattern.matcher(accountName);
                if(matcher.find()) {
                    invalidResponse = true;
                    JOptionPane.showInternalMessageDialog(parent, 
                                                          Locale.getString("INVALID_ACCOUNT_NAME", accountName),
                                                          Locale.getString("INVALID_ACCOUNT_NAME_TITLE"),
                                                          JOptionPane.ERROR_MESSAGE);
                }
            }

        } while(invalidResponse);

        return (accountName != null && accountName.length() > 0);
    }

    /**
     * Return the account name entered into the dialog.
     *
     * @return the account name.
     */
    public String getAccountName() {
        return accountName;
    }
    
    /**
     * Return the currency selected.
     *
     * @return the currency.
     */
    public Currency getAccountCurrency() {
        return accountCurrency;
    }
}