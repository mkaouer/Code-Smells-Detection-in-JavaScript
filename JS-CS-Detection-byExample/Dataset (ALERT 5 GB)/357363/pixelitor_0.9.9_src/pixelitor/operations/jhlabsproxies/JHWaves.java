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
package pixelitor.operations.jhlabsproxies;

import com.jhlabs.image.RippleFilter;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Ripple based on the JHLabs RippleFilter
 */
public class JHWaves extends OperationWithParametrizedGUI {
    private RangeParam xAmplitude = new RangeParam("X Amplitude", 0, 100, 10);
    private RangeParam xWavelength = new RangeParam("X Wavelength", 1, 100, 20);
    private RangeParam yAmplitude = new RangeParam("Y Amplitude", 0, 100, 10);
    private RangeParam yWavelength = new RangeParam("Y Wavelength", 1, 100, 20);
    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private static IntChoiceParam.Value[] waveTypeChoices = new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Sine", RippleFilter.SINE),
            new IntChoiceParam.Value("Sawtooth", RippleFilter.SAWTOOTH),
            new IntChoiceParam.Value("Triangle", RippleFilter.TRIANGLE),
            new IntChoiceParam.Value("Noise", RippleFilter.NOISE),
    };
    private IntChoiceParam waveType = new IntChoiceParam("Wave Type", waveTypeChoices);


    private RippleFilter filter;

    public JHWaves() {
        super("Waves", true);
        paramSet = new ParamSet(
                xAmplitude,
                xWavelength,
                yAmplitude,
                yWavelength,
                waveType,
                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new RippleFilter();
        }

        filter.setXAmplitude(xAmplitude.getValue());
        filter.setXWavelength(xWavelength.getValue());
        filter.setYAmplitude(yAmplitude.getValue());
        filter.setYWavelength(yWavelength.getValue());
        filter.setWaveType(waveType.getCurrentInt());

        filter.setEdgeAction(edgeAction.getCurrentInt());
        filter.setInterpolation(interpolation.getCurrentInt());

        dest = filter.filter(src, dest);
        return dest;
    }
}