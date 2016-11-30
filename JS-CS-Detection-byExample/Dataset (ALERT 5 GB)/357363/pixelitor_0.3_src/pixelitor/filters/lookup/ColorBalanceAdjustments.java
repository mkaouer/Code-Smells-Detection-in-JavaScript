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

package pixelitor.filters.lookup;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pixelitor.filters.AdjustPanel;
import pixelitor.filters.LinearIntParam;
import pixelitor.levels.RGBLookup;
import pixelitor.utils.SliderSpinner;

public class ColorBalanceAdjustments extends AdjustPanel implements ChangeListener {
	private static final int CB_MIN = -100;
	private static final int CB_MAX = 100;
	private static final int CB_INIT = 0;

	private short cyanRedValue = 0;
	private short magentaGreenValue = 0;
	private short yellowBlueValue = 0;

	private SliderSpinner cyanRedSlider;
	private SliderSpinner magentaGreenSlider;
	private SliderSpinner yellowBlueSlider;

	public ColorBalanceAdjustments(ColorBalance filter) {
		super(filter);
		Box box = Box.createVerticalBox();

        LinearIntParam cyanRedParam = new LinearIntParam("Cyan-Red", CB_MIN, CB_MAX, CB_INIT);
		cyanRedSlider = new SliderSpinner(Color.CYAN, Color.RED, cyanRedParam, this);

        LinearIntParam magentaGreenParam = new LinearIntParam("Magenta-Green", CB_MIN, CB_MAX,
				CB_INIT);
		box.add(cyanRedSlider);
		magentaGreenSlider = new SliderSpinner(Color.MAGENTA, Color.GREEN, magentaGreenParam, this);
		box.add(magentaGreenSlider);

        LinearIntParam yellowBlueParam = new LinearIntParam("Yellow-Blue", CB_MIN, CB_MAX,
				CB_INIT);
		yellowBlueSlider = new SliderSpinner(Color.YELLOW, Color.BLUE, yellowBlueParam, this);
		box.add(yellowBlueSlider);

		add(box);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		SliderSpinner source = (SliderSpinner) e.getSource();
		if (source == cyanRedSlider) {
			cyanRedValue = (short) source.getCurrentValue();
		} else if (source == magentaGreenSlider) {
			magentaGreenValue = (short) source.getCurrentValue();
		} else if (source == yellowBlueSlider) {
			yellowBlueValue = (short) source.getCurrentValue();
		}

		RGBLookup rgbLookup = new RGBLookup();
		rgbLookup.initFromColorBalance(cyanRedValue, magentaGreenValue, yellowBlueValue);
		DynamicLookupOp lkFilter = (DynamicLookupOp) op;
		lkFilter.setRGBLookup(rgbLookup);
//	    FilterManager.start(lkFilter, true);
        super.executeFilterPreview();
	}
}
