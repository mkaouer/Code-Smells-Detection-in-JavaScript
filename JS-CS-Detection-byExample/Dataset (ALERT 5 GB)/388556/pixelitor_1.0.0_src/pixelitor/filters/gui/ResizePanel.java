/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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

package pixelitor.filters.gui;

import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.filters.comp.CompositionUtils;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.OKCancelDialog;

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

public class ResizePanel extends JPanel implements KeyListener, ItemListener {
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
    private static final int NR_OF_COLUMNS = 5;

    private ResizePanel(Composition comp) {
        // so that it is set even if the user changes nothing in the dialog
//        ((Resize) operation).setTargetSize(oldWidth, oldHeight);
        oldWidth = comp.getCanvasWidth();
        oldHeight = comp.getCanvasHeight();

        originalProportion = ((double) oldWidth) / oldHeight;
        newWidth = oldWidth;
        newHeight = oldHeight;
        newWidthInPercent = 100.0;
        newHeightInPercent = 100.0;

        Object[] items = new String[]{"pixels", "percent"};
        ComboBoxModel comboBoxModel = new DefaultComboBoxModel(items);
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());

        GridBagHelper.addLabel(p, "Width:", 0, 0);
        widthTextField = new JTextField(NR_OF_COLUMNS);
        widthTextField.addKeyListener(this);
        widthTextField.setText(String.valueOf(oldWidth));
        GridBagHelper.addControl(p, widthTextField);
        pixelPercentChooser1 = new JComboBox(comboBoxModel);
        GridBagHelper.addNextControl(p, pixelPercentChooser1);

        GridBagHelper.addLabel(p, "Height:", 0, 1);
        heightTextField = new JTextField(NR_OF_COLUMNS);
        heightTextField.setText(String.valueOf(oldHeight));
        GridBagHelper.addControl(p, heightTextField);
        heightTextField.addKeyListener(this);
        JComboBox pixelPercentChooser2 = new JComboBox(comboBoxModel);
        GridBagHelper.addNextControl(p, pixelPercentChooser2);

        p.setBorder(BorderFactory.createTitledBorder("Resize from " + oldWidth + 'x' + oldHeight));
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

    private static double parseLocalizedDouble(String s) {
        double retVal = 100.0;
        try {
            Number number = doubleFormatter.parse(s);
            retVal = number.doubleValue();
        } catch (ParseException ex) {
            ExceptionHandler.showExceptionDialog(ex);
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
//        updateFilter();
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
//        updateFilter();
    }

    public boolean validData() {
        if(getNewWidth() == 0 || getNewHeight() == 0) {
            validData = false;
            errorMessage = "Width and height cannot be 0";
        }

        return validData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getNewWidth() {
        return newWidth;
    }

    public int getNewHeight() {
        return newHeight;
    }


    public static void showInDialog(final Composition comp) {
        final ResizePanel p = new ResizePanel(comp);
        OKCancelDialog d = new OKCancelDialog(p, "Resize") {
            @Override
            protected void dialogAccepted() {
                super.dialogAccepted();

                if (!p.validData()) {
                    JOptionPane.showMessageDialog(this, p.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CompositionUtils.resize(comp, p.getNewWidth(), p.getNewHeight(), false);
                dispose();
            }

            @Override
            protected void dialogCancelled() {
                super.dialogCancelled();

                dispose();

            }
        };
        d.setVisible(true);
    }

}