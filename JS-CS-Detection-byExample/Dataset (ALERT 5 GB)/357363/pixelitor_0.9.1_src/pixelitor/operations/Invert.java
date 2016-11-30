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

import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * BitInvert
 */
public class Invert extends Operation {

    public Invert() {
        super("Invert");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);

        for (int i = 0; i < destData.length; i++) {
            destData[i] = srcData[i] ^ 0x00FFFFFF;  // invert the r,g,b values
        }

        return dest;
    }
}