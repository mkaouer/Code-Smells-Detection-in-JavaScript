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

package pixelitor.filters;

import java.awt.image.BufferedImage;

import pixelitor.ImageChangeReason;

public class LastOp extends AbstractOperation {
    public LastOp() {
        super("Repeat");
    }

    @Override
	public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
		throw new IllegalStateException("should not be called");
	}

    @Override
    public void execute(ImageChangeReason changeReason) {
        Operation lastOp = Operations.lastExecutedOperation;
        if(lastOp != null) {
            lastOp.execute(changeReason);
        }
    }
}
