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
package pixelitor.operations;

import java.awt.image.BufferedImage;

/**
 *
 */
public class PixelOperation extends Operation {
    private RGBPixelOp rgbOp;

    public PixelOperation(String name, RGBPixelOp rgbOp) {
        super(name);
        this.rgbOp = rgbOp;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        return Operations.runRGBPixelOp(rgbOp, src, dest);
    }
}
