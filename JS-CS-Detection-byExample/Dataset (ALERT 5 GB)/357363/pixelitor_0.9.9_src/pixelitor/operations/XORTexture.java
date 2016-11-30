/*
 * Copyright 2010 László Balázs-Csíki
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

import pixelitor.operations.gui.BooleanParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * XOR Texture
 */
public class XORTexture extends OperationWithParametrizedGUI {
    private RangeParam zoom = new RangeParam("Zoom (%)", 10, 400, 100);

    private RangeParam xShiftParam = new RangeParam("Horizontal Shift", 0, 128, 0);
    private RangeParam yShiftParam = new RangeParam("Vertical Shift", 0, 128, 0);

    private BooleanParam sourceColors = new BooleanParam("Use Source Colors", false);
    private static float[] hsb = null;

    private static final int XOR = 0;
    private static final int AND = 1;
    private static final int OR = 2;
    private static final int MULTIPLY = 3;
    private static final int SIN = 4;

    private IntChoiceParam opSelector = new IntChoiceParam("Operation",
            new IntChoiceParam.Value[]{
                    new IntChoiceParam.Value("XOR", XOR),
                    new IntChoiceParam.Value("AND", AND),
                    new IntChoiceParam.Value("OR", OR),
                    new IntChoiceParam.Value("Multiply", MULTIPLY),
                    new IntChoiceParam.Value("Sin", SIN),
            }
    );

    public XORTexture() {
        super("XOR Texture", true);
        paramSet = new ParamSet(
                opSelector,
                zoom,
                xShiftParam,
                yShiftParam,
                sourceColors
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        boolean useSourceColors = sourceColors.getValue();
        int op = opSelector.getCurrentInt();

        int[] srcData = null;
        if (useSourceColors) {
            srcData = ImageUtils.getPixelsAsArray(src);
        }

        int width = dest.getWidth();
        int height = dest.getHeight();

        int[] destData = ImageUtils.getPixelsAsArray(dest);

        float zoomFactor = zoom.getValueAsPercentage();

        int xShift = xShiftParam.getValue();
        int yShift = yShiftParam.getValue();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                double hor = ((x + 256 - xShift) / zoomFactor);
                double ver = ((y + 256 - yShift) / zoomFactor);
                int horInt = (int) hor;
                int verInt = (int) ver;

                int value = 0;
                switch(op) {
                    case XOR:
                        value = (horInt ^ verInt) % 256;
                        break;
                    case AND:
                        value = (horInt & verInt) % 256;
                        break;
                    case OR:
                        value = (horInt | verInt) % 256;
                        break;
                    case MULTIPLY:
                        value = (int) ((hor * ver) % 256);
                        break;
                    case SIN:
                        value = (int) ((Math.sin(hor * ver/10.0) + 1) * 128);
                        break;
                    default:
                        throw new IllegalStateException("should not get here");
                }

                if (useSourceColors) {
                    destData[x + y * width] = getColorizedBrightness(srcData[x + y * width], value / 256.0f);
                } else {
                    destData[x + y * width] = (0xFF000000 | value << 16 | value << 8 | value);
                }
            }
        }
        return dest;
    }


    private static int getColorizedBrightness(int colorSource, float newBri) {
        int r = (colorSource >>> 16) & 0xFF;
        int g = (colorSource >>> 8) & 0xFF;
        int b = (colorSource) & 0xFF;

        hsb = Color.RGBtoHSB(r, g, b, hsb);
        int newRGB = Color.HSBtoRGB(hsb[0], hsb[1], newBri);
        return newRGB;
    }
}