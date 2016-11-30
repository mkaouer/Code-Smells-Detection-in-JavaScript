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
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.WoodFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.GradientParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.gui.ReseedNoiseAction;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Wood based on the JHLabs WoodFilter
 */
public class JHWood extends FilterWithParametrizedGUI {
    private RangeParam ringsParam = new RangeParam("Rings", 1, 100, 50);
    private RangeParam scaleParam = new RangeParam("Zoom (%)", 1, 500, 100);
    private RangeParam stretchParam = new RangeParam("Stretch", 1, 50, 10);
    private AngleParam angleParam = new AngleParam("Angle", 0);
    private RangeParam turbulenceParam = new RangeParam("Turbulence", 0, 100, 0);
    private RangeParam fibresParam = new RangeParam("Fibres", 0, 100, 10);
    private RangeParam gainParam = new RangeParam("Gain", 0, 100, 80);

    private GradientParam gradientParam = new GradientParam("Colors", new Color(229, 196, 148), new Color(152, 123, 81));

    private WoodFilter filter;

    public JHWood() {
        super("Wood");
        paramSet = new ParamSet(
                angleParam,
                scaleParam,
                stretchParam,
                gradientParam,
                ringsParam,
                turbulenceParam,
                fibresParam,
                gainParam,
                new ReseedNoiseAction()
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new WoodFilter();
        }

        double angle = angleParam.getValueInRadians() + Math.PI / 2;

        filter.setAngle((float) angle);
        filter.setScale(scaleParam.getValue());
        filter.setStretch(stretchParam.getValue());
        filter.setRings(ringsParam.getValueAsPercentage());
        filter.setTurbulence(turbulenceParam.getValueAsPercentage());
        filter.setFibres(fibresParam.getValueAsPercentage());
        filter.setGain(gainParam.getValueAsPercentage());
        filter.setColormap(gradientParam.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}