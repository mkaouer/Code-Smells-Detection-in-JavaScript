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

import com.jhlabs.image.ShadowFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Drop Shadow based on the JHLabs ShadowFilter
 */
public class JHDropShadow extends FilterWithParametrizedGUI {

    private AngleParam angle = new AngleParam("Angle", ImageUtils.DEG_315_IN_RADIANS);
    private RangeParam distance = new RangeParam("Distance", 0, 100, 10);
    private RangeParam opacity = new RangeParam("Opacity (%)", 0, 100, 90);
    private RangeParam softness = new RangeParam("Softness", 0, 25, 10);
    private BooleanParam shadowOnly = new BooleanParam("Shadow Only", false);
    private ColorParam color = new ColorParam("Color", Color.BLACK, false, false);

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