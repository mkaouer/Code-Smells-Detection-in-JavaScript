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
package pixelitor.filters;

import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An utility class that manages operations
 */
public class Operations {
    private static List<Operation> allOps = new ArrayList<Operation>();
    private static Operation lastExecutedOperation = null;

    /**
     * Utility class with static methods, do not instantiate
     */
    private Operations() {
    }

    public static Operation[] getAllOperationsSorted() {
        Operation[] operations = allOps.toArray(new Operation[allOps.size()]);
        Arrays.sort(operations);
        return operations;
    }

    public static Operation getRandomOperation() {
        return allOps.get((int) (Math.random() * allOps.size()));
    }

    public static Operation[] getAllOperationsShuffled() {
        Operation[] operations = allOps.toArray(new Operation[allOps.size()]);
        Collections.shuffle(Arrays.asList(operations));
        return operations;
    }

    public static void setLastExecutedOperation(Operation lastExecutedOperation) {
        if (lastExecutedOperation instanceof Fade) {
            return;
        }
        Operations.lastExecutedOperation = lastExecutedOperation;
    }

    public static Operation getLastExecutedOperation() {
        return lastExecutedOperation;
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

            destData[i] = pixelOp.changeRGB(a, r, g, b);
        }

        return dest;
    }

    public static BufferedImage getDefaultBufferedImage(BufferedImage src) {
        // TODO src should be returned here to avoid the creating of unnecessary objects
        // However, some rewriting is necessary for that
        return ImageUtils.copyImage(src);
    }

    public static void addOperation(Operation operation) {
        allOps.add(operation);
    }
}
