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

import com.jhlabs.image.RaysFilter;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.BooleanParam;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Rays based on the JHLabs RaysFilter
 */
public class JHRays extends OperationWithParametrizedGUI {
    private ImagePositionParam center = new ImagePositionParam("Center");
    private RangeParam distance = new RangeParam("Distance", 0, 100, 20);
    private RangeParam rotation = new RangeParam("Twirl", -90, 90, 0);
    private RangeParam zoom = new RangeParam("Zoom", 0, 200, 20);
    private RangeParam opacity = new RangeParam("Opacity (%)", 0, 100, 80);
    private RangeParam strength = new RangeParam("Strength", 0, 500, 200);
    private RangeParam threshold = new RangeParam("Threshold (%)", 0, 100, 25);
    private BooleanParam raysOnly = new BooleanParam("Rays Only", false);

    private RaysFilter filter;

    public JHRays() {
        super("Rays", true);
        paramSet = new ParamSet(
                center,
                raysOnly,
                strength,
                opacity,
                distance,
                rotation,
                threshold,
                zoom
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new RaysFilter();
        }

        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setStrength(strength.getValueAsPercentage());
        filter.setDistance(distance.getValue());
        filter.setRotation(rotation.getValueInRadians());
        filter.setZoom(zoom.getValueAsPercentage());
        filter.setOpacity(opacity.getValueAsPercentage());
        filter.setThreshold(threshold.getValueAsPercentage());
        filter.setRaysOnly(raysOnly.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}