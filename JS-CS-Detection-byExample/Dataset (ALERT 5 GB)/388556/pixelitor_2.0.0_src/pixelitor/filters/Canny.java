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
package pixelitor.filters;

import pd.CannyEdgeDetector;
import pixelitor.filters.gui.BooleanParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Canny edge detector - see http://en.wikipedia.org/wiki/Canny_edge_detector
 * based on CannyEdgeDetector by Tom Gibara - http://www.tomgibara.com/computer-vision/canny-edge-detector
 */
public class Canny extends FilterWithParametrizedGUI {
    private final RangeParam lowThreshold = new RangeParam("Low Threshold", 1, 1000, 250);
    private final RangeParam highThreshold = new RangeParam("High Threshold", 1, 1000, 750);
    private final RangeParam gaussianKernelWidth = new RangeParam("Gaussian Kernel Width", 2, 50, 16);
    private final RangeParam gaussianKernelRadius = new RangeParam("Gaussian Kernel Radius", 1, 10, 2);
    private final BooleanParam contrastNormalized = new BooleanParam("Contrast Normalized", false);

//    private CannyEdgeDetector detector;

    public Canny() {
        super("Canny Edge Detector", true, false);
        setParamSet(new ParamSet(
                lowThreshold,
                highThreshold,
                gaussianKernelWidth,
                gaussianKernelRadius,
                contrastNormalized
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
//        if (detector == null) {
        CannyEdgeDetector detector = new CannyEdgeDetector();
//        }

        detector.setLowThreshold(lowThreshold.getValueAsPercentage());
        detector.setHighThreshold(highThreshold.getValueAsPercentage());
        detector.setContrastNormalized(contrastNormalized.getValue());
        detector.setGaussianKernelRadius(gaussianKernelRadius.getValue());
        detector.setGaussianKernelWidth(gaussianKernelWidth.getValue());

        detector.setSourceImage(src);
        detector.process();

        dest = detector.getEdgesImage();

        return dest;
    }
}