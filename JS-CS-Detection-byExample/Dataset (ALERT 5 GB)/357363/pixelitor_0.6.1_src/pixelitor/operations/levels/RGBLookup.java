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

package pixelitor.operations.levels;

import pixelitor.operations.lookup.LookupFactory;
import pixelitor.utils.ImageUtils;

import java.awt.image.LookupTable;

/**
 * Manages 3 lookup arrays, corresponding to the
 * R, G, B channels of a pixel-by-pixel adjustment
 */
public class RGBLookup {
    private short[] finalRedMapping = new short[256]; // the added effect of R and RGB adjustments
    private short[] finalGreenMapping = new short[256]; // the added effect of G and RGB adjustments
    private short[] finalBlueMapping = new short[256]; // the added effect of B and RGB adjustments

    public RGBLookup() {
    }

    public RGBLookup(GrayScaleLookup rgb,
                     GrayScaleLookup r,
                     GrayScaleLookup g,
                     GrayScaleLookup b,
                     GrayScaleLookup rg,
                     GrayScaleLookup rb,
                     GrayScaleLookup gb
    ) {

        for (short i = 0; i < finalRedMapping.length; i++) {
            finalRedMapping[i] = rgb.mapValue(i);
            finalRedMapping[i] = r.mapValue(finalRedMapping[i]);
            finalRedMapping[i] = rg.mapValue(finalRedMapping[i]);
            finalRedMapping[i] = rb.mapValue(finalRedMapping[i]);
        }
        for (short i = 0; i < finalGreenMapping.length; i++) {
            finalGreenMapping[i] = rgb.mapValue(i);
            finalGreenMapping[i] = g.mapValue(finalGreenMapping[i]);
            finalGreenMapping[i] = rg.mapValue(finalGreenMapping[i]);
            finalGreenMapping[i] = gb.mapValue(finalGreenMapping[i]);
        }
        for (short i = 0; i < finalBlueMapping.length; i++) {
            finalBlueMapping[i] = rgb.mapValue(i);
            finalBlueMapping[i] = b.mapValue(finalBlueMapping[i]);
            finalBlueMapping[i] = rb.mapValue(finalBlueMapping[i]);
            finalBlueMapping[i] = gb.mapValue(finalBlueMapping[i]);
        }
    }

    public void initFromColorBalance(short cyanRedValue, short magentaGreenValue, short yellowBlueValue) {
        for (short i = 0; i < finalRedMapping.length; i++) {
            short r = (short) (i + cyanRedValue - (magentaGreenValue / 2) - (yellowBlueValue / 2));
            r = ImageUtils.limitTo8Bits(r);
            finalRedMapping[i] = r;
        }
        for (short i = 0; i < finalGreenMapping.length; i++) {
            short g = (short) (i + magentaGreenValue - (cyanRedValue / 2) - (yellowBlueValue / 2));
            g = ImageUtils.limitTo8Bits(g);
            finalGreenMapping[i] = g;
        }
        for (short i = 0; i < finalBlueMapping.length; i++) {
            short b = (short) (i + yellowBlueValue - (magentaGreenValue / 2) - (cyanRedValue / 2));
            b = ImageUtils.limitTo8Bits(b);
            finalBlueMapping[i] = b;
        }
    }

    // this is used only by the test class

    public int mapRGBValue(int rgb) {
        int a = (rgb >>> 24) & 0xFF;
        int r = (rgb >>> 16) & 0xFF;
        int g = (rgb >>> 8) & 0xFF;
        int b = (rgb) & 0xFF;

        r = finalRedMapping[r];
        g = finalGreenMapping[g];
        b = finalBlueMapping[b];

        rgb = (a << 24) | (r << 16) | (g << 8) | b;

        return rgb;
    }

//	public short[] getFinalRedMapping() {
//		return finalRedMapping;
//	}
//
//	public short[] getFinalGreenMapping() {
//		return finalGreenMapping;
//	}
//
//	public short[] getFinalBlueMapping() {
//		return finalBlueMapping;
//	}

    public LookupTable getLookupOp() {
        return LookupFactory.createLookupFrom3Arrays(finalRedMapping, finalGreenMapping, finalBlueMapping);
    }

    public void initFromPosterize(int numLevels) {
        for (int i = 0; i < 256; i++) {
            int mapping = 255 * (numLevels * i / 256) / (numLevels - 1);
            finalRedMapping[i] = (short) mapping;
            finalGreenMapping[i] = (short) mapping;
            finalBlueMapping[i] = (short) mapping;
        }
    }
}
