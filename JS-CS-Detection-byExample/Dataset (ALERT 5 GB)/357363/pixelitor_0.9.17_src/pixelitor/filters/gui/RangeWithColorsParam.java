/*
 * Copyright 2010 László Balázs-Csíki
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

import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.Color;

/**
 *
 */
public class RangeWithColorsParam extends RangeParam {
    private Color leftColor;
    private Color rightColor;

    public RangeWithColorsParam(Color leftColor, Color rightColor, String name, int minValue, int maxValue, int defaultValue) {
        super(name, minValue, maxValue, defaultValue);
        this.leftColor = leftColor;
        this.rightColor = rightColor;
    }

    @Override
    public JComponent createControl() {
        SliderSpinner sliderSpinner = new SliderSpinner(leftColor, rightColor, this);
        return sliderSpinner;
    }

}
