/*
 * Copyright 2009-2010 László Balázs-Csíki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.operations.gui;

import pixelitor.operations.Operation;
import pixelitor.operations.Resize;
import pixelitor.utils.GridBagHelper;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class ResizeAdjustments extends AdjustPanel implements KeyListener, ItemListener {
    private static NumberFormat doubleFormatter = new DecimalFormat("#0.00");

    private JCheckBox constrainProportionsCheckBox;
    private JComboBox pixelPercentChooser1;
    private JTextField heightTextField;
    private JTextField widthTextField;
    private double originalProportion;
    private int newWidth;
    private int newHeight;
    private double newWidthInPercent;
    private double newHeightInPercent;
    private int oldWidth;
    private int oldHeight;
    private boolean validData = true; // the defaults are valid
    private String errorMessage;

    public ResizeAdjustments(Operation operation, int oldWidth, int oldHeight) {
        super(operation);

        // to that it is set even if the user changes nothing in the dialog
        ((Resize) operation).setTargetSize(oldWidth, oldHeight);

        originalProportion = ((double) oldWidth) / oldHeight;
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        newWidth = oldWidth;
        newHeight = oldHeight;
        newWidthInPercent = 100d;
        newHeightInPercent = 100d;

        Object[] items = new String[]{"pixels", "percent"};
        ComboBoxModel comboBoxModel = new DefaultComboBoxModel(items);
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());

        GridBagHelper.addLabel(p, "Width:", 0, 0);
        widthTextField = new JTextField();
        widthTextField.addKeyListener(this);
        widthTextField.setText(String.valueOf(oldWidth));
        GridBagHelper.addControl(p, widthTextField);
        pixelPercentChooser1 = new JComboBox(comboBoxModel);
        GridBagHelper.addNextControl(p, pixelPercentChooser1);

        GridBagHelper.addLabel(p, "Height:", 0, 1);
        heightTextField = new JTextField();
        heightTextField.setText(String.valueOf(oldHeight));
        GridBagHelper.addControl(p, heightTextField);
        heightTextField.addKeyListener(this);
        JComboBox pixelPercentChooser2 = new JComboBox(comboBoxModel);
        GridBagHelper.addNextControl(p, pixelPercentChooser2);

        p.setBorder(BorderFactory.createTitledBorder("Resize from " + oldWidth + "x" + oldHeight));
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(p);

        JPanel p2 = new JPanel();
        constrainProportionsCheckBox = new JCheckBox("Constrain Proportions");
        constrainProportionsCheckBox.setSelected(true);
        p2.add(constrainProportionsCheckBox);
        p2.setLayout(new FlowLayout(FlowLayout.LEFT));
        verticalBox.add(p2);
        add(verticalBox);

        pixelPercentChooser1.addItemListener(this);
        pixelPercentChooser2.addItemListener(this);

        constrainProportionsCheckBox.addItemListener(this);
    }

    private boolean pixelsSelected() {
        return (pixelPercentChooser1.getSelectedIndex() == 0);
    }


    private boolean constrainProportions() {
        return constrainProportionsCheckBox.isSelected();
    }

    private double parseLocalizedDouble(String s) {
        double retVal = 100d;
        try {
            Number number = doubleFormatter.parse(s);
            retVal = number.doubleValue();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return retVal;
    }

    // a combo box or a checkbox was used
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == constrainProportionsCheckBox) {
            if (constrainProportions()) {
                // it just got selected, adjust the height to the width
                newHeight = (int) (newWidth / originalProportion);
                newHeightInPercent = newWidthInPercent;
                if (pixelsSelected()) {
                    heightTextField.setText(String.valueOf(newHeight));
                } else {
                    heightTextField.setText(doubleFormatter.format(newHeightInPercent));
                }
            }
        } else { // one of the combo boxes was selected
            if (pixelsSelected()) {
                widthTextField.setText(String.valueOf(newWidth));
                heightTextField.setText(String.valueOf(newHeight));
            } else {
                widthTextField.setText(doubleFormatter.format(newWidthInPercent));
                heightTextField.setText(doubleFormatter.format(newHeightInPercent));
            }
        }
        updateFilter();
    }

    private void updateFilter() {
        Resize resizeOp = (Resize) op;
        resizeOp.setTargetSize(newWidth, newHeight);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        validData = true;
        errorMessage = null;
        if (e.getSource() == widthTextField) {
            if (pixelsSelected()) {
                try {
                    newWidth = Integer.parseInt(widthTextField.getText());
                    if (constrainProportions()) {
                        newHeight = (int) (newWidth / originalProportion);
                        heightTextField.setText(String.valueOf(newHeight));
                        newHeightInPercent = ((double) newHeight) * 100 / oldHeight;
                    }
                    newWidthInPercent = ((double) newWidth) * 100 / oldWidth;
                } catch (NumberFormatException ex) {
                    if (widthTextField.getText().trim().isEmpty()) {
                        validData = false;
                        errorMessage = "the 'width' field is empty";
                    } else {
                        widthTextField.setText(String.valueOf(newWidth)); // reset
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            } else { // percent was selected
                newWidthInPercent = parseLocalizedDouble(widthTextField.getText());
                newWidth = (int) (oldWidth * newWidthInPercent / 100);
                if (constrainProportions()) {
                    newHeight = (int) (newWidth / originalProportion);
                    newHeightInPercent = newWidthInPercent;
                    heightTextField.setText(doubleFormatter.format(newHeightInPercent));
                }
            }
        } else if (e.getSource() == heightTextField) {
            if (pixelsSelected()) {
                try {
                    newHeight = Integer.parseInt(heightTextField.getText());
                    if (constrainProportions()) {
                        newWidth = (int) (newHeight * originalProportion);
                        widthTextField.setText(String.valueOf(newWidth));
                        newWidthInPercent = parseLocalizedDouble(widthTextField.getText());
                    }
                    newHeightInPercent = ((double) newHeight) * 100 / oldHeight;
                } catch (NumberFormatException ex) {
                    if (heightTextField.getText().trim().isEmpty()) {
                        validData = false;
                        errorMessage = "the 'height' field is empty";
                    } else {
                        heightTextField.setText(String.valueOf(newHeight)); // reset
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            } else {  // percent was selected
                newHeightInPercent = parseLocalizedDouble(heightTextField.getText());
                newHeight = (int) (oldHeight * newHeightInPercent / 100);
                if (constrainProportions()) {
                    newWidth = (int) (newHeight * originalProportion);
                    newWidthInPercent = newHeightInPercent;
                    widthTextField.setText(doubleFormatter.format(newWidthInPercent));
                }
            }
        }
        updateFilter();
    }

    @Override
    public boolean validData() {
        return validData;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}