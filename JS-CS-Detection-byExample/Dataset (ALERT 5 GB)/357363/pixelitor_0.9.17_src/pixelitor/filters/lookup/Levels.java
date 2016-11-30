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
package pixelitor.filters.lookup;

import pixelitor.filters.gui.AdjustPanel;
import pixelitor.filters.levels.GrayScaleLookup;
import pixelitor.filters.levels.RGBLookup;
import pixelitor.filters.levels.gui.LevelsPanel;

import java.awt.image.BufferedImage;

/**
 *
 */
public class Levels extends DynamicLookupOp {
    public Levels() {
        super("Levels");
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        return new LevelsPanel(this);
    }

    public static BufferedImage adjustRGB(BufferedImage img, int inputDark, int inputLight) {
        GrayScaleLookup grayScaleLookup = new GrayScaleLookup(inputDark, inputLight, 0, 255);
        RGBLookup rgbLookup = new RGBLookup(grayScaleLookup);
        DynamicLookupOp levels = new Levels();
        levels.setRGBLookup(rgbLookup);
        return levels.executeForOneLayer(img);
    }
}