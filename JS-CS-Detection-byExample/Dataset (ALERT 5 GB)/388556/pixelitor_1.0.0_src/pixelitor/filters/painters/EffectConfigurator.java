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
package pixelitor.filters.painters;

import com.bric.swing.ColorPicker;
import com.bric.swing.ColorSwatch;
import org.jdesktop.swingx.painter.effects.AbstractAreaEffect;
import pixelitor.PixelitorWindow;
import pixelitor.filters.gui.ParamAdjustmentListener;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 */
class EffectConfigurator extends JPanel {
    private JCheckBox checkBox;
    private ColorSwatch colorSwatch;
    private Color color;
    static final int BUTTON_SIZE = 20;
    private SliderSpinner widthSlider;
    private RangeParam widthRange;
    ParamAdjustmentListener adjustmentListener;

    EffectConfigurator(String effectName, boolean defaultSelected, final Color defaultColor, int defaultWidth) {
        this(effectName, defaultSelected, defaultColor, defaultWidth, "Width:");
    }

    EffectConfigurator(String effectName, boolean defaultSelected, final Color defaultColor, int defaultWidth, String widthName) {
        setBorder(BorderFactory.createTitledBorder("\"" + effectName + "\" Configuration"));

        checkBox = new JCheckBox();
        checkBox.setSelected(defaultSelected);

        colorSwatch = new ColorSwatch(defaultColor, BUTTON_SIZE);
        color = defaultColor;

        colorSwatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color selectedColor = ColorPicker.showDialog(PixelitorWindow.getInstance(), "Select Color", color, true);
                if (selectedColor != null) { // ok was pressed
                    color = selectedColor;
                    colorSwatch.setForeground(color);
                    colorSwatch.paintImmediately(0, 0, BUTTON_SIZE, BUTTON_SIZE);

                    if (adjustmentListener != null) {
                        adjustmentListener.paramAdjusted();
                    }
                }
            }
        });

        widthRange = new RangeParam(widthName, 1, 100, defaultWidth);
        widthSlider = new SliderSpinner(widthRange, false, SliderSpinner.TextPosition.NONE);

        setLayout(new GridBagLayout());
        GridBagHelper.addLabel(this, "Enabled:", 0, 0);
        GridBagHelper.addControl(this, checkBox);

        GridBagHelper.addLabel(this, "Color:", 0, 1);
        GridBagHelper.addControlNoFill(this, colorSwatch);

        GridBagHelper.addLabel(this, widthName, 0, 2);
        GridBagHelper.addControl(this, widthSlider);
    }

    ButtonModel getEnabledModel() {
        return checkBox.getModel();
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }

    public Color getColor() {
        return color;
    }

    public int getBrushWidth() {
        return widthSlider.getCurrentValue();
    }

    public void setAdjustmentListener(final ParamAdjustmentListener adjustmentListener) {
        if (this.adjustmentListener != null) {
            throw new IllegalStateException("only one is allowed");
        }

        this.adjustmentListener = adjustmentListener;
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustmentListener.paramAdjusted();
            }
        });

        widthRange.setAdjustmentListener(adjustmentListener);
    }

    public void updateEffectColorAndBrush(AbstractAreaEffect effect) {
        effect.setBrushColor(getColor());

        int brushWidth = getBrushWidth();
        effect.setEffectWidth(brushWidth);

        int brushSteps = 1 + brushWidth / 3;
        effect.setBrushSteps(brushSteps);
    }
}
