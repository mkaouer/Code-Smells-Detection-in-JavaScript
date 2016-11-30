/*
 * Copyright 2009-2014 Laszlo Balazs-Csiki
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

import pixelitor.ChangeReason;

import java.awt.image.BufferedImage;

public class RepeatLastOp extends Filter {
    public static final RepeatLastOp INSTANCE = new RepeatLastOp();

    private RepeatLastOp() {
        super("Repeat Last Operation");
        setEnabled(false);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        throw new IllegalStateException("should not be called");
    }

    @Override
    public void execute(ChangeReason changeReason) {
        Filter lastOp = FilterUtils.getLastExecutedFilter();
        if (lastOp != null) {
            lastOp.execute(changeReason);
        }
    }

    @Override
    public void randomizeSettings() {
        Filter lastOp = FilterUtils.getLastExecutedFilter();
        if (lastOp != null) {
            lastOp.randomizeSettings();
        }
    }
}
