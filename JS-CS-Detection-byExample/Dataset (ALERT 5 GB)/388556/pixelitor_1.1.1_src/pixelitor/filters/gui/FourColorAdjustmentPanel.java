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

import pixelitor.filters.jhlabsproxies.JHFourColorGradient;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 *
 */
public class FourColorAdjustmentPanel extends ParametrizedAdjustPanel {
    public FourColorAdjustmentPanel(JHFourColorGradient filter) {
        super(filter);
    }

    @Override
    protected void setupGUI(ParamSet params, Object otherInfo) {
        setLayout(new BorderLayout(5, 5));
        JPanel colorsPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        for (GUIParam param : params) {
            JComponent control = param.createGUI();

            if(control instanceof JButton) {
                buttonsPanel.add(control);
            } else {
                colorsPanel.add(new JLabel(param.getName() + ":"));
                colorsPanel.add(control);
            }

            add(colorsPanel, BorderLayout.CENTER);
            add(buttonsPanel, BorderLayout.SOUTH);
        }
    }
}
