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
package pixelitor.filters;

import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Draw Grid
 */
public class DrawGrid extends OperationWithParametrizedGUI {
    private final static int TYPE_GRID = 0;
    private final static int TYPE_GRADIENT_GRID = 1;


    private RangeParam widthParam = new RangeParam("Width", 1, 100, 2);
    private RangeParam spacingParam = new RangeParam("Spacing", 1, 100, 10);
    private ColorParam colorParam = new ColorParam("Color:", Color.BLACK, true);

    public DrawGrid() {
        super("Draw Grid", true);
        paramSet = new ParamSet(
                spacingParam,
                widthParam,
                colorParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        dest = ImageUtils.copyImage(src);

        Color color = colorParam.getColor();
        int width = widthParam.getValue();
        int spacing = spacingParam.getValue();

        Graphics2D g = dest.createGraphics();


        g.setColor(color);
        ImageUtils.drawGrid(g, dest.getWidth(), dest.getHeight(), width, spacing, width, spacing);

        g.dispose();


        return dest;
    }
}