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
package pixelitor.tools.brushes;

import com.jhlabs.awt.WobbleStroke;
import pixelitor.tools.StrokeType;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 *
 */
public class WobbleBrush extends StrokeBrush {
    private static final float SIZE_DIVIDING_FACTOR = 4.0f;

    public WobbleBrush() {
        super(StrokeType.WOBBLE);
    }

    @Override
    public void drawPoint(Graphics2D g, int x, int y, float thickness) {
        float smallThickness = thickness / SIZE_DIVIDING_FACTOR;

        if (thickness != lastThickness) {
            lastStroke = new WobbleStroke(0.5f, smallThickness, smallThickness);
        }
        g.setStroke(lastStroke);

        float halfThickness = thickness / 2.0f;

        Ellipse2D.Float circle = new Ellipse2D.Float(x + halfThickness, y + halfThickness, 0.1f, 0.1f);
        g.draw(circle);
    }

    @Override
    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY, float thickness) {
        super.drawLine(g, startX, startY, endX, endY, thickness / SIZE_DIVIDING_FACTOR);
    }
}
