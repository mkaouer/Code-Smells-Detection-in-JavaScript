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
 *
 */
public class MirrorFilter extends TransformFilter {
    private float centerX;
    private float centerY;
    private int cx;
    private int cy;

    public static final int LEFT_OVER_RIGHT = 0;
    public static final int RIGHT_OVER_LEFT = 1;
    public static final int BOTTOM_OVER_TOP = 2;
    public static final int TOP_OVER_BOTTOM = 3;
//    public static final int CENTRAL_SYMMETRY = 4;

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        cx = (int) (centerX * src.getWidth());
        cy = (int) (centerY * src.getHeight());

        return super.filter(src, dst);
    }

    @Override
    protected void transformInverse(int x, int y, float[] out) {
        switch(type) {
            case LEFT_OVER_RIGHT:
                if (x < cx) {
                    out[0] = x;
                    out[1] = y;
                } else {
                    out[0] = cx + cx - x;
                    out[1] = y;
                }
                break;
            case RIGHT_OVER_LEFT:
                if (x > cx) {
                    out[0] = x;
                    out[1] = y;
                } else {
                    out[0] = cx + cx - x;
                    out[1] = y;
                }
                break;
            case TOP_OVER_BOTTOM:
                if (y < cy) {
                    out[0] = x;
                    out[1] = y;
                } else {
                    out[0] = x;
                    out[1] = cy + cy - y;
                }
                break;
            case BOTTOM_OVER_TOP:
                if (y > cy) {
                    out[0] = x;
                    out[1] = y;
                } else {
                    out[0] = x;
                    out[1] = cy + cy - y;
                }
                break;
// same as rotation
//            case CENTRAL_SYMMETRY:
//                int dx = x - cx;
//                out[0] = cx - dx;
//
//                int dy = y - cy;
//                out[1] = cy - dy;
//
//                break;
        }

    }
}
