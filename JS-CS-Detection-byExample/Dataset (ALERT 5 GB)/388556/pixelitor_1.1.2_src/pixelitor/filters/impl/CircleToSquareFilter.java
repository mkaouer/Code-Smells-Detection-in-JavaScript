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
 * Distorts a circle into a square
 */
public class CircleToSquareFilter extends TransformFilter {
    private float centerX;
    private float centerY;
    private float radius;

    private int cx;
    private int cy;

    private float amount = 1.0f;

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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
        float transformedX = cx + (float) (dx * magnificationInverse);
        float transformedY = cy + (float) (dy * magnificationInverse);

        if (amount == 1.0f) {
            out[0] = transformedX;
            out[1] = transformedY;
        } else {
            out[0] = x + amount * (transformedX - x);
            out[1] = y + amount * (transformedY - y);
        }
    }

}
