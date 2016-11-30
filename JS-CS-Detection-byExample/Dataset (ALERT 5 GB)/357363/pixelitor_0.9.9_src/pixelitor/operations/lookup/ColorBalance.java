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
package pixelitor.operations.lookup;

import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.RangeWithColorsParam;
import pixelitor.operations.levels.RGBLookup;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ShortLookupTable;

/**
 *
 */
public class ColorBalance extends OperationWithParametrizedGUI {
    private static final int CB_MIN = -100;
    private static final int CB_MAX = 100;
    private static final int CB_INIT = 0;
    private RangeParam cyanRedParam = new RangeWithColorsParam(Color.CYAN, Color.RED, "Cyan-Red", CB_MIN, CB_MAX, CB_INIT);
    private RangeParam magentaGreenParam = new RangeWithColorsParam(Color.MAGENTA, Color.GREEN, "Magenta-Green", CB_MIN, CB_MAX,
            CB_INIT);
    private RangeParam yellowBlueParam = new RangeWithColorsParam(Color.YELLOW, Color.BLUE, "Yellow-Blue", CB_MIN, CB_MAX,
            CB_INIT);


    public ColorBalance() {
        super("Color Balance", false);
        paramSet = new ParamSet(
                cyanRedParam,
                magentaGreenParam,
                yellowBlueParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        RGBLookup rgbLookup = new RGBLookup();
        short cr = (short) cyanRedParam.getValue();
        short mg = (short) magentaGreenParam.getValue();
        short yb = (short) yellowBlueParam.getValue();
        rgbLookup.initFromColorBalance(cr, mg, yb);

        BufferedImageOp filterOp = new FastLookupOp((ShortLookupTable) rgbLookup.getLookupOp());
        filterOp.filter(src, dest);

        return dest;
    }
}
