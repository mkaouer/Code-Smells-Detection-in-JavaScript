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

import com.jhlabs.image.ShadowFilter;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.AngleParam;
import pixelitor.operations.gui.BooleanParam;
import pixelitor.operations.gui.ColorParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Drop Shadow based on the JHLabs ShadowFilter
 */
public class JHDropShadow extends OperationWithParametrizedGUI {
    private static final double DEG_315_IN_RADIANS = 0.7853981634;

    private AngleParam angle = new AngleParam("Angle", DEG_315_IN_RADIANS);
    private RangeParam distance = new RangeParam("Distance", 0, 100, 10);
    private RangeParam opacity = new RangeParam("Opacity", 0, 100, 90);
    private RangeParam softness = new RangeParam("Softness", 0, 25, 10);
    private BooleanParam shadowOnly = new BooleanParam("Shadow Only", false);
    private ColorParam color = new ColorParam("Color:", Color.BLACK);

    private ShadowFilter filter;

    public JHDropShadow() {
        super("Drop Shadow", true);
        paramSet = new ParamSet(
                angle,
                distance,
                opacity,
                softness,
                color,
                shadowOnly
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new ShadowFilter();
        }

        filter.setAddMargins(false);
        filter.setAngle((float) angle.getValueInIntuitiveRadians());
        filter.setDistance(distance.getValue());
        filter.setRadius(softness.getValue());
        filter.setOpacity(opacity.getValueAsPercentage());
        filter.setShadowColor(color.getColor().getRGB());
        filter.setShadowOnly(shadowOnly.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }

    public void setSoftness(int newSoftness) {
        softness.setValue(newSoftness);
    }

    public void setDistance(int newDistance) {
        distance.setValue(newDistance);
    }

    public void setOpacity(float newValue) {
        opacity.setValue((int) (100 * newValue));
    }
}