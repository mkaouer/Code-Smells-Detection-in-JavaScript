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

import pixelitor.ImageChangeReason;

import java.awt.image.BufferedImage;

public class LastOp extends Operation {
    public static final LastOp INSTANCE = new LastOp();

    private LastOp() {
        super("Repeat Last Operation");
        setEnabled(false);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        throw new IllegalStateException("should not be called");
    }

    @Override
    public void execute(ImageChangeReason changeReason) {
        Operation lastOp = Operations.getLastExecutedOperation();
        if (lastOp != null) {
            lastOp.execute(changeReason);
        }
    }

}
