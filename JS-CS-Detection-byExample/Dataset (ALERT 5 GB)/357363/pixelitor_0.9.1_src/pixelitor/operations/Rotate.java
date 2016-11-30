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

import pixelitor.ImageComponent;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * An operation that rotates an image.
 */
public class Rotate extends Operation {
    private int angleDegree;

    private int newWidth;
    private int newHeight;

    public Rotate(int angleDegree, String name) {
        super(name);
        this.angleDegree = angleDegree;
    }

    @Override
    protected boolean createDefaultDestBuffer() {
        return false; // a new object will be created depending on the angle
    }

    @Override
    public boolean runOnAllLayers() {
        return true;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();
        Graphics2D g2;

        if (angleDegree == 90 || angleDegree == 270) {
            newWidth = height;
            newHeight = width;
        } else {
            newWidth = width;
            newHeight = height;
        }
        // TODO for arbirtary rotation create a rectangle, then rotate it with the same AffineTransform
        // something like this: http://forums.sun.com/thread.jspa?threadID=5362614

        dest = new BufferedImage(newWidth, newHeight, src.getType());

        g2 = dest.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        if (angleDegree == 90) {
            g2.translate(height, 0);
        } else if (angleDegree == 180) {
            g2.translate(width, height);
        } else if (angleDegree == 270) {
            g2.translate(0, width);
        }

        g2.rotate(Math.toRadians(angleDegree));

        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return dest;
    }

    @Override
    public void afterFilterActions(ImageComponent ic) {
        ic.updateSize(newWidth, newHeight);
    }
}