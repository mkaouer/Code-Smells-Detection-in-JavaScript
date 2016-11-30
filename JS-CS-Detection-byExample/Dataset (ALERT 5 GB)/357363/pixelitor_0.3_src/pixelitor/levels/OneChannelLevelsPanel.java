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

package pixelitor.levels;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JPanel;

import pixelitor.utils.SliderSpinner;

public class OneChannelLevelsPanel extends JPanel {
	private String channelName;
	private Collection<SliderSpinner> sliders = new ArrayList<SliderSpinner>();
	private Box box = Box.createVerticalBox();
	protected GrayScaleLookup adjustment = GrayScaleLookup.getDefaultAdjustment();

    public OneChannelLevelsPanel(String channelName) {
		this.channelName = channelName;
		add(box);
	}

	public String getChannelName() {
		return channelName;
	}

	public void resetToDefaultSettings() {
		for (SliderSpinner slider : sliders) {
			slider.resetToDefaultSettings();
		}
	}

	public void addSliderSpinner(SliderSpinner sp) {
		box.add(sp);
		sliders.add(sp);
	}

	public GrayScaleLookup getAdjustment() {
		return adjustment;
	}

}
