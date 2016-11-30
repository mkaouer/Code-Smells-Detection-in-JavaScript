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

package pixelitor.utils;

import pixelitor.operations.gui.RangeParam;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * A GUI Component consisting of a JSlider, a JSpinner and optionally a default button.
 * The slider and the spinner are synchronized
 */
public class SliderSpinner extends JPanel implements ChangeListener {
    public enum TextPosition {
        BORDER, WEST, NORTH, NONE
    }

    private JSlider slider;
    private JSpinner spinner;
    private DefaultButton defaultButton;
    private RangeParam param;

    private Color leftColor;
    private Color rightColor;
    private boolean colorsUsed;

    private boolean sliderMoved = false;
    private boolean spinnerMoved = false;

    public SliderSpinner(RangeParam param, boolean addDefaultButton, TextPosition position) {
        this(null, null, param, addDefaultButton, position);
    }

    public SliderSpinner(Color leftColor, Color rightColor, RangeParam param) {
        this(leftColor, rightColor, param, true, TextPosition.BORDER);
    }

    // TODO: if color is specified, the default button is always added

    private SliderSpinner(Color leftColor, Color rightColor, RangeParam model, boolean addDefaultButton, TextPosition textPosition) {
        setLayout(new BorderLayout());
        this.param = model;

        this.leftColor = leftColor;
        this.rightColor = rightColor;
        colorsUsed = leftColor != null;

        if (textPosition == TextPosition.BORDER) {
            if ((leftColor != null) && (rightColor != null)) {
                GradientBorder border = new GradientBorder(leftColor, rightColor);
                this.setBorder(BorderFactory.createTitledBorder(border, model.getName()));
            } else {
                this.setBorder(BorderFactory.createTitledBorder(model.getName()));
                this.leftColor = Color.GRAY;
                this.rightColor = Color.GRAY;
            }
        }

//        slider = new JSlider(SwingConstants.HORIZONTAL, model.getMinimum(), model.getMaximum(), model.getDefaultValue());
        slider = new JSlider(model);
        if (textPosition == TextPosition.BORDER) {
            int max = model.getMaximum();
            int minorSpacing = (max + 1) / 8;
            int majorSpacing = 2 * minorSpacing;
            slider.setMajorTickSpacing(majorSpacing);
            slider.setMinorTickSpacing(minorSpacing);

            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
        }
        slider.addChangeListener(this);

        SpinnerModel spinnerModel =
                new SpinnerNumberModel(model.getValue(), //initial value
                        model.getMinimum(), //min
                        model.getMaximum(), //max
                        1);       //step

        spinner = new JSpinner(spinnerModel);
        spinner.addChangeListener(this);

        int spinnerHeight = (int) spinner.getPreferredSize().getHeight();

        JLabel label = new JLabel(model.getName() + ": ");
        if (textPosition == TextPosition.WEST) {
            add(label, BorderLayout.WEST);
        } else if (textPosition == TextPosition.NORTH) {
            add(label, BorderLayout.NORTH);
        }

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(slider, BorderLayout.CENTER);
        p.add(spinner);

        if (addDefaultButton) {
            defaultButton = new DefaultButton(model);
            defaultButton.setPreferredSize(new Dimension(spinnerHeight, spinnerHeight));
            if (colorsUsed) {
                defaultButton.setBackground(Color.GRAY);
            }
            p.add(defaultButton);
        }
        add(p, BorderLayout.EAST);
    }

    public int getCurrentValue() {
        return param.getValue();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object o = e.getSource();
        if (o == slider) {
            if (spinnerMoved) {
                return;
            }
            int currentValue = slider.getValue();
            sliderMoved = true;
            spinner.setValue(currentValue);
            sliderMoved = false;
        } else if (o == spinner) {
            if (sliderMoved) {
                return;
            }
            // this gets called even if the slider is modified by the user
            int currentValue = (Integer) spinner.getValue();
            spinnerMoved = true;
            param.setValue(currentValue);
            spinnerMoved = false;
        }

        if (defaultButton != null) {
            defaultButton.updateState();
        }
        if (colorsUsed) {
            if (param.isSetToDefault()) {
                defaultButton.setBackground(Color.GRAY);
            } else {
                if (param.getValue() > param.getDefaultValue()) {
                    defaultButton.setBackground(rightColor);
                } else {
                    defaultButton.setBackground(leftColor);
                }
            }
        }
    }

    //	@Override

    public void resetToDefaultSettings() {
//        slider.setValue(param.getDefaultValue());
        param.reset(false);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
