/*
 * Copyright 2009 L�szl� Bal�zs-Cs�ki
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

package pixelitor.levels;

/**
 * Contains a lookup array of 256 elements, describing the pixel-by-pixel adjustments
 * made to a single channel
 */
public class GrayScaleLookup {
	private static GrayScaleLookup defaultAdjustment = new GrayScaleLookup(0, 255, 0, 255);

	public GrayScaleLookup(int inputBlackValue, int inputWhiteValue,
			int outputBlackValue, int outputWhiteValue) {
		for (int i = 0; i < mapping.length; i++) {
			double multiplier = ((double)(outputWhiteValue - outputBlackValue))/((double)(inputWhiteValue - inputBlackValue));
			double constant = (outputBlackValue) - (multiplier * inputBlackValue);
			mapping[i] = (short)((multiplier * i) + constant);

			if(mapping[i] < 0) {
				mapping[i] = 0;
			} else if(mapping[i] > 255) {
				mapping[i] = 255;
			}
		}
	}

	private short[] mapping = new short[256];

	public short mapValue(short input) {
		return mapping[input];
	}

	public static GrayScaleLookup getDefaultAdjustment() {
		return defaultAdjustment;
	}
}
