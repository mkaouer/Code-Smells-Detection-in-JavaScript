/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.operations.gui.AdjustPanel;
import pixelitor.operations.gui.OperationWithGUI;
import pixelitor.operations.gui.ResizeAdjustments;
import pixelitor.utils.ImageUtils;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Resize extends OperationWithGUI {
    private int targetWidth;
    private int targetHeight;

    public Resize() {
        super("Resize");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        int actualWidth = src.getWidth();
        int actualHeight = src.getHeight();

        if ((actualWidth == targetWidth) && (actualHeight == targetHeight)) {
            return Operations.getDefaultBufferedImage(src);
        }

        // TODO: arguments
        boolean progressiveBilinear = false;
        if ((targetWidth < (actualWidth / 2)) || (targetHeight < (actualHeight / 2))) {
            progressiveBilinear = true;
        }

        dest = ImageUtils.getFasterScaledInstance(src, targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC, progressiveBilinear);
        return dest;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        ImageComponent ic = AppLogic.getActiveImageComponent();

        int width = ic.getCanvasWidth();
        int height = ic.getCanvasHeight();

        return new ResizeAdjustments(this, width, height);
    }

    public void setTargetSize(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    @Override
    public void randomizeSettings() {
        Random random = new Random();
        targetHeight = 10 + random.nextInt(200);
        targetWidth = 10 + random.nextInt(200);
    }
}