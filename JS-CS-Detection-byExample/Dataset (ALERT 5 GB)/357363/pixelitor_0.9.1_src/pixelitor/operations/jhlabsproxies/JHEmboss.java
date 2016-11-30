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

import com.jhlabs.image.EmbossFilter;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.AngleParam;
import pixelitor.operations.gui.BooleanParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Emboss based on the JHLabs EmbossFilter
 */
public class JHEmboss extends OperationWithParametrizedGUI {
//    private RangeParam lightDirection = new RangeParam("Light Direction (Azimuth)", 0, 360, 0);
    private AngleParam lightDirection = new AngleParam("Light Direction (Azimuth)", 0);
    private RangeParam lightElevation = new RangeParam("Light Elevation", 0, 90, 30);
    private RangeParam bumpHeight = new RangeParam("Bump Height", 2, 100, 100);
    private BooleanParam texture = new BooleanParam("Texture (Multiply with the Source Image)", false);

    private EmbossFilter filter;

    public JHEmboss() {
        super("Emboss", true);
        paramSet = new ParamSet(
                lightDirection,
                lightElevation,
                bumpHeight,
                texture
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new EmbossFilter();
        }

        filter.setAzimuth((float) lightDirection.getValueInIntuitiveRadians());
        filter.setBumpHeight(bumpHeight.getValueAsPercentage());
        filter.setElevation(lightElevation.getValueInRadians());
        filter.setEmboss(texture.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}