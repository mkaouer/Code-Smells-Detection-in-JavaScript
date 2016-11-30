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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import pixelitor.ImageChangeReason;

/**
 * An operation that rotates an image.
 */
public class Rotate extends AbstractOperation {
    private int angleDegree;

    public Rotate(int angleDegree, String name) {
        super(name);
        this.angleDegree = angleDegree;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int width = src.getWidth();
        int height = src.getHeight();
        Graphics2D g2 = null;
        if(angleDegree == 90 || angleDegree == 270) {
            dest = new BufferedImage(height, width, src.getType());
            changeReason.setSizeChanged(true);
        }
        // TODO dest should be always new, override createDefaultDestBuffer

        g2 = dest.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        if(angleDegree == 90) {
            g2.translate(height, 0);
        } else if(angleDegree == 180) {
            g2.translate(width, height);
        } else if(angleDegree == 270) {
            g2.translate(0, width);
        }


        g2.rotate(Math.toRadians(angleDegree));

        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return dest;
    }
}