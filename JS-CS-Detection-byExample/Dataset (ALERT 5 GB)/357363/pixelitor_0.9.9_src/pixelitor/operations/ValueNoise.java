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

import pixelitor.operations.gui.ActionParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Value Noise
 */
public class ValueNoise extends OperationWithParametrizedGUI {
    private static Random rand = new Random();
    private static int r1;
    private static int r2;
    private static int r3;

    static {
        reseed();
    }

    private RangeParam frequency = new RangeParam("Frequency (Scale)", 1, 100, 1);
    private RangeParam details = new RangeParam("Octaves (Details)", 1, 8, 5);
    private ActionParam reseedAction = new ActionParam("Reseed", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            reseed();
        }
    });

    public ValueNoise() {
        super("Value Noise", true);
        paramSet = new ParamSet(
                frequency,
                details,
                reseedAction
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
//        int[] srcData = ImageUtils.getPixelsAsArray(src);


        int[] destData = ImageUtils.getPixelsAsArray(dest);
        int width = dest.getWidth();
        int height = dest.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float persistence = 0.6f;
                float amplitude = 1.0f;
                int octaves = details.getValue();

                boolean detailsDependOnSourceLuminosity = false;
                if (detailsDependOnSourceLuminosity) {
                    octaves = (src.getRGB(x, y) & 0xFF) / 25; // TODO: don't use getRGB, TODO: use real luminosity
                }
                int noise = (int) (255 * generateValueNoise(x, y, octaves, frequency.getValueAsPercentage(), persistence, amplitude));

//                int noise = 100;
                destData[x + y * width] = (0xFF000000 | (noise << 16) | (noise << 8) | noise);
            }
        }

        return dest;
    }

    /**
     * Returns a double between 0 and 1
     */
    public static double generateValueNoise(int x, int y, int octaves, float frequency, float persistence, float amplitude) {
        double total = 0.0;

        for (int lcv = 0; lcv < octaves; lcv++) {
            total += smooth(x * frequency, y * frequency) * amplitude;
            frequency *= 2;
            amplitude *= persistence;
        }

//        double cloudCoverage = 0;
//        double cloudDensity = 1;
//        total = (total + cloudCoverage) * cloudDensity;

        if (total < 0) {
            total = 0.0;
        }
        if (total > 1) {
            total = 1.0;
        }

        return total;
    }

    private static double smooth(double x, double y) {
        double n1 = noise((int) x, (int) y);
        double n2 = noise((int) x + 1, (int) y);
        double n3 = noise((int) x, (int) y + 1);
        double n4 = noise((int) x + 1, (int) y + 1);

        double i1 = interpolate(n1, n2, x - (int) x);
        double i2 = interpolate(n3, n4, x - (int) x);

        return interpolate(i1, i2, y - (int) y);
    }

    public static void reseed() {
        r1 = 1000 + rand.nextInt(90000);
        r2 = 10000 + rand.nextInt(900000);
        r3 = 100000 + rand.nextInt(1000000000);
    }

    private static double noise(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;

        return (1.0 - ((n * (n * n * r1 + r2) + r3) & 0x7fffffff) / 1073741824.0);
    }

    private static double interpolate(double x, double y, double a) {
        double val = (1 - Math.cos(a * Math.PI)) * 0.5;
        return x * (1 - val) + y * val;
    }

    public void setDetails(int newDetails) {
        details.setValue(newDetails);
    }
}