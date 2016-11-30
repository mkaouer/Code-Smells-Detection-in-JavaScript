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

package org.mov.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.mov.parser.ExpressionException;
import org.mov.parser.Parser;
import org.mov.util.Locale;

/**
 * A dialog which allows the user to set the user column expressions in a table.
 * All the quotes tables in venice (e.g. the historical quote data tables and the
 * watch screen), allow the user five rows of expression columns (refered to as
 * equation columns in the UI). This dialog allows the user to edit the
 * expressions in those columns.
 *
 * @author Andrew Leppard
 */
public class ExpressionColumnDialog extends JInternalFrame implements ActionListener {

    private JButton okButton;
    private JButton cancelButton;

    private JPanel mainPanel;
    private JPanel transactionPanel;

    // Fields of a transaction
    private JComboBox expressionColumnComboBox;
    private JTextField columnNameTextField;
    private ExpressionComboBox expressionComboBox;

    private boolean isDone = false;

    private ExpressionColumn[] expressionColumns;
    private int currentExpressionColumn = 0;

    private boolean OKButtonPressed;

    // TODO: This should just be "showDialog()".
    public ExpressionColumnDialog(int expressionColumnCount) {
	super(Locale.getString("APPLY_EQUATIONS"));

	getContentPane().setLayout(new BorderLayout());

	mainPanel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	mainPanel.setLayout(gridbag);
	mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	JLabel typeLabel = new JLabel(Locale.getString("EQUATION_COLUMN"));
	c.gridwidth = 1;
	gridbag.setConstraints(typeLabel, c);
	mainPanel.add(typeLabel);

	expressionColumnComboBox = new JComboBox();

	String[] numbers = {Locale.getString("ONE"),
                            Locale.getString("TWO"),
                            Locale.getString("THREE"),
                            Locale.getString("FOUR"),
                            Locale.getString("FIVE")};

	for(int i = 0; i < expressionColumnCount; i++)
	    expressionColumnComboBox.addItem(numbers[i]);

	expressionColumnComboBox.addActionListener(this);

	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(expressionColumnComboBox, c);
	mainPanel.add(expressionColumnComboBox);

        c.fill = GridBagConstraints.HORIZONTAL;

	columnNameTextField =
	    GridBagHelper.addTextRow(mainPanel, Locale.getString("COLUMN_NAME"), "",
                                     gridbag, c, 18);

	expressionComboBox =
	    GridBagHelper.addExpressionRow(mainPanel, Locale.getString("EQUATION"), "",
                                         gridbag, c);

	JPanel buttonPanel = new JPanel();
	okButton = new JButton(Locale.getString("OK"));
	okButton.addActionListener(this);
	cancelButton = new JButton(Locale.getString("CANCEL"));
	cancelButton.addActionListener(this);
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);

	getContentPane().add(mainPanel, BorderLayout.NORTH);
	getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	// Open dialog in centre of window
	Dimension size = getPreferredSize();
	int x = (DesktopManager.getDesktop().getWidth() - size.width) / 2;
	int y = (DesktopManager.getDesktop().getHeight() - size.height) / 2;
	setBounds(x, y, size.width, size.height);
    }

    public boolean showDialog(ExpressionColumn[] expressionColumns) {

	// Creat copy of expression columns to work with
	this.expressionColumns = new ExpressionColumn[expressionColumns.length];
	for(int i = 0; i < expressionColumns.length; i++)
	    this.expressionColumns[i] = (ExpressionColumn)expressionColumns[i].clone();

	displayExpressionColumn(0);

	DesktopManager.getDesktop().add(this);
	show();

	try {
	    while(isDone == false) {
		Thread.sleep(10);
	    }
	}
	catch(Exception e) {
	    // ignore
	}

	return OKButtonPressed;
    }

    public ExpressionColumn[] getExpressionColumns() {
	return expressionColumns;
    }

    private void saveExpressionColumn(int column) {
	// Store new values the user has typed in
	expressionColumns[column].setShortName(columnNameTextField.getText());
	expressionColumns[column].setExpressionText(expressionComboBox.getExpressionText());
    }

    private void displayExpressionColumn(int column) {
	currentExpressionColumn = column;

	columnNameTextField.setText(expressionColumns[column].getShortName());
	expressionComboBox.setExpressionText(expressionColumns[column].getExpressionText());
    }

    // Make sure the expression field is correct in each expression column. If
    // any of the expressions do not parse then display an error dialog to
    // the user.
    private boolean parseExpressions() {
        boolean success = true;
        int i = 0;

        try {
            for(i = 0; i < expressionColumns.length; i++) {
                String expressionString = expressionColumns[i].getExpressionText();

                if(expressionString != null && expressionString.length() > 0)
                    expressionColumns[i].setExpression(Parser.parse(expressionString));
                else
                    expressionColumns[i].setExpression(null);
            }
        }
        catch(ExpressionException e) {
            JOptionPane.
                showInternalMessageDialog(this,
                                          e.getReason(),
                                          Locale.getString("ERROR_PARSING_EXPRESSION"),
                                          JOptionPane.ERROR_MESSAGE);
            success = false;
        }

        return success;
    }

    public void actionPerformed(ActionEvent e) {

	if(e.getSource() == okButton) {
	    saveExpressionColumn(currentExpressionColumn);

            if(parseExpressions()) {
                OKButtonPressed = true;
                dispose();
                isDone = true;
            }
	}
	else if(e.getSource() == cancelButton) {
	    saveExpressionColumn(currentExpressionColumn);

	    OKButtonPressed = false;
	    dispose();
	    isDone = true;
	}

	else if(e.getSource() == expressionColumnComboBox) {
	    // Save the current values and display new ones
	    saveExpressionColumn(currentExpressionColumn);
	    displayExpressionColumn(expressionColumnComboBox.getSelectedIndex());
	}
    }	
}
