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
import pixelitor.SamplingMethod;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RandomLines extends Operation {

    private SamplingMethod samplingMethod = SamplingMethod.SAMPLE9;

    public RandomLines() {
        super("Random Lines");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Graphics2D g2 = dest.createGraphics();
        int width = dest.getWidth();
        int height = dest.getHeight();

        Random rand = new Random();
        int numLines = width * height / 100;

        for (int i = 0; i < numLines; i++) {

            int x1 = rand.nextInt(width);
            int y1 = rand.nextInt(height);
            int x2 = rand.nextInt(width);
            int y2 = rand.nextInt(height);

            int middleX = (x1 + x2) / 2;
            int middleY = (y1 + y2) / 2;

            Color c;
            if (samplingMethod == SamplingMethod.SAMPLE9) {
                c = ImageUtils.sample9Points(src, middleX, middleY);
            } else if (samplingMethod == SamplingMethod.SAMPLE1) {
                int middleSrcColor = src.getRGB(middleX, middleY);
                c = new Color(middleSrcColor);
            } else {
                // TODO
                throw new IllegalStateException();
            }

            g2.setColor(c);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawLine(x1, y1, x2, y2);
        }

        return dest;
    }
}
