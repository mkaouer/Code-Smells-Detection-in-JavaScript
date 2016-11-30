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

import com.jhlabs.image.FeedbackFilter;
import pixelitor.filters.OperationWithParametrizedGUI;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Video Feedback based on the JHLabs FeedbackFilter
 */
public class JHVideoFeedback extends OperationWithParametrizedGUI {
    private RangeParam distance = new RangeParam("Distance", 0, 100, 0);
    private AngleParam angle = new AngleParam("Angle", 0);

    private RangeParam iterations = new RangeParam("Iterations", 2, 30, 2);

    private ImagePositionParam center = new ImagePositionParam("Center (for rotation and zoom)");
    private RangeParam rotation = new RangeParam("Rotation (degrees/iteration)", -180, 180, 0);
    private RangeParam zoom = new RangeParam("Zoom", -100, 100, 0);

    private RangeParam startOpacity = new RangeParam("Start Opacity", 0, 100, 100);
    private RangeParam endOpacity = new RangeParam("End Opacity", 0, 100, 100);

    private FeedbackFilter filter;

    public JHVideoFeedback() {
        super("Video Feedback", false);
        paramSet = new ParamSet(
                distance,
                angle,
                iterations,

                center,
                rotation,
                zoom,

                startOpacity,
                endOpacity
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new FeedbackFilter();
        }

        filter.setRotation(rotation.getValueInRadians());
        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setIterations(iterations.getValue());
        filter.setDistance(distance.getValue());
        filter.setAngle((float) angle.getValueInIntuitiveRadians());
        filter.setZoom(zoom.getValueAsPercentage());
        filter.setStartAlpha(startOpacity.getValueAsPercentage());
        filter.setEndAlpha(endOpacity.getValueAsPercentage());

        dest = filter.filter(src, dest);
        return dest;
    }
}