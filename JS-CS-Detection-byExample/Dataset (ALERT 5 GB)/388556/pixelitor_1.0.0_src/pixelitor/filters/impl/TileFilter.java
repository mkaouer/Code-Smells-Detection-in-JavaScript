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
package pixelitor.filters.impl;

import com.jhlabs.image.TransformFilter;

import java.awt.image.BufferedImage;

/**
 * Tile filter - inspired by the Paint.net tile effect
 */
public class TileFilter extends TransformFilter {
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
