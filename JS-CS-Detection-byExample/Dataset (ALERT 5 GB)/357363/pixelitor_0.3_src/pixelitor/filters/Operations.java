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

import pixelitor.utils.GUIUtils;
import pixelitor.utils.Utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.image.*;

/**
 * An utility class that manages opearations
 */
public class Operations {
    public static List<Operation> allOps = new ArrayList<Operation>();
    public static Operation lastExecutedOperation = null;

    private Operations() {} // should not be instantiated

    public static Operation[] getAllOperations() {
        Operation[] operations = allOps.toArray(new Operation[allOps.size()]);
        Arrays.sort(operations);
        return operations;
    }

    public static void setLastExecutedOperation(Operation lastExecutedOperation) {
        Operations.lastExecutedOperation = lastExecutedOperation;
    }

    public static BufferedImage convolve(Kernel kernel, BufferedImage src, BufferedImage dest) {
        BufferedImageOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
                null);
        try {
            convolveOp.filter(src, dest);
        } catch (java.awt.image.ImagingOpException e) {
            GUIUtils.showExceptionDialog(null, e);
        }

        return dest;
    }

    public static BufferedImage runRGBPixelOp(RGBPixelOp pixelOp, BufferedImage src, BufferedImage dest) {
        int[] srcData = Utils.getPixelsAsArray(src);
        int[] destData = Utils.getPixelsAsArray(dest);

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
}
