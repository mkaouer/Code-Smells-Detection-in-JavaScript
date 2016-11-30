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
import java.awt.Font;

import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.TextPainter;

/**
 *
 */
public class Text extends AbstractOperation {
    public Text() {
        super("Text");
        copySrcToDstBeforeRunning = true;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Graphics2D g = dest.createGraphics();
        TextPainter p = new TextPainter();
        p.setAntialiasing(true);
        p.setText("Pixelitor");
        p.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
        p.setPaintStretched(true);

        p.paint(g, this, dest.getWidth(), dest.getHeight());
        g.dispose();
        return dest;
    }
}