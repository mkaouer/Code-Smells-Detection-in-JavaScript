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
package pixelitor.filters.painters;

import pixelitor.ImageChangeReason;
import pixelitor.FgBgColorSelector;
import pixelitor.filters.AbstractOperation;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.GlossPainter;

/**
 *
 */
public class Gloss extends AbstractOperation {
    public Gloss() {
        super("Gloss");
        copySrcToDstBeforeRunning = true;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Graphics2D g = dest.createGraphics();
        GlossPainter p = new GlossPainter();
        p.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.3f));
        p.paint(g, this, dest.getWidth(), dest.getHeight());
        g.dispose();
        return dest;
    }
}