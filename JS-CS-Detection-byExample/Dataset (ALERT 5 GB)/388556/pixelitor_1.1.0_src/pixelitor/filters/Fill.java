/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.filters;

import pixelitor.FillType;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 */
public class Fill extends Filter {
    private FillType fillType;

    public Fill(FillType fillType) {
        super(fillType.toString());
        this.fillType = fillType;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Color c = fillType.getColor();
        ImageUtils.fillImage(dest, c);

        return dest;
    }

    @Override
    public void randomizeSettings() {
        // TODO maybe a FillType returning a random color?
    }
}
