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

import javax.swing.*;
import java.awt.Color;

public class NoDialogPixelOpFactory {
    private NoDialogPixelOpFactory() {
    } // should not be instantiated

    public static Action getRedChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                g = r;
                b = r;
                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Red (as BW)", rgbOp);
    }

    public static Action getGreenChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                r = g;
                b = g;
                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Green (as BW)", rgbOp);
    }

    public static Action getBlueChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                r = b;
                g = b;
                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Blue (as BW)", rgbOp);
    }

    public static Action getValueChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                // value = max(R, G, B)
                int maxRGB = (r > g) ? r : g;
                if (b > maxRGB) {
                    maxRGB = b;
                }

                int value = maxRGB;

                r = value;
                g = value;
                b = value;

                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Value = max(R,G,B)", rgbOp);
    }

    public static Action getDesaturateChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                // brightness = [max(R, G, B) + min (R, G, B)] / 2
                int maxRGB = (r > g) ? r : g;
                if (b > maxRGB) {
                    maxRGB = b;
                }
                int minRGB = (r < g) ? r : g;
                if (b < minRGB) {
                    minRGB = b;
                }

                int brightness = (maxRGB + minRGB) / 2;

                r = brightness;
                g = brightness;
                b = brightness;

                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
//        return new PixelOperation("Brightness = [max(R,G,B) + min (R,G,B)] / 2 = Desaturate", rgbOp);
        return new PixelOperation("Desaturate", rgbOp);
    }


    public static Action getSaturationChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                int rgbMax = (r > g) ? r : g;
                if (b > rgbMax) {
                    rgbMax = b;
                }
                int rgbMin = (r < g) ? r : g;
                if (b < rgbMin) {
                    rgbMin = b;
                }

                int saturation = 0;
                if (rgbMax != 0) {
                    saturation = (int) (((float) (rgbMax - rgbMin)) / ((float) rgbMax) * 255);
                }

                r = saturation;
                g = saturation;
                b = saturation;


                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Saturation", rgbOp);
    }

    public static Action getHueChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            private float[] tmpHSBArray = new float[]{0.0f, 0.0f, 0.0f};

            @Override
            public int changeRGB(int a, int r, int g, int b) {
                tmpHSBArray = Color.RGBtoHSB(r, g, b, tmpHSBArray);

                // Color.RGBtoHSB return all values in the 0..1 interval
                int hue = (int) (tmpHSBArray[0] * 255);

                r = hue;
                g = hue;
                b = hue;

                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Hue", rgbOp);
    }


    public static Action getHueInColorsChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            private static final float DEFAULT_SATURATION = 0.9f;
            private static final float DEFAULT_BRIGHTNESS = 0.75f;

            private float[] tmpHSBArray = new float[]{0.0f, 0.0f, 0.0f};

            @Override
            public int changeRGB(int a, int r, int g, int b) {
                if (a == 0) {
                    return 0; // for premultiplied images
                }
                tmpHSBArray = Color.RGBtoHSB(r, g, b, tmpHSBArray);
                int newRGB = Color.HSBtoRGB(tmpHSBArray[0], DEFAULT_SATURATION, DEFAULT_BRIGHTNESS); // alpha is 255 here
                newRGB &= 0x00FFFFFF;  // set alpha to 0
                return (a << 24) | newRGB; // add the real alpha
            }
        };
        return new PixelOperation("Hue (with colors)", rgbOp);
    }


//    public static Action getChannelOp() {
//        RGBPixelOp rgbOp = new NoDialogPixelOp() {
//            @Override
//            public int changeRGB(int a, int r, int g, int b) {
//
//
//                int rgb = (a << 24) | (r << 16) | (g << 8) | b;
//                return rgb;
//            }
//        };
//        return new PixelOperation("new ", rgbOp);
//    }

}
