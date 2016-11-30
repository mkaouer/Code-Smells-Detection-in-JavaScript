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

import com.jhlabs.image.SparkleFilter;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Sparkle based on the JHLabs SparkleFilter
 */
public class JHSparkle extends OperationWithParametrizedGUI {
    private BooleanParam lightOnly = new BooleanParam("Light Only", false);
    private ImagePositionParam center = new ImagePositionParam("Center");
    private RangeParam nrOfRays = new RangeParam("Number of Rays", 1, 500, 200);
    private RangeParam radius = new RangeParam("Radius", 1, 500, 50);
    private RangeParam shine = new RangeParam("Shine", 0, 100, 50);
    private RangeParam randomness = new RangeParam("Randomness", 0, 50, 25);

    private ColorParam color = new ColorParam("Color", Color.WHITE, true);

    private SparkleFilter filter;

    public JHSparkle() {
        super("Sparkle", true);
        paramSet = new ParamSet(
                center,
                lightOnly,
                color,
                nrOfRays,
                radius,
                shine,
                randomness
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new SparkleFilter();
        }

        filter.setLightOnly(lightOnly.getValue());
        filter.setRelativeCentreX(center.getRelativeX());
        filter.setRelativeCentreY(center.getRelativeY());
        filter.setRadius(radius.getValue());
        filter.setRays(nrOfRays.getValue());
        filter.setAmount(shine.getValue());
        filter.setRandomness(randomness.getValue());
        filter.setColor(color.getColor().getRGB());

        dest = filter.filter(src, dest);
        return dest;
    }
}