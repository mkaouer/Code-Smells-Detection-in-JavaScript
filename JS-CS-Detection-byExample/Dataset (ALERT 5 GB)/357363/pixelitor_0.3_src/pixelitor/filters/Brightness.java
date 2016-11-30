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

import java.awt.image.Kernel;
import java.awt.image.BufferedImage;

import pixelitor.utils.SliderSpinner;
import pixelitor.ImageChangeReason;

public class Brightness extends AbstractOperationWithDialog {
//	private float brightness = 1.0f;

    private static final int BRIGHTNESS_MIN = 1;
    private static final int BRIGHTNESS_MAX = 200;
    private static final int BRIGHTNESS_INIT = 100;

    private LinearIntParam brightnessParam = new LinearIntParam("Brightness", BRIGHTNESS_MIN,
            BRIGHTNESS_MAX, BRIGHTNESS_INIT);
    private ParamSet paramSet = new ParamSet(brightnessParam);

    public Brightness() {
        super("Brightness");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        float[] array = {brightnessParam.getValueAsFloat()};
        Kernel kernel = new Kernel(1, 1, array);
        return Operations.convolve(kernel, src, dest);
    }

	@Override
	public AdjustPanel getAdjustPanel() {

        if(adjustPanel == null) {
            adjustPanel =  new ParametrizedAdjustments(this, true, SliderSpinner.TextPosition.BORDER, false);
        } else {
            adjustPanel.setRunFiltersIfStateChanged(false);
            paramSet.reset();
            adjustPanel.setRunFiltersIfStateChanged(true);
        }

        return adjustPanel;

	}


    @Override
    public ParamSet getParams() {
        return paramSet;
    }
}
