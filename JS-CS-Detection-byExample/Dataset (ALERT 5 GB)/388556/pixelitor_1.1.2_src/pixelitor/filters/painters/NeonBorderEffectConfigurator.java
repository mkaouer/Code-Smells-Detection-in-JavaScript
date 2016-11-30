/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
import pixelitor.PixelitorWindow;
import pixelitor.utils.GridBagHelper;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 */
public class NeonBorderEffectConfigurator extends EffectConfigurator {
    private Color innerColor;
    private ColorSwatch innerColorSwatch;

    NeonBorderEffectConfigurator(boolean defaultSelected, final Color defaultColor, Color innerColorParam, int defaultWidth) {
        super("Neon Border", defaultSelected, defaultColor, defaultWidth);

        this.innerColor = innerColorParam;
        innerColorSwatch = new ColorSwatch(innerColor, BUTTON_SIZE);

        innerColorSwatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color selectedColor = ColorPicker.showDialog(PixelitorWindow.getInstance(), "Select Color", innerColor, true);
                if (selectedColor != null) { // ok was pressed
                    innerColor = selectedColor;
                    innerColorSwatch.setForeground(innerColor);
                    innerColorSwatch.paintImmediately(0, 0, BUTTON_SIZE, BUTTON_SIZE);

                    if (adjustmentListener != null) {
                        adjustmentListener.paramAdjusted();
                    }
                }
            }
        });

        GridBagHelper.addLabel(this, "Inner Color:", 0, 3);
        GridBagHelper.addControlNoFill(this, innerColorSwatch);
    }

    public Color getInnerColor() {
        return innerColor;
    }
}
