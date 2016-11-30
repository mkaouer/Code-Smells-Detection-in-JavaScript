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

import com.jhlabs.image.MotionBlur;
import com.jhlabs.image.MotionBlurFilter;
import com.jhlabs.image.MotionBlurOp;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.AngleParam;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Motion Blur based on the JHLabs MotionBlur
 */
public class JHMotionBlur extends OperationWithParametrizedGUI {
//    private RangeParam angle = new RangeParam("Angle", 0, 360, 0);
    private AngleParam angle = new AngleParam("Angle", 0);
    private RangeParam distance = new RangeParam("Distance", 0, 200, 0);
    private RangeParam rotation = new RangeParam("Spin Blur Amount (Degrees)", -45, 45, 0);
    private RangeParam zoom = new RangeParam("Zoom Blur Amount (%)", 0, 200, 0);
    private ImagePositionParam center = new ImagePositionParam("Center");

    private Mode mode;

    enum MBMethod {
        FAST {
            @Override
            public MotionBlur getImplementation() {
                MotionBlurOp op = new MotionBlurOp();
                return op;
            }
        }, GOOD {
            @Override
            public MotionBlur getImplementation() {
                MotionBlurFilter filter = new MotionBlurFilter();
                filter.setPremultiplyAlpha(false);
                filter.setWrapEdges(false);
                return filter;
            }
        };

        public abstract MotionBlur getImplementation();
    }

    private static IntChoiceParam.Value[] methodChoices = new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Fast", MBMethod.FAST.ordinal()),
            new IntChoiceParam.Value("High Quality", MBMethod.GOOD.ordinal()),
    };
    private IntChoiceParam method = new IntChoiceParam("Quality", methodChoices);

    public enum Mode {
        MOTION_BLUR {
            @Override
            public String toString() {
                return "Motion Blur";
            }
        }, SPIN_ZOOM_BLUR {
            @Override
            public String toString() {
                return "Spin and Zoom Blur";
            }
        }
    }

    public JHMotionBlur(Mode mode) {
        super(mode.toString(), false);
        this.mode = mode;

        if (mode == Mode.MOTION_BLUR) {
            paramSet = new ParamSet(
                    angle,
                    distance,
                    method
            );

        } else if (mode == Mode.SPIN_ZOOM_BLUR) {
            paramSet = new ParamSet(
                    center,
                    rotation,
                    zoom,
                    method
            );
        } else {
            throw new IllegalStateException("should not get here");
        }
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int distanceValue = distance.getValue();
        float zoomValue = zoom.getValueAsPercentage();
        float rotationValue = rotation.getValueInRadians();
        if (mode == Mode.MOTION_BLUR) {
            if (distanceValue == 0) {
                return Operations.getDefaultBufferedImage(src);
            }
        } else if (mode == Mode.SPIN_ZOOM_BLUR) {
            if (zoomValue == 0f && rotationValue == 0f) {
                return Operations.getDefaultBufferedImage(src);
            }
        }

        int intValue = method.getCurrentInt();
        MBMethod chosenMethod = MBMethod.values()[intValue];

        MotionBlur filter = chosenMethod.getImplementation();

        filter.setCentreX(center.getRelativeX());
        filter.setCentreY(center.getRelativeY());
        filter.setAngle((float) angle.getValueInIntuitiveRadians());
        filter.setDistance(distanceValue);
        filter.setRotation(rotationValue);
        filter.setZoom(zoomValue);

        dest = filter.filter(src, dest);
        return dest;
    }
}