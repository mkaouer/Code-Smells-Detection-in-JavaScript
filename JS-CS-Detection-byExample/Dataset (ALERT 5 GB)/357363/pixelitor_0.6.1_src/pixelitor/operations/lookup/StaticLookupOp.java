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

import pixelitor.ImageChangeReason;
import pixelitor.operations.Operation;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;

/**
 * A lookup filter that has no user setting
 */
public class StaticLookupOp extends Operation {

    private LookupTable lookupTable;
    private BufferedImageOp filterOp;
    //	private String name;
    private final StaticLookupType type;

    public StaticLookupOp(StaticLookupType type) {
        super(type.getMenuName());
        this.type = type;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        // initialize only when it is used for the first time
        if ((lookupTable == null) || (filterOp == null)) {
            lookupTable = type.getLookupTable();
//            filterOp = new LookupOp(lookupTable, null);
            filterOp = new FastLookupOp((ShortLookupTable) lookupTable);
        }

        filterOp.filter(src, dest);

        return dest;
    }
}
