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
package pixelitor.operations.painters;

import org.jdesktop.swingx.painter.PinstripePainter;
import pixelitor.FgBgColorSelector;
import pixelitor.operations.Operation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class Pinstripe extends Operation {
    public Pinstripe() {
        super("Pinstripes");
        copySrcToDstBeforeRunning = true;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Graphics2D g = dest.createGraphics();
        PinstripePainter pinstripePainter = new PinstripePainter(FgBgColorSelector.getFG());
        pinstripePainter.paint(g, this, dest.getWidth(), dest.getHeight());
        g.dispose();
        return dest;
    }
}
