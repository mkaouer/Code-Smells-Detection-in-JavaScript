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
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.WaterFilter;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Water Ripple based on the JHLabs WaterFilter
 */
public class JHWaterRipple extends OperationWithParametrizedGUI {
    private ImagePositionParam center = new ImagePositionParam("Center");

    private RangeParam radius = new RangeParam("Radius", 1, 500, 200);
    private RangeParam wavelength = new RangeParam("Wavelength", 1, 250, 15);
    private RangeParam amplitude = new RangeParam("Amplitude", 1, 100, 50);
    private RangeParam phase = new RangeParam("Phase (Time)", 0, 360, 0);

    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();


    private WaterFilter filter;

    public JHWaterRipple() {
        super("Water Ripple", true);
        paramSet = new ParamSet(
                center,
                radius,
                wavelength,
                amplitude,
                phase,
                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new WaterFilter();
        }

        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setRadius(radius.getValue());
        filter.setWavelength(wavelength.getValue());
        filter.setAmplitude(amplitude.getValueAsPercentage());
        filter.setPhase(phase.getValueInRadians());
        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}