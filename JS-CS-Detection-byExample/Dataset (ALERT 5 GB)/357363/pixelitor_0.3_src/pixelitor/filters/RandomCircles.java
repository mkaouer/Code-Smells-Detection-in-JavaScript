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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import pixelitor.ImageChangeReason;

public class RandomCircles extends AbstractOperation {
    public RandomCircles() {
        super("Random Circles");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Graphics2D g2 = dest.createGraphics();
        int width = dest.getWidth();
        int height = dest.getHeight();

        Random rand = new Random();
        int numCircles = width * height / 5;

        for (int i = 0; i < numCircles; i++) {

            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            int middleSrcColor = src.getRGB(x, y);
            Color c = new Color(middleSrcColor);

            g2.setColor(c);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);


            g2.fillOval(x, y, 10, 10);
        }

        return dest;
    }
}