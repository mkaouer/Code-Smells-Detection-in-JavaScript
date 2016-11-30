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
package pixelitor.utils;

import com.jhlabs.composite.RGBComposite;
import org.jdesktop.swingx.graphics.BlendComposite;
import pixelitor.layers.BlendingMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Random;

/**
 * Image utility methods
 */
public class ImageUtils {
    /**
     * Utility class with static methods, do not instantiate
     */
    private ImageUtils() {
    }

    public static BufferedImage transformToCompatibleImage(BufferedImage input) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        if (input.getColorModel().equals(gc.getColorModel())) {
            // already compatible
            return input;
        }

//        int transparency = input.getTransparency();
//        if (transparency == Transparency.TRANSLUCENT && input.getType() == BufferedImage.TYPE_CUSTOM) {
//            transparency = Transparency.OPAQUE;
//        }

        int transparency = Transparency.TRANSLUCENT;
        BufferedImage output = gc.createCompatibleImage(input.getWidth(), input.getHeight(), transparency);
        Graphics2D g = output.createGraphics();
        g.drawImage(input, 0, 0, null);
        g.dispose();

        return output;
    }

    public static BufferedImage createCompatibleImage(int width, int height) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage output = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        return output;
    }

    public static BufferedImage copyImage(BufferedImage src) {
        WritableRaster raster = null;
        try {
            raster = src.copyData(null);
        } catch (OutOfMemoryError e) {
            GUIUtils.showOutOfMemoryDialog();
        }
        return new BufferedImage(src.getColorModel(), raster, src.isAlphaPremultiplied(), null);
    }

    // From the Filthy Rich Clients book

    /**
     * Convenience method that returns a scaled instance of the
     * provided BufferedImage.
     *
     * @param img                 the original image to be scaled
     * @param targetWidth         the desired width of the scaled instance,
     *                            in pixels
     * @param targetHeight        the desired height of the scaled instance,
     *                            in pixels
     * @param hint                one of the rendering hints that corresponds to
     *                            RenderingHints.KEY_INTERPOLATION (e.g.
     *                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
     *                            RenderingHints.VALUE_INTERPOLATION_BILINEAR,
     *                            RenderingHints.VALUE_INTERPOLATION_BICUBIC)
     * @param progressiveBilinear if true, this method will use a multi-step
     *                            scaling technique that provides higher quality than the usual
     *                            one-step technique (only useful in down-scaling cases, where
     *                            targetWidth or targetHeight is
     *                            smaller than the original dimensions)
     * @return a scaled version of the original BufferedImage
     */
    public static BufferedImage getFasterScaledInstance(BufferedImage img,
                                                        int targetWidth, int targetHeight, Object hint,
                                                        boolean progressiveBilinear) {
        int prevW = img.getWidth();
        int prevH = img.getHeight();

        // in these two cases the original method from the Filthy Rich Clients book goes into infinite loop!
        if ((prevH < targetHeight) && (prevW > targetWidth)) {
            return simpleResize(img, targetWidth, targetHeight, hint);
        } else if ((prevH > targetHeight) && (prevW < targetWidth)) {
            return simpleResize(img, targetWidth, targetHeight, hint);
        }


        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;
        int w, h;
        boolean isTranslucent = img.getTransparency() != Transparency.OPAQUE;

        if (progressiveBilinear) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (progressiveBilinear && (w > targetWidth)) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (progressiveBilinear && (h > targetHeight)) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            if ((scratchImage == null) || isTranslucent) {
                // Use a single scratch buffer for all iterations
                // and then copy to the final, correctly-sized image
                // before returning
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
            prevW = w;
            prevH = h;

            ret = scratchImage;
        } while ((w != targetWidth) || (h != targetHeight));

        if (g2 != null) {
            g2.dispose();
        }

        // If we used a scratch buffer that is larger than our target size,
        // create an image of the right size and copy the results into it
        if ((targetWidth != ret.getWidth()) || (targetHeight != ret.getHeight())) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return ret;
    }

    private static BufferedImage simpleResize(BufferedImage img, int targetWidth, int targetHeight, Object hint) {
        BufferedImage ret = new BufferedImage(targetWidth, targetHeight, img.getType());
        Graphics2D g2 = ret.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
        g2.drawImage(img, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();
        return ret;
    }

    public static BufferedImage create2x2TestImage() {
        BufferedImage retVal = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB_PRE);

        retVal.setRGB(0, 0, Color.GRAY.getRGB());
        retVal.setRGB(0, 1, Color.RED.getRGB());
        retVal.setRGB(1, 0, Color.GREEN.getRGB());
        retVal.setRGB(1, 1, Color.BLUE.getRGB());

        return retVal;
    }

    public static String get2x2TestImageAsString(BufferedImage src) {
        String retVal = intColorToString(src.getRGB(0, 0)) + ", " + intColorToString(src.getRGB(0, 1)) + "\n";
        retVal += intColorToString(src.getRGB(1, 0)) + ", " + intColorToString(src.getRGB(1, 1));
        return retVal;
    }

    public static String intColorToString(int color) {
        Color c = new Color(color);
        return "[r=" + c.getRed() + ", g=" + c.getGreen() + ", b=" + c.getBlue() + "]";
    }

    /**
     * Samples 9 pixels at and around the given pixel coordinates
     *
     * @param src
     * @param x
     * @param y
     * @return the average color
     */
    public static Color sample9Points(BufferedImage src, int x, int y) {
        int averageRed = 0;
        int averageGreen = 0;
        int averageBlue = 0;
        int width = src.getWidth();
        int height = src.getHeight();

        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                int limitedX = limitSamplingIndex(i, width - 1);
                int limitedY = limitSamplingIndex(j, height - 1);

                int rgb = src.getRGB(limitedX, limitedY);
//                int a = (rgb >>> 24) & 0xFF;
                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = (rgb) & 0xFF;
                averageRed += r;
                averageGreen += g;
                averageBlue += b;
            }
        }

        return new Color(averageRed / 9, averageGreen / 9, averageBlue / 9);
    }

    private static int limitSamplingIndex(int x, int max) {
        int r = x;
        if (r < 0) {
            r = 0;
        }
        if (r > max) {
            r = max;
        }
        return r;
    }

    public static boolean hasPackedIntArray(BufferedImage image) {
        int type = image.getType();

        return (type == BufferedImage.TYPE_INT_ARGB_PRE || type == BufferedImage.TYPE_INT_RGB || type == BufferedImage.TYPE_INT_ARGB);
    }

    public static int[] getPixelsAsArray(BufferedImage src) {
        int[] pixels;

        boolean fastWay = hasPackedIntArray(src);
        if (fastWay) {
            DataBufferInt srcDataBuffer = (DataBufferInt) src.getRaster().getDataBuffer();
            pixels = srcDataBuffer.getData();
        } else {
            int width = src.getWidth();
            int height = src.getHeight();
            pixels = new int[width * height];
            PixelGrabber pg = new PixelGrabber(src, 0, 0, width, height, pixels, 0, width);

            try {
                pg.grabPixels();
            } catch (InterruptedException e) {
                GUIUtils.showExceptionDialog(null, e);
            }
        }
        return pixels;
    }

    private static URL resourcePathToURL(String fileName) {
        String iconPath = "/images/" + fileName;
        URL imgURL = ImageUtils.class.getResource(iconPath);
        if (imgURL == null) {
            JOptionPane.showMessageDialog(null, iconPath + " not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return imgURL;
    }

    public static ImageIcon loadIcon(String iconFileName) {
        URL imgURL = resourcePathToURL(iconFileName);
        ImageIcon icon = new ImageIcon(imgURL);
        return icon;
    }

    /**
     * Not used but it will be used in the future for loading image brushes
     */
    public static BufferedImage loadBufferedImage(String fileName) {
        URL imgURL = resourcePathToURL(fileName);
        BufferedImage image = null;
        try {
            image = ImageIO.read(imgURL);
        } catch (IOException e) {
            GUIUtils.showExceptionDialog(e);
        }
        return image;
    }

    public static int limitTo8Bits(int value) {
        if (value > 0xFF) {
            return 0xFF;
        }
        if (value < 0) {
            return 0;
        }
        return value;
    }

    public static short limitTo8Bits(short value) {
        if (value > 0xFF) {
            return 0xFF;
        }
        if (value < 0) {
            return 0;
        }
        return value;
    }


    private static BufferedImage convertToARGB(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dest;
    }

    public static BufferedImage convertToRGB(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dest;
    }

    public static Color getRandomColor() {
        Random rnd = new Random();
        int r = rnd.nextInt(256);
        int g = rnd.nextInt(256);
        int b = rnd.nextInt(256);
        return new Color(r, g, b);
    }

    /**
     * Calculates the average of two colors in the HSB space. Full opacity is assumed.
     */
    public static Color getHSBAverageColor(Color c1, Color c2) {
        int rgb1 = c1.getRGB();
        int rgb2 = c2.getRGB();

        int r1 = (rgb1 >>> 16) & 0xFF;
        int g1 = (rgb1 >>> 8) & 0xFF;
        int b1 = (rgb1) & 0xFF;

        int r2 = (rgb2 >>> 16) & 0xFF;
        int g2 = (rgb2 >>> 8) & 0xFF;
        int b2 = (rgb2) & 0xFF;

        float[] hsb1 = Color.RGBtoHSB(r1, g1, b1, null);
        float[] hsb2 = Color.RGBtoHSB(r2, g2, b2, null);

        float hue1 = hsb1[0];
        float hue2 = hsb2[0];
        float hue = calculateHueAverage(hue1, hue2);

        float sat = (hsb1[1] + hsb2[1])/2.0f;
        float bri = (hsb1[2] + hsb2[2])/2.0f;
        return Color.getHSBColor(hue, sat, bri);
    }

    private static float calculateHueAverage(float f1, float f2) {
        float delta = f1 - f2;
        if(delta < 0.5f && delta > -0.5f) {
            return (f1 + f2)/2.0f;
        } else if(delta >= 0.5f) { // f1 is bigger
            float retVal = f1 + (1.0f - f1 +  f2)/2.0f;
            return retVal;
        } else if(delta <= 0.5f) { // f2 is bigger
            float retVal = f2 + (1.0f - f2 +  f1)/2.0f;
            return retVal;
        } else {
            throw new IllegalStateException("should not get here");
        }
    }

    /**
     * Calculates the average of two colors in the RGB space. Full opacity is assumed.
     */
    public static Color getRGBAverageColor(Color c1, Color c2) {
        int rgb1 = c1.getRGB();
        int rgb2 = c2.getRGB();

        int r1 = (rgb1 >>> 16) & 0xFF;
        int g1 = (rgb1 >>> 8) & 0xFF;
        int b1 = (rgb1) & 0xFF;

        int r2 = (rgb2 >>> 16) & 0xFF;
        int g2 = (rgb2 >>> 8) & 0xFF;
        int b2 = (rgb2) & 0xFF;

        int r = (r1 + r2) / 2;
        int g = (g1 + g2) / 2;
        int b = (b1 + b2) / 2;

        return new Color(r, g, b);
    }


    public static Composite calculateComposite(BlendingMode blendingMode, float opacity) {
        Composite composite = blendingMode.getComposite();
        if (composite instanceof AlphaComposite) {
            return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        } else if (composite instanceof BlendComposite) {
            return ((BlendComposite) composite).derive(opacity);
        } else if (composite instanceof RGBComposite) {
            RGBComposite rgbComposite = (RGBComposite) composite;
            rgbComposite.setExtraAlpha(opacity);
            return rgbComposite;
        }
        throw new IllegalStateException("should not get here, composite = " + composite);
    }

    // without this the drawing on large images would be very slow
    // TODO is this faster? the simple g.drawImage also respects the clipping

    public static void drawImageWithClipping(Graphics g, BufferedImage img) {
        Rectangle clipBounds = g.getClipBounds();
        int clipX = (int) clipBounds.getX();
        int clipY = (int) clipBounds.getY();
        int clipWidth = (int) clipBounds.getWidth();
        int clipHeight = (int) clipBounds.getHeight();
        int clipX2 = clipX + clipWidth;
        int clipY2 = clipY + clipHeight;
        g.drawImage(img, clipX, clipY, clipX2, clipY2, clipX, clipY, clipX2, clipY2, null);
    }

    public static void serializeImage(ObjectOutputStream out, BufferedImage img) throws IOException {
        out.writeInt(img.getWidth());
        out.writeInt(img.getHeight());
        out.writeInt(img.getType());
        int[] pixelsAsArray = getPixelsAsArray(img);
        for (int pixel : pixelsAsArray) {
            out.writeInt(pixel);
        }
    }

    public static BufferedImage deserializeImage(ObjectInputStream in) throws IOException {
        int width = in.readInt();
        int height = in.readInt();
        int type = in.readInt();
        BufferedImage img = new BufferedImage(width, height, type);
        int[] pixelsAsArray = getPixelsAsArray(img);
        for (int i = 0; i < pixelsAsArray.length; i++) {
            pixelsAsArray[i] = in.readInt();
        }
        return img;
    }
}