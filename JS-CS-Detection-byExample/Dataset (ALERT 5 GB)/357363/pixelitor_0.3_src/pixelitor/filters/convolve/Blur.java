/*
 * Copyright 2009 László Balázs-Csíki
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

package pixelitor.filters.convolve;

import pixelitor.filters.AbstractOperation;
import pixelitor.filters.Operations;
import pixelitor.ImageChangeReason;

import java.awt.image.Kernel;
import java.awt.image.BufferedImage;

/**
 * A simple blur operation implemented with a 3x3 convolution.
 */
public class Blur extends AbstractOperation {
    private static final float BLUR_FACTOR = 0.1115f;

    public Blur() {
        super("Blur");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Kernel kernel = new Kernel(3, 3, new float[]{BLUR_FACTOR, BLUR_FACTOR, BLUR_FACTOR, BLUR_FACTOR, BLUR_FACTOR, BLUR_FACTOR,
                BLUR_FACTOR, BLUR_FACTOR, BLUR_FACTOR});
        return Operations.convolve(kernel, src, dest);
    }
}
