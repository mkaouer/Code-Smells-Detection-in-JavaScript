/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.MedianFilter;
import pixelitor.filters.Operation;

import java.awt.image.BufferedImage;

/**
 * 3x3 Median Filter based on the JHLabs MedianFilter
 */
public class JHMedian extends Operation {

    private MedianFilter filter;

    public JHMedian() {
        super("3x3 Median Filter");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new MedianFilter();
        }

        dest = filter.filter(src, dest);
        return dest;
    }
}