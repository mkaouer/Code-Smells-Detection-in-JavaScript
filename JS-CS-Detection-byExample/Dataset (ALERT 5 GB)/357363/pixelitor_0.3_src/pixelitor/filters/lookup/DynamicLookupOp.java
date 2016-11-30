/*
 * Copyright 2009 L�szl� Bal�zs-Cs�ki
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
package pixelitor.filters.lookup;

import pixelitor.filters.AbstractOperationWithDialog;
import pixelitor.levels.RGBLookup;
import pixelitor.ImageChangeReason;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ShortLookupTable;

/**
 *
 */
public abstract class DynamicLookupOp extends AbstractOperationWithDialog {
    private RGBLookup rgbLookup;

    protected DynamicLookupOp(String name) {
        super(name);
    }

    public void setRGBLookup(RGBLookup rgbLookup) {
        if (rgbLookup == null) {
            throw new IllegalArgumentException("rgbLookup is null");
        }
        this.rgbLookup = rgbLookup;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        if(rgbLookup == null) {
            throw new IllegalStateException("rgbLookup not initialized in DynamicLookupOp");
        }

        BufferedImageOp filterOp = new FastLookupOp((ShortLookupTable) rgbLookup.getLookupOp());
        filterOp.filter(src, dest);

		return dest;
    }
}
