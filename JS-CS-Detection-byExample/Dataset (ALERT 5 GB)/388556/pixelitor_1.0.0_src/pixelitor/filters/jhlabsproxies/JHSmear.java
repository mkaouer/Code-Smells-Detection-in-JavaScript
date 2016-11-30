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

import com.jhlabs.image.SmearFilter;
import pixelitor.filters.FilterUtils;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Smear based on the JHLabs SmearFilter
 */
public class JHSmear extends FilterWithParametrizedGUI {
    private RangeParam distance = new RangeParam("Distance", 0, 100, 0);
    private RangeParam density = new RangeParam("Density (%)", 0, 100, 50);
    private AngleParam angle = new AngleParam("Angle (only for lines)", 0);
    private RangeParam mix = new RangeParam("Opacity (%)", 0, 100, 50);

    private static IntChoiceParam.Value[] shapeChoices = new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Lines", SmearFilter.LINES),
            new IntChoiceParam.Value("Crosses", SmearFilter.CROSSES),
            new IntChoiceParam.Value("Circles", SmearFilter.CIRCLES),
            new IntChoiceParam.Value("Squares", SmearFilter.SQUARES),
    };
    private IntChoiceParam shape = new IntChoiceParam("Shape", shapeChoices);

    private SmearFilter filter;

    public JHSmear() {
        super("Smear", false);
        paramSet = new ParamSet(
                distance,
                shape,
                density,
                angle,
                mix
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int distanceValue = distance.getValue();
        if (distanceValue == 0) {
            return FilterUtils.getDefaultImage(src);
        }

        if (filter == null) {
            filter = new SmearFilter();
        }

        filter.setDistance(distanceValue);
        filter.setDensity(density.getValueAsPercentage());
        filter.setAngle((float) angle.getValueInRadians());
        filter.setMix(mix.getValueAsPercentage());
        filter.setShape(shape.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}