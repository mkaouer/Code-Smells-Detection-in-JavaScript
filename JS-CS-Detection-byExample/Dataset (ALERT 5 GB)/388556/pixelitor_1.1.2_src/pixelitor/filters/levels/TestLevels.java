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

package pixelitor.filters.levels;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestLevels {


    @Test
    public void testMapRGBValue_def() {
        GrayScaleLookup defaultAdjustment = GrayScaleLookup.getDefaultAdjustment();
        RGBLookup detailedRGBAdjustment = new RGBLookup(defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment);

        int a = 255;
        int r = 125;
        int g = 125;
        int b = 125;
        int rgbBefore = (a << 24) | (r << 16) | (g << 8) | b;
        int rgbAfter = detailedRGBAdjustment.mapRGBValue(rgbBefore);

        int rAfter = (rgbAfter >>> 16) & 0xFF;

        assertEquals(rAfter, 125);
    }

    @Test
    public void testMapRGBValue_rgb_input_black() {
        GrayScaleLookup defaultAdjustment = GrayScaleLookup.getDefaultAdjustment();
        GrayScaleLookup customRGBAdjustment = new GrayScaleLookup(50, 255, 0, 255);

        RGBLookup detailedRGBAdjustment = new RGBLookup(customRGBAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment);

        int a = 255;
        int r = 125;
        int g = 125;
        int b = 125;
        int rgbBefore = (a << 24) | (r << 16) | (g << 8) | b;
        int rgbAfter = detailedRGBAdjustment.mapRGBValue(rgbBefore);

        int rAfter = (rgbAfter >>> 16) & 0xFF;

        assertEquals(rAfter, 93);
    }

    @Test
    public void testMapRGBValue_rgb_input_white() {
        GrayScaleLookup defaultAdjustment = GrayScaleLookup.getDefaultAdjustment();
        GrayScaleLookup customRGBAdjustment = new GrayScaleLookup(0, 200, 0, 255);

        RGBLookup detailedRGBAdjustment = new RGBLookup(customRGBAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment);

        int a = 255;
        int r = 125;
        int g = 125;
        int b = 125;
        int rgbBefore = (a << 24) | (r << 16) | (g << 8) | b;
        int rgbAfter = detailedRGBAdjustment.mapRGBValue(rgbBefore);

        int rAfter = (rgbAfter >>> 16) & 0xFF;

        assertEquals(rAfter, 159);
    }


    @Test
    public void testMapRGBValue_rgb_output_black() {
        GrayScaleLookup defaultAdjustment = GrayScaleLookup.getDefaultAdjustment();
        GrayScaleLookup customRGBAdjustment = new GrayScaleLookup(0, 255, 50, 255);

        RGBLookup detailedRGBAdjustment = new RGBLookup(customRGBAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment);

        int a = 255;
        int r = 125;
        int g = 125;
        int b = 125;
        int rgbBefore = (a << 24) | (r << 16) | (g << 8) | b;
        int rgbAfter = detailedRGBAdjustment.mapRGBValue(rgbBefore);

        int rAfter = (rgbAfter >>> 16) & 0xFF;

        assertEquals(rAfter, 150);
    }

    @Test
    public void testMapRGBValue_rgb_output_white() {
        GrayScaleLookup defaultAdjustment = GrayScaleLookup.getDefaultAdjustment();
        GrayScaleLookup customRGBAdjustment = new GrayScaleLookup(0, 255, 0, 200);

        RGBLookup detailedRGBAdjustment = new RGBLookup(customRGBAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment, defaultAdjustment);

        int a = 255;
        int r = 125;
        int g = 125;
        int b = 125;
        int rgbBefore = (a << 24) | (r << 16) | (g << 8) | b;
        int rgbAfter = detailedRGBAdjustment.mapRGBValue(rgbBefore);

        int rAfter = (rgbAfter >>> 16) & 0xFF;

        assertEquals(rAfter, 98);
    }

}
