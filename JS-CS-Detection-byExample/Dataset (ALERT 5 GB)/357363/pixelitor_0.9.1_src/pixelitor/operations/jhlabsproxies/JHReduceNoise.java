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
package pixelitor.operations.jhlabsproxies;

import com.jhlabs.image.ReduceNoiseFilter;
import pixelitor.operations.Operation;

import java.awt.image.BufferedImage;

/**
 * Reduce Noise based on the JHLabs ReduceNoiseFilter
 */
public class JHReduceNoise extends Operation {

    private ReduceNoiseFilter filter;

    public JHReduceNoise() {
        super("Reduce Noise");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new ReduceNoiseFilter();
        }


        dest = filter.filter(src, dest);
        return dest;
    }
}