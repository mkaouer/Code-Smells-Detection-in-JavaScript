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

import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An utility class that manages operations
 */
public class Operations {
    public static List<Operation> allOps = new ArrayList<Operation>();
    public static Operation lastExecutedOperation = null;


    private Operations() {
    } // should not be instantiated

    public static Operation[] getAllOperations() {
        Operation[] operations = allOps.toArray(new Operation[allOps.size()]);
        Arrays.sort(operations);
        return operations;
    }

    public static void setLastExecutedOperation(Operation lastExecutedOperation) {
        Operations.lastExecutedOperation = lastExecutedOperation;
    }

    public static BufferedImage runRGBPixelOp(RGBPixelOp pixelOp, BufferedImage src, BufferedImage dest) {
        int[] srcData = ImageUtils.getPixelsAsArray(src);
        int[] destData = ImageUtils.getPixelsAsArray(dest);

        for (int i = 0; i < srcData.length; i++) {
            int rgb = srcData[i];

            int a = (rgb >>> 24) & 0xFF;
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>> 8) & 0xFF;
            int b = (rgb) & 0xFF;

            rgb = pixelOp.changeRGB(a, r, g, b);

            destData[i] = rgb;
        }

        return dest;
    }

    public static BufferedImage getDefaultBufferedImage(BufferedImage src) {
        return ImageUtils.copyImage(src);
    }
}
