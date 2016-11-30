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
package pixelitor.operations.lookup;

/**
 *
 */
public class LuminanceLookup {
    private static final int TABLE_SIZE = 256;

    private static final float redContribution = 0.2126f;
    private static final float greenContribution = 0.7152f;
    private static final float blueContribution = 0.0722f;

    private static final float[] redLuminanceTable = new float[TABLE_SIZE];
    private static final float[] greenLuminanceTable = new float[TABLE_SIZE];
    private static final float[] blueLuminanceTable = new float[TABLE_SIZE];

    static {
        for (int i = 0; i < TABLE_SIZE; i++) {
            redLuminanceTable[i] = i * redContribution;
            greenLuminanceTable[i] = i * greenContribution;
            blueLuminanceTable[i] = i * blueContribution;
        }
    }

    /**
     * Utility class with static methods, do not instantiate
     */
    private LuminanceLookup() {
    }

    public static int getLuminosity(int r, int g, int b) {
        return (int) (redLuminanceTable[r] + greenLuminanceTable[g] + blueLuminanceTable[b]);
    }

    public static int getLuminosity(int rgb) {
        int r = (rgb >>> 16) & 0xFF;
        int g = (rgb >>> 8) & 0xFF;
        int b = (rgb) & 0xFF;
        return (int) (redLuminanceTable[r] + greenLuminanceTable[g] + blueLuminanceTable[b]);
    }
}