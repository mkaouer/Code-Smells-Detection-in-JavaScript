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

import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pixelitor.utils.SliderSpinner;

public class ParametrizedAdjustments extends AdjustPanel implements
		ChangeListener {

	public ParametrizedAdjustments(OperationWithDialog filter, boolean addDefaultButtons, SliderSpinner.TextPosition textposition, boolean runFilterImmediately) {
		super(filter);
        ParamSet params = filter.getParams();
        params.reset();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (LinearIntParam param : params) {
            SliderSpinner slider = new SliderSpinner(param, this, addDefaultButtons, textposition);
            slider.setAlignmentX(Component.RIGHT_ALIGNMENT);
            add(slider);
        }

        if(runFilterImmediately) {
            stateChanged(new ChangeEvent(""));
        }
	}

	@Override
	public final void stateChanged(ChangeEvent e) {
        super.executeFilterPreview();
	}
}