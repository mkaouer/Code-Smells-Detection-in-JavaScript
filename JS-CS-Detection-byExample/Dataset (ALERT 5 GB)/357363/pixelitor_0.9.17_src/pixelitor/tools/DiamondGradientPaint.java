/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.tools;

import java.awt.Color;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * A Paint that creates a "diamond gradient"
 */
public class DiamondGradientPaint implements Paint {
    private UserDrag userDrag;
    private Color startColor;
    private Color endColor;
    private MultipleGradientPaint.CycleMethod cycleMethod;

    public DiamondGradientPaint(UserDrag userDrag, Color startColor, Color endColor, MultipleGradientPaint.CycleMethod cycleMethod) {
        this.userDrag = userDrag;
        this.startColor = startColor;
        this.endColor = endColor;
        this.cycleMethod = cycleMethod;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new DiamondGradientPaintContext(userDrag, startColor, endColor, cm, cycleMethod);
    }

    @Override
    public int getTransparency() {
        int a1 = startColor.getAlpha();
        int a2 = endColor.getAlpha();
        return (((a1 & a2) == 0xFF) ? OPAQUE : TRANSLUCENT);
    }

    static class DiamondGradientPaintContext implements PaintContext {
        private UserDrag userDrag;
        private MultipleGradientPaint.CycleMethod cycleMethod;

        private int startAlpha;
        private int startRed;
        private int startGreen;
        private int startBlue;

        private int endAlpha;
        private int endRed;
        private int endGreen;
        private int endBlue;

        private ColorModel cm;

        private float proportionalXDiff;
        private float proportionalYDiff;

        private DiamondGradientPaintContext(UserDrag userDrag, Color startColor, Color endColor, ColorModel cm, MultipleGradientPaint.CycleMethod cycleMethod) {
            this.userDrag = userDrag;
            this.cycleMethod = cycleMethod;

            startAlpha = startColor.getAlpha();
            startRed = startColor.getRed();
            startGreen = startColor.getGreen();
            startBlue = startColor.getBlue();

            endAlpha = endColor.getAlpha();
            endRed = endColor.getRed();
            endGreen = endColor.getGreen();
            endBlue = endColor.getBlue();

            this.cm = cm;

            double distance = userDrag.getDistance();
            double distanceSqr = distance * distance;
            proportionalXDiff = (float) (userDrag.getHorizontalDifference() / distanceSqr);
            proportionalYDiff = (float) (userDrag.getVerticalDifference() / distanceSqr);
        }

        @Override
        public void dispose() {

        }

        @Override
        public ColorModel getColorModel() {
            return cm;
        }

        @Override
        public Raster getRaster(int x, int y, int w, int h) {
            WritableRaster raster = cm.createCompatibleWritableRaster(w, h);
            int[] rasterData = new int[w * h * 4];

            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    int base = (j * w + i) * 4;

                    float relativeRenderX = x + i - userDrag.getStartX();
                    float relativeRenderY = y + j - userDrag.getStartY();

                    float v1 = Math.abs((relativeRenderX * this.proportionalXDiff) + (relativeRenderY * this.proportionalYDiff));
                    float v2 = Math.abs((relativeRenderX * this.proportionalYDiff) - (relativeRenderY * this.proportionalXDiff));

                    float interpolationValue = v1 + v2;

                    if (cycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE) {
                        if (interpolationValue > 1.0) {
                            interpolationValue = 1.0f;
                        }
                    } else if (cycleMethod == MultipleGradientPaint.CycleMethod.REFLECT) {
                        interpolationValue %= 1.0;
                        if (interpolationValue < 0.5) {
                            interpolationValue = 2.0f * interpolationValue;
                        } else {
                            interpolationValue = 2.0f * (1 - interpolationValue);
                        }
                    } else if (cycleMethod == MultipleGradientPaint.CycleMethod.REPEAT) {
                        interpolationValue %= 1.0;
                        if (interpolationValue < 0.5) {
                            interpolationValue = 2.0f * interpolationValue;
                        } else {
                            interpolationValue = 2.0f * (interpolationValue - 0.5f);
                        }
                    }

                    int a = (int) (startAlpha + interpolationValue * (endAlpha - startAlpha));
                    int r = (int) (startRed + interpolationValue * (endRed - startRed));
                    int g = (int) (startGreen + interpolationValue * (endGreen - startGreen));
                    int b = (int) (startBlue + interpolationValue * (endBlue - startBlue));

                    rasterData[base] = r;
                    rasterData[base + 1] = g;
                    rasterData[base + 2] = b;
                    rasterData[base + 3] = a;
                }
            }

            raster.setPixels(0, 0, w, h, rasterData);
            return raster;
        }
    }
}