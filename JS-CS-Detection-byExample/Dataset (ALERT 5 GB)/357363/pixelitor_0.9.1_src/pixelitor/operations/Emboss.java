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

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageUtils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

public class Emboss extends Operation {
    private static final int GRAY_AS_32BIT_INT = 0xFF808080;
    private static final int GRAY_AS_8BIT_INT = 0x80;

    public Emboss() {
        super("Fast Emboss");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();

        int[] srcPixels = ImageUtils.getPixelsAsArray(src);
        int[] destPixels = ImageUtils.getPixelsAsArray(dest);

        for (int y = 0; y < height - 1; y++) {
            destPixels[0 + y * width] = GRAY_AS_32BIT_INT; // first column
            destPixels[(width - 1) + y * width] = GRAY_AS_32BIT_INT; // last column
        }

        for (int x = 0; x < width; x++) {
            destPixels[x + 0 * width] = GRAY_AS_32BIT_INT; // first row
            destPixels[x + (height - 1) * width] = GRAY_AS_32BIT_INT;  // last row
        }

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int nextPixel = srcPixels[(x + 1) + y * width + 1];
                int currentPixel = srcPixels[x + y * width];

                int currentAlpha = currentPixel & 0xFF000000;
                if (currentAlpha == 0) {
                    destPixels[x + y * width] = 0; // for premultiplied images
                } else {
                    int nextRed = (nextPixel >>> 16) & 0xFF;
                    int currentRed = (currentPixel >>> 16) & 0xFF;
                    int red = (nextRed - currentRed) + GRAY_AS_8BIT_INT;

                    int nextGreen = (nextPixel >>> 8) & 0xFF;
                    int currentGreen = (currentPixel >>> 8) & 0xFF;
                    int green = (nextGreen - currentGreen) + GRAY_AS_8BIT_INT;

                    int nextBlue = nextPixel & 0xFF;
                    int currentBlue = currentPixel & 0xFF;
                    int blue = (nextBlue - currentBlue) + GRAY_AS_8BIT_INT;

                    red = ImageUtils.limitTo8Bits(red);
                    green = ImageUtils.limitTo8Bits(green);
                    blue = ImageUtils.limitTo8Bits(blue);

                    int average = (blue + green + red) / 3;

//                    destPixels[x + y * width] = (currentAlpha | red << 16 | green << 8 | blue);
                    destPixels[x + y * width] = (currentAlpha | average << 16 | average << 8 | average);
                }
            }
        }

        if (!ImageUtils.hasPackedIntArray(src)) {
            final ImageComponent currentImageComponent = AppLogic.getActiveImageComponent();
            Image image = currentImageComponent.createImage(new MemoryImageSource(width, height, destPixels, 0, width));
            dest.createGraphics().drawImage(image, 0, 0, currentImageComponent);
        }

        return dest;
    }

}
