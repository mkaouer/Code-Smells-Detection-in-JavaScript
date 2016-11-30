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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.String;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * GridBagHelper is a class that makes it easier to create form user interfaces. Using
 * GridBagLayout and this class you can easily create forms, reducing code repeitition.
 * For example to create a form which lets the user fill in two text field entries: 
 * <pre>
 * JPanel formPanel = new JPanel();
 * GridBagLayout gridbag = new GridBagLayout();
 * GridBagConstraints c = new GridBagConstraints();
 * formPanel.setLayout(gridbag);
 *           
 * c.weightx = 1.0;
 * c.ipadx = 5;
 * c.anchor = GridBagConstraints.WEST;
 *          
 * JTextField initialCapitalTextField = 
 *    GridBagHelper.addTextRow(formPanel, "Initial Capital", "", gridbag, c, 
 *                             10);
 * JTextField tradeCostTextField =
 *    GridBagHelper.addTextRow(formPanel, "Trade Cost", "", gridbag, c, 5);
 *
 * </pre>
 */
public class GridBagHelper {

    private GridBagHelper() {
        // this class cannot be instantiated
    }

    /**
     * Append a new row containing a check box button to the form.
     *
     * @param panel form panel
     * @param field text to display next to check box
     * @param isSelected is the check box currently selected?
     * @param gridbag the form's gridbag
     * @param c the form's constraints
     * @return the check box
     */
    public static JCheckBox addCheckBoxRow(JPanel panel, String field,
                                           boolean isSelected,
                                           GridBagLayout gridbag,
                                           GridBagConstraints c) {

        JCheckBox checkBox = new JCheckBox(field, isSelected);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(checkBox, c);
        panel.add(checkBox);

        return checkBox;
    }

    /**
     * Append a new row containing an expression combo box button to the form.
     *
     * @param panel form panel
     * @param field text to display next to expression combo box
     * @param value initial value of combo box
     * @param gridbag the form's gridbag
     * @param c the form's constraints
     * @return the combo box
     */
    public static ExpressionComboBox addExpressionRow(JPanel panel, String field, 
                                                      String value,
                                                      GridBagLayout gridbag,
                                                      GridBagConstraints c) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

	ExpressionComboBox comboBox = new ExpressionComboBox(value);
        if(c.gridx != -1)
            c.gridx++;
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(comboBox, c);
	panel.add(comboBox);

	return comboBox;
    }

    /**
     * Append a new row containing a password text field to the form.
     *
     * @param panel form panel
     * @param field text to display next to password text field
     * @param value initial value of password text field
     * @param gridbag the form's gridbag
     * @param c the form's constraints
     * @param length the length of the text field
     * @return the password text field
     */
    public static JPasswordField addPasswordRow(JPanel panel, String field, 
                                                String value,
                                                GridBagLayout gridbag,
                                                GridBagConstraints c,
                                                int length) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);
	    
	JPasswordField password = new JPasswordField(value, length);
        if(c.gridx != -1)
            c.gridx++;
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(password, c);
	panel.add(password);

	return password;
    }

    /**
     * Append a new row containing a text field to the form.
     *
     * @param panel form panel
     * @param field text to display next to text field
     * @param value initial value of text field
     * @param gridbag the form's gridbag
     * @param c the form's constraints
     * @param length the length of text field
     * @return the text field
     */
    public static JTextField addTextRow(JPanel panel, String field, String value,
                                        GridBagLayout gridbag,
                                        GridBagConstraints c,
                                        int length) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

	JTextField text = new JTextField(value, length);
        if(c.gridx != -1)
            c.gridx++;
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(text, c);
	panel.add(text);

	return text;
    }

    /**
     * Append a new row containing a label field to the form.
     *
     * @param panel form panel
     * @param field text to display next to the label field
     * @param labelText label text
     * @param gridbag the form's gridbag
     * @param c the form's constraints
     * @return the label field
     */
    public static JLabel addLabelRow(JPanel panel, String field, String labelText,
                                     GridBagLayout gridbag,
                                     GridBagConstraints c) {
	JLabel labelField = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(labelField, c);
	panel.add(labelField);

	JLabel label = new JLabel(labelText);
        if(c.gridx != -1)
            c.gridx++;
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(label, c);
	panel.add(label);

	return label;
    }

    /**
     * Append a new symbol list combo box to the form.
     *
     * @param panel form panel
     * @param field text to display next to the combo box
     * @param symbolList initial symbol list
     * @param gridbag the form's gridbag
     * @param c the form's constraints
     * @return the symbol list combo box
     */
    public static SymbolListComboBox addSymbolListComboBox(JPanel panel, 
                                                           String field, 
                                                           String symbolList,
                                                           GridBagLayout gridbag,
                                                           GridBagConstraints c) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

        SymbolListComboBox comboBox = new SymbolListComboBox(symbolList);
        if(c.gridx != -1)
            c.gridx++;
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(comboBox, c);
	panel.add(comboBox);

	return comboBox;

    }
}

