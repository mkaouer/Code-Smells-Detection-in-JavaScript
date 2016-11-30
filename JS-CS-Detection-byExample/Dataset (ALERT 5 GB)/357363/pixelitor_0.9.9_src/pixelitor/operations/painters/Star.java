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
package pixelitor.operations.painters;

import org.jdesktop.swingx.geom.Star2D;
import org.jdesktop.swingx.painter.effects.GlowPathEffect;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class Star extends OperationWithParametrizedGUI {
    private RangeParam innerRadius = new RangeParam("Radius 1", 0, 100, 20);
    private RangeParam outerRadius = new RangeParam("Radius 2", 0, 100, 80);
    private RangeParam numberOfBranches = new RangeParam("Number of Branches", 3, 20, 10);

    public Star() {
        super("Star", true);
        copySrcToDstBeforeRunning = true;
        paramSet = new ParamSet(
                innerRadius,
                outerRadius,
                numberOfBranches
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int x = dest.getWidth() / 2;
        int y = dest.getHeight() / 2;
        int innerRadiusVal = innerRadius.getValue();
        int outerRadiusVal = outerRadius.getValue();
        if (outerRadiusVal < innerRadiusVal) {
            int tmp = innerRadiusVal;
            innerRadiusVal = outerRadiusVal;
            outerRadiusVal = tmp;
        }
        // TODO this is an ugly workaround because Star2D won't accept equal radii
        if (innerRadiusVal == outerRadiusVal) {
            outerRadiusVal = innerRadiusVal + 1;
        }

        Star2D star = new Star2D(x, y, innerRadiusVal, outerRadiusVal, numberOfBranches.getValue());
        GlowPathEffect effect = new GlowPathEffect();
        Graphics2D g = dest.createGraphics();
        effect.apply(g, star, dest.getWidth(), dest.getHeight());
        g.dispose();
        return dest;
    }
}
