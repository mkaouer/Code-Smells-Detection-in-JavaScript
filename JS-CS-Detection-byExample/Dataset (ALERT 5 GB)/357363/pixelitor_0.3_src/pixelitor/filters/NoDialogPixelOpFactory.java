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

import javax.swing.*;
import java.awt.Color;

public class NoDialogPixelOpFactory {
    private NoDialogPixelOpFactory() {} // should not be instantiated

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

    public static Action getLuminanceChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                int luminance = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                r = luminance;
                g = luminance;
                b = luminance;

                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Luminance = 0.3*R + 0.59*G + 0.11*B", rgbOp);
    }

    public static Action getLuminance2ChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                int luminance = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);

                r = luminance;
                g = luminance;
                b = luminance;

                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Luminance = 0.21*R + 0.71*G + 0.07*B", rgbOp);
    }

    public static Action getLuminance3ChannelOp() {
        RGBPixelOp rgbOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                int luminance = (int) (Math.sqrt(0.241*r*r + 0.691*g*g + 0.068*b*b));

                r = luminance;
                g = luminance;
                b = luminance;

                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        };
        return new PixelOperation("Luminance = sqrt( 0.241*R^2 + 0.691*G^2 + 0.068*B^2)", rgbOp);
    }

    public static Action getBrightnessChannelOp() {
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
        return new PixelOperation("Brightness = [max(R,G,B) + min (R,G,B)] / 2 = Desaturate", rgbOp);
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
            // preallocated, so that an array allocation is not necessary for every pixel.
            private float[] tmpHSBArray = new float[]{0f, 0f, 0f};

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

            // preallocated, so that an array allocation is not necessary for every pixel.
            private float[] tmpHSBArray = new float[]{0f, 0f, 0f};

            @Override
            public int changeRGB(int a, int r, int g, int b) {
                tmpHSBArray = Color.RGBtoHSB(r, g, b, tmpHSBArray);
                return Color.HSBtoRGB(tmpHSBArray[0], DEFAULT_SATURATION, DEFAULT_BRIGHTNESS);
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
