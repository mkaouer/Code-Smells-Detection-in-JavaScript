/*
 * Copyright 2010 Laszlo Balazs-Csiki
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An effect configurator panel...
 */
public abstract class EffectConfiguratorPanel extends JPanel {
    private final JCheckBox enabledCheckBox;
    private final ColorSwatch colorSwatch;
    private Color color;
    static final int BUTTON_SIZE = 20;
    ParamAdjustmentListener adjustmentListener;

    private final RangeParam opacityRange;
    private final SliderSpinner opacitySlider;

    EffectConfiguratorPanel(String effectName, boolean defaultSelected, final Color defaultColor) {
        setBorder(BorderFactory.createTitledBorder('"' + effectName + "\" Configuration"));

        opacityRange = new RangeParam("Width:", 1, 100, 100);
        opacitySlider = new SliderSpinner(opacityRange, false, SliderSpinner.TextPosition.NONE);

        enabledCheckBox = new JCheckBox();
        enabledCheckBox.setSelected(defaultSelected);

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


        setLayout(new GridBagLayout());
        GridBagHelper.addLabel(this, "Enabled:", 0, 0);
        GridBagHelper.addControl(this, enabledCheckBox);

        GridBagHelper.addLabel(this, "Color:", 0, 1);
        GridBagHelper.addControlNoFill(this, colorSwatch);

        GridBagHelper.addLabel(this, "Opacity:", 0, 2);
        GridBagHelper.addControlNoFill(this, opacitySlider);
    }

    ButtonModel getEnabledModel() {
        return enabledCheckBox.getModel();
    }

    public boolean isSelected() {
        return enabledCheckBox.isSelected();
    }

    public Color getColor() {
        return color;
    }

    public float getOpacity() {
        return opacityRange.getValueAsPercentage();
    }

    public abstract int getBrushWidth();

    public void setAdjustmentListener(final ParamAdjustmentListener adjustmentListener) {
        if (this.adjustmentListener != null) {
            throw new IllegalStateException("only one is allowed");
        }

        this.adjustmentListener = adjustmentListener;
        enabledCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustmentListener.paramAdjusted();
            }
        });

        opacityRange.setAdjustmentListener(adjustmentListener);
    }

    public void updateEffectColorAndBrush(AbstractAreaEffect effect) {
        effect.setBrushColor(getColor());

        int brushWidth = getBrushWidth();
        effect.setEffectWidth(brushWidth);

        effect.setBrushSteps(calculateBrushSteps(brushWidth));
    }

    public static int calculateBrushSteps(int brushWidth) {
        int brushSteps = 1 + brushWidth / 3;
        return brushSteps;
    }
}
