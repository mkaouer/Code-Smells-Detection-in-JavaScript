/*
 * Copyright 2010 Laszlo Balazs-Csiki
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

import com.jhlabs.image.EmbossFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.*;

import java.awt.image.BufferedImage;

/**
 * Emboss based on the JHLabs EmbossFilter
 */
public class JHEmboss extends FilterWithParametrizedGUI {
    private static final double INTUITIVE_RADIANS_30 = -0.54;

    private final AngleParam lightDirection = new AngleParam("Light Direction (Azimuth) - Degrees", 0);
    private final ElevationAngleParam lightElevation = new ElevationAngleParam("Light Elevation Angle - Degrees", INTUITIVE_RADIANS_30);
    private final RangeParam bumpHeight = new RangeParam("Depth", 2, 500, 100);
    private final BooleanParam texture = new BooleanParam("Texture (Multiply with the Source Image)", false);

    private EmbossFilter filter;

    public JHEmboss() {
        super("Emboss", true, false);
        setParamSet(new ParamSet(
                lightDirection,
                lightElevation,
                bumpHeight,
                texture
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new EmbossFilter();
        }

        filter.setAzimuth((float) lightDirection.getValueInIntuitiveRadians());
        filter.setBumpHeight(bumpHeight.getValueAsPercentage());
        filter.setElevation((float) lightElevation.getValueInIntuitiveRadians());
        filter.setEmboss(texture.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}