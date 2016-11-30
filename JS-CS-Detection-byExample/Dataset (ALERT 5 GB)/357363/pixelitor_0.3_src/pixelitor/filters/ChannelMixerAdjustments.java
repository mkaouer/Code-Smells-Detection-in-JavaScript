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

package pixelitor.filters;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pixelitor.utils.SliderSpinner;

public class ChannelMixerAdjustments extends AdjustPanel implements
		ChangeListener {

	public ChannelMixerAdjustments(OperationWithDialog filter, Action[] actions) {
		super(filter);
        ParamSet params = filter.getParams();
        params.reset();

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        setLayout(new FlowLayout());

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createTitledBorder("presets"));

        for (LinearIntParam param : params) {
            JComponent slider = new SliderSpinner(param, this, true, SliderSpinner.TextPosition.LABEL);
            slider.setAlignmentX(Component.RIGHT_ALIGNMENT);
            leftPanel.add(slider);
        }
        for (Action action : actions) {
            JComponent b = new JButton(action);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(b);
        }
        add(leftPanel);
        add(rightPanel);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
        System.out.println("ChannelMixerAdjustments.stateChanged CALLED");
        executeFilterPreview();
	}
}