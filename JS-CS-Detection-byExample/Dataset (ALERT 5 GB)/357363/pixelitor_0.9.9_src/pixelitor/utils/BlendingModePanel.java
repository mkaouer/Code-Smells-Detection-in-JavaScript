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
package pixelitor.utils;

import pixelitor.layers.BlendingMode;

import javax.swing.*;
import java.awt.Composite;
import java.awt.FlowLayout;

/**
 * A GUI selector for opacity and blending mode
 */

public class BlendingModePanel extends JPanel {
    protected IntTextField opacityTextField;
    protected JComboBox blendingModeCombo;

    public BlendingModePanel(boolean longText) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Opacity:"));
        opacityTextField = new IntTextField("100", 0, 100, true);

        add(opacityTextField);

        if (longText) {
            add(new JLabel("%, Blending Mode:", SwingConstants.LEFT));
        } else {
            add(new JLabel("%", SwingConstants.LEFT));
        }

        blendingModeCombo = new JComboBox(BlendingMode.values());
        add(blendingModeCombo);

    }

    protected float getOpacity() {
        return opacityTextField.getIntValue() / 100.0f;
    }

    protected BlendingMode getBlendingMode() {
        return (BlendingMode) blendingModeCombo.getSelectedItem();
    }

    public Composite getComposite() {
        return ImageUtils.calculateComposite(getBlendingMode(), getOpacity());
    }
}
