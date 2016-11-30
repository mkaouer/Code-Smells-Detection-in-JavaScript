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
import pixelitor.operations.gui.AngleParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Tile based on the  TileFilter
 */
public class TileProxy extends OperationWithParametrizedGUI {
    private RangeParam size = new RangeParam("Tile Size", 5, 400, 50);
    private RangeParam curvature = new RangeParam("Curvature", 0, 20, 10);
    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private TileFilter filter;

    public TileProxy() {
        super("Glass Tile", true);
        paramSet = new ParamSet(
                size,
                curvature,
                edgeAction,
                interpolation
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new TileFilter();
        }

        filter.setSize(size.getValue());
        filter.setCurvature(curvature.getValue());
        filter.setEdgeAction(edgeAction.getCurrentInt());
        filter.setInterpolation(interpolation.getCurrentInt());

        dest = filter.filter(src, dest);
        return dest;
    }
}

/**
 * Tile filter - inspired by the Paint.net tile effect
 */
class TileFilter extends TransformFilter {
    private float scale;
    private float halfWidth;
    private float halfHeight;
    private float curvature;

    public void setSize(int size) {
        this.scale = (float) (Math.PI / size);
    }

    public void setCurvature(float curvature) {
        this.curvature = curvature * curvature / 10.0f;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        halfWidth = src.getWidth() / 2.0f;
        halfHeight = src.getHeight() / 2.0f;

        return super.filter(src, dst);
    }

    @Override
    protected void transformInverse(int x, int y, float[] out) {
        float i = x - halfWidth;
        float j = y - halfHeight;

        float sampleX = (float) (i + (curvature * Math.tan(i * scale)));
        float sampleY = (float) (j + (curvature * Math.tan(j * scale)));

        out[0] = halfWidth + sampleX;
        out[1] = halfHeight + sampleY;
    }
}