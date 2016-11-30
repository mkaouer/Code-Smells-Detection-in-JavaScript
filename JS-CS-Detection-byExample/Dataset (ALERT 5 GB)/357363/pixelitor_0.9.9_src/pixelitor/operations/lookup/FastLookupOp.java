/*
 * Copyright 2009-2010 László Balázs-Csíki
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

package pixelitor.operations.lookup;

import pixelitor.utils.ImageUtils;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;

/**
 * Performs 4-5 times faster than a regular lookup
 */
public class FastLookupOp implements BufferedImageOp {
    private ShortLookupTable lookupTable;

    public FastLookupOp(ShortLookupTable lookupTable) {
        super();
        this.lookupTable = lookupTable;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        boolean packedInt = ImageUtils.hasPackedIntArray(src);
        if (packedInt) {
            DataBufferInt srcDataBuffer = (DataBufferInt) src.getRaster().getDataBuffer();
            int[] srcData = srcDataBuffer.getData();

            DataBufferInt destDataBuffer = (DataBufferInt) dst.getRaster().getDataBuffer();
            int[] destData = destDataBuffer.getData();

            int length = srcData.length;
            if (length != destData.length) {
                throw new IllegalArgumentException("src and dest are not the same size");
            }

            short[][] table = lookupTable.getTable();

            for (int i = 0; i < length; i++) {
                int rgb = srcData[i];
                int a = rgb & 0xFF000000;
                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = (rgb) & 0xFF;

                if (a != 0) { // this check is necessary because of the premultiplied images
                    r = table[0][r];
                    g = table[1][g];
                    b = table[2][b];
                }

                rgb = a | (r << 16) | (g << 8) | b;
                destData[i] = rgb;
            }
        } else { // fall back to a normal LookupOp
            BufferedImageOp lookupOp = new LookupOp(lookupTable, null);
            lookupOp.filter(src, dst);
        }

        return dst;
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage src) {
        return null;
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        return null;
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        return null;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }
}
