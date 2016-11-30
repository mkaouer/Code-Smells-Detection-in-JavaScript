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

import pixelitor.operations.OperationWithParametrizedGUI;

import javax.swing.*;
import java.awt.Component;
import java.awt.FlowLayout;

public class ChannelMixerAdjustments extends AdjustPanel implements
        ParamAdjustingListener {

    public ChannelMixerAdjustments(OperationWithParametrizedGUI filter, Action[] actions) {
        super(filter);

        JPanel leftPanel = new JPanel();
        ParametrizedAdjustPanel.setupControls(leftPanel, this, filter);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Presets"));

        for (Action action : actions) {
            JComponent b = new JButton(action);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(b);
        }
        setLayout(new FlowLayout());
        add(leftPanel);
        add(rightPanel);
    }

    @Override
    public void paramAdjusted() {
        super.executeFilterPreview();
    }
}