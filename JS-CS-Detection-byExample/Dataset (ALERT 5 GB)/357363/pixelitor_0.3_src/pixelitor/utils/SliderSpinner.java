/*
 * Copyright 2009 László Balázs-Csíki
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

package pixelitor.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pixelitor.filters.LinearIntParam;

public class SliderSpinner extends JPanel implements ChangeListener, ActionListener {
    private boolean restrictMaximumSize = true;

    public enum TextPosition {
        BORDER, LABEL
    }

    private JSlider slider;
    private JSpinner spinner;
    private ChangeListener externalChangeListener;
    private JButton defaultButton = new JButton("");
    private LinearIntParam param;

    private Color leftColor;
    private Color rightColor;


    public SliderSpinner(LinearIntParam param, ChangeListener listener, boolean addDefaultButton, TextPosition position) {
        this(null, null, param, listener, addDefaultButton, position);
    }

    public SliderSpinner(Color leftColor, Color rightColor, LinearIntParam param, ChangeListener listener) {
        this(leftColor, rightColor, param, listener, true, TextPosition.BORDER);
    }

    // TODO: if color is specified, ythe default button is always added - sublacc
    private SliderSpinner(Color leftColor, Color rightColor, LinearIntParam param, ChangeListener listener, boolean addDefaultButton, TextPosition textPosition) {
        setLayout(new FlowLayout());
        this.param = param;

        this.leftColor = leftColor;
        this.rightColor = rightColor;


        if (textPosition == TextPosition.BORDER) {
            if ((leftColor != null) && (rightColor != null)) {
                GradientBorder border = new GradientBorder(leftColor, rightColor);
                this.setBorder(BorderFactory.createTitledBorder(border, param.getName()));
            } else {
                this.setBorder(BorderFactory.createTitledBorder(param.getName()));
                this.leftColor = Color.GRAY;
                this.rightColor = Color.GRAY;
            }
        }

        this.externalChangeListener = listener;

//        slider = new JSlider(SwingConstants.HORIZONTAL, param.getMinimum(), param.getMaximum(), param.getDefaultValue());
        slider = new JSlider(param);
        if (textPosition == TextPosition.BORDER) {
            int max = param.getMaximum();
            int minorSpacing = (max + 1)/8;
            int majorSpacing = 2*minorSpacing;
            slider.setMajorTickSpacing(majorSpacing);
            slider.setMinorTickSpacing(minorSpacing);

            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
        }
        slider.addChangeListener(this);

        SpinnerModel spinnerModel =
                new SpinnerNumberModel(param.getValue(), //initial value
                        param.getMinimum(), //min
                        param.getMaximum(), //max
                        1);       //step

        spinner = new JSpinner(spinnerModel);
        spinner.addChangeListener(this);


//        JComponent editor = new JSpinner.NumberEditor(spinner);
//        spinner.setEditor(editor);

        int spinnerHeight = (int) spinner.getPreferredSize().getHeight();

        if(textPosition == TextPosition.LABEL) {
            add(new JLabel(param.getName() + ": "));
        }
        add(slider);
        add(spinner);

        if (addDefaultButton) {
            defaultButton = new JButton("");
            defaultButton.addActionListener(this);
            defaultButton.setPreferredSize(new Dimension(spinnerHeight, spinnerHeight));
            defaultButton.setBackground(Color.GRAY);
            add(defaultButton);
        }
    }

    public int getCurrentValue() {
        return param.getValue();
    }

    public static void main(String[] args) {
//        GUIUtils.testJComponent(new SliderSpinner("cyan-red", -100, 100, 0, null, true, TextPosition.LABEL));
        GUIUtils.testJComponent(new SliderSpinner(new LinearIntParam("cyan-red", -100, 100, 0) , null, true, TextPosition.BORDER));
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object o = e.getSource();
        if (o == slider) {
            if (!slider.getValueIsAdjusting()) {
                int currentValue = slider.getValue();
//                param.setValue(currentValue);
                spinner.setValue(currentValue);
            }
        } else if (o == spinner) {
            // this gets callen even if the slider is mofified
            int currentValue = (Integer) spinner.getValue(); // autoboxing
            param.setValue(currentValue);
            slider.setValue(currentValue);

            ChangeEvent newEvent = new ChangeEvent(this);
            if (externalChangeListener != null) {
                externalChangeListener.stateChanged(newEvent);
            }
        }

        if (param.isSetToDefault()) {
            defaultButton.setBackground(Color.GRAY);
        } else if (param.getValue() > param.getDefaultValue()) {
            defaultButton.setBackground(rightColor);
        } else {
            defaultButton.setBackground(leftColor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // the default button was pressed
        resetToDefaultSettings();
    }

    //	@Override
    public void resetToDefaultSettings() {
//        slider.setValue(param.getDefaultValue());
        param.reset();
    }

    @Override
    public Dimension getMaximumSize() {
        if (restrictMaximumSize) {
             return getPreferredSize();
         } else {
             return super.getMaximumSize();
         }
    }

    public void setSizeRestriction(boolean restrictSize) {
        restrictMaximumSize = restrictSize;
    }

}

