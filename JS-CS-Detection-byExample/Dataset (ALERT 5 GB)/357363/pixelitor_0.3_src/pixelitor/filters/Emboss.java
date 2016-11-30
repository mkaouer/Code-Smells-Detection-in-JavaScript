/*
 * Copyright 2009 L�szl� Bal�zs-Cs�ki
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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import pixelitor.AppLogic;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.utils.Utils;

public class Emboss extends AbstractOperation {
    private static final int GRAY_AS_32BIT_INT = 0x80808080;
    private static final int GRAY_AS_8BIT_INT = 0x80;

    public Emboss() {
        super("Emboss");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int width = src.getWidth();
        int height = src.getHeight();

        int[] srcPixels = Utils.getPixelsAsArray(src);
        int[] destPixels = Utils.getPixelsAsArray(dest);

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

                int nextRed = (nextPixel >>> 16) & 0xFF;
                int currentRed = (currentPixel >>> 16) & 0xFF;
                int red = (nextRed - currentRed) + GRAY_AS_8BIT_INT;

                int nextGreen = (nextPixel >>> 8) & 0xFF;
                int currentGreen = (currentPixel >>> 8) & 0xFF;
                int green = (nextGreen - currentGreen) + GRAY_AS_8BIT_INT;

                int nextBlue = nextPixel & 0xFF;
                int currentBlue = currentPixel & 0xFF;
                int blue = (nextBlue - currentBlue) + GRAY_AS_8BIT_INT;

                int average = (blue + green + red) / 3;
                destPixels[x + y * width] = (average << 16 | average << 8 | average);
            }
        }

        if (!Utils.hasPackedIntArray(src)) {
            final ImageComponent currentImageComponent = AppLogic.getActiveImageComponent();
            Image image = currentImageComponent.createImage(new MemoryImageSource(width, height, destPixels, 0, width));
            dest.createGraphics().drawImage(image, 0, 0, currentImageComponent);
        }

        return dest;
    }
}
