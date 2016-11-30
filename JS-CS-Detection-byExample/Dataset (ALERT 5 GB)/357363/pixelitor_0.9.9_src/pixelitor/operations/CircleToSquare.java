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
package pixelitor.operations;

import com.jhlabs.image.TransformFilter;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Circle To Square
 */
public class CircleToSquare extends OperationWithParametrizedGUI {
    private RangeParam radius = new RangeParam("Radius", 10, 700, 100);
    private ImagePositionParam center = new ImagePositionParam("Center");
    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private CircleToSquareFilter filter;

    public CircleToSquare() {
        super("Circle to Square", true);
        paramSet = new ParamSet(
                center,
                radius,
                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new CircleToSquareFilter();
        }

        filter.setCenterX(center.getRelativeX());
        filter.setCenterY(center.getRelativeY());

        filter.setRadius(radius.getValue());

        filter.setEdgeAction(edgeAction.getCurrentInt());
        filter.setInterpolation(interpolation.getCurrentInt());

        dest = filter.filter(src, dest);
        return dest;
    }
}

class CircleToSquareFilter extends TransformFilter {
    private float centerX;
    private float centerY;
    private float radius;

    private int cx;
    private int cy;

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        cx = (int) (centerX * src.getWidth());
        cy = (int) (centerY * src.getHeight());

        return super.filter(src, dst);
    }

    @Override
    protected void transformInverse(int x, int y, float[] out) {
        int dx = x - cx;
        int dy = y - cy;
        int xDist = Math.abs(dx);
        int yDist = Math.abs(dy);
        if (xDist > radius || yDist > radius) { // out of the target square
            out[0] = x;
            out[1] = y;
            return;
        }

        double angle;
        if (xDist >= yDist) {
            angle = Math.atan2(dy, xDist);
        } else {
            angle = Math.atan2(dx, yDist);
        }
        double magnificationInverse = Math.cos(angle);
        out[0] = cx + (float) (dx * magnificationInverse);
        out[1] = cy + (float) (dy * magnificationInverse);
    }

}