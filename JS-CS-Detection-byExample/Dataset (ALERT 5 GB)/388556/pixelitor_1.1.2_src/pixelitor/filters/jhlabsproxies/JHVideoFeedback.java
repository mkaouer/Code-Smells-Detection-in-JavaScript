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
import pixelitor.filters.FilterUtils;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Video Feedback based on the JHLabs FeedbackFilter
 */
public class JHVideoFeedback extends FilterWithParametrizedGUI {
//    private RangeParam distance = new RangeParam("Distance", 0, 100, 0);
//    private AngleParam angle = new AngleParam("Angle", 0);

    // distance and angle are not cool

    private RangeParam iterations = new RangeParam("Iterations", 2, 30, 3);

    private ImagePositionParam center = new ImagePositionParam("Center");
    private RangeParam rotationParam = new RangeParam("Rotation (degrees/iteration)", -30, 30, 0);
    private RangeParam zoom = new RangeParam("Zoom (percent/iteration)", -100, -5, -10);

    private RangeParam startOpacity = new RangeParam("Start Opacity (%)", 0, 100, 100);
    private RangeParam endOpacity = new RangeParam("End Opacity (%)", 0, 100, 100);

    private FeedbackFilter filter;

    public JHVideoFeedback() {
        super("Video Feedback");
        paramSet = new ParamSet(
//                distance,
                iterations,
                center,
                zoom,
//                angle,

                rotationParam,

                startOpacity,
                endOpacity
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {

        if((rotationParam.getValue() == 0) && (zoom.getValue() == 0)) {
            return FilterUtils.getDefaultImage(src);
        }


        if (filter == null) {
            filter = new FeedbackFilter();
        }

        float rotation = rotationParam.getValueInRadians();

        filter.setRotation(rotation);



        filter.setZoom(zoom.getValueAsPercentage());

        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setIterations(iterations.getValue());
//        filter.setDistance(distance.getValue());
//        filter.setAngle((float) angle.getValueInIntuitiveRadians());
        filter.setStartAlpha(startOpacity.getValueAsPercentage());
        filter.setEndAlpha(endOpacity.getValueAsPercentage());

        dest = filter.filter(src, dest);
        return dest;
    }
}